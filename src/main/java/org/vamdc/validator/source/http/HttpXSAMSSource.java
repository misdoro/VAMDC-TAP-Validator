package org.vamdc.validator.source.http;

import java.io.IOException;

import net.ivoa.xml.votable.v1.VOTABLE;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.validator.Setting;
import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.io.Input;
import org.vamdc.validator.source.XSAMSSource;
import org.vamdc.validator.source.XSAMSSourceException;
import org.vamdc.xsams.io.PrettyPrint;

public class HttpXSAMSSource implements XSAMSSource {

	private String baseURLStr = "";
	private String filename = "";

	private CapabilitiesClient caps;

	private PrettyPrint prettyprinter = new PrettyPrint(); 

	public HttpXSAMSSource() throws XSAMSSourceException{
		String vosiURL = Setting.ServiceVOSIURL.getValue();
		String tapURL = Setting.ServiceTAPURL.getValue();
		if (vosiURL!=null && vosiURL.length()>0){
			caps = new CapabilitiesClient(vosiURL);
			baseURLStr = caps.getTapEndpoint();
		}else{
			caps = CapabilitiesClient.empty();
			baseURLStr = tapURL;
		}
		baseURLStr+=Setting.ServiceTAPSuffix.getValue();
		
		Tool.getURL(baseURLStr);
	}

	@Override
	public InputStream getXsamsStream(String query,XSAMSIOModel document) throws XSAMSSourceException {
		if (caps.getAvailabilityEndpoint()!=null){
			checkAvailability();
		}
		return doQuery(query,document);
	}
	
	@Override
	public Map<HeaderMetrics, String> getMetrics(String query)
			throws XSAMSSourceException {
		if (caps.getAvailabilityEndpoint()!=null){
			checkAvailability();
		}
		URL requestURL = prepareRequestURL(query);
		try {
			HttpURLConnection conn = Input.openConnection(requestURL);
			conn.setRequestMethod("HEAD");
			
			handleStatusCode(conn);
			
			return processHeaders(conn.getHeaderFields());
		} catch (IOException e) {
			transformException(requestURL,e);
			return Collections.emptyMap();
		}
	}

	@Override
	public Collection<String> getRestrictables() {
		return caps.getRestrictables();
	}

	@Override
	public Collection<String> getSampleQueries() {
		return caps.getSampleQueries();
	}


	
	private void checkAvailability() throws XSAMSSourceException {
		AvailabilityClient avail = new AvailabilityClient(caps.getAvailabilityEndpoint());
		if (!avail.isAvailable())
			throw new XSAMSSourceException("Service not available: "+avail.getMessage());
	}


	private InputStream doQuery(String query, XSAMSIOModel document) throws XSAMSSourceException{
		URL requestURL = prepareRequestURL(query);

		InputStream result=null;
		try{
			result = openConnection(requestURL);
			//From here we must know the filename
			document.setFilename(filename);
			
			if (Setting.PrettyPrint.getBool())
				result=prettyprinter.transform(result);
		}  catch (IOException e) {
			transformException(requestURL, e);
		}
		return result;
	}

	private void transformException(URL requestURL, IOException e)
			throws XSAMSSourceException {
		throw new XSAMSSourceException("IO exception while opening http connection:"+e.getMessage()+" for query "+requestURL.toString());
	}

	private URL prepareRequestURL(String query) throws XSAMSSourceException {
		try {
			String encodedUrl = baseURLStr+URLEncoder.encode(query,"UTF-8");
			return Tool.getURL(encodedUrl);
		}catch (UnsupportedEncodingException e1) {}
		return null;
	}
	
	private void handleStatusCode(HttpURLConnection connection) throws XSAMSSourceException {
		try {
			switch(connection.getResponseCode()){
				case HttpURLConnection.HTTP_OK:
					return;
				case HttpURLConnection.HTTP_NO_CONTENT:
					throw new XSAMSSourceException("Node contains no data for query "+connection.getURL());
				case HttpURLConnection.HTTP_BAD_REQUEST:
					InputStream es = connection.getErrorStream();
					if (es!=null)
						throw new XSAMSSourceException(unmarshalVotable(es));
				default:
					throw new XSAMSSourceException("Response code "+connection.getResponseCode()+" for "+connection.getURL());
			}
		} catch (IOException e) {e.printStackTrace();
		} catch (JAXBException e) {e.printStackTrace();
		}
	}

	private VOTABLE unmarshalVotable(InputStream eStream)
			throws JAXBException, XSAMSSourceException {
		if (eStream==null)
			return null;
		JAXBContext votc = JAXBContext.newInstance(VOTABLE.class);
		Unmarshaller um = votc.createUnmarshaller();
		Object vot = um.unmarshal(eStream);

		if (VOTABLE.class.isAssignableFrom(vot.getClass()))
			return (VOTABLE) vot;
		return null;
	}

	/**
	 * Open URL connection, with timeouts set
	 * @param address connection URL
	 * @return Stream of data
	 * @throws IOException 
	 * @throws XSAMSSourceException 
	 */
	private InputStream openConnection(URL adress) throws IOException, XSAMSSourceException{
		HttpURLConnection conn = Input.openConnection(adress);
		handleStatusCode(conn);
		extractFilename(conn);
		
		InputStream responseStream = conn.getInputStream();
		if ("gzip".equalsIgnoreCase(conn.getContentEncoding())) {
			responseStream = new GZIPInputStream(responseStream);
		}else{
			if (Setting.UseGzip.getBool())
				System.out.println("WARNING: node doesn't seem to support transfer compression!");
		}
		return responseStream;
	}
	
	private void extractFilename(URLConnection conn) {
		String disposition = conn.getHeaderField("Content-Disposition");
		if (disposition!=null && disposition.contains("filename=")){
			int pos = disposition.indexOf("=")+1;
			if (pos<disposition.length()-1)
			this.filename=disposition.substring(pos).trim();
		}else 
			this.filename=null;
	}

	private Map<HeaderMetrics, String> processHeaders(
			Map<String, List<String>> headerFields) {
		Map<HeaderMetrics,String> result = new TreeMap<HeaderMetrics,String>();
		for (HeaderMetrics header:HeaderMetrics.values()){
			List<String> values = headerFields.get(header.name().replace("_", "-"));
			if (values!=null && values.size()>0)
				result.put(header, values.get(0));
		}
		return result;
	}



}

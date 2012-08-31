package org.vamdc.validator.source.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.validator.Setting;
import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.source.XSAMSSource;
import org.vamdc.validator.source.XSAMSSourceException;
import org.vamdc.xsams.io.IOSettings;
import org.vamdc.xsams.io.PrettyPrint;

public class HttpXSAMSSource implements XSAMSSource {

	private String baseURLStr = "";
	private String filename = "";

	//Default connect and read timeouts for http connections

	private CapabilitiesClient caps;
	private AvailabilityClient avail;

	private PrettyPrint prettyprinter = new PrettyPrint(); 

	public HttpXSAMSSource() throws XSAMSSourceException{
		String vosiURL = Setting.ServiceVOSIURL.getValue();
		String tapURL = Setting.ServiceTAPURL.getValue();
		if (vosiURL!=null && vosiURL.length()>0){
			caps = new CapabilitiesClient(vosiURL);
			if (caps!=null){
				baseURLStr = caps.getTapEndpoint();
			}
		}else{
			baseURLStr = tapURL;
		}
		baseURLStr+=Setting.ServiceTAPSuffix.getValue();
		try {
			new URL(baseURLStr);
		} catch (MalformedURLException e) {
			throw new XSAMSSourceException("Service base URL '"+baseURLStr+"' is malformed \r\n");
		}
	}

	@Override
	public InputStream getXsamsStream(String query,XSAMSIOModel document) throws XSAMSSourceException {
		if (baseURLStr==null)
			throw new XSAMSSourceException("base URL is null :(");
		if (caps!=null){
			if ( caps.getAvailabilityEndpoint().equals(""))
				throw new XSAMSSourceException("availability endpoint is not defined");
			avail = new AvailabilityClient(caps.getAvailabilityEndpoint());

			if (!avail.isAvailable()){
				throw new XSAMSSourceException("Service not available: "+avail.getMessage());
			}else{
				return doQuery(query,document);
			}
		}else{
			return doQuery(query,document);
		}
	}

	/**
	 * Perform query
	 * @param query
	 * @param document 
	 * @return
	 * @throws XSAMSSourceException
	 */
	public InputStream doQuery(String query, XSAMSIOModel document) throws XSAMSSourceException{
		URL requestURL = prepareRequestURL(query);

		transferConnectionSettings();
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

	private void transferConnectionSettings() {
		IOSettings.httpConnectTimeout.setIntValue(Setting.HTTP_CONNECT_TIMEOUT.getInt());
		IOSettings.httpDataTimeout.setIntValue(Setting.HTTP_DATA_TIMEOUT.getInt());

		int compress = 0;
		if (Setting.UseGzip.getBool())
			compress=1;
		IOSettings.compress.setIntValue(compress);
	}

	private URL prepareRequestURL(String query)
			throws XSAMSSourceException {
		URL result=null;
		String encodedQuery="";
		try {
			encodedQuery = URLEncoder.encode(query,"UTF-8");
			result = new URL(baseURLStr+encodedQuery);

		}catch (MalformedURLException e) {
			throw new XSAMSSourceException("Service base URL '"+baseURLStr+encodedQuery+"' is malformed \r\n");
		}catch (UnsupportedEncodingException e1) {}
		return result;
	}

	@Override
	public Collection<String> getRestrictables() {
		if (caps!=null)
			return caps.getRestrictables();
		else return new ArrayList<String>();
	}

	@Override
	public Collection<String> getSampleQueries() {
		if (caps!=null)
			return caps.getSampleQueries();
		else return new ArrayList<String>();
	}

	@Override
	public Map<HeaderMetrics, String> getMetrics(String query)
			throws XSAMSSourceException {
		URL requestURL = prepareRequestURL(query);
		try {
			HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
			conn.setRequestMethod("HEAD");
			conn.setConnectTimeout(Setting.HTTP_CONNECT_TIMEOUT.getInt());
			conn.setReadTimeout(Setting.HTTP_DATA_TIMEOUT.getInt());
			if (conn.getResponseCode()!=200)
				throw new XSAMSSourceException("Response code "+conn.getResponseCode()+" for "+requestURL.toString());
			return processHeaders(conn.getHeaderFields());
		} catch (IOException e) {
			transformException(requestURL,e);
			return Collections.emptyMap();
		}
	}
	
	/**
	 * Open URL connection, with timeouts set
	 * @param address connection URL
	 * @return Stream of data
	 * @throws IOException 
	 */
	private InputStream openConnection(URL adress) throws IOException{
		URLConnection conn = adress.openConnection();
		//Allow gzip encoding
		if (IOSettings.compress.getIntValue()==1)
			conn.setRequestProperty("Accept-Encoding", "gzip");
		//Set timeouts
		conn.setConnectTimeout(IOSettings.httpConnectTimeout.getIntValue());
		conn.setReadTimeout(IOSettings.httpDataTimeout.getIntValue());

		checkHttpResultCode(adress, conn);
		
		extractFilename(conn);
		
		InputStream responseStream = conn.getInputStream();
		String contentEncoding = conn.getContentEncoding();
		if ("gzip".equalsIgnoreCase(contentEncoding)) {
			responseStream = new GZIPInputStream(responseStream);
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

	private void checkHttpResultCode(URL adress, URLConnection conn)
			throws IOException {
		if (adress.getProtocol().equals("http")|| adress.getProtocol().equals("https")){
			HttpURLConnection httpc= (HttpURLConnection) conn;
			if (httpc.getResponseCode()!=200)
				throw new IOException("Server responded with code "+httpc.getResponseCode());
		}
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

package org.vamdc.validator.source.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.validator.Setting;
import org.vamdc.validator.source.XSAMSSource;
import org.vamdc.validator.source.XSAMSSourceException;
import org.vamdc.xsams.io.IOSettings;
import org.vamdc.xsams.io.Input;
import org.vamdc.xsams.io.PrettyPrint;

public class HttpXSAMSSource implements XSAMSSource {

	private String baseURLStr = "";

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
	public InputStream getXsamsStream(String query) throws XSAMSSourceException {
		if (baseURLStr==null)
			throw new XSAMSSourceException("base URL is null :(");
		if (caps!=null){
			if ( caps.getAvailabilityEndpoint().equals(""))
				throw new XSAMSSourceException("availability endpoint is not defined");
			avail = new AvailabilityClient(caps.getAvailabilityEndpoint());

			if (!avail.isAvailable()){
				throw new XSAMSSourceException("Service not available: "+avail.getMessage());
			}else{
				return doQuery(query);
			}
		}else{
			return doQuery(query);
		}
	}

	/**
	 * Perform query
	 * @param query
	 * @return
	 * @throws XSAMSSourceException
	 */
	public InputStream doQuery(String query) throws XSAMSSourceException{
		URL requestURL = prepareRequestURL(query);

		transferConnectionSettings();
		
		try{
			if (Setting.PrettyPrint.getBool())
				return prettyprinter.transform(
						Input.openConnection(requestURL));
			return Input.openConnection(requestURL);
		}  catch (IOException e) {
			transformException(requestURL, e);
			return null;
		}
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
			if (conn.getResponseCode()!=200)
				throw new XSAMSSourceException("Response code "+conn.getResponseCode()+" for "+requestURL.toString());
			return processHeaders(conn.getHeaderFields());
		} catch (IOException e) {
			transformException(requestURL,e);
			return Collections.emptyMap();
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

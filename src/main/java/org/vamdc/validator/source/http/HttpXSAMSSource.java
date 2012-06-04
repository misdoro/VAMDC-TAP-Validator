package org.vamdc.validator.source.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

import org.vamdc.validator.Setting;
import org.vamdc.validator.interfaces.XSAMSSource;
import org.vamdc.validator.interfaces.XSAMSSourceException;
import org.vamdc.xsams.io.IOSettings;
import org.vamdc.xsams.io.Input;
import org.vamdc.xsams.io.PrettyPrint;

public class HttpXSAMSSource extends XSAMSSource {

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
		URL requestURL = null;
		
		IOSettings.httpConnectTimeout.setIntValue(Setting.HTTP_CONNECT_TIMEOUT.getIntValue());
		IOSettings.httpDataTimeout.setIntValue(Setting.HTTP_DATA_TIMEOUT.getIntValue());
		int pretty = 0;
		if (Setting.PrettyPrint.getBool())
			pretty=1;
		IOSettings.prettyprint.setIntValue(pretty);
		
		int compress = 0;
		if (Setting.UseGzip.getBool())
			compress=1;
		IOSettings.compress.setIntValue(compress);
		
		try {
			requestURL = new URL(baseURLStr+URLEncoder.encode(query,"UTF-8"));

			//Prettyprint if requested
			if (Setting.PrettyPrint.getBool())
				return prettyprinter.transform(
								Input.openConnection(requestURL));
			return Input.openConnection(requestURL);
		} catch (MalformedURLException e) {
			try {
				throw new XSAMSSourceException("Service base URL '"+baseURLStr+URLEncoder.encode(query,"UTF-8")+"' is malformed \r\n");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			throw new XSAMSSourceException("IO exception while opening http connection:"+e.getMessage()+" for query "+requestURL.toString());
		}
		return null;
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

}

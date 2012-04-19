package org.vamdc.validator.source.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

import org.vamdc.validator.Settings;
import org.vamdc.validator.interfaces.XSAMSSource;
import org.vamdc.validator.interfaces.XSAMSSourceException;
import org.vamdc.xsams.io.Input;
import org.vamdc.xsams.io.PrettyPrint;

public class HttpXSAMSSource extends XSAMSSource {

	private String baseURLStr = "";

	//Default connect and read timeouts for http connections

	private CapabilitiesClient caps;
	private AvailabilityClient avail;

	private PrettyPrint prettyprinter = new PrettyPrint(); 

	public HttpXSAMSSource() throws XSAMSSourceException{
		String vosiURL = Settings.get(Settings.ServiceVOSIURL);
		String tapURL = Settings.get(Settings.ServiceTAPURL);
		if (vosiURL!=null && vosiURL.length()>0){
			caps = new CapabilitiesClient(vosiURL);
			if (caps!=null){
				baseURLStr = caps.getTapEndpoint()+Settings.get(Settings.ServiceTAPSuffix);

				try {
					new URL(baseURLStr);
				} catch (MalformedURLException e) {
					throw new XSAMSSourceException("Service base URL '"+baseURLStr+"' is malformed \r\n");
				}
			}
		}else{
			baseURLStr = tapURL+Settings.get(Settings.ServiceTAPSuffix);
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
		URL requestURL;
		try {
			requestURL = new URL(baseURLStr+URLEncoder.encode(query,"UTF-8"));

			//Prettyprint if requested
			if (Settings.getBoolean(Settings.ServicePrettyOut))
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
			throw new XSAMSSourceException("IO exception while opening http connection:"+e.getMessage());
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

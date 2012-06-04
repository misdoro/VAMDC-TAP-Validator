package org.vamdc.validator.source.http;

import java.util.ArrayList;
import java.util.Collection;

import net.ivoa.xml.voresource.v1.Capability;
import net.ivoa.xml.voresource.v1.Interface;
import net.ivoa.xml.vosicapabilities.v1.Capabilities;

import org.vamdc.validator.Setting;
import org.vamdc.validator.interfaces.XSAMSSourceException;
import org.vamdc.xml.vamdc_tap.v1.VamdcTap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Simple client for capabilities endpoint, 
 * giving out tap sync, capabilities and availability endpoint addresses
 * @author doronin
 *
 */
public class CapabilitiesClient {
	
	public final static String STDIDCapabilities = "ivo://ivoa.net/std/VOSI#capabilities";
	public final static String STDIDAvailability = "ivo://ivoa.net/std/VOSI#availability";
	
	private Capabilities caps=null;
	private String endpointAvail="";
	private String endpointCapab="";
	private String endpointTAPXS="";
	private Collection<String> restrictables=new ArrayList<String>();
	private Collection<String> sampleQueries=new ArrayList<String>();
	public CapabilitiesClient(String endpointURL) throws XSAMSSourceException{
		Client client = Client.create();
		client.setConnectTimeout(Setting.HTTP_CONNECT_TIMEOUT.getIntValue());
		client.setReadTimeout(Setting.HTTP_DATA_TIMEOUT.getIntValue());
		
		WebResource availabilityResource = client.resource(endpointURL);
		try{
			caps = availabilityResource.get(Capabilities.class);
		}catch (Exception e){
			//caps = new Capabilities();
			throw new XSAMSSourceException(e.getMessage());
		}
		for (Capability cap:caps.getCapabilities()){
			if (cap.getStandardID().equals(CapabilitiesClient.STDIDAvailability)){
				Interface intf = cap.getInterfaces().get(0);
				endpointAvail = intf.getAccessURLs().get(0).getValue();
			}
			if (cap.getStandardID().equals(CapabilitiesClient.STDIDCapabilities)){
				Interface intf = cap.getInterfaces().get(0);
				endpointCapab = intf.getAccessURLs().get(0).getValue();
			}
			if (cap instanceof VamdcTap){
				Interface intf = cap.getInterfaces().get(0);
				endpointTAPXS = intf.getAccessURLs().get(0).getValue();
				VamdcTap vts = (VamdcTap)cap;
				restrictables.addAll(vts.getRestrictables());
				sampleQueries.addAll(vts.getSampleQueries());
			}
		}
	}
	
	public String getTapEndpoint(){
		return endpointTAPXS;
	}
	
	public String getAvailabilityEndpoint(){
		return endpointAvail;
	}
	
	public String getCapabilitiesEndpoint(){
		return endpointCapab;
	}
	
	public Collection<String> getRestrictables(){
		return restrictables;
	}
	
	public Collection<String> getSampleQueries(){
		return sampleQueries;
	}
	
}

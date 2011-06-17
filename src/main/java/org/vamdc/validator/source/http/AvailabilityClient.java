package org.vamdc.validator.source.http;

import org.vamdc.validator.Settings;

import net.ivoa.xml.vosiavailability.v1.Availability;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class AvailabilityClient {

	private Availability avail=null;
	public AvailabilityClient(String endpointURL){
		Client client = Client.create();
		client.setConnectTimeout(Settings.getInt(Settings.HTTP_CONNECT_TIMEOUT));
		client.setReadTimeout(Settings.getInt(Settings.HTTP_DATA_TIMEOUT));
		WebResource availResource = client.resource(endpointURL);
		avail = availResource.get(Availability.class);
		System.out.println("TAP Service is available!"+avail.isAvailable());
	}
	
	public String getMessage(){
		String out="";
		for (String note:avail.getNotes()){
			out+=note+"\n";
		}
		return out;
	}
	
	public boolean isAvailable(){
		return avail.isAvailable();
	}
	
}

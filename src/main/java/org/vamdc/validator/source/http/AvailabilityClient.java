package org.vamdc.validator.source.http;

import org.vamdc.validator.Setting;

import net.ivoa.xml.vosiavailability.v1.Availability;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class AvailabilityClient {

	private Availability avail=null;
	public AvailabilityClient(String endpointURL){
		Client client = Client.create();
		client.setConnectTimeout(Setting.HTTP_CONNECT_TIMEOUT.getInt());
		client.setReadTimeout(Setting.HTTP_DATA_TIMEOUT.getInt());
		WebResource availResource = client.resource(endpointURL);
		avail = availResource.get(Availability.class);
		System.out.println("TAP Service is available!"+avail.isAvailable());
	}
	
	public String getMessage(){
		StringBuilder out=new StringBuilder();
		for (String note:avail.getNotes()){
			out.append(note).append("\n");
		}
		return out.toString();
	}
	
	public boolean isAvailable(){
		return avail.isAvailable();
	}
	
}

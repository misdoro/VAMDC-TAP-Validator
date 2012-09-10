package org.vamdc.validator.source.http;

import net.ivoa.xml.vosiavailability.v1.Availability;

/**
 * Availability client and wrapper
 */
public class AvailabilityClient {

	private final Availability avail;
	
	public AvailabilityClient(String endpointURL){
		avail = Tool.getJerseyClient().resource(endpointURL).get(Availability.class);
	}
	
	public String getMessage(){
		StringBuilder out=new StringBuilder();
		for (String note:avail.getNote()){
			out.append(note).append("\n");
		}
		return out.toString();
	}
	
	public boolean isAvailable(){
		return avail.isAvailable();
	}
	
}

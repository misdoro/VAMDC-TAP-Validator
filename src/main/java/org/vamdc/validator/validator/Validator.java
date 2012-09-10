package org.vamdc.validator.validator;


import org.apache.xerces.parsers.SAXParser;
import org.vamdc.validator.interfaces.DocumentElementsLocator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException; 
import org.xml.sax.SAXNotSupportedException;

import java.io.IOException;
import java.io.InputStream;

public class Validator{
	private SAXParser parser = null;
	
	private ElementHandler handler = null;
	

	public Validator(String schemaLocation, String namespaces) throws XSAMSValidatorException{
		parser = new SAXParser();
		setFeature("http://xml.org/sax/features/validation",true);
		setFeature("http://apache.org/xml/features/validation/schema",true); 
		setFeature("http://apache.org/xml/features/validation/schema-full-checking",true);
		setFeature("http://apache.org/xml/features/validation/dynamic",true);
		setFeature("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef",true);
		setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
		if (schemaLocation!=null && schemaLocation.length()>0)
			setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
				schemaLocation);
		if (namespaces!=null && namespaces.length()>0 && (namespaces.split(" ").length%2 == 0))
			setProperty("http://apache.org/xml/properties/schema/external-schemaLocation",
				namespaces);

		System.out.println("Initialized validator module");
	}
	
	public DocumentElementsLocator getElements(){
		return handler;
	}
	
	private void setFeature(String
			feature, boolean setting) throws XSAMSValidatorException{

		try {
			parser.setFeature(feature, setting);
		} catch (SAXNotRecognizedException e) {
			throw new XSAMSValidatorException("Unrecognized feature: "+feature);
		} catch (SAXNotSupportedException e) {
			throw new XSAMSValidatorException("Unsupported feature: "+feature);
		}
	}
	
	private void setProperty(String
			name, String value) throws XSAMSValidatorException{

		try {
			parser.setProperty(name, value);
		} catch (SAXNotRecognizedException e) {
			throw new XSAMSValidatorException("Unrecognized property: "+name);
		} catch (SAXNotSupportedException e) {
			throw new XSAMSValidatorException("Unsupported property: "+name);
		}
	}

	public void parse(InputStream xmlstream) throws IOException{
		//Reset counters
		handler = new ElementHandler();
		parser.setErrorHandler(handler);
		parser.setContentHandler(handler);
		
		//Create source
		InputSource src = new InputSource();
		src.setByteStream(xmlstream);
		src.setEncoding("UTF-8");
		try {
			//Parse
			parser.parse(src);
		} catch (SAXException e) {
			throw new IOException(e.getMessage());
		};
	}

	
}

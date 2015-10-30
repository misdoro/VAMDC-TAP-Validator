package org.vamdc.validator.transform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public class GetReturnables {


	
	public static void main (String argv[]) {
		try {
			process(new FileInputStream("/home/doronin/xsams.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void process(InputStream stream){
		URL xsl = GetReturnables.class.getResource("/xsams2xpaths.xsl");
		//URL xsl = GetReturnables.class.getResource("/xpath.xslt");
		System.out.println(xsl);
		File stylesheet;
		try {
			stylesheet = new File(xsl.toURI());
			System.out.println(stylesheet.exists());
			
			TransformerFactory tFactory = TransformerFactory.newInstance();
			StreamSource stylesource = new StreamSource(stylesheet);
			StreamSource XMLSrc = new StreamSource(stream);
			Transformer transformer = tFactory.newTransformer(stylesource);
			XPathOutputStream xsstream = new XPathOutputStream();
			StreamResult result = new StreamResult(xsstream);
            transformer.transform(XMLSrc, result);
            
            for (String path:xsstream.getKeys()){
            	
            	System.out.println("pl"+path);
            }
            System.out.println("Total elements:"+xsstream.getKeys().size());
            
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
	}
	
}

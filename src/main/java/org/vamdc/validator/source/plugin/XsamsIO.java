package org.vamdc.validator.source.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.vamdc.xsams.schema.XSAMSData;
import org.vamdc.xsams.io.NSPrefixMapper;

/*
 * Provides methods for XSAMS input and output
 */

public class XsamsIO  {
	private JAXBContext jc = null;
	private Marshaller m = null;
	private boolean isReady = false;



	public XsamsIO(){
		try{
			//Setup JAXB
			jc = JAXBContext.newInstance(
					XSAMSData.class);
					
			m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			m.setProperty( "com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper());
			//m.setProperty(name, value)
			isReady = true;
		}catch( JAXBException je ) {
			je.printStackTrace();
		}

	}

	public InputStream getXSAMSStream(XSAMSData xsams) {
		//Don't do anything if we are already processing something
		if (!isReady) return null;
		XSAMSMarshallThread out = new XSAMSMarshallThread(xsams, jc, m);
		return out.getStream();
	}

	private class XSAMSMarshallThread implements Runnable{
		PipedInputStream in;
		PipedOutputStream out;
		XSAMSData xsamsRoot;
		JAXBContext context;
		Marshaller marshaller;
		boolean threadIsReady = false;

		public XSAMSMarshallThread(XSAMSData xsams, JAXBContext jc, Marshaller m){
			try {
				in = new PipedInputStream();
				out = new PipedOutputStream(in);
				context = jc;
				marshaller = m;
				xsamsRoot=xsams;
				threadIsReady = (context!=null && marshaller!=null && xsamsRoot!=null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			if (threadIsReady)
				try {
					marshaller.marshal(xsamsRoot, out);
					out.close();
				} catch (JAXBException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		public InputStream getStream() {
			if (!threadIsReady) return null;
			new Thread(this).start();
			return in;
		}
	}



}

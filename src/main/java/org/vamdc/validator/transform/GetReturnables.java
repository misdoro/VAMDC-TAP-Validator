package org.vamdc.validator.transform;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

	private static class XpathStream extends ByteArrayOutputStream{

		
		private Set<String> paths=new TreeSet<String>();
		
		private int laststart=0;
		
		public XpathStream(){
			super(32768);
		}
		
		@Override
		public void write(int b){
			//System.out.print(b);
			
			super.write(b);
			if (b=='\n'){				
				paths.add(this.toString());
				this.reset();
			}
		}
		
		@Override
		public synchronized void write(byte b[], int off, int len){
			super.write(b, off, len);
			
			findTokens();
			
			cutTopLines();
		}

		private void cutTopLines() {
			if (count>0 && buf[count-1]=='\n'){//Last symbol is EOL
				this.reset();
				laststart=0;
			}else if (count>0){//EOL in the middle
				for (int i=count-1;i>0;i--){
					if (buf[i]=='\n'){//Copy the trailing line to the beginning, update the count and reset the start
						System.arraycopy(buf, i+1, buf, 0, count-i-1);
						count-=i+1;
						laststart=0;
						break;
					}
				}
			}
		}

		private void findTokens() {
			if (laststart>0)
				laststart=0;
			for (int i=0;i<count-1;i++){
				if (buf[i]=='\n'){
					
					paths.add(new String(this.buf,laststart,i-laststart));
					laststart=i+1;
				}
			}
		}
		
		public Collection<String> getKeys(){
			return paths;
		}
		
	}
	
	public static void main (String argv[]) {
		URL xsl = GetReturnables.class.getResource("/xsams2xpaths.xsl");
		System.out.println(xsl);
		File stylesheet;
		try {
			stylesheet = new File(xsl.toURI());
		
			System.out.println(stylesheet.exists());
			File xsams=new File(new URI("file:/home/doronin/xsams.xml"));
		
			TransformerFactory tFactory = TransformerFactory.newInstance();
			StreamSource stylesource = new StreamSource(stylesheet);
			StreamSource XMLSrc = new StreamSource(xsams);
			Transformer transformer = tFactory.newTransformer(stylesource);
			XpathStream xsstream = new XpathStream();
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}

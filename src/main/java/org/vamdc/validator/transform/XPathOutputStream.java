package org.vamdc.validator.transform;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

class XPathOutputStream extends ByteArrayOutputStream{
	
		
		private Set<String> paths=new TreeSet<String>();
		
		private int laststart=0;
		
		public XPathOutputStream(){
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

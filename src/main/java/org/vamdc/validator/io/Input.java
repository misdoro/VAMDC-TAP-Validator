package org.vamdc.validator.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.vamdc.validator.Setting;
import org.vamdc.validator.ValidatorMain;

public class Input {

	public static HttpURLConnection openConnection(URL requestURL) throws IOException{
		HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
		conn.setConnectTimeout(Setting.HTTP_CONNECT_TIMEOUT.getInt());
		conn.setReadTimeout(Setting.HTTP_DATA_TIMEOUT.getInt());
		conn.setRequestProperty("User-Agent", "VAMDC-TAP Validator/"+ValidatorMain.VERSION);
		//Allow gzip encoding
		if (Setting.UseGzip.getBool()){
			conn.setRequestProperty("Accept-Encoding", "gzip");
		}
		return conn;
	}
	
	public static InputStream openStream(HttpURLConnection conn) throws IOException{
		InputStream result = conn.getInputStream();
		if ("gzip".equalsIgnoreCase(conn.getContentEncoding()))
			result= new GZIPInputStream(result);
		return result;
	}
	
	public static InputStream openStream(URL loc) throws IOException {
		if (loc.getProtocol().toLowerCase().startsWith("http")){
			HttpURLConnection conn = Input.openConnection(loc);
			if (conn.getResponseCode()==HttpURLConnection.HTTP_OK){
				return Input.openStream(conn);
			}
			else 
				throw new IOException("Status "+conn.getResponseCode()+conn.getResponseMessage());
		}
		return loc.openStream();

	}

}

package org.vamdc.validator.source.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.vamdc.validator.Setting;
import org.vamdc.validator.source.XSAMSSourceException;

import com.sun.jersey.api.client.Client;

public class Tool {

	static Client getJerseyClient() {
		Client client = Client.create();
		client.setConnectTimeout(Setting.HTTP_CONNECT_TIMEOUT.getInt());
		client.setReadTimeout(Setting.HTTP_DATA_TIMEOUT.getInt());
		return client;
	}

	static URL getURL(String url) throws XSAMSSourceException{
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new XSAMSSourceException("Service base URL '"+url+"' is malformed \r\n");
		}
	}

	public static HttpURLConnection openConnection(URL requestURL) throws IOException{
		HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
		conn.setConnectTimeout(Setting.HTTP_CONNECT_TIMEOUT.getInt());
		conn.setReadTimeout(Setting.HTTP_DATA_TIMEOUT.getInt());
		//Allow gzip encoding
		if (Setting.UseGzip.getBool()){
			conn.setRequestProperty("Accept-Encoding", "gzip");
		}
		return conn;

	}

}

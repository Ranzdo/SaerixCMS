package com.saerix.cms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.saerix.cms.util.HttpError;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ResourceHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange handle) throws IOException {
		if(!handle.getRequestMethod().equals("POST") && !handle.getRequestMethod().equals("GET")) {
			HttpError.send404(handle);
			return;
		}
		
		List<String> ahost = handle.getRequestHeaders().get("Host");
		if(ahost == null) {
			HttpError.send404(handle);
			return;
		}
		if(ahost.size() == 0) {
			HttpError.send404(handle);
			return;
		}
		
		String uriRequest = handle.getRequestURI().toString();
		String[] segmentsAndPara = uriRequest.split("\\?");
		String segments = segmentsAndPara[0];
		String[] segmentsArray = segments.split("/");
		String hostValue = ahost.get(0).split(":")[0];
		
		File dir;
		if(hostValue.equals(SaerixCMS.getProperties().get("cms_hostname"))) {
			dir = new File("resources"+File.separator+"cms_resources"+File.separator);
		}
		else {
			dir = new File("resources"+File.separator+hostValue);
		}
		
		if(!dir.exists()) {
			HttpError.send404(handle);
			return;
		}
		
		StringBuilder path = new StringBuilder();
		for(int i = 2; i < segmentsArray.length;i++) {
			path.append(File.separator+segmentsArray[i]);
		}
		
		File file = new File(dir.getAbsolutePath()+path.toString());
		
		if(!file.exists() || file.isDirectory()){
			HttpError.send404(handle);
			return;
		}
		
		handle.sendResponseHeaders(200, 0);
		byte[] buffer = new byte[1024];
		InputStream is = new FileInputStream(file);
		OutputStream os = handle.getResponseBody();
		while(is.read(buffer) != -1) {
			os.write(buffer);
		}
		is.close();
		os.flush();
		os.close();
	}
}

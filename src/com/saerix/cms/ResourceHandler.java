package com.saerix.cms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saerix.cms.util.HttpError;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ResourceHandler implements HttpHandler {

	private SaerixHttpServer server;
	
	private Map<File, byte[]> chachedFiles = Collections.synchronizedMap(new HashMap<File, byte[]>());
	
	public ResourceHandler(SaerixHttpServer server) {
		this.server = server;
	}
	
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
		
		//CMS res
		if(hostValue.equals(server.getInstance().getProperties().get("cms_hostname"))) {
			StringBuilder path = new StringBuilder();
			for(int i = 2; i < segmentsArray.length;i++) {
				path.append("/"+segmentsArray[i]);
			}
			
			
			InputStream is = getClass().getResourceAsStream("/com/saerix/cms/cms/resources"+path.toString());
			if(is == null) {
				HttpError.send404(handle);
				return;
			}
			
			handle.sendResponseHeaders(200, 0);
			
			byte[] buffer = new byte[1];
			OutputStream os = handle.getResponseBody();
			while(is.read(buffer) != -1) {
				os.write(buffer);
			}
			is.close();
			os.flush();
			os.close();
			return;
		}
		
		File dir = new File("resources"+File.separator+hostValue);
	
		
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
		
		byte[] cache;
		synchronized (chachedFiles) {
			cache = chachedFiles.get(file);
		}
		
		if(cache != null && !server.getInstance().isInDevMode()) {
			OutputStream os = handle.getResponseBody();
			os.write(cache);
			os.flush();
			os.close();
		}
		else if(file.length() < 5242880 && !server.getInstance().isInDevMode()) {
			InputStream is = new FileInputStream(file);
			cache = new byte[(int) file.length()];
			is.read(cache);
			is.close();
			synchronized (chachedFiles) {
				cache = chachedFiles.put(file, cache);
			}
			OutputStream os = handle.getResponseBody();
			os.write(cache);
			os.flush();
			os.close();
		}
		else {
			byte[] buffer = new byte[1];
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
}

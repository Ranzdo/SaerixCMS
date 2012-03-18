package com.saerix.cms;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.saerix.cms.util.ParameterFilter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

//TODO A ssl server
public class SaerixHttpServer  {
	
	private HttpServer server;
	// private HttpsServer secureServer;
	private RootHandler handler = new RootHandler();

	public SaerixHttpServer() {
		try {
			server = HttpServer.create(new InetSocketAddress(Integer.parseInt(SaerixCMS.getProperties().getProperty("port"))), 0);
			startServer();
			
			/*secureServer = HttpsServer.create(new InetSocketAddress(Integer.parseInt(SaerixCMS.getProperties().getProperty("secure_port"))), 0);
			startSSLServer();*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*private void startSSLServer() throws KeyStoreException, KeyManagementException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException {
		char[] passphrase = "passphrase".toCharArray();
		
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream("testkeys"), passphrase);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, passphrase);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);
		SSLContext ssl = SSLContext.getInstance("TLS");
		ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		
		secureServer.setHttpsConfigurator(new HttpsConfigurator(ssl) {
		    public void configure (HttpsParameters params) {
		        SSLContext c = getSSLContext();
		        SSLParameters sslparams = c.getDefaultSSLParameters();
		        params.setNeedClientAuth(true);
		        params.setSSLParameters(sslparams);
		        }
		});
		
		secureServer.createContext("/", handler);
		secureServer.setExecutor(SaerixCMS.executor());
		secureServer.start();
	}*/
	
	private void startServer() throws IOException {   
	    HttpContext pagecontext = server.createContext("/", handler);
	    pagecontext.getFilters().add(new ParameterFilter());
	    
	    server.setExecutor(SaerixCMS.executor());
	    server.start();
	}
	
	public HttpServer getServer() {
		return server;
	}
}

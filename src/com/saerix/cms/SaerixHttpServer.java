package com.saerix.cms;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.saerix.cms.database.Database;
import com.saerix.cms.database.basemodels.HostModel;
import com.saerix.cms.database.basemodels.HostModel.HostRow;
import com.saerix.cms.host.CMSHost;
import com.saerix.cms.host.DatabaseHost;
import com.saerix.cms.host.Host;
import com.saerix.cms.host.HostException;
import com.saerix.cms.libapi.LibraryException;
import com.saerix.cms.util.ParameterFilter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

//TODO A ssl server
public class SaerixHttpServer  {
	private SaerixCMS instance;
	private Map<String, Host> loadedHosts = Collections.synchronizedMap(new HashMap<String, Host>());
	private CMSHost cmsHost;
	private HttpServer server;
	// private HttpsServer secureServer;
	private RootHandler handler = new RootHandler(this);
	private ResourceHandler resoruceHandler = new ResourceHandler();

	public SaerixHttpServer(SaerixCMS instance, int port, int secure_port, String cmsHostName) throws IOException, LibraryException {
		this.instance = instance;
		this.cmsHost = new CMSHost(cmsHostName);
		server = HttpServer.create(new InetSocketAddress(port), 0);
		startServer();
		
		/*secureServer = HttpsServer.create(new InetSocketAddress(Integer.parseInt(SaerixCMS.getProperties().getProperty("secure_port"))), 0);
		startSSLServer();*/
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
	    server.createContext("/res/", resoruceHandler);
	    
	    server.setExecutor(SaerixCMS.executor());
	    server.start();
	}
	
	public HttpServer getServer() {
		return server;
	}
	
	public SaerixCMS getInstance() {
		return instance;
	}
	
	public Host getHost(String hostName) throws HostException {
		Host host = loadedHosts.get(hostName);
		if(host != null)
			return host;
		
		try {
			try {
				HostRow row = (HostRow) ((HostModel) Database.getTable("hosts")).getHost(hostName);
				if(row != null) {
					host = new DatabaseHost(row.getId(), hostName);
					loadedHosts.put(hostName, host);
					return host;
				}
			}
			catch(SQLException e) {
				throw (HostException) new HostException().initCause(e);
			}
		
		return cmsHost;
		
		}
		catch(LibraryException e) {
			throw (HostException) new HostException().initCause(e);
		}
	}
}

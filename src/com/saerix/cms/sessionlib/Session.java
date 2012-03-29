package com.saerix.cms.sessionlib;

public class Session {
	private final String id;
	private long lastActivity;
	private UserData userData = new UserData();
	
	public Session(String id) {
		this.id = id;
		activity();
	}
	
	public String getId() {
		return id;
	}
	
	public long getLastActivity() {
		return lastActivity;
	}
	
	public void activity() {
		lastActivity = System.currentTimeMillis();
	}
	
	public UserData userdata() {
		return userData;
	}
	
	public void destroy() {
		SessionLibrary.getInstance().destroy();
	}
}

package com.saerix.cms.sessionlib;

import java.util.HashMap;
import java.util.Map;

import com.saerix.cms.libapi.Library;
import com.saerix.cms.libapi.LibraryConfig;

@LibraryConfig(name = "session")
public class SessionLibrary extends Library {	
	private RandomString sessionIdGenerator = new RandomString(20);
	
	private String sessionKey = "session";
	
	private Map<Thread, Session> cachedSessions = new HashMap<Thread, Session>(); 
	private Map<String, Session> sessions = new HashMap<String, Session>();

	private long expire = 0;
	
	@Override
	public void onEnable() {
		getLibraryLoader().registerListener(new SessionListener(this));
	}
	
	public Session getSession(String id) {
		return sessions.get(id);
	}
	
	void addSession(Session session) {
		sessions.put(session.getId(), session);
	}
	
	void addCachedSession(Session session) {
		cachedSessions.put(Thread.currentThread(), session);
	}
	
	void removeSession(Session session) {
		sessions.remove(session.getId());
	}

	String getSessionKey() {
		return sessionKey;
	}
	
	Session generateNewSession() {
		return new Session(this, sessionIdGenerator.nextString());
	}
	
	public void setExpireTime(int sec) {
		this.expire = sec*1000;
	}
	
	public long getExpireTime() {
		return expire;
	}
	
	public Session session() {
		return cachedSessions.get(Thread.currentThread());
	}
	
	public void destroy(String id) {
		removeSession(sessions.get(id));
	}
}

package com.saerix.cms.sessionlib;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.saerix.cms.libapi.Listener;
import com.saerix.cms.libapi.events.PageLoadEvent;
import com.sun.net.httpserver.HttpExchange;

public class SessionListener extends Listener {
	
	private SessionLibrary library;

	public SessionListener(SessionLibrary library) {
		this.library = library;
	}
	
	@Override
	public void onPageLoad(PageLoadEvent event) {

		List<String> cookies = event.getCookies().get(library.getSessionKey());
		Session session;
		
		if(cookies == null ? true : cookies.size() < 1 ? true : (session = library.getSession(cookies.get(0))) == null) {
			session = setANewSession(event.getHandle());
		}
		
		if(library.getExpireTime() != 0 && System.currentTimeMillis()-session.getLastActivity() > library.getExpireTime()) {
			library.removeSession(session);
			session = setANewSession(event.getHandle());
		}
		
		session.activity();
		library.addCachedSession(session);
	}
	
	private Session setANewSession(HttpExchange handle) {
		Session session = library.generateNewSession();
		ArrayList<String> setCookies = new ArrayList<String>();
		try {
			setCookies.add(URLEncoder.encode(library.getSessionKey(), "UTF-8")+"="+URLEncoder.encode(session.getId(), "UTF-8")+"; Path=/;");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		handle.getResponseHeaders().put("Set-Cookie", setCookies);
		library.addSession(session);
		return session;
	}
}

package com.saerix.cms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

public class ParameterFilter extends Filter {

    @Override
    public String description() {
        return "Parses the requested URI for parameters";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain)
        throws IOException {
        parseGetParameters(exchange);
        parsePostParameters(exchange);
        parseCookies(exchange);
        chain.doFilter(exchange);
    }

    private void parseGetParameters(HttpExchange exchange)
        throws UnsupportedEncodingException {

        Map<String, List<String>> parameters = new HashMap<String, List<String>>();
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);
        exchange.setAttribute("getparameters", parameters);
    }

    private void parsePostParameters(HttpExchange exchange)
        throws IOException {
        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, List<String>> parameters = new HashMap<String, List<String>>();
            InputStreamReader isr =
                new InputStreamReader(exchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, parameters);
            br.close();
            isr.close();
        }
    }
    
    private void parseCookies(HttpExchange exchange) {
    	Map<String, List<String>> parameters = new HashMap<String, List<String>>();
    	
    	List<String> cookielist = exchange.getRequestHeaders().get("Cookie");
    	if(cookielist == null ? true : cookielist.size() < 1)
    		return;
    	
    	String[] cookies = cookielist.get(0).split("[;]");
    	
    	for(String cookie : cookies) {
    		String[] cookiesplit = cookie.split("[=]");
    		if(cookiesplit[0].equals(""))
    			continue;
    		
    		String key = cookiesplit[0];
    		String value = "";
    		
    		if(cookiesplit.length > 1) {
    			value = cookiesplit[1];
    		}
    		
			if (parameters.containsKey(key)) {
				 List<String> list = parameters.get(key);
				 if(list == null) {
					 List<String> newList = new ArrayList<String>();
					 newList.add(value);
					 parameters.put(key, newList);
				 }
				 else
					 list.add(value);
				 
			 } 
			 else {
				 List<String> newList = new ArrayList<String>();
				 newList.add(value);
				 parameters.put(key, newList);
			 }
    	}
    	
    	exchange.setAttribute("cookies", parameters);
    }
    
     private void parseQuery(String query, Map<String, List<String>> parameters)
         throws UnsupportedEncodingException {

         if (query != null) {
             String pairs[] = query.split("[&]");

             for (String pair : pairs) {
                 String param[] = pair.split("[=]");

                 String key = "";
                 String value = "";
                 if (param.length > 0) {
                     key = URLDecoder.decode(param[0],
                         System.getProperty("file.encoding"));
                 }

                 if (param.length > 1) {
                     value = URLDecoder.decode(param[1],
                         System.getProperty("file.encoding"));
                 }
                 
                 if (parameters.containsKey(key)) {
                	 List<String> list = parameters.get(key);
                	 if(list == null) {
                		 List<String> newList = new ArrayList<String>();
                		 newList.add(value);
                		 parameters.put(key, newList);
                	 }
                	 else
                		 list.add(value);
                	 
                 } 
                 else {
            		 List<String> newList = new ArrayList<String>();
            		 newList.add(value);
            		 parameters.put(key, newList);
                 }
             }
         }
    }
}
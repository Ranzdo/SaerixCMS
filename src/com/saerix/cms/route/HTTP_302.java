package com.saerix.cms.route;

import java.util.Map;

import com.saerix.cms.controller.Controller;
import com.saerix.cms.util.URLUtil;

public class HTTP_302 extends Controller {
	HTTP_302(String url) {
		redirect(url, false);
	}
	
	HTTP_302(String hostName, String segments, Map<String,String> parameters, boolean secure) {
		redirect(URLUtil.getURL(hostName, segments, parameters, secure));
	}
}

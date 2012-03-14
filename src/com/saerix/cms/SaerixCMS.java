package com.saerix.cms;

import java.io.IOException;

public class SaerixCMS {

	public static void main(String[] args) {
		try {
			new SaerixCMS();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private SaerixHttpServer server;
	
	public SaerixCMS() throws IOException {
		server = new SaerixHttpServer(8000);
	}

}

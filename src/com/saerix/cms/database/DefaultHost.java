package com.saerix.cms.database;

import com.saerix.cms.SaerixHttpServer;
import com.saerix.cms.host.DatabaseHost;
import com.saerix.cms.libapi.LibraryException;

public class DefaultHost extends DatabaseHost {
	public DefaultHost(SaerixHttpServer server, String hostName) throws LibraryException {
		super(server, -1, hostName);
	}
}

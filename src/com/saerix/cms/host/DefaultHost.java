package com.saerix.cms.host;

import com.saerix.cms.SaerixHttpServer;

public class DefaultHost extends DatabaseHost {
	public DefaultHost(SaerixHttpServer server, String hostName) throws HostException  {
		super(server, -1, hostName);
	}
}

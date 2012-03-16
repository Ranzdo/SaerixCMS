package com.saerix.cms;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SaerixCMSExecutor extends ThreadPoolExecutor {
	
	public SaerixCMSExecutor() {
		super(0 ,Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		
	}
	
}

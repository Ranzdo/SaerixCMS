package com.saerix.cms.database;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Cache {
	private static Map<Class<? extends Model>, Model> cachedModels = Collections.synchronizedMap(new HashMap<Class<? extends Model>, Model>());
	
	public static synchronized Model getCachedModel(Class<? extends Model> clazz) {
		return cachedModels.get(clazz);
	}

	public static synchronized void cacheModel(Class<? extends Model> clazz, Model model) {
		cachedModels.put(clazz, model);
	}

}

package com.iskyshop.icache.provider;

import java.util.Properties;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;

@SuppressWarnings("deprecation")
public class ICacheProvider implements CacheProvider {

	@Override
	public Cache buildCache(String regionName, Properties properties) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMinimalPutsEnabledByDefault() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long nextTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void start(Properties properties) throws CacheException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}

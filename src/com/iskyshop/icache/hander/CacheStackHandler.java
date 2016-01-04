package com.iskyshop.icache.hander;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.iskyshop.icache.ICacheConcurrentMap;
import com.iskyshop.icache.entity.CacheEntity;

/**
 * 
 * <p>
 * Title: CacheStackHandler.java
 * </p>
 * 
 * <p>
 * Description:缓存操作类，对缓存进行管理，采用处理队列，定时循环清理的方式
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2015-12-31
 * 
 * @version iskyshop_b2b2c v2.0 2016版
 */
public class CacheStackHandler {
	private static final long SECOND_TIME = 1000;
	private static final ICacheConcurrentMap<String, CacheEntity> map;
	private static final List<CacheEntity> tempList;

	static {
		tempList = new ArrayList<CacheEntity>();
		map = new ICacheConcurrentMap<String, CacheEntity>(
				new HashMap<String, CacheEntity>(1 << 18));
		new Thread(new TimeoutTimerThread()).start();
	}

	/**
	 * 增加缓存对象
	 * 
	 * @param key
	 * @param ce
	 */
	public static void addCache(String key, CacheEntity ce) {
		addCache(key, ce, ce.getValidityTime());
	}

	/**
	 * 增加缓存对象
	 * 
	 * @param key
	 * @param ce
	 * @param validityTime
	 *            有效时间
	 */
	public static synchronized void addCache(String key, CacheEntity ce,
			int validityTime) {
		ce.setTimeoutStamp(System.currentTimeMillis() + validityTime
				* SECOND_TIME);
		map.put(key, ce);
		// 添加到过期处理队列
		tempList.add(ce);
	}

	/**
	 * 获取缓存对象
	 * 
	 * @param key
	 * @return
	 */
	public static synchronized CacheEntity getCache(String key) {
		return map.get(key);
	}

	/**
	 * 检查是否含有制定key的缓冲
	 * 
	 * @param key
	 * @return
	 */
	public static synchronized boolean isConcurrent(String key) {
		return map.containsKey(key);
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 */
	public static synchronized void removeCache(String key) {
		map.remove(key);
	}

	/**
	 * 获取缓存大小
	 * 
	 * @param key
	 */
	public static int getCacheSize() {
		return map.size();
	}

	/**
	 * 清除全部缓存
	 */
	public static synchronized void clearCache() {
		tempList.clear();
		map.clear();
		System.out.println("clear cache");
	}

	static class TimeoutTimerThread implements Runnable {
		public void run() {
			while (true) {
				try {
					checkTime();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * 过期缓存的具体处理方法
		 * 
		 * @throws Exception
		 */
		private void checkTime() throws Exception {
			// "开始处理过期 ";
			CacheEntity tce = null;
			long timoutTime = 1000L;

			// " 过期队列大小 : "+tempList.size());
			if (1 > tempList.size()) {
				System.out.println("过期队列空，开始轮询");
				timoutTime = 1000L;
				Thread.sleep(timoutTime);
				return;
			}

			tce = tempList.get(0);
			timoutTime = tce.getTimeoutStamp() - System.currentTimeMillis();
			// " 过期时间 : "+timoutTime);
			if (0 < timoutTime) {
				// 设定过期时间
				Thread.sleep(timoutTime);
				return;
			}
			System.out.print(" 清除过期缓存 ： " + tce.getCacheKey());
			// 清除过期缓存和删除对应的缓存队列
			tempList.remove(tce);
			removeCache(tce.getCacheKey());
		}
	}
}

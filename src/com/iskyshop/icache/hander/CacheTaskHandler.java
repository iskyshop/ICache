package com.iskyshop.icache.hander;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.iskyshop.icache.ICacheConcurrentMap;
import com.iskyshop.icache.entity.CacheEntity;

/**
 * 
* <p>Title: CacheTaskHandler.java</p>

* <p>Description:��������࣬�Ի�����й���,�����ʽ����Timer��ʱ�ķ�ʽ </p>

* <p>Copyright: Copyright (c) 2015</p>

* <p>Company: ������֮�̿Ƽ����޹�˾ www.iskyshop.com</p>

* @author erikzhang

* @date 2015-12-31

* @version iskyshop_b2b2c v2.0 2016��
 */
public class CacheTaskHandler {
	private static final long SECOND_TIME = 1000;// Ĭ�Ϲ���ʱ�� 20��
	private static final int DEFUALT_VALIDITY_TIME = 20;// Ĭ�Ϲ���ʱ�� 20��
	private static final Timer timer;
	private static final ICacheConcurrentMap<String, CacheEntity> map;

	static {
		timer = new Timer();
		map = new ICacheConcurrentMap<String, CacheEntity>(
				new HashMap<String, CacheEntity>(1 << 18));
	}

	/**
	 * ���ӻ������
	 * 
	 * @param key
	 * @param ce
	 */
	public static void addCache(String key, CacheEntity ce) {
		addCache(key, ce, DEFUALT_VALIDITY_TIME);
	}

	/**
	 * ���ӻ������
	 * 
	 * @param key
	 * @param ce
	 * @param validityTime
	 *            ��Чʱ��
	 */
	public static synchronized void addCache(String key, CacheEntity ce,
			int validityTime) {
		map.put(key, ce);
		// ��ӹ��ڶ�ʱ
		timer.schedule(new TimeoutTimerTask(key), validityTime * SECOND_TIME);
	}

	/**
	 * ��ȡ�������
	 * 
	 * @param key
	 * @return
	 */
	public static synchronized CacheEntity getCache(String key) {
		return map.get(key);
	}

	/**
	 * ����Ƿ����ƶ�key�Ļ���
	 * 
	 * @param key
	 * @return
	 */
	public static synchronized boolean isConcurrent(String key) {
		return map.containsKey(key);
	}

	/**
	 * ɾ������
	 * 
	 * @param key
	 */
	public static synchronized void removeCache(String key) {
		map.remove(key);
	}

	/**
	 * ��ȡ�����С
	 * 
	 * @param key
	 */
	public static int getCacheSize() {
		return map.size();
	}

	/**
	 * ���ȫ������
	 */
	public static synchronized void clearCache() {
		if (null != timer) {
			timer.cancel();
		}
		map.clear();
		System.out.println("clear cache");
	}

/**
 * 
* <p>Title: CacheTaskHandler.java</p>

* <p>Description:�����ʱ���涨ʱ������ </p>

* <p>Copyright: Copyright (c) 2015</p>

* <p>Company: ������֮�̿Ƽ����޹�˾ www.iskyshop.com</p>

* @author erikzhang

* @date 2015-12-31

* @version iskyshop_b2b2c v2.0 2016��
 */
	static class TimeoutTimerTask extends TimerTask {
		private String ceKey;

		public TimeoutTimerTask(String key) {
			this.ceKey = key;
		}

		@Override
		public void run() {
			CacheTaskHandler.removeCache(ceKey);
			System.out.println("remove : " + ceKey);
		}
	}
}
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
 * Description:��������࣬�Ի�����й������ô�����У���ʱѭ������ķ�ʽ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ������֮�̿Ƽ����޹�˾ www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2015-12-31
 * 
 * @version iskyshop_b2b2c v2.0 2016��
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
	 * ���ӻ������
	 * 
	 * @param key
	 * @param ce
	 */
	public static void addCache(String key, CacheEntity ce) {
		addCache(key, ce, ce.getValidityTime());
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
		ce.setTimeoutStamp(System.currentTimeMillis() + validityTime
				* SECOND_TIME);
		map.put(key, ce);
		// ��ӵ����ڴ������
		tempList.add(ce);
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
		 * ���ڻ���ľ��崦����
		 * 
		 * @throws Exception
		 */
		private void checkTime() throws Exception {
			// "��ʼ������� ";
			CacheEntity tce = null;
			long timoutTime = 1000L;

			// " ���ڶ��д�С : "+tempList.size());
			if (1 > tempList.size()) {
				System.out.println("���ڶ��пգ���ʼ��ѯ");
				timoutTime = 1000L;
				Thread.sleep(timoutTime);
				return;
			}

			tce = tempList.get(0);
			timoutTime = tce.getTimeoutStamp() - System.currentTimeMillis();
			// " ����ʱ�� : "+timoutTime);
			if (0 < timoutTime) {
				// �趨����ʱ��
				Thread.sleep(timoutTime);
				return;
			}
			System.out.print(" ������ڻ��� �� " + tce.getCacheKey());
			// ������ڻ����ɾ����Ӧ�Ļ������
			tempList.remove(tce);
			removeCache(tce.getCacheKey());
		}
	}
}

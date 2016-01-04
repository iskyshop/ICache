package com.iskyshop.icache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ICacheConcurrentMap<K, V> implements Map<K, V> {

	final ReadWriteLock lock = new ReentrantReadWriteLock();

	final Lock r = lock.readLock();

	final Lock w = lock.writeLock();

	final Map<K, V> map;

	public ICacheConcurrentMap(Map<K, V> map) {
		this.map = map;
		if (map == null)
			throw new NullPointerException();
	}

	public void clear() {
		w.lock();
		try {
			map.clear();
		} finally {
			w.unlock();
		}
	}

	public boolean containsKey(Object key) {
		r.lock();
		try {
			return map.containsKey(key);
		} finally {
			r.unlock();
		}

	}

	public boolean containsValue(Object value) {
		r.lock();
		try {
			return map.containsValue(value);
		} finally {
			r.unlock();
		}

	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	public V get(Object key) {
		r.lock();
		try {
			return map.get(key);
		} finally {
			r.unlock();
		}
	}

	public boolean isEmpty() {
		r.lock();
		try {
			return map.isEmpty();
		} finally {
			r.unlock();
		}
	}

	public Set<K> keySet() {
		r.lock();
		try {
			return new HashSet<K>(map.keySet());
		} finally {
			r.unlock();
		}

	}

	public V put(K key, V value) {
		w.lock();
		try {
			return map.put(key, value);
		} finally {
			w.unlock();
		}
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		w.lock();
		try {
			map.putAll(m);
		} finally {
			w.unlock();
		}
	}

	public V remove(Object key) {
		w.lock();
		try {
			return map.remove(key);
		} finally {
			w.unlock();
		}

	}

	public int size() {
		r.lock();
		try {
			return map.size();
		} finally {
			r.unlock();
		}

	}

	public Collection<V> values() {
		r.lock();
		try {
			return new ArrayList<V>(map.values());
		} finally {
			r.unlock();
		}

	}
}

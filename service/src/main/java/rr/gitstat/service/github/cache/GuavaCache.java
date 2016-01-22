package rr.gitstat.service.github.cache;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;

public class GuavaCache<T> implements Cache<T> {

	com.google.common.cache.Cache<String, T> _theCache = CacheBuilder.newBuilder().maximumSize(100)
			.expireAfterAccess(6, TimeUnit.HOURS).build();

	public T get(String key) {
		try {
			return _theCache.get(key, new Callable<T>() {
				public T call() throws Exception {
					return null;
				}
			});
		} catch (Exception e) {
		}
		return null;
	}

	public void put(String key, T value) {
		if (key != null && value != null) {
			_theCache.put(key, value);
		}
	}

	public Cache<T> create(Map<String, Object> properties) {
		return this;
	}
}

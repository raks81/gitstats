package rr.gitstat.service.github.cache;

import java.util.Map;

public interface Cache<T> {
	public Cache<T> create(Map<String, Object> properties);
	public T get(String key);
	public void put(String key, T value);
}

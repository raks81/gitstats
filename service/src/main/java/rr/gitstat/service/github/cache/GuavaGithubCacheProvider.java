package rr.gitstat.service.github.cache;

import java.util.HashMap;
import java.util.Map;


public class GuavaGithubCacheProvider implements GitHubCacheProvider {

	private Map<String, Cache> allCache = new HashMap<String, Cache>();

	public synchronized <T> Cache<T> createCache(String cacheName) {
		Cache<T> cache = new GuavaCache<T>().create(null);
		allCache.put(cacheName, cache);
		return cache;
	}

	public <T> Cache<T> getCache(String cacheName) {
		return allCache.get(cacheName);
	}

	public void clearCache(String cacheName) {
		throw new RuntimeException("Not yet implemented for Guava cache");
	}

}

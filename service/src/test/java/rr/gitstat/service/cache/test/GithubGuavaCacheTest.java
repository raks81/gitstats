package rr.gitstat.service.cache.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import rr.gitstat.service.github.cache.Cache;
import rr.gitstat.service.github.cache.GitHubCacheProvider;
import rr.gitstat.service.github.cache.GuavaGithubCacheProvider;

public class GithubGuavaCacheTest {

	GitHubCacheProvider cacheProvider = null;

	@Before
	public void setup() {
		cacheProvider = new GuavaGithubCacheProvider();
	}

	@Test
	public void shouldCreateCacheAndStoreValue() {
		// Check there is nothing in the provider
		assertNull(cacheProvider.getCache("test1"));

		// Create an empty cache
		Cache<String> cache1 = cacheProvider.createCache("test1");

		// Check the cache is empty
		assertNull(cache1.get("key1"));

		// Add an item to cache
		cache1.put("key1", "value1");

		// Check the cache is present in the provider
		assertNotNull(cacheProvider.getCache("test1"));
		assertNotNull(cache1.get("key1"));
		assertEquals(cache1.get("key1"), "value1");

		// Get the cache from provider
		assertNotNull(cacheProvider.getCache("test1"));

	}
}

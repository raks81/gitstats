package rr.gitstat.service.github.cache;

public interface GitHubCacheProvider {

	public <T> Cache<T> createCache(String cacheName);

	public <T> Cache<T> getCache(String cacheName);

	public void clearCache(String cacheName);

}

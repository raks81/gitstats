package rr.gitstat.service.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import rr.gitstat.model.mapper.GithubJsonToPojoMapper;
import rr.gitstat.service.github.cache.Cache;
import rr.gitstat.service.github.cache.GitHubCacheProvider;
import rr.gitstat.service.github.cache.GuavaGithubCacheProvider;

public class GithubClient {

	private static final Logger logger = Logger.getLogger(GithubClient.class.getName());

	// API Keys
	public static final String REPO_INFO_API = "https://api.github.com/repos/{owner}/{repo}";
	public static final String USER_REPOS_API = "https://api.github.com/users/{username}/repos";
	public static final String USER_INFO_API = "https://api.github.com/users/{username}";
	public static final String COMMIT_ACTIVITY_API = "https://api.github.com/repos/{owner}/{repo}/stats/commit_activity";
	public static final String REPO_PARTICIPATION_API = "https://api.github.com/repos/{owner}/{repo}/stats/participation";

	private static GitHubCacheProvider cacheProvider = new GuavaGithubCacheProvider();

	private static final Map<String, Cache> cache = new HashMap<>();
	private static final Map<String, WebTarget> webTargets = new HashMap<>();

	private boolean useCache = true;

	private static String GITHUB_API_TOKEN = "4cf819212f3ceb37141490ea8b3c4a045d2e1073";
	private Client client;

	public GithubClient() {
		client = ClientBuilder.newClient();
		webTargets.put(USER_INFO_API, client.target(USER_INFO_API));
		webTargets.put(USER_REPOS_API, client.target(USER_REPOS_API));
		webTargets.put(REPO_INFO_API, client.target(REPO_INFO_API));
		webTargets.put(COMMIT_ACTIVITY_API, client.target(COMMIT_ACTIVITY_API));
		webTargets.put(REPO_PARTICIPATION_API, client.target(REPO_PARTICIPATION_API));
	}

	public <T> T makeGithubAPICall(String apiKey, Map<String, String> params, Class<T> type)
			throws JsonParseException, JsonMappingException, IOException {

		String cacheKey = buildCacheKey(params);
		Cache<T> apiCache = cache.get(apiKey);
		if (apiCache == null) {
			cache.put(apiKey, cacheProvider.createCache(apiKey));
			apiCache = cache.get(apiKey);
		}

		if (useCache && apiCache.get(cacheKey) == null) {
			logger.info("Fetching stats from remote for: " + apiKey);
			String json = "[]";
			for (int i = 0; i < 3; i++) {
				Response res = null;
				try {
					res = prepareRequest(apiKey, params).get();
					if (res.getStatus() == 202) {
						// Stats not ready yet. Wait for 5 seconds and retry
						i++;
						try {
							Thread.sleep(5000l);
						} catch (InterruptedException e) {
							// Do nothing
						}
					} else if (res.getStatus() == 200) {
						json = res.readEntity(String.class);
						apiCache.put(cacheKey, new GithubJsonToPojoMapper().mapJsonToPojo(json, type));
						break;
					} else {
						throw new RuntimeException(
								"Call to github failed: " + res.getStatus() + " - " + res.toString());
					}
				} finally {
					if (res != null) {
						try {
							res.close();
						} catch (Exception e) {

						}
					}
				}
			}
		}
		return apiCache.get(cacheKey);
	}

	private Builder prepareRequest(String apiKey, Map<String, String> params) {
		WebTarget target = webTargets.get(apiKey);
		for (String key : params.keySet()) {
			target = target.resolveTemplate(key, params.get(key));
		}
		return target.request("application/json").header(HttpHeaders.AUTHORIZATION, "token " + GITHUB_API_TOKEN)
				.header(HttpHeaders.USER_AGENT, "User-Agent: Gitstat");
	}

	private String buildCacheKey(Map<String, String> params) {
		List<String> sortedParams = new ArrayList(params.keySet());
		Collections.sort(sortedParams);
		StringBuilder sb = new StringBuilder();
		for (String key : sortedParams) {
			sb.append(params.get(key)).append("/");
		}
		return sb.toString();
	}

	public static GitHubCacheProvider getCacheProvider() {
		return cacheProvider;
	}

	public static void setCacheProvider(GitHubCacheProvider cacheProvider) {
		GithubClient.cacheProvider = cacheProvider;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}
}

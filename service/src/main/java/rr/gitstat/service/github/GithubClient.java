package rr.gitstat.service.github;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import rr.gitstat.model.github.Repo;
import rr.gitstat.model.github.RepoCommitStat;
import rr.gitstat.model.mapper.GithubJsonToPojoMapper;

public class GithubClient {

	private Client client;
	private WebTarget userTarget;
	private WebTarget userRepoTarget;
	private WebTarget repoTarget;
	private WebTarget commitActivityTarget;

	public GithubClient() {
		client = ClientBuilder.newClient();
		userTarget = client.target("https://api.github.com/users/{username}");
		userRepoTarget = client.target("https://api.github.com/users/{username}/repos");
		repoTarget = client.target("https://api.github.com/repos/{owner}/{repo}");
		commitActivityTarget = client.target("https://api.github.com/repos/{owner}/{repo}/stats/commit_activity");

	}

	public String findUserByUsername(String username) {
		Response res = userTarget.resolveTemplate("username", username).request("application/json")
				.header(HttpHeaders.AUTHORIZATION, "token " + System.getenv("GITHUB_API_TOKEN")).get();
		return res.readEntity(String.class);
	}

	public String findRepositoriesByUser(String username) {
		Response res = userRepoTarget.resolveTemplate("username", username).request("application/json")
				.header(HttpHeaders.AUTHORIZATION, "token " + System.getenv("GITHUB_API_TOKEN")).get();
		return res.readEntity(String.class);
	}

	public Repo getRepo(String owner, String repo) throws JsonParseException, JsonMappingException, IOException {
		Response res = repoTarget.resolveTemplate("owner", owner).resolveTemplate("repo", repo)
				.request("application/json")
				.header(HttpHeaders.AUTHORIZATION, "token " + System.getenv("GITHUB_API_TOKEN")).get();
		String json = res.readEntity(String.class);
		return new GithubJsonToPojoMapper().mapRepoJsonToPojo(json);
	}

	public RepoCommitStat[] findCommitActivity(String owner, String repo)
			throws JsonParseException, JsonMappingException, IOException {
		String json = "[]";
		for (int i = 0; i < 3;) {
			Response res = commitActivityTarget.resolveTemplate("owner", owner).resolveTemplate("repo", repo)
					.request("application/json")
					.header(HttpHeaders.AUTHORIZATION, "token " + System.getenv("GITHUB_API_TOKEN")).get();
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
				break;
			}
		}
		return new GithubJsonToPojoMapper().mapRepoCommitStatJsonToPojo(json);
	}
}

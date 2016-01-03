package rr.gitstat.service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class GithubClient {

	private Client client;
	private WebTarget userTarget;
	private WebTarget userRepoTarget;

	public GithubClient() {
		client = ClientBuilder.newClient();
		userTarget = client.target("https://api.github.com/users/{username}");
		userRepoTarget = client.target("https://api.github.com/users/{username}/repos");
	}

	public String findUserByUsername(String username) {
		Response res = userTarget.resolveTemplate("username", username).request("application/json").get();
		return res.readEntity(String.class);
	}

	public String findRepositoriesByUser(String username) {
		Response res = userRepoTarget.resolveTemplate("username", username).request("application/json").get();
		return res.readEntity(String.class);
	}

}

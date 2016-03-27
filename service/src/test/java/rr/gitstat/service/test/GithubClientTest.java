package rr.gitstat.service.test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import rr.gitstat.model.github.Repo;
import rr.gitstat.model.github.RepoCommitStat;
import rr.gitstat.model.github.RepoParticipation;
import rr.gitstat.model.github.User;
import rr.gitstat.service.github.GithubClient;

public class GithubClientTest {

	private GithubClient client;

	@Before
	public void setup() {
		client = new GithubClient();
	}

	@Test
	public void testGetUserInfo() throws JsonParseException, JsonMappingException, IOException {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("username", "raks81");
		User user = client.makeGithubAPICall(GithubClient.USER_INFO_API, params, User.class);

		Assert.assertNotNull(user);
		Assert.assertTrue(user.getLogin().equals("raks81"));
	}

	@Test
	public void testGetNonExistingUser() throws JsonParseException, JsonMappingException, IOException {
		try {
			Map<String, String> params = new LinkedHashMap<>();
			params.put("username", "someuser-that-doest-exist-007");
			client.makeGithubAPICall(GithubClient.USER_INFO_API, params, User.class);
			Assert.assertTrue(false);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			Assert.assertTrue(e.getMessage().contains("404"));
		}
	}

	@Test
	public void shouldFetchRepoMetadata() throws JsonParseException, JsonMappingException, IOException {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("owner", "raks81");
		params.put("repo", "go-acousticid");
		Repo repo = client.makeGithubAPICall(GithubClient.REPO_INFO_API, params, Repo.class);
		Assert.assertNotNull(repo);
		Assert.assertEquals(repo.getName(), "go-acousticid");

		params.put("owner", "antirez");
		params.put("repo", "redis");
		Repo repo2 = client.makeGithubAPICall(GithubClient.REPO_INFO_API, params, Repo.class);
		Assert.assertNotNull(repo2);
		Assert.assertEquals(repo2.getName(), "redis");

	}

	@Test
	public void shouldReturnCommitActivityOfRepo() throws JsonParseException, JsonMappingException, IOException {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("owner", "raks81");
		params.put("repo", "go-acousticid");
		RepoCommitStat[] response = client.makeGithubAPICall(GithubClient.COMMIT_ACTIVITY_API, params,
				RepoCommitStat[].class);
		Assert.assertNotNull(response);
		// Assert.assertTrue(response.contains("Not Found"));
	}

	@Test
	public void shouldReturnRepoParticipation() throws JsonParseException, JsonMappingException, IOException {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("owner", "raks81");
		params.put("repo", "go-acousticid");
		RepoParticipation participation = client.makeGithubAPICall(GithubClient.REPO_PARTICIPATION_API, params,
				RepoParticipation.class);

		Assert.assertNotNull(participation);
		Assert.assertTrue(participation.getAll().size() > 0);
	}
}

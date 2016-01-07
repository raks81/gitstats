package rr.gitstat.service.test;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import rr.gitstat.model.github.Repo;
import rr.gitstat.model.github.RepoCommitStat;
import rr.gitstat.service.github.GithubClient;

public class GithubClientTest {

	private GithubClient client;

	@Before
	public void setup() {
		client = new GithubClient();
	}

	@Test
	public void testGetUserInfo() {
		String response = client.findUserByUsername("raks81");
		Assert.assertNotNull(response);
		Assert.assertTrue(response.contains("raks81"));
	}

	@Test
	public void testGetNonExistingUser() {
		String response = client.findUserByUsername("someuser-that-doest-exist-007");
		Assert.assertNotNull(response);
		Assert.assertTrue(response.contains("Not Found"));
	}

	@Test
	public void shouldFetchRepoMetadata() throws JsonParseException, JsonMappingException, IOException {
		Repo repo = client.getRepo("raks81", "go-acousticid");
		Assert.assertNotNull(repo);
		// Assert.assertTrue(response.contains("Not Found"));
	}

	@Test
	public void shouldReturnCommitActivityOfRepo() throws JsonParseException, JsonMappingException, IOException {
		RepoCommitStat[] response = client.findCommitActivity("raks81", "go-acousticid");
		Assert.assertNotNull(response);
		// Assert.assertTrue(response.contains("Not Found"));
	}
}

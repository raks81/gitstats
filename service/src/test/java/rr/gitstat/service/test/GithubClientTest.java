package rr.gitstat.service.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rr.gitstat.service.GithubClient;

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
}

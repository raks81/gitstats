package rr.gitstat.web.test;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import rr.gitstat.web.rest.GitstatUIDataService;

public class GitstatUIDataServiceTest {

	@Test
	public void shouldReturnUIJsonString() throws JsonParseException, JsonMappingException, IOException {
		GitstatUIDataService service = new GitstatUIDataService();
		String json = service.data("raks81/go-acousticid,joelittlejohn/jsonschema2pojo");
		Assert.assertNotNull(json);
	}

	@Test
	public void shouldFetchDailyCommitStatistics() throws JsonParseException, JsonMappingException, IOException {
		GitstatUIDataService service = new GitstatUIDataService();
		List<List<String>> commits = service.getRepoCommitDailyStat("raks81/go-acousticid");
		Assert.assertNotNull(commits);
		Assert.assertTrue(commits.size() == 2);
		Assert.assertTrue(commits.get(0).size() > 360);
		Assert.assertTrue(commits.get(1).size() > 360);
	}
	
	@Test
	public void shouldFetchWeeklyCommitStatistics() throws JsonParseException, JsonMappingException, IOException {
		GitstatUIDataService service = new GitstatUIDataService();
		List<List<String>> commits = service.getRepoCommitWeeklyStat("raks81/go-acousticid");
		Assert.assertNotNull(commits);
		Assert.assertTrue(commits.size() == 2);
		Assert.assertTrue(commits.get(0).size() == 52);
		Assert.assertTrue(commits.get(1).size() == 52);
	}
}

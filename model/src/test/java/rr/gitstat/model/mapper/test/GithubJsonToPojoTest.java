package rr.gitstat.model.mapper.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import rr.gitstat.model.github.RepoCommitStat;
import rr.gitstat.model.mapper.GithubJsonToPojoMapper;

public class GithubJsonToPojoTest {

	@Test
	public void shouldMapRepoJsonStringToPojo() throws JsonParseException, JsonMappingException, IOException {
		String schemaContent = new Scanner(this.getClass().getResourceAsStream("/sample_commit_history.json")).useDelimiter("\\Z")
				.next();
		GithubJsonToPojoMapper mapper = new GithubJsonToPojoMapper();
		RepoCommitStat[] repoCommitStats = mapper.mapRepoCommitStatJsonToPojo(schemaContent);
		assertNotNull(repoCommitStats);
		assertThat(repoCommitStats.length, is(52));
	}
}

package rr.gitstat.model.mapper.test;

import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import rr.gitstat.model.github.Repo;
import rr.gitstat.model.mapper.GithubJsonToPojo;

public class GithubJsonToPojoTest {

	@Test
	public void shouldMapRepoJsonStringToPojo() throws JsonParseException, JsonMappingException, IOException {
		String schemaContent = new Scanner(this.getClass().getResourceAsStream("/sample_repo.json")).useDelimiter("\\Z")
				.next();
		GithubJsonToPojo mapper = new GithubJsonToPojo();
		Repo repo = mapper.mapRepoJsonToPojo(schemaContent);
		assertNotNull(repo);
		assertThat(repo.getName(), is("go-acousticid"));
		assertThat(repo.getFullName(), is("raks81/go-acousticid"));
		assertThat(repo.getStargazersCount(), is(1));
		assertThat(repo.getWatchers(), is(1));
		assertNotNull(repo.getOwner());
		assertThat(repo.getOwner().getLogin(), is("raks81"));
	}
}

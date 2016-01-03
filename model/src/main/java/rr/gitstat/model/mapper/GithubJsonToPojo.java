package rr.gitstat.model.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import rr.gitstat.model.github.Repo;

public class GithubJsonToPojo {

	public Repo mapRepoJsonToPojo(String repoJson) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Repo repo = mapper.readValue(repoJson, Repo.class);
		return repo;
	}
}

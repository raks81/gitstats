package rr.gitstat.model.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import rr.gitstat.model.github.Repo;
import rr.gitstat.model.github.RepoCommitStat;

public class GithubJsonToPojoMapper {

	public RepoCommitStat[] mapRepoCommitStatJsonToPojo(String repoCommitHistoryJson)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		RepoCommitStat[] repoCommitStats = mapper.readValue(repoCommitHistoryJson, RepoCommitStat[].class);
		return repoCommitStats;
	}

	public Repo mapRepoJsonToPojo(String repoJson) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Repo repo = mapper.readValue(repoJson, Repo.class);
		return repo;
	}
}

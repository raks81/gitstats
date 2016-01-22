package rr.gitstat.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import rr.gitstat.model.github.Repo;
import rr.gitstat.model.github.RepoCommitStat;
import rr.gitstat.model.ui.Cell;
import rr.gitstat.model.ui.Data;
import rr.gitstat.model.ui.Row;
import rr.gitstat.model.ui.TsValues;
import rr.gitstat.model.ui.UIDataModel;
import rr.gitstat.service.github.GithubClient;

@Path("ui")
public class GitstatUIDataService {

	@GET
	@Consumes("text/plain")
	@Produces("application/json")
	@Path("data")
	public String data(@QueryParam("p") String projects) throws JsonParseException, JsonMappingException, IOException {

		// projects="raks81/go-acousticid,joelittlejohn/jsonschema2pojo";
		StringTokenizer st = new StringTokenizer(projects, ",");
		List<String> projectsList = new ArrayList<String>();
		while (st.hasMoreElements()) {
			projectsList.add(st.nextToken());
		}

		UIDataModel dataModel = new UIDataModel();

		Map<String, Repo> repos = new HashMap<String, Repo>();

		// Add stars
		Row starsRow = new Row();
		starsRow.getCells().add(createTextCell("title", "Stars"));
		for (String project : projectsList) {
			if (repos.get(project) == null) {
				repos.put(project, getRepo(project));
			}
			starsRow.getCells()
					.add(createTextCell("number", Integer.toString(repos.get(project).getStargazersCount())));
		}
		dataModel.getRows().add(starsRow);

		// Add watchers
		Row watchersRow = new Row();
		watchersRow.getCells().add(createTextCell("title", "Watchers"));
		for (String project : projectsList) {
			if (repos.get(project) == null) {
				repos.put(project, getRepo(project));
			}
			watchersRow.getCells()
					.add(createTextCell("number", Integer.toString(repos.get(project).getSubscribersCount())));
		}
		dataModel.getRows().add(watchersRow);

		// Add commits
		Row commitsRow = new Row();
		commitsRow.getCells().add(createTextCell("title", "Commits"));
		for (String project : projectsList) {
			commitsRow.getCells().add(createTimeSeriesCell(getRepoCommitWeeklyStat(project), "Commits"));
		}
		dataModel.getRows().add(commitsRow);

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(dataModel);
	}

	private Cell createTextCell(String type, String value) {
		Cell title = new Cell();
		title.setType(type);
		Data data = new Data();
		data.setValue(value);
		title.setData(data);
		return title;
	}

	private Cell createTimeSeriesCell(List<List<String>> tsValuesList, String name) {
		TsValues tsValues = new TsValues();
		tsValues.setColumns(tsValuesList);
		tsValuesList.get(0).add(0, "x");
		tsValuesList.get(1).add(0, name);
		tsValues.setColumns(tsValuesList);

		Data data = new Data();
		data.setValue(name);
		data.setTsValues(tsValues);
		Cell tsCell = new Cell();

		tsCell.setData(data);
		tsCell.setType("timeseries");

		return tsCell;
	}

	private Repo getRepo(String project) {
		try {
			GithubClient client = new GithubClient();
			StringTokenizer st = new StringTokenizer(project, "/");
			Map<String, String> params = new LinkedHashMap<>();
			params.put("owner", st.nextToken());
			params.put("repo", st.nextToken());
			return client.makeGithubAPICall(GithubClient.REPO_INFO_API, params, Repo.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<List<String>> getRepoCommitDailyStat(String project)
			throws JsonParseException, JsonMappingException, IOException {
		GithubClient client = new GithubClient();
		StringTokenizer st = new StringTokenizer(project, "/");

		Map<String, String> params = new LinkedHashMap<>();
		params.put("owner", st.nextToken());
		params.put("repo", st.nextToken());
		RepoCommitStat[] stats = client.makeGithubAPICall(GithubClient.COMMIT_ACTIVITY_API, params,
				RepoCommitStat[].class);
		List<List<String>> commits = new ArrayList<List<String>>();
		commits.add(new ArrayList<String>());
		commits.add(new ArrayList<String>());
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");

		DateTime date = new DateTime((stats[0].getWeek() * 1000L));
		for (RepoCommitStat stat : stats) {
			for (int dayCommit : stat.getDays()) {
				commits.get(0).add(dtf.print(date));
				commits.get(1).add(Integer.toString(dayCommit));
				date = date.plusDays(1);
			}
		}
		return commits;
	}

	public List<List<String>> getRepoCommitWeeklyStat(String project)
			throws JsonParseException, JsonMappingException, IOException {
		GithubClient client = new GithubClient();
		StringTokenizer st = new StringTokenizer(project, "/");

		Map<String, String> params = new LinkedHashMap<>();
		params.put("owner", st.nextToken());
		params.put("repo", st.nextToken());
		RepoCommitStat[] stats = client.makeGithubAPICall(GithubClient.COMMIT_ACTIVITY_API, params,
				RepoCommitStat[].class);
		List<List<String>> commits = new ArrayList<List<String>>();
		commits.add(new ArrayList<String>());
		commits.add(new ArrayList<String>());
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
		for (RepoCommitStat stat : stats) {
			DateTime date = new DateTime((stat.getWeek() * 1000L));
			commits.get(0).add(dtf.print(date));
			commits.get(1).add(Integer.toString(stat.getTotal()));
		}
		return commits;
	}
}

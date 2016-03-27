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
import rr.gitstat.model.github.RepoParticipation;
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
		// Set repo
		for (String project : projectsList) {
			if (repos.get(project) == null) {
				repos.put(project, getRepo(project));
			}
		}

		// Add description
		addRepoTextRow(projectsList, dataModel, repos, "text", "Description", "getDescription");

		// Add Homepage
		addRepoTextRow(projectsList, dataModel, repos, "link", "Home page", "getHomePage");

		// Add Github page
		addRepoTextRow(projectsList, dataModel, repos, "link", "Github page", "getHtmlUrl");

		// Clone URL
		addRepoTextRow(projectsList, dataModel, repos, "text", "Clone URL", "getCloneUrl");
		
		// Language
		addRepoTextRow(projectsList, dataModel, repos, "text", "Language", "getLanguage");

		// Created at
		addRepoTextRow(projectsList, dataModel, repos, "text", "Created", "getCreatedAt");

		// Updated at
		addRepoTextRow(projectsList, dataModel, repos, "text", "Updated", "getUpdatedAt");

		// Fork Count
		addRepoTextRow(projectsList, dataModel, repos, "number", "Forks", "getForksCount");

		// Add stars
		addRepoTextRow(projectsList, dataModel, repos, "number", "Stars", "getStargazersCount");

		// Add watchers
		addRepoTextRow(projectsList, dataModel, repos, "number", "Watchers", "getSubscribersCount");

		// Add commits
		Row commitsRow = new Row();
		commitsRow.getCells().add(createTextCell("title", "Commits"));
		for (String project : projectsList) {
			commitsRow.getCells().add(createTimeSeriesCell(getRepoCommitWeeklyStat(project), "Commits"));
		}
		dataModel.getRows().add(commitsRow);

		// Add participation
		Row participationRow = new Row();
		participationRow.getCells().add(createTextCell("title", "Participation"));
		for (String project : projectsList) {
			participationRow.getCells().add(createDonutCell(getRepoParticipation(project), "Participation"));
		}
		dataModel.getRows().add(participationRow);

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(dataModel);
	}

	private void addRepoTextRow(List<String> projectsList, UIDataModel dataModel, Map<String, Repo> repos, String type,
			String title, String getter) {
		Row row = new Row();
		row.getCells().add(createTextCell("title", title));
		for (String project : projectsList) {
			try {
				row.getCells()
						.add(createTextCell(type, Repo.class.getMethod(getter).invoke(repos.get(project)).toString()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				row.getCells().add(new Cell());
			}
		}
		dataModel.getRows().add(row);
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

	private Cell createDonutCell(Map<String, String> valuesMap, String name) {
		List<List<String>> valuesList = new ArrayList<List<String>>();
		for (String key : valuesMap.keySet()) {
			List<String> t = new ArrayList<String>();
			t.add(key);
			t.add(valuesMap.get(key));
			valuesList.add(t);
		}
		TsValues tsValues = new TsValues();
		tsValues.setColumns(valuesList);

		Data data = new Data();
		data.setValue(name);
		data.setTsValues(tsValues);
		Cell tsCell = new Cell();

		tsCell.setData(data);
		tsCell.setType("donut");

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

	public Map<String, String> getRepoParticipation(String project)
			throws JsonParseException, JsonMappingException, IOException {
		GithubClient client = new GithubClient();
		StringTokenizer st = new StringTokenizer(project, "/");

		Map<String, String> params = new LinkedHashMap<>();
		params.put("owner", st.nextToken());
		params.put("repo", st.nextToken());
		RepoParticipation stats = client.makeGithubAPICall(GithubClient.REPO_PARTICIPATION_API, params,
				RepoParticipation.class);
		Integer totalOwner = 0;
		Integer total = 0;
		for (Integer i : stats.getOwner()) {
			totalOwner += i;
		}
		for (Integer i : stats.getAll()) {
			total += i;
		}

		Long totalOwnerPercentage = Math.round(((double) totalOwner / (double) total) * 100);
		Map<String, String> participation = new HashMap<>();
		participation.put("Owner", Long.toString(totalOwnerPercentage));
		participation.put("Others", Long.toString(100 - totalOwnerPercentage));
		return participation;
	}
}

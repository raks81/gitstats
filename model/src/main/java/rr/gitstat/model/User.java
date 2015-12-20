package rr.gitstat.model;

import lombok.Data;

@Data
public class User {
	private String login;
	private long githubId;
	private String type;
}

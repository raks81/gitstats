package rr.gitstat.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "GITHUB_USER")
public class GitstatUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	private String login;
	private Long githubId;
	private String type;
	
	@ManyToMany(mappedBy="collaborators")
	private List<GitstatRepo> collaboratingRepos;
}

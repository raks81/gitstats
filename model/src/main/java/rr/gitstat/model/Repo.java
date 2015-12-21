package rr.gitstat.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "GITHUB_REPO")
@Getter
@Setter
public class Repo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long repoId;
	
	private Long githubRepoId;
	
	private String name;
	private String fullName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date pushedDate;

	@OneToOne
	@JoinColumn(name = "OWNER", nullable = false, updatable = true)
	private User owner;

	@OneToMany
	@JoinTable(name = "REPO_COLLABORATORS")
	private List<User> collaborators;

}

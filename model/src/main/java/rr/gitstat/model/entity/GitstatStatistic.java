package rr.gitstat.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "REPO_STAT")
@Getter
@Setter
public class GitstatStatistic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long statId;
	private Integer stars;
	private Integer watchers;

	@Temporal(TemporalType.TIMESTAMP)
	private Date statDate;
	
	@ManyToOne
	private GitstatRepo repo;
}

package rr.gitstat.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "REPO_STAT")
@Getter
@Setter
public class Stat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long statId;
	private Integer stars;
	private Integer watchers;

	@Temporal(TemporalType.TIMESTAMP)
	private Date statDate;
}

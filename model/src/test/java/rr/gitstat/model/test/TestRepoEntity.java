package rr.gitstat.model.test;

import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

import rr.gitstat.model.Repo;

public class TestRepoEntity {

	private EntityManagerFactory entityManagerFactory;

	@Before
	protected void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("org.hibernate.tutorial.jpa");
	}

	@Test
	public void test() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		Repo repo = new Repo();
		
		
		entityManager.getTransaction().commit();
		entityManager.close();

		fail("Not yet implemented");
	}

}

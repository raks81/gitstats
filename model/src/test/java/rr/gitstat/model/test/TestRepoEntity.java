package rr.gitstat.model.test;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

import rr.gitstat.model.Repo;

public class TestRepoEntity {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("rr.gitstat");
	}

	@Test
	public void testPersist() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		Repo repo = new Repo();
		repo.setGithubRepoId(1111111l);
		repo.setCreatedDate(Calendar.getInstance().getTime());
		repo.setName("test-repo");
		repo.setFullName("rakrao/test-repo");
		entityManager.persist(repo);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<Repo> repos = entityManager.createQuery("from Repo", Repo.class).getResultList();

		for (Repo readRepo : repos) {
			System.out.println(readRepo.toString());
		}
		entityManager.getTransaction().commit();
		entityManager.close();

		System.out.println("Read Successfully!");

		System.out.println("Persisted!");

	}

	@Test
	public void testRead() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<Repo> repos = entityManager.createQuery("from Repo", Repo.class).getResultList();

		for (Repo repo : repos) {
			System.out.println(repo.toString());
		}
		entityManager.getTransaction().commit();
		entityManager.close();

		System.out.println("Read Successfully!");
	}

}

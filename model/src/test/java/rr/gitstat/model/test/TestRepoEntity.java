package rr.gitstat.model.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rr.gitstat.model.Repo;
import rr.gitstat.model.Stat;
import rr.gitstat.model.User;

public class TestRepoEntity {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("rr.gitstat");

		// Create some users
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		User rr1 = new User();
		rr1.setGithubId(111111L);
		rr1.setLogin("raks81");
		rr1.setType("User");

		entityManager.persist(rr1);

		User rr2 = new User();
		rr2.setGithubId(111112L);
		rr2.setLogin("raks82");
		rr2.setType("User");
		entityManager.persist(rr2);

		User rr3 = new User();
		rr3.setGithubId(111113L);
		rr3.setLogin("raks83");
		rr3.setType("User");
		entityManager.persist(rr3);

		entityManager.getTransaction().commit();
		entityManager.close();

	}

	@Test
	public void testRepo() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		// Get the users
		User rr1 = entityManager.createQuery("from User where login = 'raks81' ", User.class).getSingleResult();
		Assert.assertNotNull(rr1);
		Assert.assertEquals("null", "raks81", rr1.getLogin());
		User rr2 = entityManager.createQuery("from User where login = 'raks82' ", User.class).getSingleResult();
		Assert.assertNotNull(rr2);
		Assert.assertEquals("null", "raks82", rr2.getLogin());

		User rr3 = entityManager.createQuery("from User where login = 'raks83' ", User.class).getSingleResult();
		Assert.assertNotNull(rr3);
		Assert.assertEquals("null", "raks83", rr3.getLogin());

		// Create repo
		Repo repo = new Repo();
		repo.setGithubRepoId(1111111l);
		repo.setCreatedDate(Calendar.getInstance().getTime());
		repo.setName("test-repo");
		repo.setFullName("rr/test-repo");
		repo.setOwner(rr1);

		// Add collaborators
		repo.setCollaborators(new ArrayList<User>());
		repo.getCollaborators().add(rr1);
		repo.getCollaborators().add(rr2);
		repo.getCollaborators().add(rr3);

		// Add a few stats
		Stat stat1 = new Stat();
		stat1.setRepo(repo);
		stat1.setStars(403);
		stat1.setWatchers(503);
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		stat1.setStatDate(yesterday.getTime());

		Stat stat2 = new Stat();
		stat2.setRepo(repo);
		stat2.setStars(405);
		stat2.setWatchers(506);
		stat2.setStatDate(Calendar.getInstance().getTime());

		repo.setStats(new ArrayList<Stat>());
		repo.getStats().add(stat1);
		repo.getStats().add(stat2);

		entityManager.persist(repo);
		entityManager.getTransaction().commit();
		entityManager.close();
		System.out.println("Persisted!");

		// Read repo data
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<Repo> repos = entityManager.createQuery("from Repo", Repo.class).getResultList();
		for (Repo readRepo : repos) {
			// Check repo
			Assert.assertNotNull(readRepo);
			Assert.assertEquals("null", "rr/test-repo", readRepo.getFullName());
			Assert.assertNotNull(readRepo.getCreatedDate());

			// Check collaborators
			Assert.assertNotNull(readRepo.getCollaborators());
			Assert.assertTrue(readRepo.getCollaborators().size() > 0);
			
			//Check if collabortor's repo mapping exists
			Assert.assertNotNull(readRepo.getCollaborators().get(0).getCollaboratingRepos());
			Assert.assertTrue(readRepo.getCollaborators().get(0).getCollaboratingRepos().size() > 0);

			// Check stats
			Assert.assertNotNull(readRepo.getStats());
			Assert.assertTrue(readRepo.getStats().size() > 0);
			Assert.assertNotNull(readRepo.getStats().get(0).getRepo());
			Assert.assertEquals("null", "rr/test-repo", readRepo.getStats().get(0).getRepo().getFullName());
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		System.out.println("Read Successfully!");

	}
}

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
		repo.setCollaborators(new ArrayList<User>());
		repo.getCollaborators().add(rr1);
		repo.getCollaborators().add(rr2);
		repo.getCollaborators().add(rr3);
		entityManager.persist(repo);
		entityManager.getTransaction().commit();
		entityManager.close();
		System.out.println("Persisted!");

		// Read repo data
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<Repo> repos = entityManager.createQuery("from Repo", Repo.class).getResultList();
		for (Repo readRepo : repos) {
			Assert.assertNotNull(readRepo);
			Assert.assertEquals("null", "rr/test-repo", readRepo.getFullName());
			Assert.assertNotNull(readRepo.getCreatedDate());
			Assert.assertNotNull(readRepo.getCollaborators());
			Assert.assertTrue(readRepo.getCollaborators().size() > 1);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		System.out.println("Read Successfully!");

	}
}

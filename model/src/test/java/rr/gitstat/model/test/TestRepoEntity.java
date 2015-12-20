package rr.gitstat.model.test;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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

//		User rr2 = new User(null, "raks82", 11112L, "User");
//		entityManager.persist(rr2);
//
//		User rr3 = new User(null, "raks83", 11113L, "User");
//		entityManager.persist(rr3);

		entityManager.getTransaction().commit();
		entityManager.close();

	}

	@Test
	public void testRepo() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		//Get the owner
		User rr1 = entityManager.find(User.class, 1L);
		
		//Create repo
		Repo repo = new Repo();
		repo.setGithubRepoId(1111111l);
		repo.setCreatedDate(Calendar.getInstance().getTime());
		repo.setName("test-repo");
		repo.setFullName("rr/test-repo");
		repo.setOwner(rr1);
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
}

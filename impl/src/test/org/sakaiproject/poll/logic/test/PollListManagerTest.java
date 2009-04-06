package org.sakaiproject.poll.logic.test;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.poll.dao.PollDao;
import org.sakaiproject.poll.logic.test.stubs.ExternalLogicStubb;
import org.sakaiproject.poll.model.Poll;
import org.sakaiproject.poll.service.impl.PollListManagerImpl;
import org.springframework.test.AbstractTransactionalSpringContextTests;

public class PollListManagerTest extends AbstractTransactionalSpringContextTests {

	private static Log log = LogFactory.getLog(PollListManagerTest.class);	
	
	private TestDataPreload tdp = new TestDataPreload();

	private PollListManagerImpl pollListManager;
	private ExternalLogicStubb externalLogicStubb;
	
	protected String[] getConfigLocations() {
		// point to the needed spring config files, must be on the classpath
		// (add component/src/webapp/WEB-INF to the build path in Eclipse),
		// they also need to be referenced in the project.xml file
		return new String[] { "hibernate-test.xml", "spring-hibernate.xml" };
	}

	// run this before each test starts
	protected void onSetUpBeforeTransaction() throws Exception {
	}
	
	// run this before each test starts and as part of the transaction
	protected void onSetUpInTransaction() {
		PollDao dao = (PollDao) applicationContext.getBean("org.sakaiproject.poll.dao.impl.PollDaoTarget");
		if (dao == null) {
			log.error("onSetUpInTransaction: DAO could not be retrieved from spring context");
			return;
		}
		
		pollListManager = new PollListManagerImpl();
		pollListManager.setDao(dao);
		
		externalLogicStubb = new ExternalLogicStubb();
		pollListManager.setExternalLogic(externalLogicStubb);
		
		// preload testData
		tdp.preloadTestData(dao);
	}
	
    public void testGetPollById() {
    	externalLogicStubb.currentUserId = TestDataPreload.USER_UPDATE;
    	
    	//we shouldNot find this poll
    	Poll pollFail = pollListManager.getPollById(Long.valueOf(99));
    	assertNull(pollFail);
    	
    	//this one should exist
    	externalLogicStubb.currentUserId = TestDataPreload.USER_UPDATE;
    	Poll poll1 = pollListManager.getPollById(Long.valueOf(1));
    	assertNotNull(poll1);
    	
    	//it should have options
    	assertNotNull(poll1.getPollOptions());
    	assertTrue(poll1.getPollOptions().size() > 0);
    	
    	//we expect this one to fails
		externalLogicStubb.currentUserId = TestDataPreload.USER_NO_ACCEESS;
		try {
			Poll poll2 = pollListManager.getPollById(Long.valueOf(1));
			fail("should not be allowed to read this poll");
		} 
		catch (SecurityException e) {
			e.printStackTrace();
		}
    }
	
    
    public void testSavePoll() {
    	externalLogicStubb.currentUserId = TestDataPreload.USER_UPDATE;
		
    	Poll poll1 = new Poll();
		poll1.setCreationDate(new Date());
		poll1.setVoteOpen(new Date());
		poll1.setVoteClose(new Date());
		poll1.setDescription("this is some text");
		poll1.setText("something");
		poll1.setOwner(TestDataPreload.USER_UPDATE);
		
		poll1.setSiteId(TestDataPreload.LOCATION1_ID);
		
		
		//If this has a value something is wrong without POJO
		assertNull(poll1.getPollId());
		
		pollListManager.savePoll(poll1);
		
		//if this is null we have a problem
		assertNotNull(poll1.getPollId());
		
		Poll poll2 = pollListManager.getPollById(poll1.getPollId());
		assertNotNull(poll2);
		assertEquals(poll1.getPollText(), poll2.getPollText());
		
		//TODO add failure cases - null parameters
		
		//we should not be able to save empty polls
		
		//a user needs privileges to save the poll
		try {
			pollListManager.savePoll(null);
			fail();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		
		//a user needs privileges to save the poll
		try {
			Poll poll = new Poll();
			poll.setText("sdfgsdf");
			pollListManager.savePoll(poll);
			fail();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		externalLogicStubb.currentUserId = TestDataPreload.USER_NO_ACCEESS;
		try {
			pollListManager.savePoll(poll1);
			fail();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (SecurityException se) {
			se.printStackTrace();
		}
		
    }
	
    
    public void testDeletePoll() {
    	
    	externalLogicStubb.currentUserId = TestDataPreload.USER_UPDATE;
    	Poll poll1 = new Poll();
		poll1.setCreationDate(new Date());
		poll1.setVoteOpen(new Date());
		poll1.setVoteClose(new Date());
		poll1.setDescription("this is some text");
		poll1.setText("something");
		poll1.setOwner(TestDataPreload.USER_UPDATE);
		poll1.setSiteId(TestDataPreload.LOCATION1_ID);
		
		
		
		//we should not be able to delete a poll that hasn't been saved
		try {
			pollListManager.deletePoll(poll1);
			fail();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		pollListManager.savePoll(poll1);
		
		externalLogicStubb.currentUserId = TestDataPreload.USER_NO_ACCEESS;
		
    	try {
			pollListManager.deletePoll(poll1);
			fail();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		externalLogicStubb.currentUserId = TestDataPreload.USER_UPDATE;
    	try {
			pollListManager.deletePoll(poll1);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
    }
}

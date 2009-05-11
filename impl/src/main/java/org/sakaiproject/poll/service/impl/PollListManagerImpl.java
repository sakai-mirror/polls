/**********************************************************************************
 * $URL: $
 * $Id:  $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.poll.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.genericdao.api.search.Order;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.poll.dao.PollDao;
import org.sakaiproject.poll.logic.ExternalLogic;
import org.sakaiproject.poll.logic.PollListManager;
import org.sakaiproject.poll.model.Option;
import org.sakaiproject.poll.model.Poll;
import org.sakaiproject.poll.model.Vote;
import org.sakaiproject.poll.util.PollUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class PollListManagerImpl implements PollListManager {

    // use commons logger
    private static Log log = LogFactory.getLog(PollListManagerImpl.class);
    






    private PollDao dao;
    public void setDao(PollDao dao) {
		this.dao = dao;
	}
    
    private ExternalLogic externalLogic;    
    public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}

	public void init() {
        

        externalLogic.registerFunction(PERMISSION_VOTE);
        externalLogic.registerFunction(PERMISSION_ADD);
        externalLogic.registerFunction(PERMISSION_DELETE_OWN);
        externalLogic.registerFunction(PERMISSION_DELETE_ANY);
        externalLogic.registerFunction(PERMISSION_EDIT_ANY);
        externalLogic.registerFunction(PERMISSION_EDIT_OWN);
        log.info(this + " init()");

    }

    public void destroy() {

    }



    public List<Poll> findAllPollsForUserAndSitesAndPermission(String userId, String[] siteIds,
            String permissionConstant) {
        if (userId == null || permissionConstant == null) {
            throw new IllegalArgumentException("userId and permissionConstant must be set");
        }
        List<Poll> polls = null;
        // get all allowed sites for this user
        List<String> allowedSites = externalLogic.getSitesForUser(userId, permissionConstant);
        if (! allowedSites.isEmpty()) {
            if (siteIds != null) {
                if (siteIds.length > 0) {
                    // filter down to just the requested ones
                    for (int j = 0; j < allowedSites.size(); j++) {
                        String siteId = allowedSites.get(j);
                        boolean found = false;
                        for (int i = 0; i < siteIds.length; i++) {
                            if (siteId.equals(siteIds[i])) {
                                found = true;
                            }
                        }
                        if (!found) {
                            allowedSites.remove(j);
                        }
                    }
                } else {
                    // no sites to search so EXIT here
                    return new ArrayList<Poll>();
                }
            }
            String[] siteIdsToSearch = allowedSites.toArray(new String[allowedSites.size()]);
            Search search = new Search();
            if (siteIdsToSearch.length > 0) {
                search.addRestriction(new Restriction("siteId", siteIdsToSearch));
            }
            if (PollListManager.PERMISSION_VOTE.equals(permissionConstant)) {
                // limit to polls which are open
                Date now = new Date();
                search.addRestriction(new Restriction("voteOpen", now));
                search.addRestriction(new Restriction("voteClose", now));
            } else {
                // show all polls
            }
            search.addOrder(new Order("creationDate"));
            polls = dao.findBySearch(Poll.class, search);
        }
        if (polls == null) {
            polls = new ArrayList<Poll>();
        }
        return polls;
    }



    public boolean savePoll(Poll t) throws SecurityException, IllegalArgumentException {
        boolean newPoll = false;
        
        if (t == null || t.getText() == null || t.getSiteId() == null || t.getVoteOpen() == null|| t.getVoteClose() == null) {
        	throw new IllegalArgumentException("you must supply a question, siteId & open and close dates");
        }
        
        if (!externalLogic.isUserAdmin() && !externalLogic.isAllowedInLocation(PollListManager.PERMISSION_ADD, externalLogic.getSiteRefFromId(t.getSiteId()),
        			externalLogic.getCurrentuserReference())) {
        	throw new SecurityException();
        }
        
        if (t.getId() == null) {
            newPoll = true;
            t.setId(externalLogic.getNewUUID());
        }

        // we may need to truncate the description field
        if (t.getDetails().length() > 254)
            t.setDetails(t.getDetails().substring(0, 254));

       
        dao.save(t);

  
        log.debug(" Poll  " + t.toString() + "successfuly saved");
        if (newPoll)
        	externalLogic.postEvent("poll.add", "poll/site/"
                    + t.getSiteId() + "/poll/" + t.getId(), true);
        else
        	externalLogic.postEvent("poll.update", "poll/site/"
                    + t.getSiteId() + " /poll/" + t.getId(), true);

        return true;
    }

    public boolean deletePoll(Poll t) throws SecurityException, IllegalArgumentException {
    	if (t == null) {
    		throw new NullPointerException("Poll can't be null");
    	}
    	
    	if (t.getPollId() == null) {
    		throw new IllegalArgumentException("Poll id can't be null");
    	}
    	
        if (!pollCanDelete(t)) {
            throw new SecurityException("user:" + externalLogic.getCurrentuserReference() + " can't delete poll: " + t.getId());
        }
       
            dao.delete(t);
        
        log.info("Poll id " + t.getId() + " deleted");
        externalLogic.postEvent("poll.delete", "poll/site/"
                + t.getSiteId() + "/poll/" + t.getId(), true);
        return true;
    }

    public List<Poll> findAllPolls(String siteId) {
        
        Search search = new Search();
        search.addOrder(new Order("creationDate", false));
        search.addRestriction(new Restriction("siteId", siteId));
        
        List<Poll> polls = dao.findBySearch(Poll.class, search);
        return polls;
    }

    public Poll getPollById(Long pollId) throws SecurityException {
        
       return getPollById(pollId, true);
    }

    public Poll getPollById(Long pollId, boolean includeOptions) throws SecurityException {
    	
    	
    	Search search = new Search();
        search.addRestriction(new Restriction("pollId", pollId));
        Poll poll = dao.findOneBySearch(Poll.class, search);
        if (poll != null) {
            if (includeOptions) {
                List<Option> optionList = getOptionsForPoll(poll);
                poll.setOptions(optionList);
            }
        }
        
         if (poll == null)
        	 return null;
      //user needs at least site visit to read a poll
    	if (!externalLogic.isAllowedInLocation("site.visit", externalLogic.getSiteRefFromId(poll.getSiteId()), externalLogic.getCurrentuserReference()) && !externalLogic.isUserAdmin()) {
    		throw new SecurityException("user:" + externalLogic.getCurrentuserReference() + " can't read poll " + pollId);
    	}
        
        return poll;
    }


    // OPTIONS

    public List<Option> getOptionsForPoll(Poll poll) {
        return getOptionsForPoll(poll.getPollId());
    }

    public List<Option> getOptionsForPoll(Long pollId) {
        Poll poll;
		try {
			poll = getPollById(pollId, false);
		} catch (SecurityException e) {
			throw new SecurityException(e);
		}
        if (poll == null) {
            throw new IllegalArgumentException("Cannot get options for a poll ("+pollId+") that does not exist");
        }
        Search search = new Search();
        search.addRestriction(new Restriction("pollId", pollId));
        search.addOrder(new Order("optionId"));
        List<Option> optionList = dao.findBySearch(Option.class, search);
        return optionList;
    }

    public Poll getPollWithVotes(Long pollId) {
    	Search search = new Search();
        search.addRestriction(new Restriction("pollId", pollId));
        return dao.findOneBySearch(Poll.class, search);

    }

    public Option getOptionById(Long optionId) {
    	Search search = new Search();
        search.addRestriction(new Restriction("optionId", optionId));
        Option option = dao.findOneBySearch(Option.class, search);
        // if the id is null set it
        if (option.getUUId() == null) {
            option.setUUId( UUID.randomUUID().toString() );
            saveOption(option);
        }
        return option;
    }

    public void deleteOption(Option option) {
        
            dao.delete(option);
            log.info("Option id " + option.getId() + " deleted");
    }

    public boolean saveOption(Option t) {
        if (t.getUUId() == null 
                || t.getUUId().trim().length() == 0) {
            t.setUUId( UUID.randomUUID().toString() );
        }


        dao.save(t);
        log.info("Option  " + t.toString() + "successfuly saved");
        return true;
    }

    
    // INTERNAL

    private boolean pollCanDelete(Poll poll) {
        if (externalLogic.isUserAdmin() || this.isSiteOwner())
            return true;
        if (externalLogic.isAllowedInLocation(PERMISSION_DELETE_ANY, externalLogic.getCurrentLocationReference()))
            return true;

        if (externalLogic.isAllowedInLocation(PERMISSION_DELETE_OWN, externalLogic.getCurrentLocationReference())
        		&& poll.getOwner().equals(externalLogic.getCurrentuserReference()))
            return true;

        return false;
    }

    private boolean isSiteOwner() {
        if (externalLogic.isUserAdmin())
            return true;
        else if (externalLogic.isAllowedInLocation("site.upd", externalLogic.getCurrentLocationReference()))
        	return true;
        else
        	return false;
    }


    public Poll getPoll(String ref) {
        // we need to get the options here
        
        Search search = new Search();
        search.addRestriction(new Restriction("id", ref));
        Poll poll = dao.findOneBySearch(Poll.class, search);
        // if the id is null set it
        if (poll.getId() == null) {
            poll.setId(externalLogic.getNewUUID());
            savePoll(poll);
        }
        return poll;
    }
    
    
    
	public boolean isAllowedViewResults(Poll poll, String userId) {
		if (externalLogic.isUserAdmin())
			return true;

		if (poll.getDisplayResult().equals("open"))
			return true;

		if (poll.getOwner().equals(userId))
			return true;

		if (poll.getDisplayResult().equals("afterVoting")) {
			
			Search search = new Search();
	        search.addRestriction(new Restriction("pollId", poll.getPollId()));
	        search.addRestriction(new Restriction("userId", userId));
	        
	        List<Vote> votes = dao.findBySearch(Vote.class, search);		
	        //System.out.println("got " + pollCollection.size() + "votes for this poll");
	        if (votes.size() > 0)
	        	return true;
		}

		if ((poll.getDisplayResult().equals("afterClosing") || poll.getDisplayResult().equals("afterVoting") )&& poll.getVoteClose().before(new Date()))
			return true;

		//the owner can view the results
		if(poll.getOwner().equals(userId))
			return true;

		return false;
	}
    

}

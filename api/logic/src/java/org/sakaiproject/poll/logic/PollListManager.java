/**********************************************************************************
 * $URL: $
 * $Id:  $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2007, 2008, 2009 The Sakai Foundation
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

package org.sakaiproject.poll.logic;

import java.util.List;

import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.poll.model.Option;
import org.sakaiproject.poll.model.Poll;

/**
 * This is the interface for the Manager for our poll tool, 
 * it handles the data access functionality of the tool, we currently
 * have 2 implementations (memory and hibernate)
 * @author DH
 *
 */
public interface PollListManager extends EntityProducer {

//  the permissions

    public static final String PERMISSION_VOTE = "poll.vote";
    public static final String PERMISSION_ADD = "poll.add";
    public static final String PERMISSION_DELETE_OWN = "poll.deleteOwn";
    public static final String PERMISSION_DELETE_ANY = "poll.deleteAny";
    public static final String PERMISSION_EDIT_ANY = "poll.editAny";
    public static final String PERMISSION_EDIT_OWN = "poll.editOwn";

    public static final String REF_POLL_TYPE ="poll";
    /**
     *  Save a poll
     * @param t - the poll object to save
     * @return - true for success, false if failure
     */
    public boolean savePoll(Poll t);

    /**
     * Delete a poll
     * @param t - the poll object to remove
     * @return - true for success, false if failure
     */
    public boolean deletePoll(Poll t) throws PermissionException;

    public boolean saveOption(Option t);

    /**
     * Gets all the task objects for the site
     * @param siteId - the siteId of the site
     * @return - a collection of task objects (empty collection if none found)
     */
    @SuppressWarnings("unchecked")
    public List findAllPolls(String siteId);

    /**
     * Get all the polls for a user in a set of sites (can be one) given the permission,
     * will return only polls that can be voted on if the permission is {@link #PERMISSION_VOTE}
     * 
     * @param userId a sakai internal user id (not eid)
     * @param siteIds an array of site ids (can be null or empty to get the polls for all without security check)
     * @param permissionConstant either the {@link #PERMISSION_VOTE} (for all polls a user can vote on) or 
     * {@link #PERMISSION_ADD} for all the polls the user can control
     * @return the list of all polls this user can access
     */
    public List<Poll> findAllPollsForUserAndSitesAndPermission(String userId, String[] siteIds, String permissionConstant);

    /**
     * Retrieve a specific poll
     * @param pollId
     * @return a single poll object
     */
    public Poll getPollById(Long pollId);

    /**
     * Retrieve a specific poll
     * @param pollId
     * @param includeOptions if true then options included, else no options (poll only)
     * @return a single poll object
     */
    public Poll getPollById(Long pollId, boolean includeOptions);

    /**
     * Get a specific poll with all its votes
     * @param pollId
     * @return a poll object
     */
    public Poll getPollWithVotes(Long pollId);

    /**
     *  get a poll by its Entity  Reference  
     */
    public Poll getPoll(String ref);

    /**
     *  Get a specific option by its id
     */
    public Option getOptionById(Long optionId);

    public void deleteOption(Option option);

    public List<Option> getOptionsForPoll(Poll poll);

    /**
     * Get all options for a specific poll
     * @param pollId the id for a poll
     * @return all options OR empty if there are no options for this poll
     * @throws IllegalArgumentException if the pollId is invalid
     */
    public List<Option> getOptionsForPoll(Long pollId);
    
    /**
     *  Can the this user view the results for this poll?
     * @param poll
     * @param userId
     * @return true if the user can view this poll
     */
    
    public boolean isAllowedViewResults(Poll poll, String userId);
    
    /**
     * Is this poll public?
     * @param poll
     * @return
     */
    public boolean isPollPublic(Poll poll);

}

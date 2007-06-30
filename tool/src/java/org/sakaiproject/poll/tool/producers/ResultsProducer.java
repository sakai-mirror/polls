/**********************************************************************************
 * $URL: $
 * $Id:  $
 ***********************************************************************************
 *
 * Copyright (c) 2006,2007 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.poll.tool.producers;

import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.poll.model.Option;
import org.sakaiproject.poll.logic.PollListManager;
import org.sakaiproject.poll.logic.PollVoteManager;
import org.sakaiproject.poll.model.OptionImpl;
import org.sakaiproject.poll.model.Poll;
import org.sakaiproject.poll.model.Vote;
import org.sakaiproject.user.api.UserDirectoryService;

import uk.org.ponder.beanutil.entity.EntityID;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.EntityCentredViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.localeutil.LocaleGetter;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.util.NumberFormatter;


import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResultsProducer implements ViewComponentProducer,NavigationCaseReporter,ViewParamsReporter {

	public static final String VIEW_ID = "voteResults";
	
	  private UserDirectoryService userDirectoryService;
	  private PollListManager pollListManager;
	  private PollVoteManager pollVoteManager;
	  private ToolManager toolManager;
	  private MessageLocator messageLocator;
	  private LocaleGetter localegetter;
	  
	  
	  private static Log m_log  = LogFactory.getLog(ResultsProducer.class);
	  
	public String getViewID() {
		// TODO Auto-generated method stub
		return VIEW_ID;
	}
	
	


	  
	public void setMessageLocator(MessageLocator messageLocator) {
		  
	    this.messageLocator = messageLocator;
	  }

	  public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
	    this.userDirectoryService = userDirectoryService;
	  }

	  public void setPollListManager(PollListManager pollListManager) {
	    this.pollListManager = pollListManager;
	  }

	  public void setToolManager(ToolManager toolManager) {
	    this.toolManager = toolManager;
	  }

	  public void setLocaleGetter(LocaleGetter localegetter) {
	    this.localegetter = localegetter;
	  }
		public void setPollVoteManager(PollVoteManager pvm){
			this.pollVoteManager = pvm;
		}
		
	public void fillComponents(UIContainer tofill, ViewParameters viewparams,
			ComponentChecker checker) {
		
		EntityCentredViewParameters ecvp = (EntityCentredViewParameters) viewparams;
		 
		 //m_log.info(ecvp.toPathInfo());
		 
		 //m_log.info(ecvp.getELPath());
		 
		 //hack but this needs to work
		 String strId = ecvp.getELPath().substring(ecvp.getELPath().indexOf(".") + 1);
		 m_log.debug("got id of " + strId);
		 Poll poll = pollListManager.getPollById(new Long(strId));
		 
		 
		 //get the number of votes
		 int voters = pollVoteManager.getDisctinctVotersForPoll(poll);
		 //Object[] args = new Object[] { new Integer(voters).toString()};
		 if (poll.getMaxOptions()>1)
			 UIOutput.make(tofill,"poll-size",messageLocator.getMessage("results_poll_size",new Integer(voters).toString()));
		 
		 m_log.debug(voters + " have voted on this poll");
		 
		 UIOutput.make(tofill,"question",poll.getText());
		 m_log.debug("got poll " + poll.getText());
		 List pollOptions = poll.getPollOptions();
		 
		 m_log.debug("got a list of " + pollOptions.size() + " options");
		 //appeng an option for no votes
		 if (poll.getMinOptions()==0) {
			 Option noVote = new OptionImpl(new Long(0));
			 noVote.setOptionText(messageLocator.getMessage("result_novote"));
			 noVote.setPollId(poll.getPollId());
			 pollOptions.add(noVote);
		}
		 
		 List votes = pollVoteManager.getAllVotesForPoll(poll);
		 int totalVotes= votes.size();
		 m_log.debug("got " + totalVotes + " votes");
		 List collation = new ArrayList();
		 
		 for (int i=0; i <pollOptions.size(); i++ ) {
			 CollatedVote collatedVote = new CollatedVote();
			 Option option = (Option) pollOptions.get(i);
			 collatedVote.setoptionId(option.getId());
			 collatedVote.setOptionText(option.getOptionText());
			 for (int q=0; q <votes.size(); q++ ) {
				 Vote vote = (Vote)votes.get(q);
				 if (vote.getPollOption().equals(option.getId())){
					 m_log.debug("got a vote for option " + option.getId());
					 collatedVote.incrementVotes();
					 
				 }
				 
			 }
			 collation.add(collatedVote);
			 
		 }
		 
		 UIOutput.make(tofill,"answers-title",messageLocator.getMessage("results_answers_title"));
		 
		 for (int i=0; i <collation.size(); i++ ) {
			 CollatedVote cv = (CollatedVote)collation.get(i);
			 UIBranchContainer resultRow = UIBranchContainer.make(tofill,"answer-row:",cv.getoptionId().toString());
			 UIOutput.make(resultRow,"answer-option",cv.getOptionText());
			 UIOutput.make(resultRow,"answer-numVotes",new Long(cv.getVotes()).toString());
			 m_log.debug("about to do the calc: (" + cv.getVotes()+"/"+ totalVotes +")*100");
			 double percent = (double)0;
			 if (totalVotes>0)
				 percent = ((double)cv.getVotes()/(double)totalVotes)*(double)100;
			 else 
				 percent = (double) 0;
			 m_log.debug("result is "+ percent);
			 NumberFormatter nf = new NumberFormatter();
			 UIOutput.make(resultRow,"answer-percVotes", nf.format(percent) + "%");
			 
		 }
		UIOutput.make(tofill,"votes-total",new Integer(totalVotes).toString());
		 //the cancel button
		 UIForm form = UIForm.make(tofill,"actform");
		 UICommand.make(form,"cancel",messageLocator.getMessage("results_cancel"),"#{pollToolBean.cancel}"); 
		 

	}
	
	  public List reportNavigationCases() {
		    List togo = new ArrayList(); // Always navigate back to this view.
		    togo.add(new NavigationCase(null, new SimpleViewParameters(VIEW_ID)));
		    togo.add(new NavigationCase("cancel", new SimpleViewParameters(PollToolProducer.VIEW_ID)));
		    return togo;
		  }	
	  public ViewParameters getViewParameters() {
		    return new EntityCentredViewParameters(VIEW_ID, new EntityID("Poll", null));

		  }
	  
	  
	  
	  private class CollatedVote {
		  private Long optionId ;
		  private String optionText;
		  private int votes;
		  
		  public CollatedVote() {
			  this.votes=0;
		  }
		 public void setoptionId(Long val){
			 this.optionId = val;
		 }
		  
		 public Long getoptionId(){
			 return this.optionId;
		 }
		 
		 public void setOptionText(String t){
			 this.optionText = t;
		 }
		 public String getOptionText(){
			 return this.optionText;
		 }
		 
		 public void setVotes(int i){
			 this.votes = i;
		 }
		 public int getVotes(){
			 return this.votes;
		 }
		 public void incrementVotes(){
			 this.votes++;
		 }
		 
	  }
}

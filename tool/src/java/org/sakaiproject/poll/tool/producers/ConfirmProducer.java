/**********************************************************************************
 * $URL: $
 * $Id:  $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2007 Sakai Foundation
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

package org.sakaiproject.poll.tool.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UICommand;

import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

import org.sakaiproject.poll.model.Poll;
import org.sakaiproject.poll.tool.params.PollViewParameters;
import org.sakaiproject.poll.tool.params.VoteBean;
import org.sakaiproject.poll.tool.params.VoteCollectionViewParameters;

import uk.org.ponder.rsf.flow.ARIResult;
import uk.org.ponder.rsf.flow.ActionResultInterceptor;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;


import java.util.List;
import java.util.ArrayList;


public class ConfirmProducer implements ViewComponentProducer, ViewParamsReporter {

	public static final String VIEW_ID = "voteThanks";
	private static Log log = LogFactory.getLog(PollVoteProducer.class);
	private VoteBean voteBean;
	
	
	private MessageLocator messageLocator;
	
	
	public String getViewID() {
		// TODO Auto-generated method stub
		return VIEW_ID;
	}
	
	  public void setVoteBean(VoteBean vb){
		  this.voteBean = vb;
	  }

		
	  public void setMessageLocator(MessageLocator messageLocator) {
			  
		  this.messageLocator = messageLocator;
	  }


	  
	public void fillComponents(UIContainer tofill, ViewParameters viewparams,
			ComponentChecker arg2) {
		// TODO Auto-generated method stub
		
		VoteCollectionViewParameters params = (VoteCollectionViewParameters) viewparams;
		
		
		String voteId; 
		if (params.id != null)
			voteId = params.id;
		else 
			voteId="VoteId is missing!";
		
		UIOutput.make(tofill,"confirm-msg",messageLocator.getMessage("thanks_msg"));
		UIOutput.make(tofill,"confirm-ref-msg",messageLocator.getMessage("thanks_ref"));
		UIOutput.make(tofill,"ref-number",voteId);
		UIForm form = UIForm.make(tofill,"back","");
		UICommand.make(form,"cancel",messageLocator.getMessage("thanks_done"),"#{pollToolBean.cancel}");
	}

	


	public ViewParameters getViewParameters() {
		// TODO Auto-generated method stub
		return new VoteCollectionViewParameters(); 
	}


	
}

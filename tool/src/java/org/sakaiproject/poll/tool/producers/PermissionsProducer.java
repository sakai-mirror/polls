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
import org.sakaiproject.poll.logic.PollListManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.cover.FunctionManager;
import uk.org.ponder.rsf.components.UIBoundBoolean; 

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIELBinding;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.localeutil.LocaleGetter;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.site.api.Site;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PermissionsProducer implements ViewComponentProducer,NavigationCaseReporter {

	public static final String VIEW_ID = "votePermissions";
	
	  private UserDirectoryService userDirectoryService;
	  private PollListManager pollListManager;
	  private ToolManager toolManager;
	  private MessageLocator messageLocator;
	  private LocaleGetter localegetter;
	  
	  private static final String PERMISSION_PREFIX ="poll";
	  private static Log m_log = LogFactory.getLog(PermissionsProducer.class);
	  
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
	  
		Map perms = null;
		
		public void setRoleperms(Map perms)
		{
			this.perms = perms;
		}

	public void fillComponents(UIContainer tofill, ViewParameters arg1,
			ComponentChecker arg2) {
		
		
		try {
			
			//populate the site name ect
			UIOutput.make(tofill,"permissions-title",messageLocator.getMessage("permissions_title"));
			UIOutput.make(tofill,"permissions-instruction",messageLocator.getMessage("permissions_instruction"));
			
			Site site = SiteService.getSite(toolManager.getCurrentPlacement().getContext());
			UIOutput.make(tofill,"site-name",site.getTitle());
			
		//we need a list of permissions	
		
		String[] perms = new String[]{
				PollListManager.PERMISSION_VOTE,
				PollListManager.PERMISSION_ADD,
				PollListManager.PERMISSION_DELETE_OWN,
				PollListManager.PERMISSION_DELETE_ANY,
				PollListManager.PERMISSION_EDIT_OWN,
				PollListManager.PERMISSION_EDIT_ANY
		};
		for (int i =0; i < perms.length;i++){
			String thisPerm = (String)perms[i];
			thisPerm = thisPerm.substring(thisPerm.indexOf('.') + 1);
			UIBranchContainer b = UIBranchContainer.make(tofill,"head-row:", new Integer(i).toString());
			UIOutput.make(b,"perm-name",thisPerm);
		}
		
		AuthzGroup group = AuthzGroupService.getAuthzGroup("/site/" + toolManager.getCurrentPlacement().getContext());
		Set roles = group.getRoles();
		Iterator i = roles.iterator();
		UIForm form = UIForm.make(tofill,"perm-form");
		UIOutput.make(form,"permissions-role",messageLocator.getMessage("permissions_role"));
			while ( i.hasNext()){
				Role role = (Role)i.next();
				m_log.debug("got role " + role.getId());
				UIBranchContainer row = UIBranchContainer.make(form,"permission-row:",role.getId());
				UIOutput.make(row,"role",role.getId());
				//now iterate through the permissions
				String prefix = "#{roleperms." + role.getId();
				for (int ip =0; ip < perms.length;ip++){
					String thisPerm = (String)perms[ip];
					thisPerm = thisPerm.substring(thisPerm.indexOf('.') + 1);
					UIBranchContainer col = UIBranchContainer.make(row,"box-row:", thisPerm);
					m_log.debug("drawing box for "+ thisPerm + " for role " + role.getId());
					//new Boolean(role.isAllowed((String)perms[ip]))
					UIBoundBoolean.make(col, "perm-box", prefix +"."+ thisPerm + "}", new Boolean(role.isAllowed((String)perms[ip])));
					 									  
				}
			}
			UICommand sub = UICommand.make(form, "submit",messageLocator.getMessage("new_poll_submit"), "#{permissionAction.setPermissions}");
				sub.parameters.add(new UIELBinding("#{permissionAction.submissionStatus}", "submit"));
		   UICommand cancel = UICommand.make(form, "cancel",messageLocator.getMessage("vote_cancel"),"#{permissionAction.cancel}");
		   cancel.parameters.add(new UIELBinding("#{permissionAction.submissionStatus}", "cancel"));
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	  public List reportNavigationCases() {
		    List togo = new ArrayList(); // Always navigate back to this view.
		    togo.add(new NavigationCase(null, new SimpleViewParameters(VIEW_ID)));
		    togo.add(new NavigationCase("Success", new SimpleViewParameters(PollToolProducer.VIEW_ID)));
		    togo.add(new NavigationCase("cancel", new SimpleViewParameters(PollToolProducer.VIEW_ID)));
		    return togo;
		  }	

}

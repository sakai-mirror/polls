/**********************************************************************************
 * $URL$
 * $Id$
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

package org.sakaiproject.poll.tool.params;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.poll.api.model.PollRolePerms;



public class RoleFactory {

	private static Log m_log = LogFactory.getLog(RoleFactory.class);
	public Map<String, PollRolePerms> getRoles()
	{
		m_log.debug("Getting permRoles");
		Map<String, PollRolePerms> perms = new HashMap<String, PollRolePerms>();
		try {
			//TODO we need a better way to do this
			/*
			AuthzGroup group = AuthzGroupService.getAuthzGroup("/site/" + ToolManager.getCurrentPlacement().getContext());
			Set<Role> roles = group.getRoles();
			Iterator<Role> i = roles.iterator();
			
			while (i.hasNext())
			{
				Role role = (Role)i.next();
				String name = role.getId();
				m_log.debug("Adding element for " + name); 
				perms.put(name, new PollRolePerms(name, 
						role.isAllowed(PollListManager.PERMISSION_VOTE),
						role.isAllowed(PollListManager.PERMISSION_ADD),
						role.isAllowed(PollListManager.PERMISSION_DELETE_OWN),
						role.isAllowed(PollListManager.PERMISSION_DELETE_ANY),
						role.isAllowed(PollListManager.PERMISSION_EDIT_OWN),
						role.isAllowed(PollListManager.PERMISSION_EDIT_ANY)
						));
			}
			*/
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return perms;
	}
}
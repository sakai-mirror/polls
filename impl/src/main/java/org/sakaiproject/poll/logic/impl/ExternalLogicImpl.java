/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2007 The Sakai Foundation
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

package org.sakaiproject.poll.logic.impl;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.poll.logic.ExternalLogic;
import org.sakaiproject.poll.model.PollRolePerms;



public class ExternalLogicImpl implements ExternalLogic {

	 private static Log log = LogFactory.getLog(ExternalLogicImpl.class);

	public String getCurrentLocationId() {
		// TODO Auto-generated method stub
		return "something";
	}

	public String getCurrentLocationReference() {
		// TODO Auto-generated method stub
		return "/site/something";
	}

	public String getCurrentUserId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCurrentuserReference() {
		// TODO Auto-generated method stub
		return null;
	}

	public TimeZone getLocalTimeZone() {
		// TODO Auto-generated method stub
		return TimeZone.getDefault();
	}

	public String getNewUUID() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getRoleIdsInRealm(String realmId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, PollRolePerms> getRoles(String locationReference) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteRefFromId(String siteId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteTile(String locationReference) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getSitesForUser(String userId, String permission) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAllowedInLocation(String permission,
			String locationReference) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isAllowedInLocation(String permission,
			String locationReference, String userRefence) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isRoleAllowedInRealm(String roleId, String realmId,
			String permission) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isUserAdmin(String userId) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isUserAdmin() {
		// TODO Auto-generated method stub
		return true;
	}

	public void postEvent(String eventId, String reference, boolean modify) {
		// TODO Auto-generated method stub
		
	}

	public void registerFunction(String function) {
		// TODO Auto-generated method stub
		
	}

	public void setToolPermissions(Map<String, PollRolePerms> permMap,
			String locationReference) throws SecurityException,
			IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	public String getCurrentSessionIP() {
		// TODO Auto-generated method stub
		return null;
	}

	public String escapeFormatedText(String text) {
		// TODO Auto-generated method stub
		return null;
	}
	


	
}

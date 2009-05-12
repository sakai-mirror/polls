/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/polls/branches/SAK-16313/impl/src/main/java/org/sakaiproject/poll/dao/PollDao.java $
 * $Id: PollDao.java 62188 2009-05-11 14:18:12Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation
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

package org.sakaiproject.poll.impl.dao;

import org.sakaiproject.genericdao.api.GeneralGenericDao;
import org.sakaiproject.poll.model.Poll;

public interface PollDao extends GeneralGenericDao {
	
	/**
	 * Get the number of distinct voters on a poll
	 * @param poll
	 * @return
	 */
	 public int getDisctinctVotersForPoll(Poll poll);

}

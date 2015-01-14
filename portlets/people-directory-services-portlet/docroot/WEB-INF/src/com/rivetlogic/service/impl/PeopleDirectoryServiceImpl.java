/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.rivetlogic.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserConstants;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.comparator.UserScreenNameComparator;
import com.rivetlogic.model.PeopleDirectoryResult;
import com.rivetlogic.model.UserData;
import com.rivetlogic.service.base.PeopleDirectoryServiceBaseImpl;

/**
 * The implementation of the people directory remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.rivetlogic.service.PeopleDirectoryService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have security checks based on the propagated JAAS credentials because this service can be accessed remotely.
 * </p>
 *
 * @author Alejandro Soto
 * @see com.rivetlogic.service.base.PeopleDirectoryServiceBaseImpl
 * @see com.rivetlogic.service.PeopleDirectoryServiceUtil
 */
public class PeopleDirectoryServiceImpl extends PeopleDirectoryServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.rivetlogic.service.PeopleDirectoryServiceUtil} to access the people directory remote service.
	 */
    
    /** The Constant _log. */
    private static final Log _log = LogFactoryUtil.getLog(PeopleDirectoryServiceImpl.class);
    
    /**
     * Search for all the portal users
     * 
     * @param keywords If keyword is empty string it will search all the users 
     * @param start Page beginning
     * @param end Page end
     * @return
     * 
     * @throws PortalException
     * @throws SystemException
     */
    public PeopleDirectoryResult search(String keywords, int start, int end) throws PortalException, SystemException {
        long globalGroupId = PortalUtil.getDefaultCompanyId();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        keywords = (keywords.trim().length() > 0) ? keywords : null;
        
        List<User> resultUsers = UserLocalServiceUtil.search(globalGroupId, keywords, 0, params, start, end,
                new UserScreenNameComparator(Boolean.TRUE));
        List<UserData> userList = new ArrayList<UserData>();
        
        Integer searchCount = UserLocalServiceUtil.searchCount(globalGroupId, keywords, 0, params);
        
        for (User user : resultUsers) {
            userList.add(processUserInformation(user));
        }
        
        PeopleDirectoryResult p = new PeopleDirectoryResult();
        p.setTotal((int) searchCount);
        p.setUsers(userList);
        return p;
    }
    
    /**
     * Retrieves and processes user information
     * 
     * @param user
     * @return
     * @throws PortalException
     * @throws SystemException
     */
    private UserData processUserInformation(User user) throws PortalException, SystemException {
        UserData curUser = new UserData();
        curUser.setUserId(user.getUserId());
        curUser.setFullName(user.getFullName());
        curUser.setEmailAddress(user.getEmailAddress());
        curUser.setUserPhone(getPhoneField(user));
        curUser.setSkypeName(user.getContact().getSkypeSn());
        curUser.setJobTitle(user.getJobTitle());
        curUser.setScreenName(user.getScreenName());
        curUser.setBirthDate(user.getBirthday());
        curUser.setCity(getCityField(user));
        curUser.setPortraitUrl(UserConstants.getPortraitURL("/image", user.isMale(), user.getPortraitId()));
        return curUser;
    }
    
    /**
     * Retrieves only one phone number
     * 
     * @param user
     * @return
     */
    private String getPhoneField(User user) {
        String phoneStr = StringPool.BLANK;
        try {
            if (user.getPhones().size() > 0) {
                for (com.liferay.portal.model.Phone phone : user.getPhones()) {
                    if (phone.isPrimary()) {
                        phoneStr = phone.getNumber();
                        break;
                    }
                }
            }
        } catch (SystemException e) {
            _log.error("Error while looking for user phone field", e);
        }
        return phoneStr;
    }
    
    /**
     * Retrieves city field
     * 
     * @param user
     * @return
     */
    private String getCityField(User user) {
        String cityStr = StringPool.BLANK;
        try {
            if (user.getAddresses().size() > 0) {
                for (com.liferay.portal.model.Address address : user.getAddresses()) {
                    if (address.isPrimary()) {
                        cityStr = address.getCity();
                        break;
                    }
                }
            }
        } catch (SystemException e) {
            _log.error("Error while looking for user city field", e);
        }
        return cityStr;
    }

}
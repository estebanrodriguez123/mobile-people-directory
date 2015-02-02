/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.rivetlogic.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserConstants;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.comparator.UserScreenNameComparator;
import com.rivetlogic.service.base.PeopleDirectoryServiceBaseImpl;
import com.rivetlogic.service.data.PeopleDirectoryResult;
import com.rivetlogic.service.data.UserData;

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
 * @author Brian Wing Shun Chan
 * @see com.rivetlogic.service.base.PeopleDirectoryServiceBaseImpl
 * @see com.rivetlogic.service.PeopleDirectoryServiceUtil
 */
public class PeopleDirectoryServiceImpl extends PeopleDirectoryServiceBaseImpl {
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
     * @throws com.liferay.portal.kernel.exception.SystemException 
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
     * Retrieves all the system users
     * 
     * @return
     * @throws SystemException
     * @throws PortalException
     */
    public PeopleDirectoryResult fetchAll() throws SystemException, PortalException {
        List<UserData> resultUsers = new ArrayList<UserData>();
        int total = UserLocalServiceUtil.getUsersCount();
        List<User> users = UserLocalServiceUtil.getUsers(0, total);
        
        for (User user : users) {
            resultUsers.add(processUserInformation(user));
        }
        
        PeopleDirectoryResult p = new PeopleDirectoryResult();
        p.setTotal((int) total);
        p.setUsers(resultUsers);
        return p;
    }
    
    /**
     * Retrieves all the system users from the given date
     * 
     * @return
     * @throws SystemException
     * @throws PortalException
     */
    
    public PeopleDirectoryResult usersFetchByDate(Timestamp modifiedDate) throws SystemException, PortalException {
    	DynamicQuery userQuery = DynamicQueryFactoryUtil.forClass(
    			User.class, PortalClassLoaderUtil.getClassLoader());
    	
    	List<UserData> resultUsers = new ArrayList<UserData>();
    	
    	Date date = new Date(modifiedDate.getTime());
    	
    	Criterion criterion = null;
    	criterion = RestrictionsFactoryUtil.gt("modifiedDate", date);
    	userQuery.add(criterion);
    	
    	List<User> users = UserLocalServiceUtil.dynamicQuery(userQuery);
    	
    	for (User user : users) {
            resultUsers.add(processUserInformation(user));
        }
    	
    	PeopleDirectoryResult usersPD = new PeopleDirectoryResult();
    	
    	usersPD.setTotal(users.size());
    	usersPD.setUsers(resultUsers);
        return usersPD;
    }
    
    /**
     * Get the count of active users
     * 
     * @return
     * @throws PortalException
     * @throws SystemException
     */
    public int getActiveUsersCount() throws PortalException, SystemException {
        long globalGroupId = PortalUtil.getDefaultCompanyId();
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

        return UserLocalServiceUtil.searchCount(globalGroupId, null, 0, params);
    }
    
    /**
     * Retrieves and processes user information
     * 
     * @param user
     * @return
     * @throws PortalException
     * @throws com.liferay.portal.kernel.exception.SystemException 
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
        curUser.setModifiedDate(user.getModifiedDate());
        curUser.setMale(user.isMale());
        curUser.setDeleted(user.getStatus() == 5 ? true : false);
        
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
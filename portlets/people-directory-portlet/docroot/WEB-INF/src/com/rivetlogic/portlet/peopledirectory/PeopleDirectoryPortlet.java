/**
 * Copyright (C) 2005-2014 Rivet Logic Corporation.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package com.rivetlogic.portlet.peopledirectory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.comparator.UserScreenNameComparator;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.rivetlogic.util.Constants;
import com.rivetlogic.util.PeopleDirectoryUtil;
import com.rivetlogic.util.PropsValues;
import com.rivetlogic.util.SkillsUtil;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * The Class PeopleDirectoryPortlet.
 * 
 * @author parasjain
 * @author juancarrillo
 */
public class PeopleDirectoryPortlet extends MVCPortlet {
    
    private static final Log _log = LogFactoryUtil.getLog(PeopleDirectoryPortlet.class);
    
    /*
     * (non-Javadoc)
     * 
     * @see com.liferay.util.bridges.mvc.MVCPortlet#serveResource(javax.portlet.
     * ResourceRequest, javax.portlet.ResourceResponse)
     */
    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) 
    	throws IOException, PortletException {
    	
        String cmd = ParamUtil.getString(request, Constants.COMMAND);
        try {
            if (cmd.equalsIgnoreCase(Constants.COMMAND_SEARCH)) {
                performKeywordSearch(request, response);
            } else if (cmd.equalsIgnoreCase(Constants.COMMAND_SHOW_COMPLETE_PROFILE)) {
                performCompleteProfileSearch(request, response);
            } else if(cmd.equalsIgnoreCase(Constants.COMMAND_SEARCH_SKILLS)) {
                performSkillSearch(request, response);
            } else if(cmd.equalsIgnoreCase(Constants.COMMAND_SKILLS_SUGGESTION)) {
                JSONArray array = SkillsUtil.searchSuggestions(ParamUtil.getString(request, Constants.PARAMETER_SKILL_SUGGESTION));
                writeJSON(request, response, array);
            }
        } catch (SystemException e) {
            _log.error(Constants.LOG_SERVER_RESOURCE_ERROR, e);
        } catch (PortalException e) {
            _log.error(Constants.LOG_SERVER_RESOURCE_ERROR, e);
        }
    }
    
    /**
     * Perform keyword search.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @throws SystemException
     *             the system exception
     * @throws PortalException
     *             the portal exception
     * @throws IOException
     * 				the io exception 
     */
    private void performKeywordSearch(ResourceRequest request, ResourceResponse response) 
    	throws SystemException, PortalException, IOException {
    	
        long companyId = PortalUtil.getCompanyId(request);
        String keywords = ParamUtil.getString(request, Constants.PARAMETER_KEYWORDS);
        int start = ParamUtil.getInteger(request, Constants.PARAMETER_START);
        int end = ParamUtil.getInteger(request, Constants.PARAMETER_END);
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        
        try {
            List<User> resultUsers = UserLocalServiceUtil.search(companyId, keywords, 0, params, start, end,
                    new UserScreenNameComparator(Boolean.TRUE));
            Integer searchCount = UserLocalServiceUtil.searchCount(companyId, keywords, 0, params);
            JSONArray usersArray = JSONFactoryUtil.createJSONArray();
            
            for (User user : resultUsers) {
                usersArray.put(buildJsonObject(user, request));
            }
            
            JSONObject resultsObject = JSONFactoryUtil.createJSONObject();
            resultsObject.put(Constants.JSON_RESULTS_ARRAY, usersArray);
            resultsObject.put(Constants.JSON_RESULTS_SEARCH_COUNT, searchCount);
            writeJSON(request, response, resultsObject);
            
        } catch (SystemException e) {
            _log.error(Constants.LOG_KEYWORD_SEARCH_ERROR, e);
        }
    }
    
    private JSONObject buildJsonObject(User user, ResourceRequest request) throws SystemException, PortalException {
        PortletPreferences preferences = request.getPreferences();
        boolean skillsEnabled = GetterUtil.getBoolean(preferences.getValue(Constants.SKILLS_INTEGRATION, PropsValues.SKILLS_INTEGRATION));
        
        JSONObject jsonUser = JSONFactoryUtil.createJSONObject();
        jsonUser.put(Constants.JSON_USER_ID, user.getUserId());
        jsonUser.put(Constants.JSON_USER_FULL_NAME, user.getFullName());
        jsonUser.put(Constants.JSON_USER_EMAIL_ADDRESS, user.getDisplayEmailAddress());
        jsonUser.put(Constants.JSON_USER_PORTRAIT_URL,
                user.getPortraitURL((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY)));
        jsonUser.put(Constants.JSON_USER_PHONE, PeopleDirectoryUtil.getPhoneField(user));
        jsonUser.put(Constants.JSON_USER_SKYPE_NAME, user.getContact().getSkypeSn());
        
        if(skillsEnabled) {
            JSONArray jsonSkills = getSkillsArray(user);
            jsonUser.put(Constants.JSON_SKILLS_ARRAY, jsonSkills);
        }
        return jsonUser;
    }
    
    /**
     * Perform complete profile search.
     * 
     * @param request
     *            the portlet request
     * @param response
     *            the portlet response
     * @throws IOException 
     */
    private void performCompleteProfileSearch(ResourceRequest request, ResourceResponse response) 
    	throws IOException {
    	
        long userId = ParamUtil.getLong(request, Constants.PARAMETER_USER_ID);
        PortletPreferences preferences = request.getPreferences();
        boolean skillsEnabled = GetterUtil.getBoolean(preferences.getValue(Constants.SKILLS_INTEGRATION, PropsValues.SKILLS_INTEGRATION));
        
        try {
            User user = UserLocalServiceUtil.getUser(userId);
            JSONObject jsonUser = JSONFactoryUtil.createJSONObject();
            jsonUser.put(Constants.JSON_USER_JOB_TITLE, user.getJobTitle());
            jsonUser.put(Constants.JSON_USER_SCREEN_NAME, user.getScreenName());
            jsonUser.put(Constants.JSON_USER_DOB, (new SimpleDateFormat(Constants.DOB_FORMAT)).format(user.getBirthday()));
            jsonUser.put(Constants.JSON_USER_CITY, PeopleDirectoryUtil.getCityField(user));
            jsonUser.put(Constants.JSON_USER_PHONE, PeopleDirectoryUtil.getPhoneField(user));
            jsonUser.put(Constants.JSON_USER_SKYPE_NAME, user.getContact().getSkypeSn());
            
            if(skillsEnabled) {
                JSONArray jsonSkills = getSkillsArray(user);
                jsonUser.put(Constants.JSON_SKILLS_ARRAY, jsonSkills);
            }
            
            writeJSON(request, response, jsonUser);
            
        } catch (PortalException e) {
            _log.error(Constants.LOG_COMPLETE_PROFILE_SEARCH_ERROR, e);
        } catch (SystemException e) {
            _log.error(Constants.LOG_COMPLETE_PROFILE_SEARCH_ERROR, e);
        }
    }
    
    private JSONArray getSkillsArray(User user) {
        JSONArray array = JSONFactoryUtil.createJSONArray();
            try {
            Serializable value = user.getExpandoBridge().getAttribute("skills");
            if(value != null) {
                String[] skills = value.toString().split(",");
                for(String skill : skills) {
                    array.put(skill);
                }
            }
        } catch(Exception e) {
            _log.warn("Error getting user skills");
        }
        return array;
    }
    
    /**
     * @param request
     * @param response
     * @throws SystemException
     * @throws PortalException
     * @throws IOException
     */
    private void performSkillSearch(ResourceRequest request, ResourceResponse response) throws SystemException, PortalException, IOException {

        String skills = request.getParameter(Constants.PARAMETER_SKILLS);
        int start = ParamUtil.getInteger(request, Constants.PARAMETER_START);
        int end = ParamUtil.getInteger(request, Constants.PARAMETER_END);
        JSONArray usersArray = JSONFactoryUtil.createJSONArray();
        
        long searchCount = SkillsUtil.countUsersBySkills(skills);
        _log.debug(String.format("Found %s users for skills: %s", searchCount, skills));
        List<User> users = SkillsUtil.searchUsersBySkills(skills, start, end);
      
        for(User user : users) {
            usersArray.put(buildJsonObject(user, request));
        }
        
        JSONObject resultsObject = JSONFactoryUtil.createJSONObject();
        resultsObject.put(Constants.JSON_RESULTS_ARRAY, usersArray);
        resultsObject.put(Constants.JSON_RESULTS_SEARCH_COUNT, searchCount);
        writeJSON(request, response, resultsObject);
        
    }
    
    

}
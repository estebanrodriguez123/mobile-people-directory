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
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.comparator.UserScreenNameComparator;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * The Class PeopleDirectoryPortlet.
 * 
 * @author parasjain
 */
public class PeopleDirectoryPortlet extends MVCPortlet {
    
    /** The Constant _log. */
    private static final Log _log = LogFactoryUtil.getLog(PeopleDirectoryPortlet.class);
    
    private static final String COMMAND = "pdAction";
    private static final String COMMAND_SEARCH = "keyword-search";
    private static final String COMMAND_SHOW_COMPLETE_PROFILE = "show-complete-profile";
    private static final String LOG_SERVER_RESOURCE_ERROR = "Error from server resource";
    private static final String LOG_COMPLETE_PROFILE_SEARCH_ERROR = "Error while performing complete profile search";
    private static final String LOG_KEYWORD_SEARCH_ERROR = "Error while performing keyword search";
    private static final String LOG_RETURNING_JSON_ERROR = "Error while returning json";
    private static final String DOB_FORMAT = "MMM d, yyyy";
    
    public static final String PARAMETER_KEYWORDS = "keywords";
    public static final String PARAMETER_USER_ID = "userId";
    
    private static final String PARAMETER_START = "start";
    private static final String PARAMETER_END = "end";
    private static final String JSON_USER_ID = "id";
    private static final String JSON_USER_FULL_NAME = "fullName";
    private static final String JSON_USER_SCREEN_NAME = "screenName";
    private static final String JSON_USER_EMAIL_ADDRESS = "emailAddress";
    private static final String JSON_USER_PORTRAIT_URL = "portraitUrl";
    private static final String JSON_USER_JOB_TITLE = "jobTitle";
    private static final String JSON_USER_DOB = "dob";
    private static final String JSON_USER_CITY = "city";
    private static final String JSON_USER_PHONE = "phone";
    private static final String JSON_RESULTS_ARRAY = "resultsArray";
    private static final String JSON_RESULTS_SEARCH_COUNT = "searchCount";
    
    /*
     * (non-Javadoc)
     * 
     * @see com.liferay.util.bridges.mvc.MVCPortlet#serveResource(javax.portlet.
     * ResourceRequest, javax.portlet.ResourceResponse)
     */
    @Override
    public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException,
        PortletException {
        UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(resourceRequest);
        String cmd = uploadRequest.getParameter(COMMAND);
        
        if (cmd.equalsIgnoreCase(COMMAND_SEARCH)) {
            try {
                performKeywordSearch(resourceRequest, resourceResponse);
            } catch (SystemException e) {
                _log.error(LOG_SERVER_RESOURCE_ERROR, e);
            } catch (PortalException e) {
                _log.error(LOG_SERVER_RESOURCE_ERROR, e);
            }
        } else if (cmd.equalsIgnoreCase(COMMAND_SHOW_COMPLETE_PROFILE)) {
            performCompleteProfileSearch(uploadRequest, resourceResponse);
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
     */
    private void performKeywordSearch(ResourceRequest request, ResourceResponse response) throws SystemException,
        PortalException {
        Long companyId = PortalUtil.getCompanyId(request);
        UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
        String keywords = ParamUtil.getString(uploadRequest, PARAMETER_KEYWORDS);
        int start = ParamUtil.getInteger(uploadRequest, PARAMETER_START);
        int end = ParamUtil.getInteger(uploadRequest, PARAMETER_END);
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        
        try {
            List<User> resultUsers = UserLocalServiceUtil.search(companyId, keywords, 0, params, start, end,
                    new UserScreenNameComparator(Boolean.TRUE));
            Integer searchCount = UserLocalServiceUtil.searchCount(companyId, keywords, 0, params);
            JSONArray usersArray = JSONFactoryUtil.createJSONArray();
            
            for (User user : resultUsers) {
                JSONObject jsonUser = JSONFactoryUtil.createJSONObject();
                jsonUser.put(JSON_USER_ID, user.getUserId());
                jsonUser.put(JSON_USER_FULL_NAME, user.getFullName());
                jsonUser.put(JSON_USER_SCREEN_NAME, user.getScreenName());
                jsonUser.put(JSON_USER_EMAIL_ADDRESS, user.getDisplayEmailAddress());
                jsonUser.put(JSON_USER_PORTRAIT_URL,
                        user.getPortraitURL((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY)));
                usersArray.put(jsonUser);
            }
            
            JSONObject resultsObject = JSONFactoryUtil.createJSONObject();
            resultsObject.put(JSON_RESULTS_ARRAY, usersArray);
            resultsObject.put(JSON_RESULTS_SEARCH_COUNT, searchCount);
            
            returnJSONObject(response, resultsObject);
        } catch (SystemException e) {
            _log.error(LOG_KEYWORD_SEARCH_ERROR, e);
        }
    }
    
    /**
     * Perform complete profile search.
     * 
     * @param uploadRequest
     *            the upload request
     * @param resourceResponse
     *            the resource response
     */
    private void performCompleteProfileSearch(UploadPortletRequest uploadRequest, ResourceResponse resourceResponse) {
        long userId = ParamUtil.getLong(uploadRequest, PARAMETER_USER_ID);
        try {
            User user = UserLocalServiceUtil.getUser(userId);
            JSONObject jsonUser = JSONFactoryUtil.createJSONObject();
            jsonUser.put(JSON_USER_JOB_TITLE, user.getJobTitle());
            jsonUser.put(JSON_USER_SCREEN_NAME, user.getScreenName());
            jsonUser.put(JSON_USER_DOB, (new SimpleDateFormat(DOB_FORMAT)).format(user.getBirthday()));
            jsonUser.put(JSON_USER_CITY, (user.getAddresses().size() > 0 ? user.getAddresses().get(0).getCity()
                    : StringPool.BLANK));
            jsonUser.put(JSON_USER_PHONE, (user.getPhones().size() > 0 ? user.getPhones().get(0).getNumber()
                    : StringPool.BLANK));
            
            returnJSONObject(resourceResponse, jsonUser);
        } catch (PortalException e) {
            _log.error(LOG_COMPLETE_PROFILE_SEARCH_ERROR, e);
        } catch (SystemException e) {
            _log.error(LOG_COMPLETE_PROFILE_SEARCH_ERROR, e);
        }
    }
    
    /**
     * Return json object.
     * 
     * @param response
     *            the response
     * @param jsonObj
     *            the json obj
     */
    public static void returnJSONObject(ResourceResponse response, JSONObject jsonObj) {
        HttpServletResponse servletResponse = PortalUtil.getHttpServletResponse(response);
        PrintWriter pw;
        try {
            pw = servletResponse.getWriter();
            pw.write(jsonObj.toString());
            pw.close();
        } catch (IOException e) {
            _log.error(LOG_RETURNING_JSON_ERROR, e);
        }
    }
}
/*
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

package com.rivetlogic.util;

import com.liferay.portal.kernel.dao.orm.CustomSQLParam;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.rivetlogic.portlet.peopledirectory.PeopleDirectoryPortlet;

import java.util.LinkedHashMap;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

/**
 * The Class PeopleDirectoryUtil.
 * 
 * @author raghu kanakamedala
 */
public class PeopleDirectoryUtil {
    
    /** The Constant _log. */
    private static final Log _log = LogFactoryUtil.getLog(PeopleDirectoryPortlet.class);
    
    /**
     * Gets the default row count.
     * 
     * @param renderRequest
     *            the render request
     * @return the default row count
     */
    public static String getDefaultRowCount(RenderRequest renderRequest) {
        PortletPreferences preferences = renderRequest.getPreferences();
        return preferences.getValue("DEFAULT_RECORD_COUNT", "5");
    }
    
    /**
     * Gets the phone field.
     * 
     * @param user
     *            the user
     * @return the phone field
     */
    public static String getPhoneField(User user) {
        String phoneStr = "";
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
     * Gets the city field.
     * 
     * @param user
     *            the user
     * @return the city field
     */
    public static String getCityField(User user) {
        String cityStr = "";
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
    
    /**
     * Gets the user params.
     * 
     * @return the user params
     */
    public static LinkedHashMap<String, Object> getUserParams() {
        
        LinkedHashMap<String, Object> userParams = new LinkedHashMap<String, Object>();
        userParams.put("contactFrom", new CustomSQLParam(
                "left join Phone on User_.userId = Phone.userId left join Address on User_.userId = Address.userId",
                null));
        return userParams;
    }
}

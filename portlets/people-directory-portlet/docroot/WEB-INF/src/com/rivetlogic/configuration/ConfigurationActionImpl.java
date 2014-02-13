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

package com.rivetlogic.configuration;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

/**
 * The Class ConfigurationActionImpl.
 */
public class ConfigurationActionImpl extends DefaultConfigurationAction {
    
    public static final String PREFERENCE_VIEW_ALL_RESULTS_PER_PAGE = "VIEW_ALL_RESULTS_PER_PAGE";
    public static final String PREFERENCE_SEARCH_RESULTS_PER_PAGE = "SEARCH_RESULTS_PER_PAGE";
    public static final String PARAMETER_VIEW_ALL_RESULTS_PER_PAGE = "viewAllResultsPerPage";
    public static final String PARAMETER_SEARCH_RESULTS_PER_PAGE = "searchResultsPerPage";
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.liferay.portal.kernel.portlet.DefaultConfigurationAction#processAction
     * (javax.portlet.PortletConfig, javax.portlet.ActionRequest,
     * javax.portlet.ActionResponse)
     */
    @Override
    public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse)
        throws Exception {
        
        String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
        if (cmd.equals(Constants.UPDATE)) {
            setPreference(actionRequest, PREFERENCE_VIEW_ALL_RESULTS_PER_PAGE,
                    ParamUtil.getString(actionRequest, PARAMETER_VIEW_ALL_RESULTS_PER_PAGE));
            setPreference(actionRequest, PREFERENCE_SEARCH_RESULTS_PER_PAGE,
                    ParamUtil.getString(actionRequest, PARAMETER_SEARCH_RESULTS_PER_PAGE));
        }
        super.processAction(portletConfig, actionRequest, actionResponse);
    }
}

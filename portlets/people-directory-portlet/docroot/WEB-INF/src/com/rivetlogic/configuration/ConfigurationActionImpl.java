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

package com.rivetlogic.configuration;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.rivetlogic.util.Constants;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

/**
 * The Class ConfigurationActionImpl.
 */
public class ConfigurationActionImpl extends DefaultConfigurationAction {
    
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
            setPreference(actionRequest, Constants.PREFERENCE_VIEW_ALL_RESULTS_PER_PAGE,
                    ParamUtil.getString(actionRequest, Constants.PARAMETER_VIEW_ALL_RESULTS_PER_PAGE));
            setPreference(actionRequest, Constants.PREFERENCE_SEARCH_RESULTS_PER_PAGE,
                    ParamUtil.getString(actionRequest, Constants.PARAMETER_SEARCH_RESULTS_PER_PAGE));
            setPreference(actionRequest, Constants.SKYPE_INTEGRATION,
                ParamUtil.getString(actionRequest, Constants.SKYPE_INTEGRATION));
            setPreference(actionRequest, Constants.HANGOUTS_INTEGRATION,
                    ParamUtil.getString(actionRequest, Constants.HANGOUTS_INTEGRATION));
        }
        super.processAction(portletConfig, actionRequest, actionResponse);
    }
}

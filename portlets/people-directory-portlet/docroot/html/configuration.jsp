<%--
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
--%>

<%@ include file="init.jsp"%>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />
<div class="configuration">
	<aui:form action="<%=configurationURL%>" method="post" name="fm">
 	   <aui:input name="<%=Constants.CMD%>" type="hidden" value="<%=Constants.UPDATE%>" />
	    <aui:fieldset label="label.search.container">
	        <aui:input name="<%= Constants.PARAMETER_VIEW_ALL_RESULTS_PER_PAGE%>" label="view-all-results-per-page" value='<%=viewAllResultsPerPage%>' />
			<aui:input name="<%= Constants.PARAMETER_SEARCH_RESULTS_PER_PAGE%>" label="search-results-per-page" type="text" value="<%=searchResultsPerPage%>"/>
			<aui:input name="<%= Constants.SKYPE_INTEGRATION%>" label="skype-integrated" type="checkbox" value="<%= skypeEnabled %>"/>
			<aui:input name="<%= Constants.HANGOUTS_INTEGRATION%>" label="hangouts-integrated" type="checkbox" value="<%= hangoutsEnabled %>"/>
	    </aui:fieldset>
			<aui:button type="submit" />
	</aui:form>
</div>

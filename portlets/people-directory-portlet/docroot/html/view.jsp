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

<c:choose>
	<c:when test="<%= themeDisplay.isSignedIn() %>">
		<div id="people-directory-container">
			<aui:input type="hidden" id="maxItems" name="maxItems" value='<%=PortletProps.get("max.search.items")%>' />
			
			<%
				String tabValue = ParamUtil.getString(request, "tab", "search");
				PortletURL portletURL = renderResponse.createRenderURL();
				String currentURL = PortalUtil.getCurrentURL(renderRequest);
				portletURL.setParameter("tab", tabValue);
				
				int recordCount = GetterUtil.getInteger(preferences.getValue(ConfigurationActionImpl.PREFERENCE_VIEW_ALL_RESULTS_PER_PAGE, DEFAULT_RECORD_COUNT)); 
			%>
			
			<liferay-ui:tabs names="search,view-all" tabsValues="search,view"
				url='<%=portletURL.toString()%>' param="tab" />
				
			<c:choose>
				<c:when test='<%= tabValue.equals("search") %>'>
					<div id="simpleSearchForm">
						<aui:fieldset cssClass="search-criteria">
							<aui:input id="<%= PeopleDirectoryPortlet.PARAMETER_KEYWORDS %>" name="<%= PeopleDirectoryPortlet.PARAMETER_KEYWORDS %>" type="text"
								cssClass="simple-search-keywords" label="people-directory.label.search-user"
								placeholder="people-directory.label.type-keywords" />
						</aui:fieldset>
					</div>
				</c:when>
 				<c:when test='<%= tabValue.equals("view") %>'>
		 			<div id="viewAll">
						<%
						    LinkedHashMap<String, Object> userParams = PeopleDirectoryUtil.getUserParams();
									String orderByCol = ParamUtil.getString(request, "orderByCol",CustomComparatorUtil.COLUMN_FIRST_NAME);
									String orderByType = ParamUtil.getString(request,"orderByType", CustomComparatorUtil.ORDER_DEFAULT);
									OrderByComparator orderComparator = CustomComparatorUtil
											.getUserOrderByComparator(orderByCol, orderByType);
						%>
						<liferay-ui:search-container delta="<%=recordCount %>"
							emptyResultsMessage="no-users-were-found" orderByCol="<%=orderByCol%>"
							orderByType="<%=orderByType%>" orderByColParam="orderByCol"
							orderByTypeParam="orderByType"
							orderByComparator="<%=orderComparator%>" iteratorURL='<%=portletURL%>'>
					
							<liferay-ui:search-container-results
								results="<%=UserLocalServiceUtil.search(
												company.getCompanyId(), null,
												WorkflowConstants.STATUS_APPROVED, userParams,
												searchContainer.getStart(),
												searchContainer.getEnd(),
												searchContainer.getOrderByComparator())%>"
								total="<%=UserLocalServiceUtil.searchCount(
												company.getCompanyId(), null,
												WorkflowConstants.STATUS_APPROVED, userParams)%>" />
					
							<liferay-ui:search-container-row indexVar="indexer"
								className="com.liferay.portal.model.User" keyProperty="userId" modelVar="user">
								<%
									PortletURL profileURL = renderResponse.createRenderURL();
									profileURL.setParameter("mvcPath","/html/profile.jsp");
									profileURL.setParameter(PeopleDirectoryPortlet.PARAMETER_USER_ID, String.valueOf(user.getUserId()));
									profileURL.setParameter("backURL", currentURL);
									String columnHref = profileURL.toString();
								%>
								<liferay-ui:search-container-column-text name="name"
									property="fullName" orderable="true" orderableProperty="<%= CustomComparatorUtil.COLUMN_FIRST_NAME %>"
									href='<%=columnHref%>' />
					
								<liferay-ui:search-container-column-text name="email"
									property="emailAddress" orderable="true"
									orderableProperty="<%= CustomComparatorUtil.COLUMN_EMAIL_ADDRESS %>" href='<%=columnHref%>' />
					
								<liferay-ui:search-container-column-text name="job-title"
									property="jobTitle" orderable="true" orderableProperty="<%= CustomComparatorUtil.COLUMN_JOB_TITLE %>"
									href='<%=columnHref%>' />
					
								<liferay-ui:search-container-column-text name="<%= CustomComparatorUtil.COLUMN_CITY %>">
									<%=PeopleDirectoryUtil.getCityField(user)%>
								</liferay-ui:search-container-column-text>
								<liferay-ui:search-container-column-text name="<%= CustomComparatorUtil.COLUMN_PHONE %>">
									<%=PeopleDirectoryUtil.getPhoneField(user)%>
								</liferay-ui:search-container-column-text>
				
							</liferay-ui:search-container-row>
					
							<liferay-ui:search-iterator />
					
						</liferay-ui:search-container>
					</div>
 				</c:when>
			</c:choose>
			
			<div id="searchResults" class="people_paginator"></div>
			<div id="paginator"></div>
		</div>
	</c:when>
	<c:otherwise>
		<%
		SessionMessages.add(renderRequest, portletDisplay.getId() + SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		SessionErrors.add(renderRequest, "portlet-user-not-logged");
		%>
		<liferay-ui:error key="portlet-user-not-logged" message="portlet-user-not-logged" />
	</c:otherwise>
</c:choose>

<script type="text/javascript" charset="utf-8">

AUI().ready('node', 'event','event-key', function(A) {
    var portletNamespace = <portlet:namespace />portletNamespace;
	if (themeDisplay.isSignedIn()){
		if (A.one('#' + portletNamespace + 'keywords') != null){
		    A.one('#' + portletNamespace + 'keywords').on('keyup', function(){
		           var searchText =A.one('#' + portletNamespace + 'keywords').get('value');
		           
		           // Disabling "Search By Content" for now           
		           var searchContent = false;
		           
		           var maxItems = A.one('#' + portletNamespace + 'maxItems').get('value');
		           if (searchText != null && searchText.length > 2){
		               performSearch(searchText, searchContent, maxItems);
		           }
		    });
		}
	}
});

</script>
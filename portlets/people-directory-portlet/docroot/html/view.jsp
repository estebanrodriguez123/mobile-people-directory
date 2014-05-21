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

<div id="<portlet:namespace/>view">
	<c:choose>
		<c:when test="<%= themeDisplay.isSignedIn() %>">
			<div id="people-directory-container">
				<aui:input type="hidden" id="maxItems" name="maxItems" value='<%= PropsValues.MAX_SEARCH_ITEMS %>' />
				
				<%
					String tabValue = ParamUtil.getString(request, Constants.TAB, Constants.SEARCH);
					PortletURL portletURL = renderResponse.createRenderURL();
					portletURL.setParameter(Constants.TAB, tabValue);
				%>
				
				<liferay-ui:tabs names="search,view-all" tabsValues="search,view"
					url='<%=portletURL.toString()%>' param="tab" />
					
				<c:choose>
					<c:when test='<%= tabValue.equals(Constants.SEARCH) %>'>
						<c:if test="<%= skypeEnabled %>">
							<div id="modal"></div>
							<div class="skype-users-to-call">
								<span class="action-header"><liferay-ui:message key="skype-actions"/></span>
								<ul id="users">
								</ul>
							    <div class="portlet-msg-error"><liferay-ui:message key="error.message.select.one.user"/></div>
							    <hr>
							    <aui:button-row>
									<aui:button name="skype-open" icon="icon-skype" value="action.open.skype"/>
									<aui:button name="skype-call" icon="icon-phone" value="action.call.skype"/>
							    </aui:button-row>
							    <div class="alredy-in-list-msg">
								    <h3 class="header"><liferay-ui:message key="error"/></h3>
									<p class="content"><liferay-ui:message key="already-in-list"/></p>							    
							    </div>
							</div>
						</c:if>
						<div id="simpleSearchForm">
							<aui:fieldset cssClass="search-criteria">
								<aui:input id="<%= Constants.PARAMETER_KEYWORDS %>" name="<%= Constants.PARAMETER_KEYWORDS %>" type="text"
									cssClass="simple-search-keywords" label="people-directory.label.search-user" placeholder="people-directory.label.type-keywords"
								/>
							</aui:fieldset>
						</div>
					</c:when>
	 				<c:when test='<%= tabValue.equals(Constants.VIEW) %>'>
			 			<div id="viewAll">
							<%
							    LinkedHashMap<String, Object> userParams = PeopleDirectoryUtil.getUserParams();
										String orderByCol = ParamUtil.getString(request, Constants.ORDER_BY_COL,CustomComparatorUtil.COLUMN_FIRST_NAME);
										String orderByType = ParamUtil.getString(request, Constants.ORDER_BY_TYPE, CustomComparatorUtil.ORDER_DEFAULT);
										OrderByComparator orderComparator = CustomComparatorUtil
												.getUserOrderByComparator(orderByCol, orderByType);
							%>
							<liferay-ui:search-container delta="<%= viewAllResultsPerPage %>"
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
										profileURL.setParameter(Constants.MVC_PATH, Constants.PEOPLE_DIRECTORY_PROFILE_PAGE);
										profileURL.setParameter(Constants.PARAMETER_USER_ID, String.valueOf(user.getUserId()));
										profileURL.setParameter(Constants.BACK_URL, currentURL);
										String columnHref = profileURL.toString();
									%>
									<liferay-ui:search-container-column-text name="name"
										property="fullName" orderable="true" orderableProperty="<%= CustomComparatorUtil.COLUMN_FIRST_NAME %>"
										href='<%=columnHref%>' />
											
									<liferay-ui:search-container-column-text name="email"
										property="emailAddress" orderable="true"
										orderableProperty="<%= CustomComparatorUtil.COLUMN_EMAIL_ADDRESS %>" href='<%="mailto:"+user.getEmailAddress()%>' />
						
									<liferay-ui:search-container-column-text name="job-title"
										property="jobTitle" orderable="true" orderableProperty="<%= CustomComparatorUtil.COLUMN_JOB_TITLE %>"
										href='<%=columnHref%>' />
						
									<liferay-ui:search-container-column-text name="<%= CustomComparatorUtil.COLUMN_CITY %>">
										<%=PeopleDirectoryUtil.getCityField(user)%>
									</liferay-ui:search-container-column-text>
									<liferay-ui:search-container-column-jsp name="<%= CustomComparatorUtil.COLUMN_PHONE %>" path="/html/include/phone_with_skype.jsp" />
					
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
</div>
<aui:script>
	AUI().applyConfig({
	    groups : {
	    	'aui-paginator-old': {
	    		base : '<%= request.getContextPath()%>',
	            async : false,
	            modules : {
	            	'aui-paginator-old': {
	        			path: '/js/third-party/aui-paginator-old.js',
	        			requires: ['aui-paginator.css','aui-paginator-core-css', 'aui-paginator-skin.css']
	        		},
	        		'aui-paginator-core-css': {
	        			path: '/css/third-party/aui-paginator-core.css'
	        		}, 
	        		'aui-paginator-skin.css': {
	        			path: '/css/third-party/aui-paginator-skin.css'
	        		},
	        		'aui-paginator.css': {
	        			path: '/css/third-party/aui-paginator.css'
	        		}
	            }		
	    	},
	    	'jquery': {
	    		base : '<%= request.getContextPath()%>/js/third-party/',
	            async : false,
	            modules : {
	            	'jquery': {
	                	path: 'jquery-1.6.4.min.js'
	                }
	            }
	    	},
	        'people-directory' : {
	            base : '<%= request.getContextPath()%>/js/',
	            async : false,
	            modules : {
	        	<c:if test="<%= skypeEnabled %>">
	        		'skype-plugin-people-directory': {
	        			path: 'skype-plugin.js',
	        			requires: ['skype-ui']
	        		},
	        		'skype-ui': {
	        			path: 'third-party/skype-uri.js'
	        		}
	        	</c:if>
	            }
	        }
	    }
	});
</aui:script>
<aui:script use="people-directory-plugin,skype-plugin-people-directory">
	Liferay.PeopleDirectory.init(
		{
			portletId: "<%= request.getAttribute(WebKeys.PORTLET_ID) %>",
			namespace: "<portlet:namespace/>",
			container: A.one("#<portlet:namespace/>view"),
			rowCount: "<%=searchResultsPerPage%>",
			fields: ["name", "email", "job-title", "city", "phone"]
		}
	);
	<c:if test="<%= skypeEnabled %>">
		Liferay.SkypePluginPeopleDirectory.init(
			{
				namespace: "<portlet:namespace/>",
				container: A.one("#<portlet:namespace/>view"),
			}
		);	
	</c:if>
</aui:script>
<%@ include file="include/templates.jspf"%>
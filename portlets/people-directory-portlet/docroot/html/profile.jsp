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

<%
long userId = ParamUtil.getLong(request, Constants.PARAMETER_USER_ID);

User userSelected = null;

try {
	userSelected = UserLocalServiceUtil.getUser(userId);
	
} catch (NoSuchUserException e) {
	SessionErrors.add(renderRequest, e.getClass());
}

String backURL = ParamUtil.getString(request, Constants.BACK_URL);
   String imageId = userId + "-picture";
%>

<liferay-ui:error exception="<%= NoSuchUserException.class %>" message="user-could-not-be-found"/>

<c:if test="<%= Validator.isNotNull(userSelected) %>">
	<article id="articleView">
        
        <liferay-ui:header cssClass="profile-info-title" backURL="<%=backURL%>" title="<%=HtmlUtil.escape(userSelected.getFullName())%>"/>
        		
		<img src="<%=userSelected.getPortraitURL(themeDisplay)%>" height="55" width="60" id="<%=imageId %>" alt="<%= HtmlUtil.escapeAttribute(userSelected.getFullName()) %>" />
		
        <div class="profile-info">
            <span><label><liferay-ui:message key="people-directory.label.job-title" />:</label> <%= HtmlUtil.escape(userSelected.getJobTitle()) %></span>
            <span><label><liferay-ui:message key="people-directory.label.email" />:</label> <%= HtmlUtil.escape(userSelected.getEmailAddress()) %></span>
            <span><label><liferay-ui:message key="people-directory.label.city" />:</label> <%=(userSelected.getAddresses().size() > 0 ? userSelected.getAddresses().get(0).getCity() : StringPool.BLANK)%></span>
            <span><label><liferay-ui:message key="people-directory.label.phone" />:</label> <%= (userSelected.getPhones().size() > 0 ? userSelected.getPhones().get(0).getNumber() : StringPool.BLANK)%></span>
        </div>
		
        <div class="clearfix"></div>
        
	</article>
</c:if>

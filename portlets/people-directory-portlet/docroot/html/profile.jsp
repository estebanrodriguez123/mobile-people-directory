<%--
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
--%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@ include file="init.jsp"%>

<%
	User userSelected = UserLocalServiceUtil.getUser(ParamUtil.getLong(
			request, "userId"));
	String backURL = ParamUtil.getString(request, "backURL");
    String imageId = userSelected.getUserId() + "-picture";
%>

<div class="back-link">
	<div class="back-link-url">
		<aui:a href='<%=backURL%>'><liferay-ui:message key ="people-directory.label.back-to-all"/></aui:a>
	</div>
</div>

<article id="articleView">
	
	<table >
		<tbody>
			<tr>
				<td colspan="2">
					<h1><%=userSelected.getFullName()%></h1>
				</td>
			</tr>
			<tr>
				<td><img src="<%=userSelected.getPortraitURL(themeDisplay)%>" height="55" width="60" id="<%=imageId %>" /> </td>
				<td valign="top">
					<table>
						<tr>
							<td valign="top">
								<h2><liferay-ui:message key="people-directory.label.job-title" /></h2>
							</td>
							<td valign="top"><%=userSelected.getJobTitle()%></td>
						</tr>
						<tr>
							<td valign="top">
								<h2><liferay-ui:message key="people-directory.label.email" /></h2>
							</td>
							<td valign="top"><%=userSelected.getEmailAddress()%></td>
						</tr>
						<tr>
							<td valign="top">
								<h2><liferay-ui:message key="people-directory.label.city" /></h2>
							</td>
							<td valign="top"><%=(userSelected.getAddresses().size() > 0 ? userSelected.getAddresses().get(0).getCity() : StringPool.BLANK)%></td>
						</tr>
						<tr>
							<td valign="top">
								<h2><liferay-ui:message key="people-directory.label.phone" /></h2>
							</td>
							<td valign="top"><%=(userSelected.getPhones().size() > 0 ? userSelected.getPhones().get(0).getNumber() : StringPool.BLANK)%></td>
						</tr>
					</table>
				</td>
			</tr>

		</tbody>
	</table>
</article>


<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="theme" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.Collections"%>
<%@ page import="com.liferay.portal.kernel.util.StringPool"%>
<%@ page import="com.liferay.portal.kernel.util.StringUtil"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portlet.expando.model.ExpandoValue" %>
<%@ page import="com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil" %>
<%@ page import="com.liferay.portal.service.ClassNameLocalServiceUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys"%>
<%@ page import="com.liferay.portlet.expando.service.permission.ExpandoColumnPermissionUtil"%>
<%@ page import="com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil"%>
<%@ page import="com.liferay.portlet.expando.model.ExpandoColumn"%>
<%@ page import="com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil"%>
<%@ page import="com.liferay.portlet.expando.model.ExpandoTable"%>
<%@ page import="com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil"%>
<%@ page import="com.liferay.portlet.asset.model.AssetCategory"%>
<%@ page import="com.liferay.portal.util.comparator.GroupIdComparator"%>
<%@ page import="com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil"%>
<%@ page import="com.liferay.portlet.asset.model.AssetVocabulary"%>
<%@ page import="com.liferay.portal.service.CompanyLocalServiceUtil"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>

<portlet:defineObjects/>
<theme:defineObjects/>

<%
	long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);
	long companyId = PortalUtil.getDefaultCompanyId();
	long groupId = CompanyLocalServiceUtil.fetchCompany(companyId).getGroupId();
	
	User selUser = (User) request.getAttribute("user.selUser");
	
	String selectedSkills = StringPool.BLANK;
	String skillsList = StringPool.BLANK;
	List<String> selectedSkillsList = Collections.emptyList();
	
	String selectedHobbies = StringPool.BLANK;
	String hobbiesList = StringPool.BLANK;
	List<String> selectedHobbiesList = Collections.emptyList();
	
	ExpandoTable tableSkills = ExpandoTableLocalServiceUtil.getDefaultTable(companyId, classNameId);
	ExpandoColumn columnSkills = ExpandoColumnLocalServiceUtil.getColumn(tableSkills.getTableId(), "skills");
	
	boolean skillsPermission = ExpandoColumnPermissionUtil.contains(permissionChecker, columnSkills, ActionKeys.UPDATE);
	
	if(skillsPermission) {
	    if(Validator.isNotNull(selUser)) {
	        ExpandoValue valueSkills = ExpandoValueLocalServiceUtil.getValue(classNameId, tableSkills.getName(), columnSkills.getName(), selUser.getUserId());
			if(Validator.isNotNull(valueSkills)) {
			    selectedSkills = valueSkills.getData();
			}
			selectedSkillsList = Arrays.asList(StringUtil.split(selectedSkills));
	    }
		skillsList = getCategoriesTree(groupId, "Skills");
	}
	
	ExpandoTable tableHobbies = ExpandoTableLocalServiceUtil.getDefaultTable(companyId, classNameId);
	ExpandoColumn columnHobbies = ExpandoColumnLocalServiceUtil.getColumn(tableHobbies.getTableId(), "hobbies");
	
	boolean hobbiesPermission = ExpandoColumnPermissionUtil.contains(permissionChecker, columnHobbies, ActionKeys.UPDATE);
	
	if(hobbiesPermission) {
	    if(Validator.isNotNull(selUser)) {
		    ExpandoValue valueHobbies = ExpandoValueLocalServiceUtil.getValue(classNameId, tableHobbies.getName(), columnHobbies.getName(), selUser.getUserId());
			if(Validator.isNotNull(valueHobbies)) {
			    selectedHobbies = valueHobbies.getData();
			}
			selectedHobbiesList = Arrays.asList(StringUtil.split(selectedHobbies));
	    }
		hobbiesList = getCategoriesTree(groupId, "Hobbies");
	}	
%>


<aui:script>
<%-- 
  update rlcacheid everytime new version is build for distribution, this 
  because hooks does not handle well cache and scss processing
--%>
<% String rlcacheid = "?build=6"; %>              
AUI().applyConfig({
    groups : {
        'rivet-custom' : {
            base : Liferay.AUI.getJavaScriptRootPath() + '/rivetlogic/',
            async : false,
            modules : {
								'rl-skills-hobbies': {
												path: 'rl-skills-hobbies.js<%=rlcacheid %>'
								},
                'rl-skills-hobbies-css': {
                        path: 'rl-skills-hobbies.css<%=rlcacheid %>',
                        type: 'css'
                }
            }
        }
    }
});
</aui:script>

<aui:container>
	<h3><liferay-ui:message key="skills"/></h3>
	<c:if test="<%= skillsPermission %>">	
	<aui:row>
		<aui:col width="60" cssClass="well">
			<div class="skills-list sh-list">
				<p><liferay-ui:message key="please-select-skills"/></p>
				<%= skillsList %>
			</div>
		</aui:col>
		<aui:col width="40">
			<aui:field-wrapper>
				<aui:input cssClass="skills-name input-inline" name="skill-name" label="" placeholder="skill-name"/>
				<aui:button name="add-skill" value="add" icon="icon-plus" onClick="addskills()" />
			</aui:field-wrapper>
			<div class="well sh-list">
				<p><liferay-ui:message key="selected-skills"/></p>
				<div class="selected-skills-list">
				<c:forEach items="<%= selectedSkillsList %>" var="skill">
					<span class="label label-info"><i class="icon-tag"></i><span>${ skill }<a class="delete js-delete selected-skills-item" href="javascript:;"><i class="icon-remove-sign"></i></a></span></span> 
				</c:forEach>
				</div>
			</div>
		</aui:col>
	</aui:row>
	<aui:input type="hidden" name="ExpandoAttributeName--skills--"  value="skills"/>
	<aui:input type="hidden" name="ExpandoAttribute--skills--"  value="<%= selectedSkills %>" cssClass="selected-skills-value"/>
	</c:if>
	
	<h3><liferay-ui:message key="hobbies"/></h3>
	<c:if test="<%= hobbiesPermission %>">
	<aui:row>
		<aui:col width="60" cssClass="well">
			<div class="hobbies-list sh-list">
				<p><liferay-ui:message key="please-select-hobbies"/></p>
				<%= hobbiesList %>
			</div>
		</aui:col>
		<aui:col width="40">
			<aui:field-wrapper>
				<aui:input cssClass="hobbies-name input-inline" name="hobby-name" label="" placeholder="hobby-name"/>
				<aui:button name="add-hobby" value="add" icon="icon-plus" onClick="addhobbies()" />
			</aui:field-wrapper>
			<div class="well sh-list">
				<p><liferay-ui:message key="selected-hobbies"/></p>
				<div class="selected-hobbies-list">
				<c:forEach items="<%= selectedHobbiesList %>" var="hobby">
					<span class="label label-info"><i class="icon-tag"></i><span>${ hobby }<a class="delete js-delete selected-hobbies-item" href="javascript:;"><i class="icon-remove-sign"></i></a></span></span> 
				</c:forEach>
				</div>
			</div>
		</aui:col>
	</aui:row>
	<aui:input type="hidden" name="ExpandoAttributeName--hobbies--"  value="hobbies"/>
	<aui:input type="hidden" name="ExpandoAttribute--hobbies--"  value="<%= selectedHobbies %>" cssClass="selected-hobbies-value"/>
	</c:if>
</aui:container>

<%!
	private void buildTree(StringBuilder sb, List<AssetCategory> cats, String name) throws Exception {
    	sb.append("<ul>");
    	for(AssetCategory cat : cats) {
    	    sb.append("<li>");
        	sb.append("<i class=\"icon-tag\"></i><a href=\"javascript:void(0);\" class=\""+name.toLowerCase()+"-list-item\">").append(cat.getName()).append("</a>");
        	List<AssetCategory> children = AssetCategoryLocalServiceUtil.getChildCategories(cat.getCategoryId());
        	if(!children.isEmpty()) {
        	    buildTree(sb, children, name);
        	}
        	sb.append("</li>");
    	}
    	sb.append("</ul>");
	}

	private String getCategoriesTree(long groupId, String name) throws Exception {
	    List<AssetVocabulary> vocabularies = AssetVocabularyLocalServiceUtil.getGroupVocabularies(groupId, false);
	    StringBuilder tree = new StringBuilder();
		AssetVocabulary vocabulary = null;
		
		for(AssetVocabulary v : vocabularies) {
		    //TODO: Make vocabulary configurable by Id?
		    if(Validator.equals(name, v.getName())) vocabulary = v;
		}
		
		if(Validator.isNotNull(vocabulary)) {
		    List<AssetCategory> rootCats = AssetCategoryLocalServiceUtil
			        .getVocabularyRootCategories(vocabulary.getVocabularyId(), -1, -1, null);
			buildTree(tree, rootCats, name);
		}
		
		return tree.toString();
	}
%>
<aui:script use="base,rl-skills-hobbies,rl-skills-hobbies-css">
	var skillsHobbies = new A.RivetSkillsHobbies({namespace: '<portlet:namespace />'});
	<c:if test="<%= skillsPermission %>">
	skillsHobbies.initSkills();
	</c:if>
</aui:script>

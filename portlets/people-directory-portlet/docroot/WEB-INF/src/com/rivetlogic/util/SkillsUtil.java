/**
 * Copyright (C) 2005-2016 Rivet Logic Corporation.
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

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetVocabulary;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to query users based on tags and categories.
 * @author joseross
 *
 */
public class SkillsUtil {

    public static long countUsersBySkills(String skills) throws SystemException, PortalException {
        DynamicQuery query = buildQuery(skills);
        
        if(query != null) {
            return ExpandoValueLocalServiceUtil.dynamicQueryCount(query);
        }
        
        return 0;
    }
    
    @SuppressWarnings("unchecked")
    public static List<User> searchUsersBySkills(String skills, int start, int end) throws SystemException, PortalException {
        DynamicQuery query = buildQuery(skills);
        
        if(query != null) {
            query.setLimit(start, end);
            List<ExpandoValue> results = ExpandoValueLocalServiceUtil.dynamicQuery(query);
            return getUsers(results);
        }
        
        return Collections.emptyList();
    }
    
    @SuppressWarnings("unchecked")
    public static List<String> searchExistingCategories(String term) throws SystemException, PortalException {
        long companyId = PortalUtil.getDefaultCompanyId();
        long groupId = CompanyLocalServiceUtil.fetchCompany(companyId).getGroupId();
        
        AssetVocabulary skills = null;
        
        List<AssetVocabulary> vocabularies = AssetVocabularyLocalServiceUtil.getGroupVocabularies(groupId, false);
        for(AssetVocabulary vocabulary : vocabularies) {
            if(vocabulary.getName().equals("Skills")) skills = vocabulary;
        }
        
        if(skills != null) {
            DynamicQuery query = AssetCategoryLocalServiceUtil.dynamicQuery();
            query.add(RestrictionsFactoryUtil.eq("vocabularyId", skills.getVocabularyId()));
            query.add(RestrictionsFactoryUtil.ilike("name", String.format("%%%s%%",term)));
            List<AssetCategory> categories = (List<AssetCategory>) AssetCategoryLocalServiceUtil.dynamicQuery(query);
            List<String> values = new ArrayList<String>();
            for(AssetCategory cat : categories) {
                values.add(cat.getName());
            }
            return values;
        }
        
        return Collections.emptyList();
    }
    
    private static DynamicQuery buildQuery(String terms) throws PortalException, SystemException {
        long companyId = PortalUtil.getDefaultCompanyId();
        long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);
        ExpandoTable table = ExpandoTableLocalServiceUtil.getDefaultTable(companyId, classNameId);
        ExpandoColumn column = ExpandoColumnLocalServiceUtil.getColumn(table.getTableId(), "skills");
        
        if(column != null) {
            DynamicQuery query = ExpandoValueLocalServiceUtil.dynamicQuery();
            
            query.add(RestrictionsFactoryUtil.eq("companyId", companyId));
            query.add(RestrictionsFactoryUtil.eq("tableId", table.getTableId()));
            query.add(RestrictionsFactoryUtil.eq("columnId", column.getColumnId()));
            
            String[] skills = terms.split(StringPool.COMMA);
            if(skills.length > 0) {
                for(String skill : skills) {
                    query.add(RestrictionsFactoryUtil.ilike("data", String.format("%%%s%%", skill)));
                }
            }
            
            return query;
        }
        
        return null;
    }
     
    private static List<User> getUsers(List<ExpandoValue> results) throws PortalException, SystemException {
        List<User> users = new ArrayList<User>();
        for(ExpandoValue entry : results) {
            users.add(UserLocalServiceUtil.getUser(entry.getClassPK()));
        }
        return users;
    }

    public static JSONArray searchSuggestions(String term) throws SystemException, PortalException {
        List<String> suggestions = new ArrayList<String>();
        suggestions.addAll(searchExistingCategories(term));
        JSONArray array = JSONFactoryUtil.createJSONArray();
        for(String suggestion : suggestions) {
            JSONObject o = JSONFactoryUtil.createJSONObject();
            o.put("text", suggestion);
            o.put("value", suggestion);
            array.put(o);
        }
        return array;
    }
    
}

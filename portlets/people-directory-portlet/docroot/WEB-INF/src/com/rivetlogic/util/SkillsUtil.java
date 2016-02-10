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
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to query users based on tags and categories.
 * @author joseross
 *
 */
public class SkillsUtil {

    public static int countTaggedUsers(String skills) throws SystemException {
        DynamicQuery query = buildQuery();
        List<AssetEntry> results = executeUserQuery(query, skills);
        return results.size();
    }
    
    public static List<User> searchTaggedUsers(String skills, int start, int end) throws SystemException, PortalException {
        DynamicQuery query = buildQuery();
        query.setLimit(start, end);
        List<AssetEntry> results = executeUserQuery(query, skills);
        return getUsers(results);
    }
    
    public static List<String> searchExistingTags(String term) throws SystemException {
        DynamicQuery query = buildQuery();
        return executeTagsQuery(query, term);
    }
    
    public static List<String> searchExistingCategories(String term) throws SystemException {
        DynamicQuery query = buildQuery();
        return executeCategoriesQuery(query, term);
    }
    
    private static DynamicQuery buildQuery() {
        DynamicQuery query = AssetEntryLocalServiceUtil.dynamicQuery();
        query.add(RestrictionsFactoryUtil.eq("classNameId", PortalUtil.getClassNameId(User.class)));
        return query;
    }
    
    @SuppressWarnings("unchecked")
    private static List<String> executeTagsQuery(DynamicQuery query, String term) throws SystemException {
        List<AssetEntry> results = AssetEntryLocalServiceUtil.dynamicQuery(query);
        List<String> tags = new ArrayList<String>();
        for(AssetEntry entry : results) {
            for(String tag : entry.getTagNames()) {
                if(tag.toLowerCase().contains(term.toLowerCase()) && !tags.contains(tag))
                    tags.add(tag);
            }
        }
        return tags;
    }
    
    @SuppressWarnings("unchecked")
    private static List<String> executeCategoriesQuery(DynamicQuery query, String term) throws SystemException {
        List<AssetEntry> results = AssetEntryLocalServiceUtil.dynamicQuery(query);
        List<String> categories = new ArrayList<String>();
        for(AssetEntry entry : results) {
            for(AssetCategory category : entry.getCategories()) {
                String name = category.getTitleCurrentValue();
                if(name.toLowerCase().contains(term.toLowerCase()) && !categories.contains(name))
                    categories.add(name);
            }
        }
        return categories;
    }
    
    @SuppressWarnings("unchecked")
    private static List<AssetEntry> executeUserQuery(DynamicQuery query, String skills) throws SystemException {
        List<AssetEntry> results = AssetEntryLocalServiceUtil.dynamicQuery(query);
        List<AssetEntry> tagged = new ArrayList<AssetEntry>();
        String [] skillsArray = skills.split(",");
        for(AssetEntry entry : results) {
            if(checkSkills(entry.getTagNames(), skillsArray) + checkSkills(entry.getCategories(), skillsArray) >= skillsArray.length)
                tagged.add(entry);
        }
        return tagged;
    }
    
    private static int checkSkills(String[] currentTags, String[] searchTags) {
        int count = 0;
        List<String> current = Arrays.asList(currentTags);
        for(String tag : searchTags) {
            if(current.contains(tag)) {
                count++;
            }
        }
        return count;
    }
    
    private static int checkSkills(List<AssetCategory> cats, String[] searchTags) {
        int count = 0;
        List<String> current = new ArrayList<String>();
        for(AssetCategory cat : cats) {
            current.add(cat.getTitleCurrentValue());
        }
        for(String tag : searchTags) {
            if(current.contains(tag)) {
                count++;
            }
        }
        return count;
    }
    
    private static List<User> getUsers(List<AssetEntry> results) throws PortalException, SystemException {
        List<User> users = new ArrayList<User>();
        for(AssetEntry entry : results) {
            users.add(UserLocalServiceUtil.getUser(entry.getClassPK()));
        }
        return users;
    }

    public static JSONArray searchSuggestions(String term) throws SystemException {
        List<String> suggestions = new ArrayList<String>();
        suggestions.addAll(searchExistingTags(term));
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

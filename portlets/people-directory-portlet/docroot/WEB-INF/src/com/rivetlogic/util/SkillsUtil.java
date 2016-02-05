package com.rivetlogic.util;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillsUtil {

    public static int countTaggedUsers(String skills) throws SystemException {
        DynamicQuery query = buildQuery();
        List<AssetEntry> results = executeQuery(query, skills);
        return results.size();
    }
    
    public static List<User> searchTaggedUsers(String skills, int start, int end) throws SystemException, PortalException {
        DynamicQuery query = buildQuery();
        query.setLimit(start, end);
        List<AssetEntry> results = executeQuery(query, skills);
        return getUsers(results);
    }
    
    private static DynamicQuery buildQuery() {
        DynamicQuery query = AssetEntryLocalServiceUtil.dynamicQuery();
        query.add(RestrictionsFactoryUtil.eq("classNameId", PortalUtil.getClassNameId(User.class)));
        return query;
    }
    
    private static List<AssetEntry> executeQuery(DynamicQuery query, String skills) throws SystemException {
        List<AssetEntry> results = AssetEntryLocalServiceUtil.dynamicQuery(query);
        List<AssetEntry> tagged = new ArrayList<AssetEntry>();
        for(AssetEntry entry : results) {
            if(hasAllSkills(entry.getTagNames(), skills.split(",")))
                tagged.add(entry);
        }
        return tagged;
    }
    
    private static boolean hasAllSkills(String[] currentTags, String[] searchTags) {
        boolean match = currentTags.length > 0 && searchTags.length > 0;
        List<String> current = Arrays.asList(currentTags);
        for(String tag : searchTags) {
            match = match && current.contains(tag);
        }
        return match;
    }
    
    private static List<User> getUsers(List<AssetEntry> results) throws PortalException, SystemException {
        List<User> users = new ArrayList<User>();
        for(AssetEntry entry : results) {
            users.add(UserLocalServiceUtil.getUser(entry.getClassPK()));
        }
        return users;
    }
    
}

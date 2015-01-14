package com.rivetlogic.model;

import java.util.List;

import com.liferay.portal.kernel.json.JSON;

/**
 * @author Alejandro Soto
 *
 */
public class PeopleDirectoryResult {
	@JSON
    int total;
    
    @JSON
	List<UserData> users;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<UserData> getUsers() {
        return users;
    }

    public void setUsers(List<UserData> users) {
        this.users = users;
    }
	
    
}

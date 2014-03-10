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

package com.rivetlogic.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.model.User;

public class BirthdayComparator extends OrderByComparator {

    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactoryUtil.getLog(BirthdayComparator.class);
    public static String ORDER_BY_ASC = "contact_.birthday ASC";
    public static String ORDER_BY_DESC = "contact_.birthday DESC";
    
    public BirthdayComparator() {
        this(false);
    }
    
    public BirthdayComparator(boolean asc) {
        _asc = asc;
    }
    
    public int compare(Object obj1, Object obj2) {
        
        User instance1 = (User) obj1;
        User instance2 = (User) obj2;
        
        int value = 0;
        try {
            value = instance1.getBirthday().compareTo(instance2.getBirthday());
        } catch (PortalException e) {
            _log.warn(e);
        } catch (SystemException e) {
            _log.warn(e);
        }
        
        if (_asc) {
            return value;
        } else {
            return -value;
        }
        
    }
    
    public String getOrderBy() {
        
        if (_asc) {
            return ORDER_BY_ASC;
        } else {
            return ORDER_BY_DESC;
        }
    }
    
    private boolean _asc;
}

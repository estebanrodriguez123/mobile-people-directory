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

import com.liferay.portal.kernel.util.OrderByComparator;

public class CustomComparatorUtil {
    
    public static final String ORDER_DEFAULT = "asc";
    public static final String COLUMN_FIRST_NAME = "firstName";
    public static final String COLUMN_EMAIL_ADDRESS = "emailAddress";
    public static final String COLUMN_JOB_TITLE = "jobTitle";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_PHONE = "phone";
    
    public static OrderByComparator getUserOrderByComparator(String orderByCol, String orderByType) {
        
        boolean orderByAsc = false;
        
        if (orderByType.equals(ORDER_DEFAULT)) {
            orderByAsc = true;
        }
        
        OrderByComparator orderByComparator = null;
        
        if (orderByCol.equalsIgnoreCase(COLUMN_FIRST_NAME)) {
            orderByComparator = new FirstNameComparator(orderByAsc);
        } else if (orderByCol.equalsIgnoreCase(COLUMN_EMAIL_ADDRESS)) {
            orderByComparator = new EmailComparator(orderByAsc);
        } else if (orderByCol.equalsIgnoreCase(COLUMN_JOB_TITLE)) {
            orderByComparator = new JobTitleComparator(orderByAsc);
        } else if (orderByCol.equalsIgnoreCase(COLUMN_CITY)) {
            orderByComparator = new CityComparator(orderByAsc);
        } else if (orderByCol.equalsIgnoreCase(COLUMN_PHONE)) {
            orderByComparator = new PhoneComparator(orderByAsc);
        }
        
        return orderByComparator;
    }
}

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

import com.liferay.portal.kernel.util.PropsUtil;

public class PropsValues {

	public static final String MAX_SEARCH_ITEMS = PropsUtil.get(PropsKeys.MAX_SEARCH_ITEMS);
	
	public static final String DEFAULT_RECORD_COUNT = PropsUtil.get(PropsKeys.DEFAULT_RECORD_COUNT);
	
	public static final String SKYPE_ENABLED = PropsUtil.get(PropsKeys.SKYPE_ENABLED);
	
	public static final String HANGOUTS_INTEGRATION = PropsUtil.get(PropsKeys.HANGOUTS_INTEGRATION);
	
}

/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.rivetlogic.service;

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link PeopleDirectoryService}.
 *
 * @author Brian Wing Shun Chan
 * @see PeopleDirectoryService
 * @generated
 */
public class PeopleDirectoryServiceWrapper implements PeopleDirectoryService,
	ServiceWrapper<PeopleDirectoryService> {
	public PeopleDirectoryServiceWrapper(
		PeopleDirectoryService peopleDirectoryService) {
		_peopleDirectoryService = peopleDirectoryService;
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _peopleDirectoryService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_peopleDirectoryService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _peopleDirectoryService.invokeMethod(name, parameterTypes,
			arguments);
	}

	/**
	* Search for all the portal users
	*
	* @param keywords If keyword is empty string it will search all the users
	* @param start Page beginning
	* @param end Page end
	* @return
	* @throws PortalException
	* @throws com.liferay.portal.kernel.exception.SystemException
	* @throws SystemException
	*/
	@Override
	public com.rivetlogic.service.data.PeopleDirectoryResult search(
		java.lang.String keywords, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _peopleDirectoryService.search(keywords, start, end);
	}

	/**
	* Retrieves all the system users
	*
	* @return
	* @throws SystemException
	* @throws PortalException
	*/
	@Override
	public com.rivetlogic.service.data.PeopleDirectoryResult fetchAll()
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _peopleDirectoryService.fetchAll();
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public PeopleDirectoryService getWrappedPeopleDirectoryService() {
		return _peopleDirectoryService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedPeopleDirectoryService(
		PeopleDirectoryService peopleDirectoryService) {
		_peopleDirectoryService = peopleDirectoryService;
	}

	@Override
	public PeopleDirectoryService getWrappedService() {
		return _peopleDirectoryService;
	}

	@Override
	public void setWrappedService(PeopleDirectoryService peopleDirectoryService) {
		_peopleDirectoryService = peopleDirectoryService;
	}

	private PeopleDirectoryService _peopleDirectoryService;
}
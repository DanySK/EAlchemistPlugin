/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.javaee.jca;

import org.eclipse.emf.ecore.EObject;

public interface IConnectorResource {

	/**
	 * Return the first element in the EList.
	 */
	public abstract EObject getRootObject();

	/**
	 * Return the Connector
	 */
	public abstract Connector getConnector();
}

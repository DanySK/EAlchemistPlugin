/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.javaee.applicationclient.internal.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.jst.javaee.applicationclient.ApplicationClient;
import org.eclipse.jst.javaee.applicationclient.ApplicationClientDeploymentDescriptor;
import org.eclipse.jst.javaee.applicationclient.IApplicationClientResource;
import org.eclipse.jst.javaee.core.JEEXMLLoadImpl;
import org.eclipse.jst.javaee.core.internal.util.JavaeeResourceImpl;
import org.eclipse.wst.common.internal.emf.resource.IRootObjectResource;

/**
 * <!-- begin-user-doc -->
 * The <b>Resource </b> associated with the package.
 * <!-- end-user-doc -->
 * @see org.eclipse.jst.javaee.applicationclient.internal.util.ApplicationclientResourceFactoryImpl
 * @generated
 */
public class ApplicationclientResourceImpl extends JavaeeResourceImpl implements IRootObjectResource, IApplicationClientResource {
	/**
	 * Creates an instance of the resource.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param uri the URI of the new resource.
	 * @generated
	 */
	public ApplicationclientResourceImpl(URI uri) {
		super(uri);
	}
	
	@Override
	protected XMLLoad createXMLLoad() {
		 return new JEEXMLLoadImpl(createXMLHelper());
	}

	
	@Override
	protected XMLHelper createXMLHelper() {
		
		return new AppClientXMLHelperImpl(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jst.javaee.applicationclient.IApplicationClientResource#getRootObject()
	 */
	public EObject getRootObject() {
		if (contents == null || contents.isEmpty())
			return null;
		Object root = getContents().get(0);
		if(root == null){
			return null;
		}
		return (EObject)((ApplicationClientDeploymentDescriptor)root).getApplicationClient();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jst.javaee.applicationclient.IApplicationClientResource#getApplicationClient()
	 */
	public ApplicationClient getApplicationClient() {
		return (ApplicationClient)getRootObject();
		
	}

} //ApplicationclientResourceImpl

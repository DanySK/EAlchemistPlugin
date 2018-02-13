/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Sep 29, 2003
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code
 * Generation&gt;Code and Comments
 */
package org.eclipse.jst.j2ee.jca.internal.plugin;


import org.eclipse.jst.j2ee.internal.jca.operations.ConnectorComponentImportDataModelProvider;
import org.eclipse.jst.j2ee.internal.moduleextension.EarModuleExtensionImpl;
import org.eclipse.jst.j2ee.internal.moduleextension.JcaModuleExtension;
import org.eclipse.jst.j2ee.jca.project.facet.ConnectorFacetProjectCreationDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;


public class JcaModuleExtensionImpl extends EarModuleExtensionImpl implements JcaModuleExtension {

	/**
	 *  
	 */
	public JcaModuleExtensionImpl() {
		super();
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jst.j2ee.internal.internal.moduleextension.JcaModuleExtension#createProjectInfo()
	 */
	//	public J2EEJavaProjectInfo createProjectInfo() {
	//		return new ConnectorProjectInfo();
	//	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jst.j2ee.internal.internal.moduleextension.JcaModuleExtension#createImportOperation(org.eclipse.core.resources.IProject,
	 *      org.eclipse.jst.j2ee.internal.internal.commonarchivecore.RARFile)
	 */
	//	public J2EEImportOperation createImportOperation(IProject proj, RARFile rarFile){
	//		return new RARImportOperation(proj, rarFile);
	//	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jst.j2ee.internal.internal.moduleextension.EarModuleExtension#createProjectDataModel()
	 */
	public IDataModel createProjectDataModel() {
		return DataModelFactory.createDataModel(new ConnectorFacetProjectCreationDataModelProvider());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jst.j2ee.internal.internal.moduleextension.EarModuleExtension#createImportDataModel()
	 */
	public IDataModel createImportDataModel() {
		return DataModelFactory.createDataModel(new ConnectorComponentImportDataModelProvider());
	}
}

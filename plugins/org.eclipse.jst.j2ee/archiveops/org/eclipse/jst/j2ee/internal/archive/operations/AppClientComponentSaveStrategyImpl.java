/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.j2ee.internal.archive.operations;

import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * @deprecated this will be deleted post WTP 3.2.  This code is no longer
 * called because the IArchive API is used to handle imports.
 * 
 * @author jsholl
 */
public class AppClientComponentSaveStrategyImpl extends J2EEComponentSaveStrategyImpl {

	public AppClientComponentSaveStrategyImpl(IVirtualComponent vComponent) {
		super(vComponent);
	}

}

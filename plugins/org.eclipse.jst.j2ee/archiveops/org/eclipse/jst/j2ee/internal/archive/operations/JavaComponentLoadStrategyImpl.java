/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.j2ee.internal.archive.operations;

import java.util.List;

import org.eclipse.jst.j2ee.classpathdep.IClasspathDependencyConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public class JavaComponentLoadStrategyImpl extends ComponentLoadStrategyImpl {

	public JavaComponentLoadStrategyImpl(IVirtualComponent vComponent) {
		super(vComponent);
	}
	
	public JavaComponentLoadStrategyImpl(IVirtualComponent vComponent, boolean includeClasspathComponents) {
		super(vComponent, includeClasspathComponents);
	}
	
	@Override
	public List getFiles() {
		super.getFiles();
		if (includeClasspathComponents) {
			addMappedClassFolders(IClasspathDependencyConstants.RUNTIME_MAPPING_INTO_COMPONENT_PATH);
		}
		return filesHolder.getFiles();
	}
}

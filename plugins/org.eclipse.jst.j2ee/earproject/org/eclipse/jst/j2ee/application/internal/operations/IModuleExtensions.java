/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.j2ee.application.internal.operations;

import org.eclipse.jst.j2ee.internal.plugin.IJ2EEModuleConstants;

// @deprecated @see org.eclipse.jst.j2ee.internal.plugin.IJ2EEModuleConstants

public interface IModuleExtensions {

	public String DOT_WAR = IJ2EEModuleConstants.WAR_EXT;
	public String DOT_JAR = IJ2EEModuleConstants.JAR_EXT; 
	public String DOT_RAR = IJ2EEModuleConstants.RAR_EXT;
	public String DOT_EAR = IJ2EEModuleConstants.EAR_EXT;

}

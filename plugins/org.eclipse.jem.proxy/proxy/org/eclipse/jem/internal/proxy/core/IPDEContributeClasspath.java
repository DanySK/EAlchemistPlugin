/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*


 */
package org.eclipse.jem.internal.proxy.core;

import org.eclipse.core.runtime.CoreException;


/**
 * Contribute to classpath PDE entries that are needed for a launch.
 * <p> 
 * Use the <code>INSTANCE()</code> to access the contributor. If the value
 * is <code>null</code>, then PDE is not available.
 * 
 * @since 1.0.2
 */
public interface IPDEContributeClasspath {
	
	public static IPDEContributeClasspath INSTANCE = PDEContributeClasspathInstance.getInstance();

	/**
	 * Return the classpath contributions for the plugins in the given project.
	 * <p>
	 * This will add in the libraries from any fragments of any plugin that this
	 * project references, either directly or indirectly through other plugins.
	 * <p>
	 * It is used by launches to get the fragment libraries since these are not
	 * added by default to the classpath for a project by PDE. That is because they
	 * aren't needed for compilation purposes, but they are needed for running.
	 * 
	 * @param controller the controller to contribute to.
	 * @param info configuration info for the project
	 * @throws CoreException
	 * 
	 * @since 1.0.2
	 */
	public abstract void getPDEContributions(IConfigurationContributionController controller, IConfigurationContributionInfo info) throws CoreException;
}

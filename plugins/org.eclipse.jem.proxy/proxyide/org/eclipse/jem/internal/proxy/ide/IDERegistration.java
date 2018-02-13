/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jem.internal.proxy.ide;
/*


 */

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;

import org.eclipse.jem.internal.proxy.core.*;
import org.eclipse.jem.internal.proxy.ide.awt.IDERegisterAWT;
import org.eclipse.jem.internal.proxy.remote.LocalFileConfigurationContributorController;
/**
 * This is the registration class for starting an IDERemote VM.
 */

public class IDERegistration {
	
	public static ProxyFactoryRegistry startAnImplementation(
		IConfigurationContributor[] contributors,
		boolean attachAWT,
		IProject project,
		String vmName,
		String pluginName,
		IProgressMonitor pm) throws CoreException {
			IDERegistration idereg = new IDERegistration(pluginName);
			return idereg.startImplementation(contributors, attachAWT, project, vmName, pm); 
		}
	
	public IDERegistration() {
	}
	
	private IDERegistration(String pluginName) {
		this.pluginName = pluginName;
	}
	private String pluginName;	

	/**
	 * This will create a remote VM and return an initialized REMProxyFactoryRegistry.
	 * Passed in are:
	 *      project: The project this is being started on. Must not be null and must be a JavaProject. (Currently ignored for IDE).
	 *      attachAWT: Should AWT be attached to this implementation.
	 *      contributors: Contributors to the configuration. Can be null.
	 *      pm: ProgressMonitor to use. Must not be null.
	 *      vmName: Name for the vm. Can be null.
	 */
	public ProxyFactoryRegistry startImplementation(
		IConfigurationContributor[] contributors,
		boolean attachAWT,
		IProject project,
		String vmName,
		IProgressMonitor pm)
		throws CoreException {

		URL[] classPaths = null;
		IJavaProject javaProject = null;
		if (project != null) {
			javaProject = JavaCore.create(project);
			// Add in the paths for the project	 	
			classPaths = ProxyLaunchSupport.convertStringPathsToURL(JavaRuntime.computeDefaultRuntimeClassPath(javaProject));
		} else
			classPaths = new URL[0];

		final IJavaProject jp = javaProject;

		final ProxyLaunchSupport.LaunchInfo launchInfo = new ProxyLaunchSupport.LaunchInfo();
		contributors = ProxyLaunchSupport.fillInLaunchInfo(contributors == null ? ProxyLaunchSupport.EMPTY_CONFIG_CONTRIBUTORS : contributors, launchInfo, jp != null ? jp.getElementName() : null);
		final LocalFileConfigurationContributorController controller = new LocalFileConfigurationContributorController(classPaths, new URL[3][], launchInfo);
		final IConfigurationContributor[] contribs = contributors;
		for (int i = 0; i < contributors.length; i++) {
			final int ii = i;
			// Run in safe mode so that anything happens we don't go away.
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// Don't need to do anything. Platform.run logs it for me.
				}

				public void run() throws Exception {
					contribs[ii].initialize(launchInfo.getConfigInfo());
				}
			});				
		}			
		for (int i = 0; i < contributors.length; i++) {
			final int ii = i;
			// Run in safe mode so that anything happens we don't go away.
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// Don't need to do anything. Platform.run logs it for me.
				}

				public void run() throws Exception {
					contribs[ii].contributeClasspaths(controller);
				}
			});				
		}
		classPaths = controller.getFinalClasspath();

		final BaseProxyFactoryRegistry registry = (BaseProxyFactoryRegistry) createIDEProxyFactoryRegistry(vmName, pluginName, classPaths);
		ProxyLaunchSupport.performExtensionRegistrations(registry, launchInfo);
		for (int i = 0; i < contribs.length; i++) {
			final int ii = i;
			// Run in safe mode so that anything happens we don't go away.
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// Don't need to do anything. Platform.run logs it for me.
				}

				public void run() throws Exception {
					contribs[ii].contributeToRegistry(registry);
				}
			});	
		}

		return registry;
	}

	public static ProxyFactoryRegistry createIDEProxyFactoryRegistry(String aName, String aPluginName, URL[] otherURLs) {
		// Create the registry.
		IDEProxyFactoryRegistry registry =
			new IDEProxyFactoryRegistry(aName, IDEProxyFactoryRegistry.createSpecialLoader(aPluginName, otherURLs));
		initRegistry(registry);
		return registry;
	}

	public static ProxyFactoryRegistry createIDEProxyFactoryRegistry(String aName, ClassLoader loader) {
		// Create the registry.
		IDEProxyFactoryRegistry registry = new IDEProxyFactoryRegistry(aName, loader);
		initRegistry(registry);
		return registry;
	}

	private static void initRegistry(IDEProxyFactoryRegistry registry) {
		new IDEStandardBeanTypeProxyFactory(registry);
		new IDEStandardBeanProxyFactory(registry);
		new IDEMethodProxyFactory(registry);
		// Always support AWT for now
		IDERegisterAWT.registerAWT(registry);
	}
}

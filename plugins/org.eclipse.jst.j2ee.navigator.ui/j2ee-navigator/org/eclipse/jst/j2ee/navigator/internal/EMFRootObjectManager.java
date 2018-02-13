/*******************************************************************************
 * Copyright (c) 2003, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.j2ee.navigator.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelEvent;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelListener;

/**
 * Root Manager for Java EE EMF providers
 */
public class EMFRootObjectManager implements EditModelListener, IResourceChangeListener, IResourceDeltaVisitor {



	private final Map projectEditModels = new HashMap();
	private final Map projectRootObjects = new HashMap();

	private final List listeners = new ArrayList();

	public interface IRefreshHandlerListener {
		void onRefresh(Object element);
	}

	/**
	 *  
	 */
	public EMFRootObjectManager() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}



	public void dispose() {
		if (projectEditModels == null)
			return;
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		EditModel editModel = null;
		Object[] keys = projectEditModels.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			editModel = (EditModel) projectEditModels.remove(keys[i]);
			if (editModel != null) {
				editModel.removeListener(this);
				editModel.releaseAccess(this);
			}
		}
	}

	public Object getRootObject(IProject project) {
		if (project == null || !project.isAccessible())
			return null;

		Object rootObject = null;
		synchronized (projectRootObjects) {
			rootObject = projectRootObjects.get(project);

			if (rootObject == null || ((EObject) rootObject).eResource() == null) {
				EditModel editModel = getEditModelForProject(project);
				if (editModel != null) {
					rootObject = editModel.getPrimaryRootObject();
					projectRootObjects.put(project, rootObject);
				}
			}
		}
		return rootObject;
	}


	protected EditModel getEditModelForProject(IProject project) {
		if (project == null)
			return null;
		EditModel editModel = null;
		synchronized (projectEditModels) {
			editModel = (EditModel) projectEditModels.get(project);
			IVirtualComponent component = ComponentCore.createComponent(project);
			ArtifactEdit artifactEdit = null;
			try {
				if (editModel == null && project.isAccessible()) {
					artifactEdit = ArtifactEdit.getArtifactEditForRead(component);
					if (artifactEdit != null) {
						editModel = (EditModel) artifactEdit.getAdapter(EditModel.class);
						if (editModel != null) {
							editModel.addListener(this);
							addMapping(project, editModel);
						}
					}
				}
			} finally {
				if (artifactEdit!=null)
					artifactEdit.dispose();
			}
		}
		return editModel;
	}

	protected void addMapping(IProject project, EditModel editModel) {
		projectEditModels.put(project, editModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModelListener#editModelChanged(org.eclipse.wst.common.internal.emfworkbench.integration.EditModelEvent)
	 */
	public void editModelChanged(EditModelEvent anEvent) {
		IProject affectedProject = anEvent.getEditModel().getProject();
		switch (anEvent.getEventCode()) {
			case EditModelEvent.UNLOADED_RESOURCE :
			case EditModelEvent.REMOVED_RESOURCE : {
				Object oldRootObject = projectRootObjects.remove(affectedProject);
				if (oldRootObject != null)
					notifyListeners(affectedProject);
			}
				break;
			case EditModelEvent.SAVE : {
				Object oldRootObject = projectRootObjects.get(affectedProject);
				if (oldRootObject == null)
					notifyListeners(affectedProject);
			}
				break;
			case EditModelEvent.PRE_DISPOSE :
				disposeCache(affectedProject);
				break;
			default :
				break;
		}
	}


	/**
	 * @param aProject
	 */
	private void notifyListeners(IProject aProject) {
		for (int x = 0; x < listeners.size(); ++x) {
			IRefreshHandlerListener refreshHandler = (IRefreshHandlerListener) listeners.get(x);
			refreshHandler.onRefresh(aProject);
		}

	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		final IResourceDelta delta = event.getDelta();

		if (delta != null) {
			try {
				delta.accept(EMFRootObjectManager.this);
			} catch (CoreException e) {
				Logger.getLogger().logError(e);
			} catch (SWTException swte) {
				Logger.getLogger().logError(swte);
			} catch (SWTError swte) {
				Logger.getLogger().logError(swte);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		if (resource != null) {
			switch (resource.getType()) {
				case IResource.ROOT :
					return true;
				case IResource.PROJECT :
					boolean projectOpenStateChanged = ((delta.getFlags() & IResourceDelta.OPEN) != 0);
					if (delta.getKind() == IResourceDelta.REMOVED || projectOpenStateChanged)
						disposeCache((IProject) resource);
					return false;
			}
		}
		return false;
	}


	protected void disposeCache(IProject affectedProject) {
		synchronized (projectEditModels) {
			projectRootObjects.remove(affectedProject);
			//System.out.println("REMOVING edit model: " + affectedProject.getName());
			EditModel editModel = (EditModel) projectEditModels.remove(affectedProject);
			//System.out.println("REMOVED edit model " + affectedProject.getName());
			if (editModel != null) {
				editModel.removeListener(this);
				editModel.releaseAccess(this);
				//System.out.println("RELEASED edit model " + affectedProject.getName());
			}
		}
	}

	public void addRefreshHandlerListener(IRefreshHandlerListener aListener) {
		synchronized (getListeners()) {
			if (aListener != null && !getListeners().contains(aListener))
				getListeners().add(aListener);
		}
	}

	public void removeRefreshHandlerListener(IRefreshHandlerListener aListener) {
		synchronized (listeners) {
			listeners.remove(aListener);
		}
	}

	/**
	 * Insert the method's description here. Creation date: (4/11/2001 4:42:58 PM)
	 * 
	 * @return java.util.List
	 */
	protected java.util.List getListeners() {
		return listeners;
	}

}

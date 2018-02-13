/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Feb 17, 2005
 */
package org.eclipse.jst.j2ee.navigator.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelEvent;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelListener;
import org.eclipse.jst.j2ee.navigator.internal.plugin.J2EENavigatorPlugin;

public class FlexibleEMFModelManager extends EMFModelManager implements EditModelListener{

	private List rootObjects = new ArrayList();
	private static final Object[] EMPTY_OBJECT = new Object[0];
	private final List artifactEdits = new ArrayList();
	private final Map artifactEditToRootObject = new HashMap();
	
	/**
	 * @param aProject
	 * @param provider
	 */
	public FlexibleEMFModelManager(IProject aProject, EMFRootObjectProvider provider) {
		super(aProject,provider);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.j2ee.navigator.internal.EMFModelManager#getModels()
	 */
	@Override
	public Object[] getModels() {
		try {
		
			IProject project = getProject();
			if ( project== null || !project.isAccessible())
				return EMPTY_OBJECT;
			
			synchronized (rootObjects) {
				if (!isValid(rootObjects)) {
					rootObjects = getRootObjects();
				}
			}
		} catch (Exception ex) {
			J2EENavigatorPlugin.logError(ex);
		}
		if (rootObjects != null) {
			 return rootObjects.toArray() ;
		}
		return EMPTY_OBJECT;
				
	}
	
	private boolean isValid(List rootObjects) {
		if (rootObjects == null || rootObjects.size()==0) 
			return false;
		for (int x=0; x< rootObjects.size(); ++x) {
			EObject eObject = (EObject) rootObjects.get(x);
			if (eObject != null && eObject.eResource() == null) 
				return false;
		}
		return true;
	}
	
	/**
	 * @param project
	 * @return
	 */
	private List getRootObjects() {
	  	  List artifacts = getArtifactEdits();
	  	  List flexibleObjects = new ArrayList();
	  	  ArtifactEdit artifactEdit = null;
	  	  EObject contentModelRoot = null;
	  	  for (int x=0; x< artifacts.size(); ++x) {
	  	  	 artifactEdit = (ArtifactEdit)artifacts.get(x);
	  	     contentModelRoot = artifactEdit.getContentModelRoot();
			 if (contentModelRoot != null) {
		  	     flexibleObjects.add(contentModelRoot);
		  	     if (!rootObjects.contains(contentModelRoot))
		  	    	 rootObjects.add(contentModelRoot);
		  	     artifactEditToRootObject.put(artifactEdit,contentModelRoot);
			 }
	  	  }
	  	  return flexibleObjects;
	}
	
	protected List getArtifactEdits() {
		
		IProject project = getProject();
		if (project == null)
			return null;
		synchronized (artifactEdits) {
			if (artifactEdits.size() ==0 && project.isAccessible()) {
				ArtifactEdit artifactEdit = null;
				try{
					artifactEdit = ArtifactEdit.getArtifactEditForRead(project);
					if(artifactEdit != null) {
						artifactEdits.add(artifactEdit);
						artifactEdit.addListener(this);
					}
				} catch(Exception e){
					J2EENavigatorPlugin.logError(e);
				}
			}
		}
		return artifactEdits;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.j2ee.navigator.internal.EMFModelManager#dispose()
	 */
	@Override
	public void dispose() {
		if (artifactEdits != null) {
			for (int x=0; x< artifactEdits.size(); ++x) {
				ArtifactEdit artifactEdit = (ArtifactEdit)artifactEdits.get(x);
				if (artifactEdit != null) {
					artifactEdit.removeListener(this);
					artifactEdit.dispose();
				}
				
			}
		}
		rootObjects.clear();
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModelListener#editModelChanged(org.eclipse.wst.common.internal.emfworkbench.integration.EditModelEvent)
	 */
	public void editModelChanged(EditModelEvent anEvent) {
		IProject affectedProject = anEvent.getEditModel().getProject();
		
		switch (anEvent.getEventCode()) {
			case EditModelEvent.UNLOADED_RESOURCE :
			case EditModelEvent.REMOVED_RESOURCE : {
				Object oldRootObject = removeRootObject(anEvent.getEditModel(),affectedProject);
				getRootObjects();
				if (oldRootObject != null)
					notifyListeners(affectedProject);
				}
				break;
			case EditModelEvent.SAVE : {
				Object oldRootObject = getRootObject(anEvent.getEditModel(),affectedProject);
				if (oldRootObject == null)
					notifyListeners(affectedProject);
			}
				break;
			case EditModelEvent.PRE_DISPOSE :
					dispose(anEvent.getEditModel(),affectedProject);
				break;
			default :
				break;
		}
		
	}

	

	/**
	 * @param editModel
	 * @param affectedProject
	 * @return
	 */
	private Object getRootObject(EditModel editModel, IProject project) {
		ArtifactEdit artifactEdit = getArtifactEdit(editModel,project);
		if (artifactEdit != null) {
			return artifactEditToRootObject.get(artifactEdit);
		}
		return null;
	}

	/**
	 * @param editModel
	 * @param affectedProject
	 * @return
	 */
	private Object removeRootObject(EditModel editModel, IProject project) {
		ArtifactEdit artifactEdit = getArtifactEdit(editModel,project);
		if (artifactEdit != null) {
			rootObjects.remove(artifactEditToRootObject.get(artifactEdit));
			artifactEditToRootObject.remove(artifactEdit);
		}
		return artifactEdit;
	}

	/**
	 * @param affectedProject
	 * @param editModel
	 */
	private void dispose( EditModel editModel, IProject project) {
		ArtifactEdit artifactEdit = getArtifactEdit(editModel,project);
		if (artifactEdit != null) {
			artifactEdit.removeListener(this);
			artifactEdits.remove(artifactEdit);
			artifactEditToRootObject.remove(artifactEdit);
		}
		
	}
	
	private ArtifactEdit getArtifactEdit(EditModel editModel, IProject project) {
		if (artifactEdits != null) {
			for (int x=0; x<artifactEdits.size(); ++x) {
				ArtifactEdit artifactEdit = (ArtifactEdit)artifactEdits.get(x);
				if (artifactEdit.hasEditModel(editModel)) 
					return artifactEdit;
			}
		}
		return null;
	}
	 
}

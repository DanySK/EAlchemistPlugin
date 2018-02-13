/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelEvent;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelListener;

public class NonFlexibleEMFModelManager extends EMFModelManager implements EditModelListener{

	private EditModel editModel;
	private Object rootObject;
	private static final Object[] EMPTY_OBJECT = new Object[0];
	/**
	 * @param aProject
	 * @param provider
	 */
	public NonFlexibleEMFModelManager(IProject aProject, EMFRootObjectProvider provider) {
		super(aProject, provider);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.j2ee.navigator.internal.EMFModelManager#getModels()
	 */
	@Override
	public Object[] getModels() {
		if (getProject() == null || !getProject().isAccessible())
			return EMPTY_OBJECT;
		
		//synchronized (rootObject) {
			if (rootObject == null || ((EObject) rootObject).eResource() == null) {
				EditModel editModel = getEditModel();
				if (editModel != null) {
					rootObject = editModel.getPrimaryRootObject();
				}
			}
		//}
		if (rootObject==null) 
			return EMPTY_OBJECT;
		Object[] objects = new Object[1];
		objects[0] = rootObject;
		return objects;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.j2ee.navigator.internal.EMFModelManager#dispose()
	 */
	@Override
	public void dispose() {
		if (editModel != null) {
			editModel.removeListener(this);
			editModel.releaseAccess(this);
			editModel = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModelListener#editModelChanged(org.eclipse.wst.common.internal.emfworkbench.integration.EditModelEvent)
	 */
	public void editModelChanged(EditModelEvent anEvent) {
		IProject affectedProject = anEvent.getEditModel().getProject();
		switch (anEvent.getEventCode()) {
			case EditModelEvent.UNLOADED_RESOURCE :
			case EditModelEvent.REMOVED_RESOURCE : 
				if (rootObject != null){
					notifyListeners(affectedProject);
			     }
				break;
			case EditModelEvent.SAVE : {
				if (rootObject == null)
					notifyListeners(affectedProject);
				}
				break;
			case EditModelEvent.PRE_DISPOSE :
					dispose();
				break;
			default :
				break;
		}
		
	}
	
	

	protected EditModel getEditModel() {
		IProject project = getProject();
		if (project == null)
			return null;
		
		//TODO fix up
		//synchronized (editModel) {
//			if (editModel == null && project.isAccessible()) {
//				//System.out.println("getEditModelForProject " + project.getName());
//				J2EENature nature = J2EENature.getRegisteredRuntime(project);
//				if (nature != null) {
//					editModel = nature.getJ2EEEditModelForRead(this);
//					if (editModel != null) {
//						editModel.addListener(this);
//					}
//				}
//			}
		//}
		return editModel;
	}
	
	protected void disposeCache(IProject affectedProject) {
		//synchronized (editModel) {
			if (editModel != null) {
				editModel.removeListener(this);
				editModel.releaseAccess(this);
				editModel = null;
			}
		//}
	}


}

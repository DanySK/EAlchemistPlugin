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
/*
 * Created on Jun 11, 2004
 */
package org.eclipse.jst.j2ee.internal.war.ui.util;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.jst.j2ee.internal.web.plugin.WebPlugin;
import org.eclipse.jst.j2ee.internal.web.providers.WebAppEditResourceHandler;
import org.eclipse.jst.j2ee.webapplication.WebApp;


/**
 * @author jialin
 */
public class WebFilterMappingGroupItemProvider extends WebGroupItemProvider {

	/**
	 * @param adapterFactory
	 */
	public WebFilterMappingGroupItemProvider(AdapterFactory adapterFactory, WeakReference weakWebApp) {
		super(adapterFactory,weakWebApp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.edit.provider.ITreeItemContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Collection getChildren(Object object) {
		Object webApp = weakWebApp.get();
		if(null != webApp){
			return ((WebApp)webApp).getFilterMappings();
		}
		return Collections.EMPTY_LIST;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.edit.provider.ItemProvider#getImage()
	 */
	@Override
	public Object getImage(Object object) {
		return WebPlugin.getDefault().getImage("filter_mapping"); //$NON-NLS-1$
	}
	/* (non-Javadoc)
	 * @see org.eclipse.emf.edit.provider.ItemProvider#getText()
	 */
	@Override
	public String getText(Object object) {
		return WebAppEditResourceHandler.getString("Filter_Mappings_1"); //$NON-NLS-1$ 
	}
	/* (non-Javadoc)
	 * @see org.eclipse.emf.edit.provider.ITreeItemContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object object) {
		return !getChildren(object).isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.edit.provider.ITreeItemContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object object) {
		return weakWebApp.get();
	}
}

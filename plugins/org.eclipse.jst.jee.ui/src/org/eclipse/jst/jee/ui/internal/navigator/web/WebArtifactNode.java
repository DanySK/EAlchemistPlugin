/***********************************************************************
 * Copyright (c) 2008 by SAP AG, Walldorf. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SAP AG - initial API and implementation
 ***********************************************************************/
package org.eclipse.jst.jee.ui.internal.navigator.web;

import org.eclipse.jst.javaee.core.JavaEEObject;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * Wrapper node for representing string values of the model in Deployment descriptor tree.
 * (e.g. Welcome Files)
 * 
 * @author Dimitar Giormov
 *
 */
public class WebArtifactNode implements JavaEEObject{

	private Image image;
	private String text;
 	private JavaEEObject javaee; 
 	  
	public WebArtifactNode(JavaEEObject javaee, String text, Image image) { 
		super(); 
 		this.javaee = javaee; 
		this.text = text;
		this.image = image; 
	} 


	public Image getImage() {
		return image;
	}

	public String getText() {
		return text;
	}

	public JavaEEObject getJavaEEObject() {
		return javaee;
	}

}

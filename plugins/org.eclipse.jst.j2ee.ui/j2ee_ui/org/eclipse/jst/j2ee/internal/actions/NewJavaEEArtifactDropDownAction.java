/*******************************************************************************
 * Copyright (c) 2007,2009 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Kaloyan Raev, kaloyan.raev@sap.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.j2ee.internal.actions;

/**
 * A wizard is added to the "New Java EE Artifact" drop down if it has a parameter 'javaeeartifact':
 *     <wizard
 *         name="My Java EE Project Wizard"
 *         icon="icons/wiz.gif"
 *         category="mycategory"
 *         id="xx.MyWizard">
 *         <class class="org.xx.MyWizard">
 *             <parameter name="javaeeartifact" value="true"/>
 *         </class> 
 *         <description>
 *             My Wizard
 *         </description>
 *      </wizard>
 */
public class NewJavaEEArtifactDropDownAction extends NewJavaEEDropDownAction {
	
	private final static String ATT_JAVAEEARTIFACT = "javaeeartifact";//$NON-NLS-1$

	@Override
	protected String getTypeAttribute() {
		return ATT_JAVAEEARTIFACT;
	}

}

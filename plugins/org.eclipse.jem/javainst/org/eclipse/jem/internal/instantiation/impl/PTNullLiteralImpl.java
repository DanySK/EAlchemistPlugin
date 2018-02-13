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
package org.eclipse.jem.internal.instantiation.impl;
/*


 */
import org.eclipse.emf.ecore.EClass;

import org.eclipse.jem.internal.instantiation.*;
import org.eclipse.jem.internal.instantiation.InstantiationPackage;
import org.eclipse.jem.internal.instantiation.PTNullLiteral;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Null Literal</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class PTNullLiteralImpl extends PTExpressionImpl implements PTNullLiteral {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PTNullLiteralImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return InstantiationPackage.eINSTANCE.getPTNullLiteral();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.instantiation.impl.ExpressionImpl#accept0(org.eclipse.jem.internal.instantiation.ParseVisitor)
	 */
	protected void accept0(ParseVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}	

} //NullLiteralImpl

/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jem.internal.proxy.initParser;
/*


 */


public class BooleanLiteral extends Expression {
	public boolean value;
public BooleanLiteral(boolean aValue){

	value = aValue;

}

public boolean isComplete(){
	return true;
}
/**
 * evaluate method comment.
 */
public Object evaluate() {
	return new Boolean(value);
}
/**
 * evaluate method comment.
 */
public Class getTypeClass(){
	return Boolean.TYPE;
}
/**
 * true or false cannot consume any kind of expression so we must return null
 */
public Expression push(char[] token , char delimiter){

	return null;	

}

/**
 * This is primitive
 */
public boolean isPrimitive() {
	return true;
}
	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.core.initParser.Expression#getTypeClassName()
	 */
	protected String getTypeClassName() {
		return Boolean.TYPE.getName();
	}

}

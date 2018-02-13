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
package org.eclipse.jem.internal.proxy.ide;
/*


 */

import org.eclipse.jem.internal.proxy.core.IIntegerBeanProxy;

/**
 * IDE Implementation of IIntegerBeanTypeProxy
 */
final class IDEIntegerTypeBeanTypeProxy extends IDEPrimitiveBeanTypeProxy {
// Some values are cache'd for speed
	private final IDEIntegerBeanProxy zeroProxy;
	private final IDEIntegerBeanProxy oneProxy;
	private final IDEIntegerBeanProxy twoProxy;
	private final IDEIntegerBeanProxy threeProxy;
IDEIntegerTypeBeanTypeProxy(IDEProxyFactoryRegistry aRegistry, Class aClass) {
	super(aRegistry, aClass);
	// Create the cached values
	zeroProxy = new IDEIntegerBeanProxy(aRegistry, new Integer(0), this );
	oneProxy = new IDEIntegerBeanProxy(aRegistry, new Integer(1), this );
	twoProxy = new IDEIntegerBeanProxy(aRegistry, new Integer(2), this );
	threeProxy = new IDEIntegerBeanProxy(aRegistry, new Integer(3), this );
		
}
/**
 * Static helper to create a bean proxy
 * Package protected because everyone should go through the factory API
 * that is defined as part of IBeanProxyFactory
 */
IIntegerBeanProxy createIntegerBeanProxy(int anInt) {
	switch(anInt){
		case 0: return zeroProxy ;
		case 1: return oneProxy ;
		case 2: return twoProxy ;
		case 3: return threeProxy ;
		default: return new IDEIntegerBeanProxy(fProxyFactoryRegistry,new Integer(anInt),this);
	}
}
/* Specialized from IDEBeanTypeProxy to ensure IIntegerBeanProxies are created correctly
 */
protected IIDEBeanProxy newBeanProxy(Object anObject){
	Number n = anObject instanceof Character ? new Integer(((Character) anObject).charValue()) : (Number) anObject;
	switch(n.intValue()){
		case 0: return zeroProxy ;
		case 1: return oneProxy ;
		case 2: return twoProxy ;
		case 3: return threeProxy ;
		default: return new IDEIntegerBeanProxy(fProxyFactoryRegistry, n, this);
	}
	
	
}
int getPrimitiveType(){
	return INTEGER;
}
}


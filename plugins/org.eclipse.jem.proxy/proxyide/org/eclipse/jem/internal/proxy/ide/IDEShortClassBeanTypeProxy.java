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

import org.eclipse.jem.internal.proxy.core.*;
/**
 * Short BeanType Proxy.
 */
final class IDEShortClassBeanTypeProxy extends IDENumberBeanTypeProxy {
protected IDEShortClassBeanTypeProxy(IDEProxyFactoryRegistry aRegistry, Class aClass) {
	super(aRegistry, aClass, new Short((short)0));
}
INumberBeanProxy createShortBeanProxy(Short aShort){
	return new IDENumberBeanProxy(fProxyFactoryRegistry,aShort,this);
}
}

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
 * Char BeanType Proxy.
 */
final class IDECharacterClassBeanTypeProxy extends IDEBeanTypeProxy {
	
protected IDECharacterClassBeanTypeProxy(IDEProxyFactoryRegistry aRegistry, Class aClass) {
	super(aRegistry, aClass);
}
ICharacterBeanProxy createCharacterBeanProxy(Character aChar){
	return new IDECharacterBeanProxy(fProxyFactoryRegistry,aChar,this);
}
}

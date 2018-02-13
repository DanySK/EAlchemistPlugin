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
package org.eclipse.jem.internal.proxy.ide.awt;
/*


 */


import org.eclipse.jem.internal.proxy.awt.*;
import org.eclipse.jem.internal.proxy.core.*;
import org.eclipse.jem.internal.proxy.ide.IDEProxyFactoryRegistry;
/**
 * Standard AWT Bean Proxy Factory.
 * Package protected because it should not referenced
 * outside of the package other than through the interface.
 */
class IDEStandardAWTBeanProxyFactory implements IStandardAwtBeanProxyFactory {

	final IDEStandardAWTBeanTypeProxyFactory fAWTBeanTypeFactory;
	
public IDEStandardAWTBeanProxyFactory(IDEProxyFactoryRegistry factory) {
	factory.registerBeanProxyFactory(IStandardAwtBeanProxyFactory.REGISTRY_KEY, this);
	fAWTBeanTypeFactory = (IDEStandardAWTBeanTypeProxyFactory)factory.getBeanTypeProxyFactoryExtension(IDEStandardAWTBeanTypeProxyFactory.BEAN_TYPE_FACTORY_KEY);
}
public IDimensionBeanProxy createDimensionBeanProxyWith(int width, int height){
	return fAWTBeanTypeFactory.dimensionType.createDimensionBeanProxy(width,height);
}

public IPointBeanProxy createPointBeanProxyWith(int x, int y){
	return fAWTBeanTypeFactory.pointType.createPointBeanProxy(x,y);
}
public IRectangleBeanProxy createBeanProxyWith(int x, int y, int width, int height){
	return fAWTBeanTypeFactory.rectangleType.createRectangleBeanProxy(x,y,width,height);
}
public void terminateFactory(boolean wait) {
}
}


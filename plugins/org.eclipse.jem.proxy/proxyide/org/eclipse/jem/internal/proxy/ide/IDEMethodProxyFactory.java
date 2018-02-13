/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * $RCSfile: IDEMethodProxyFactory.java,v $ $Revision: 1.14 $ $Date: 2006/05/17 20:13:05 $
 */
package org.eclipse.jem.internal.proxy.ide;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.jem.internal.proxy.common.AmbiguousMethodException;
import org.eclipse.jem.internal.proxy.common.MethodHelper;
import org.eclipse.jem.internal.proxy.core.*;

public class IDEMethodProxyFactory implements IMethodProxyFactory {

	final protected IDEProxyFactoryRegistry fProxyFactoryRegistry;

	final protected static Map primLookup = new HashMap();

	IDEBeanTypeProxy accessibleType, fieldType, methodType, constructorType;
	// Cached copy of a few typical method type proxies.
	static {
		primLookup.put("int", Integer.TYPE); //$NON-NLS-1$
		primLookup.put("char", Character.TYPE); //$NON-NLS-1$
		primLookup.put("long", Long.TYPE); //$NON-NLS-1$
		primLookup.put("short", Short.TYPE); //$NON-NLS-1$
		primLookup.put("double", Double.TYPE); //$NON-NLS-1$
		primLookup.put("boolean", Boolean.TYPE); //$NON-NLS-1$
		primLookup.put("byte", Byte.TYPE); //$NON-NLS-1$
		primLookup.put("float", Float.TYPE); //$NON-NLS-1$
	}

	IDEMethodProxyFactory(IDEProxyFactoryRegistry aRegistry) {
		fProxyFactoryRegistry = aRegistry;
		fProxyFactoryRegistry.registerMethodProxyFactory(this);
		accessibleType = new IDEBeanTypeProxy(aRegistry, AccessibleObject.class);
		fieldType = new IDEFieldTypeProxy(aRegistry);
		methodType = new IDEMethodTypeProxy(aRegistry);
		constructorType = new IDEConstructorTypeProxy(aRegistry);
		fProxyFactoryRegistry.getBeanTypeProxyFactory().registerBeanTypeProxy(accessibleType, true);
		fProxyFactoryRegistry.getBeanTypeProxyFactory().registerBeanTypeProxy(fieldType, true);
		fProxyFactoryRegistry.getBeanTypeProxyFactory().registerBeanTypeProxy(methodType, true);
		fProxyFactoryRegistry.getBeanTypeProxyFactory().registerBeanTypeProxy(constructorType, true);
	}

	/**
	 * Return an instance of an IDEConstructorProxy This is package protected because the only people who can use this are priveledge objects that
	 * have the aMethod instance. These are part of the idevm package only. If you have to make this method public you are doing things incorrectly.
	 * To get a method proxy you must use the interface methods on IMethodProxyFactory to do lookup by string or else on IBeanTypeProxy that has
	 * getMethod(String) as well
	 */
	IConstructorProxy getConstructorProxy(Constructor aConstructor) {

		return (IConstructorProxy) constructorType.newBeanProxy(aConstructor);

	}
	
	IConstructorProxy[] getConstructors(Class aClass) {
		Constructor[] ctors = aClass.getConstructors();
		IConstructorProxy[] ctorProxies = new IConstructorProxy[ctors.length];
		for (int i = 0; i < ctors.length; i++) {
			ctorProxies[i] = getConstructorProxy(ctors[i]);
		}
		return ctorProxies;
	}

	IConstructorProxy[] getDeclaredConstructors(Class aClass) {
		Constructor[] ctors = aClass.getDeclaredConstructors();
		IConstructorProxy[] ctorProxies = new IConstructorProxy[ctors.length];
		for (int i = 0; i < ctors.length; i++) {
			ctorProxies[i] = getConstructorProxy(ctors[i]);
		}
		return ctorProxies;
	}

	IConstructorProxy getConstructorProxy(Class aClass, Class[] args) {

		try {
			Constructor ctor = aClass.getConstructor(args);
			return getConstructorProxy(ctor);
		} catch (NoSuchMethodException exc) {
			return null;
		}
	}

	IConstructorProxy getDeclaredConstructorProxy(Class aClass, Class[] args) {

		try {
			Constructor ctor = aClass.getDeclaredConstructor(args);
			return getConstructorProxy(ctor);
		} catch (NoSuchMethodException exc) {
			return null;
		}
	}

	IFieldProxy[] getFields(Class aClass) {
		Field[] fields = aClass.getFields();
		IFieldProxy[] fieldProxies = new IFieldProxy[fields.length];
		for (int i = 0; i < fields.length; i++) {
			fieldProxies[i] = getFieldProxy(fields[i]);
		}
		return fieldProxies;
	}

	IFieldProxy[] getDeclaredFields(Class aClass) {
		Field[] fields = aClass.getDeclaredFields();
		IFieldProxy[] fieldProxies = new IFieldProxy[fields.length];
		for (int i = 0; i < fields.length; i++) {
			fieldProxies[i] = getFieldProxy(fields[i]);
		}
		return fieldProxies;
	}

	/**
	 * Return an instance of an IDEFieldProxy This is package protected because the only people who can use this are priveledge objects that have the
	 * aField instance. These are part of the idevm package only. If you have to make this method public you are doing things incorrectly. To get a
	 * field proxy you must use the interface methods on IBeanTypeProxy that has getField(String) as well
	 */
	IFieldProxy getFieldProxy(Field aField) {

		return (IFieldProxy) fieldType.newBeanProxy(aField);

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.core.IMethodProxyFactory#getFieldProxy(org.eclipse.jem.internal.proxy.core.IExpression, java.lang.String, java.lang.String)
	 */
	public IProxyField getFieldProxy(IExpression expression, String className, String fieldName) {
		// We are getting the class resolved through the expression. Might as well because it probably will
		// be needed again and this way when the expression is finished they will be resolved for later usage.
		IStandardBeanTypeProxyFactory beanTypeProxyFactory = fProxyFactoryRegistry.getBeanTypeProxyFactory();
		IProxyBeanType beanType = beanTypeProxyFactory.getBeanTypeProxy(expression, className);
		return beanType.getFieldProxy(expression, fieldName);
	}

	/**
	 * Return an instance of an IDEMethodProxy This is package protected because the only people who can use this are priveledge objects that have the
	 * aMethod instance. These are part of the idevm package only. If you have to make this method public you are doing things incorrectly. To get a
	 * method proxy you must use the interface methods on IMethodProxyFactory to do lookup by string or else on IBeanTypeProxy that has
	 * getMethod(String) as well
	 */
	IDEMethodProxy getMethodProxy(Method aMethod) {

		return (IDEMethodProxy) methodType.newBeanProxy(aMethod);

	}

	public IMethodProxy getMethodProxy(String className, String methodName, String[] parameterTypes) {
		try {
			Class aClass = fProxyFactoryRegistry.loadClass(className);
			return getMethodProxy(aClass, methodName, parameterTypes);
		} catch (ClassNotFoundException exc) {
			ProxyPlugin.getPlugin().getLogger().log(new Status(IStatus.WARNING, ProxyPlugin.getPlugin().getBundle().getSymbolicName(), 0, "", exc)); //$NON-NLS-1$
			return null;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.core.IMethodProxyFactory#getMethodProxy(org.eclipse.jem.internal.proxy.core.IExpression, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public IProxyMethod getMethodProxy(IExpression expression, String className, String methodName, String[] parameterTypes) {
		// We are getting the class and parmtypes resolved through the expression. Might as well because they probably will
		// be needed again and this way when the expression is finished they will be resolved for later usage.
		IStandardBeanTypeProxyFactory beanTypeProxyFactory = fProxyFactoryRegistry.getBeanTypeProxyFactory();
		IProxyBeanType beanType = beanTypeProxyFactory.getBeanTypeProxy(expression, className);
		IProxyBeanType[] parmTypes = getParameterTypes(expression, parameterTypes, beanTypeProxyFactory);
		return beanType.getMethodProxy(expression, methodName, parmTypes);
	}	
	
	/**
	 * Helper method for Beantypes and Proxy bean types to get the proxy.
	 * @param expression
	 * @param classType
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 * 
	 * @since 1.1.0
	 */
	public IProxyMethod getMethodProxy(IExpression expression, IProxyBeanType classType, String methodName, String[] parameterTypes) {
		// We are getting the parmtypes resolved through the expression. Might as well because they probably will
		// be needed again and this way when the expression is finished they will be resolved for later usage.
		IStandardBeanTypeProxyFactory beanTypeProxyFactory = fProxyFactoryRegistry.getBeanTypeProxyFactory();
		IProxyBeanType[] parmTypes = getParameterTypes(expression, parameterTypes, beanTypeProxyFactory);
		return classType.getMethodProxy(expression, methodName, parmTypes);
	}

	/**
	 * @param expression
	 * @param parameterTypes
	 * @param beanTypeProxyFactory
	 * @return
	 * 
	 * @since 1.1.0
	 */
	protected IProxyBeanType[] getParameterTypes(IExpression expression, String[] parameterTypes, IStandardBeanTypeProxyFactory beanTypeProxyFactory) {
		IProxyBeanType[] parmTypes;
		if (parameterTypes == null || parameterTypes.length == 0)
			parmTypes = null;
		else {
			parmTypes = new IProxyBeanType[parameterTypes.length];
			for (int i = 0; i < parameterTypes.length; i++) {
				parmTypes[i] = beanTypeProxyFactory.getBeanTypeProxy(expression, parameterTypes[i]);
			}
		}
		return parmTypes;
	}

	public IMethodProxy getMethodProxy(Class cls, String methodName, String[] parameterTypes) {
		try {
			Class[] parmClasses = null;
			if (parameterTypes != null) {
				parmClasses = new Class[parameterTypes.length];
				for (int i = 0; i < parmClasses.length; i++) {
					Class temp = (Class) primLookup.get(parameterTypes[i]);
					if (temp == null)
						temp = fProxyFactoryRegistry.loadClass(parameterTypes[i]);
					parmClasses[i] = temp;
				}
			}
			return getMethodProxy(cls.getMethod(methodName, parmClasses));
		} catch (ClassNotFoundException e) {
			ProxyPlugin.getPlugin().getLogger().log(new Status(IStatus.WARNING, ProxyPlugin.getPlugin().getBundle().getSymbolicName(), 0, "", e)); //$NON-NLS-1$
		} catch (NoSuchMethodException e) {
			ProxyPlugin.getPlugin().getLogger().log(new Status(IStatus.WARNING, ProxyPlugin.getPlugin().getBundle().getSymbolicName(), 0, "", e)); //$NON-NLS-1$
		}
		return null;
	}
	
	IMethodProxy getDeclaredMethodProxy(Class cls, String methodName, String[] parameterTypes) {
		try {
			Class[] parmClasses = null;
			if (parameterTypes != null) {
				parmClasses = new Class[parameterTypes.length];
				for (int i = 0; i < parmClasses.length; i++) {
					Class temp = (Class) primLookup.get(parameterTypes[i]);
					if (temp == null)
						temp = fProxyFactoryRegistry.loadClass(parameterTypes[i]);
					parmClasses[i] = temp;
				}
			}
			return getMethodProxy(cls.getDeclaredMethod(methodName, parmClasses));
		} catch (ClassNotFoundException e) {
			ProxyPlugin.getPlugin().getLogger().log(new Status(IStatus.WARNING, ProxyPlugin.getPlugin().getBundle().getSymbolicName(), 0, "", e)); //$NON-NLS-1$
		} catch (NoSuchMethodException e) {
			ProxyPlugin.getPlugin().getLogger().log(new Status(IStatus.WARNING, ProxyPlugin.getPlugin().getBundle().getSymbolicName(), 0, "", e)); //$NON-NLS-1$
		}
		return null;
	}	

	IDEMethodProxy getMethodProxy(Class aClass, String methodName, Class[] args) {
		try {
			Method method = aClass.getMethod(methodName, args);
			return getMethodProxy(method);
		} catch (NoSuchMethodException exc) {
			ProxyPlugin.getPlugin().getLogger().log(
					new Status(IStatus.WARNING, ProxyPlugin.getPlugin().getBundle().getSymbolicName(), 0, "Unable to find method " + aClass.getName() //$NON-NLS-1$
							+ ":" + methodName + " args=" + args, exc)); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
	}
	
	IMethodProxy getDeclaredMethodProxy(Class aClass, String methodName, Class[] args) {
		try {
			Method method = aClass.getDeclaredMethod(methodName, args);
			return getMethodProxy(method);
		} catch (NoSuchMethodException exc) {
			ProxyPlugin.getPlugin().getLogger().log(
					new Status(IStatus.WARNING, ProxyPlugin.getPlugin().getBundle().getSymbolicName(), 0, "Unable to find method " + aClass.getName() //$NON-NLS-1$
							+ ":" + methodName + " args=" + args, exc)); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
	}	

	IMethodProxy getMethodProxy(IDEBeanTypeProxy aTypeProxy, String methodName, String[] parameterTypes) {
		return getMethodProxy(aTypeProxy.fClass, methodName, parameterTypes);
	}
	
	IMethodProxy[] getMethods(Class aClass) {
		Method[] methods = aClass.getMethods();
		IMethodProxy[] methodProxies = new IMethodProxy[methods.length];
		for (int i = 0; i < methods.length; i++) {
			methodProxies[i] = getMethodProxy(methods[i]);
		}
		return methodProxies;
	}
	
	IMethodProxy getCompatibleMethod(Class aClass, String methodName, Class[] parmTypes) throws NoSuchMethodException, AmbiguousMethodException {
		Method method = MethodHelper.findCompatibleMethod(aClass, methodName, parmTypes);
		return getMethodProxy(method);
	}
	
	IConstructorProxy getCompatibleConstructor(Class aClass, Class[] parmTypes) throws AmbiguousMethodException, NoSuchMethodException, IllegalAccessException {
		Constructor method = MethodHelper.findCompatibleConstructor(aClass, parmTypes);
		return getConstructorProxy(method);
	}
	

	IMethodProxy[] getDeclaredMethods(Class aClass) {
		Method[] methods = aClass.getDeclaredMethods();
		IMethodProxy[] methodProxies = new IMethodProxy[methods.length];
		for (int i = 0; i < methods.length; i++) {
			methodProxies[i] = getMethodProxy(methods[i]);
		}
		return methodProxies;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.internal.proxy.core.IMethodProxyFactory#getInvokable(java.lang.String, java.lang.String, java.lang.String[])
	 */
	public IInvokable getInvokable(String className, String methodName, String[] parameterTypes) {
		return getMethodProxy(className, methodName, parameterTypes);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.core.IBeanProxyFactory#terminateFactory(boolean)
	 */
	public void terminateFactory(boolean wait) {
	}

}

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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import org.eclipse.jem.internal.proxy.common.IVMServer;
import org.eclipse.jem.internal.proxy.core.*;
/**
 * This implementation runs the Beans inside the Eclipse IDE
 * It should only be used by plugins that can guarantee their their beans do
 * not change during the lifetime of the IDE and can run at the same JDK level
 * that the Eclipse IDE is running
 */

public class IDEProxyFactoryRegistry extends BaseProxyFactoryRegistry implements IVMServer {

	public static final String IDE_REGISTRY_TYPE_ID = "org.eclipse.jem.IDE";	//$NON-NLS-1$
	protected String fName;
	protected ClassLoader fClassLoader;
	protected IDECallbackRegistry fCallbackRegistry;
	
	/*
	 * Special classloader that firsts tries to load from bundle then tries from url paths so
	 * that bundle is treated as being at the head of the url paths.
	 * 
	 * @since 1.0.0
	 */
	private static class IDESpecialClassLoader extends URLClassLoader {
		
		private Bundle bundle;
		// This is the bundle that contains the IDE Eclipse code.
		// This must be checked first because there are IDE specific stuff that is used on the "remote vm" too and
		// so must be common.
		private Bundle ideBundle;	 

		public IDESpecialClassLoader(URL[] urls, Bundle bundle) {
			super(urls, null);
			ideBundle = ProxyPlugin.getPlugin().getBundle();
			this.bundle = bundle;
		}
		
		public IDESpecialClassLoader(URL[] urls) {
			this(urls, null);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.ClassLoader#findClass(java.lang.String)
		 */
		protected Class findClass(String name) throws ClassNotFoundException {
			try {
				return ideBundle.loadClass(name);
			} catch (ClassNotFoundException e) {
				if (bundle != null) {
					try {
						return bundle.loadClass(name);
					} catch (ClassNotFoundException e1) {
					}
				}
				return super.findClass(name);
			}
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.ClassLoader#findResource(java.lang.String)
		 */
		public URL findResource(String name) {
			URL r = ideBundle.getResource(name);
			if (r == null && bundle != null)
				r = bundle.getResource(name);
			return r != null ? r : super.findResource(name);
		}
}

/**
 * Create a special loader that has the plugin classloader of the passed in plugin
 * as the parent loader, plus all of the urls, plus the remotevm.jar file that is
 * needed by IDE Proxy to work.
 */	
public static ClassLoader createSpecialLoader(String pluginName, URL[] otherURLs) {
	Bundle bundle = pluginName != null ? Platform.getBundle(pluginName) : null;
	
	URL[] mustHaveUrls = ProxyPlugin.getPlugin().urlLocalizeAllFromBundleAndFragments(ProxyPlugin.getPlugin().getBundle(), "vm/remotevm.jar"); //$NON-NLS-1$
	
	URL[] urls = null;
	if (otherURLs != null) {
		urls = new URL[otherURLs.length+mustHaveUrls.length];
		System.arraycopy(mustHaveUrls, 0, urls, 0, mustHaveUrls.length);
		System.arraycopy(otherURLs, 0, urls, mustHaveUrls.length, otherURLs.length);
	} else 
		urls = mustHaveUrls;
		
	return bundle != null ? new IDESpecialClassLoader(urls, bundle) : new IDESpecialClassLoader(urls);
}

public IDEProxyFactoryRegistry(String aName, ClassLoader loader) {
	super(IDE_REGISTRY_TYPE_ID);
	fName = aName;
	fClassLoader = loader;
}

ClassLoader getPluginClassLoader(){
	return fClassLoader;
}

protected void registryTerminated(boolean wait){
	List runnables = null;
	synchronized (this) {
		runnables = shutdownRunnables;
		shutdownRunnables = null;
	}
	if (runnables != null) {
		for (Iterator itr = runnables.iterator(); itr.hasNext();) {
			try {
				((Runnable) itr.next()).run();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
}

Class loadClass(String aClassName) throws ClassNotFoundException, ExceptionInInitializerError, LinkageError {
	return fClassLoader.loadClass(aClassName);
}

public ICallbackRegistry getCallbackRegistry(){
	if ( fCallbackRegistry == null ){
		fCallbackRegistry = new IDECallbackRegistry(this);
	}
	return fCallbackRegistry;
}


/**
 * Get a bean proxy from the bean of the correct type.
 * 
 * @param returnType
 * @param bean
 * @return
 * 
 * @since 1.0.0
 */
IBeanProxy getBeanProxy(Class returnType, Object bean) {
	IDEStandardBeanTypeProxyFactory proxyFactory = (IDEStandardBeanTypeProxyFactory) this.getBeanTypeProxyFactory();
	if (!returnType.isPrimitive()) {
		return IDEStandardBeanProxyFactory.createBeanProxy(this, bean);
	} else if (returnType == Integer.TYPE) {
		return proxyFactory.intType.newBeanProxy(bean);
	} else if (returnType == Boolean.TYPE) {
		return proxyFactory.booleanType.newBeanProxy(bean);
	} else if (returnType == Float.TYPE) {
		return proxyFactory.floatType.newBeanProxy(bean);
	} else if (returnType == Long.TYPE) {
		return proxyFactory.longType.newBeanProxy(bean);
	} else if (returnType == Short.TYPE) {
		return proxyFactory.shortType.newBeanProxy(bean);
	} else if (returnType == Double.TYPE) {
		return proxyFactory.doubleType.newBeanProxy(bean);
	} else if (returnType == Byte.TYPE) {
		return proxyFactory.byteType.newBeanProxy(bean);
	} else if (returnType == Character.TYPE) {
		return proxyFactory.charType.newBeanProxy(bean);
	} else {
		throw new RuntimeException("Unknown primitive type " + returnType.getName()); //$NON-NLS-1$
	}
}

List shutdownRunnables;

public synchronized void addShutdownListener(Runnable runnable) {
	if (shutdownRunnables == null) {
		shutdownRunnables = new ArrayList();
	} else if (shutdownRunnables.contains(runnable))
		return;
	shutdownRunnables.add(runnable);
}

public synchronized void removeShutdownListener(Runnable runnable) {
	if (shutdownRunnables != null)
		shutdownRunnables.remove(runnable);
}

}

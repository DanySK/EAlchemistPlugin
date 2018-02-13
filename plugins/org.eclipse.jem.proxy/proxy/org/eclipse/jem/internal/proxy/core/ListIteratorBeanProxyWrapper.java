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
package org.eclipse.jem.internal.proxy.core;
/*


 */


import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
/**
 * This is a wrapper for an java.util.ListIterator proxy.
 * It provides the ListIterator methods to interface to
 * the proxy.
 */

public class ListIteratorBeanProxyWrapper extends IteratorBeanProxyWrapper {

	/**
	 * Construct with the ListIterator.
	 */
	public ListIteratorBeanProxyWrapper(IBeanProxy anIteratorProxy) {
		super(anIteratorProxy);
		
		if (!anIteratorProxy.getTypeProxy().isKindOf(anIteratorProxy.getProxyFactoryRegistry().getBeanTypeProxyFactory().getBeanTypeProxy("java.util.ListIterator"))) //$NON-NLS-1$
			throw new ClassCastException(MessageFormat.format(ProxyMessages.ClassCast_EXC__IncorrectType, new Object[] {anIteratorProxy.getTypeProxy().getTypeName(), "java.util.ListIterator"}));  //$NON-NLS-1$
	}
	
	
	/**
	 * ListIterator accessors
	 */
	public void add(IBeanProxy object) throws ThrowableProxy {
		fConstants.getListIteratorAdd().invoke(fIterator, object);
	}
	public boolean hasPrevious() {
		try {
			return ((IBooleanBeanProxy) fConstants.getListIteratorHasPrevious().invoke(fIterator)).booleanValue();
		} catch (ThrowableProxy e) {
			// This shouldn't occur, so just log it.
			ProxyPlugin.getPlugin().getLogger().log(new Status(IStatus.ERROR, ProxyPlugin.getPlugin().getBundle().getSymbolicName(), 0, ProxyMessages.UnexpectedException_EXC_, e)); 
			return false;
		}			
	}
	public int nextIndex() {
		try {
			return ((IIntegerBeanProxy) fConstants.getListIteratorNextIndex().invoke(fIterator)).intValue();
		} catch (ThrowableProxy e) {
			// This shouldn't occur, so just log it.
			ProxyPlugin.getPlugin().getLogger().log(new Status(IStatus.ERROR, ProxyPlugin.getPlugin().getBundle().getSymbolicName(), 0, ProxyMessages.UnexpectedException_EXC_, e)); 
			return -1;
		}			
	}			
	public IBeanProxy previous() throws ThrowableProxy {
		return fConstants.getListIteratorPrevious().invoke(fIterator);
	}
	public int previousIndex() {
		try {
			return ((IIntegerBeanProxy) fConstants.getListIteratorPreviousIndex().invoke(fIterator)).intValue();
		} catch (ThrowableProxy e) {
			// This shouldn't occur, so just log it.
			ProxyPlugin.getPlugin().getLogger().log(new Status(IStatus.ERROR, ProxyPlugin.getPlugin().getBundle().getSymbolicName(), 0, ProxyMessages.UnexpectedException_EXC_, e)); 
			return -1;
		}
	}
	public void remove() throws ThrowableProxy {
		fConstants.getListIteratorRemove().invoke(fIterator);
	}
	public void set(IBeanProxy object) throws ThrowableProxy {
		fConstants.getListIteratorSet().invoke(fIterator, object);
	}				
	
}

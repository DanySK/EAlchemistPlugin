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
package org.eclipse.jem.internal.proxy.vm.remote;
/*


 */


import java.util.*;
/**
 * A HashMap where the key is
 * done as an identity (i.e. found by '==' not equals()).
 */

public class IdentityMap extends HashMap{
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 * 
	 * @since 1.1.0
	 */
	private static final long serialVersionUID = -6817274833221383683L;

	/**
	 * Key that returns true on equals() only
	 * if the item it is wrappering is '=='
	 * not equals()
	 */
	static class IdentityKey {
		
		public static IdentityKey createKey(Object obj) {
			return obj != null ? new IdentityKey(obj) : null;
		}
		
		final Object o;
		
		public IdentityKey(Object obj) {
			o = obj;
		}
		
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof IdentityKey)) return false;
			if (this.o == (((IdentityKey) obj).o)) return true;
			return false;
		}
		
		public int hashCode() {
			return o.hashCode();
		}
	}
	
	public IdentityMap() {
	}
	
	public IdentityMap(int capacity) {
		super(capacity);
	}
	
	public IdentityMap(int capacity, float loadFactor) {
		super(capacity, loadFactor);
	}
	
	public boolean containsKey(Object key) {
		return super.containsKey(IdentityKey.createKey(key));
	}
	
	public Object get(Object key) {
		return super.get(IdentityKey.createKey(key));
	}
	
	public Object put(Object key, Object value) {
		return super.put(IdentityKey.createKey(key), value);
	}
	
	public Object remove(Object key) {
		return super.remove(IdentityKey.createKey(key));
	}
	
	/**
	 * NOTE: Didn't bother implementing entrySet(). If that becomes
	 * needed, then it will be implemented.
	 */
	public Set entrySet() {
		throw  new UnsupportedOperationException();
	}
}

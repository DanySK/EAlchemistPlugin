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
package org.eclipse.jem.internal.proxy.remote;
/*


 */


/**
 * Tag interface to indicate this proxy is a constant proxy, i.e. there
 * is no bean on the server representing this (e.g. Strings and Integers).
 */

public interface IREMConstantBeanProxy extends IREMBeanProxy {

}

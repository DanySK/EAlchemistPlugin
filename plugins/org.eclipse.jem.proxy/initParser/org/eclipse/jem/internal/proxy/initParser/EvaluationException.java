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

import java.lang.reflect.InvocationTargetException;

/**
 * This is an InitializationStringEvaluationException, but
 * in addition, the exception that it wrappers is what should be
 * returned. This means it was a valid exception, parsing had
 * gone fine, but it was an evaluation that occured. This is
 * distinguished from the straight InitializationStringEvaluationException,
 * which means some error during parsing and evaluation.
 * 
 * Note: InvocationTargetExceptions will be unwrappered to the true exception.
 */
public class EvaluationException extends InitializationStringEvaluationException {

	/**
	 * Comment for <code>serialVersionUID</code>
	 * 
	 * @since 1.1.0
	 */
	private static final long serialVersionUID = -6997239021885310986L;

	/**
	 * Constructor for ReturnableException.
	 * @param exc
	 */
	public EvaluationException(Throwable exc) {
		super(exc instanceof InvocationTargetException ? ((InvocationTargetException) exc).getTargetException() : exc);
	}

}

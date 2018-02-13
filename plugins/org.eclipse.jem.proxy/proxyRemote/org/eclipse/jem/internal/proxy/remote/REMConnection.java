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


import java.io.*;
import java.net.Socket;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.jem.internal.proxy.common.CommandException;
import org.eclipse.jem.internal.proxy.common.remote.*;
import org.eclipse.jem.internal.proxy.common.remote.Commands.*;
import org.eclipse.jem.internal.proxy.core.ProxyPlugin;
import org.eclipse.jem.util.TimerTests;
/**
 * The default implementation of the connection.
 *
 * It uses the property "proxyvm.bufsize" to determine the buffer size to use. If not specified, it uses the system default
 */
public class REMConnection implements IREMConnection, IREMExpressionConnection {
	
	public final static String INVOKE_STEP = "Invoke"; //$NON-NLS-1$
	public final static String INVOKE_METHOD_STEP = "Invoke Method"; //$NON-NLS-1$
	protected Socket fSocket = null;
	protected DataInputStream in = null;
	protected DataOutputStream out = null;
	private static final int TIME_OUT = 1000 * 60;	// Wait up to a minute before timeout.
	
	public REMConnection(Socket socket, boolean noTimeouts) {
		try {
			fSocket = socket;
			fSocket.setSoLinger(true, 30);	// Wait 30 seconds if necessary for the final stuff to go out after closing.
			if (!noTimeouts)
				fSocket.setSoTimeout(TIME_OUT);	// Timeout period
			Integer bufSize = Integer.getInteger("proxyvm.bufsize"); //$NON-NLS-1$
			if (bufSize == null)
				bufSize = new Integer(16000);
			out = new DataOutputStream(new BufferedOutputStream(fSocket.getOutputStream(), bufSize.intValue()));	// It didn't take the hint, so we will buffer it.
			in = new DataInputStream(new BufferedInputStream(fSocket.getInputStream(), bufSize.intValue()));	// It didn't take the hint, so we will buffer it.
		} catch (IOException e) {
			ProxyPlugin.getPlugin().getLogger().log(new Status(IStatus.ERROR, ProxyPlugin.getPlugin().getBundle().getSymbolicName(), 0, "", e)); //$NON-NLS-1$
			
			if (out != null)
				try {
					out.close();
				} catch (IOException e2) {
				}
			if (in != null)
				try {
					in.close();
				} catch (IOException e3) {
				}
			try {
				fSocket.close();
			} catch (IOException e4) {
			}
			fSocket = null;
			in = null;
			out = null;
		}
	}
	
	/**
	 * Did this construct correctly.
	 * This is needed because an exception could be thrown in the ctor and
	 * that's not a good thing to do.
	 */
	public boolean isConnected() {
		return fSocket != null;
	}
	
	/**
	 * Terminate the server. 
	 */
	public void terminateServer() {
		if (isConnected()) {
			try {
				Commands.sendTerminateCommand(out);
			} catch (IOException e) {
			}
			close();
		}
	}
	
	/**
	 * Close the connection.
	 */
	public void close() {
		if (isConnected()) {
			try {
				Commands.sendQuitCommand(out);
			} catch (IOException e) {
			} finally {
				try {
					out.close();
				} catch (IOException e2) {
				}
				try {
					in.close();
				} catch (IOException e3) {
				}
				try {
					fSocket.close();
				} catch (IOException e4) {
				}
			}
			fSocket = null;
			in = null;
			out = null;
		}
	}
		
					
	/**
	 * Return the class information.
	 */
	public GetClassReturn getClass(String className) throws CommandException {
		if (isConnected())
			try {
				// It's simple, just pass onto Commands.
				return Commands.sendGetClassCommand(out, in, className);
			} catch (CommandErrorException e) {
				if (e.getErrorCode() != Commands.GET_CLASS_NOT_FOUND)
					throw e;	// Not class not found, send it on
			}			
		return null;	// Not found, so return null				
	}
	
	
	/**
	 * Return the class information given an ID.
	 */
	public GetClassIDReturn getClassFromID(int classID) throws CommandException {
		if (isConnected()) {
			// It's simple, just pass onto Commands.
			return Commands.sendGetClassFromIDCommand(out, in, classID);
		}
		return null;	// Not found, so return null		
	}	
	
	/**
	 * Get the object data from an id.
	 */
	public void getObjectData(int classID, Commands.ValueObject valueReturn) throws CommandException {
		if (isConnected()) {
			// Just pass onto Commands.
			Commands.sendGetObjectData(out, in, classID, valueReturn);
		} else
			valueReturn.set();	// Set it to null since we aren't connected.
	}	

	/**
	 * Get a new instance using an init string
	 */
	public void getNewInstance(int classID, String initString, Commands.ValueObject newInstance) throws CommandException {
		if (isConnected()) {
			// Just pass onto Commands.
			Commands.sendNewInstance(out, in, classID, initString, newInstance);
		} else
			newInstance.set();	// Set it to null since we aren't connected.
	}
	
	/**
	 * Release the id.
	 */
	public void releaseID(int id){
		if (isConnected())
			try {
				// Just pass onto Commands.
				Commands.releaseObjectCommand(out, id);
			} catch (IOException e) {
				// Don't care it didn't work
			}
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMConnection#getArrayContents(int, org.eclipse.jem.internal.proxy.common.remote.Commands.ValueObject)
	 */
	public void getArrayContents(int arrayID, Commands.ValueObject returnValue) throws CommandException {
		if (isConnected()) {
			// It's simple, just pass onto Commands.
			Commands.sendGetArrayContentsCommand(out, in, arrayID, returnValue);
		}			
	}
	
	/**
	 * Invoke the method call
	 */	
	public void invokeMethod(int methodID, Commands.ValueObject invokeOn, Commands.ValueObject parms, Commands.ValueObject returnValue) throws CommandException {
		if (isConnected()) {
			// It's simple, just pass onto Commands.
			TimerTests.basicTest.startCumulativeStep(INVOKE_METHOD_STEP);
			Commands.sendInvokeMethodCommand(out, in, methodID, invokeOn, parms, returnValue);
			TimerTests.basicTest.stopCumulativeStep(INVOKE_METHOD_STEP);
		}			
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMConnection#invokeMethod(org.eclipse.jem.internal.proxy.common.remote.Commands.ValueObject, java.lang.String, org.eclipse.jem.internal.proxy.common.remote.Commands.ValueObject, org.eclipse.jem.internal.proxy.common.remote.Commands.ValueObject, org.eclipse.jem.internal.proxy.common.remote.Commands.ValueObject, org.eclipse.jem.internal.proxy.common.remote.Commands.ValueObject)
	 */
	public void invokeMethod(ValueObject classType, String methodName, ValueObject parmTypes, ValueObject invokeOn, ValueObject parms,
			ValueObject returnValue) throws CommandException {
		if (isConnected()) {
			// It's simple, just pass onto Commands.
			TimerTests.basicTest.startCumulativeStep(INVOKE_STEP);
			Commands.sendInvokeMethodCommand(out, in, classType, methodName, parmTypes, invokeOn, parms, returnValue);
			TimerTests.basicTest.stopCumulativeStep(INVOKE_STEP);
		}			
	}
	
	/* ************************************************************
	 * Put the Expression processing commands here.
	 * 
	 * ************************************************************/
	
	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMExpressionConnection#startExpressionProcessing()
	 */
	public void startExpressionProcessing(int expressionID, byte trace) throws IOException {
		if (isConnected())
			ExpressionCommands.sendStartExpressionProcessingCommand(expressionID, trace, out);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMExpressionConnection#pushExpressionCommand(byte)
	 */
	public void pushExpressionCommand(int expressionID, byte subcommand) throws IOException {
		if (isConnected())
			ExpressionCommands.sendExpressionCommand(expressionID, out, subcommand);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMExpressionConnection#stopExpressionProcessing()
	 */
	public void stopExpressionProcessing(int expressionID) throws IOException {
		if (isConnected())
			ExpressionCommands.sendEndExpressionProcessingCommand(expressionID, out);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMExpressionConnection#pushValueObject(org.eclipse.jem.internal.proxy.common.remote.Commands.ValueObject)
	 */
	public void pushValueObject(ValueObject valueObject) throws CommandException {
		if (isConnected())
			Commands.writeValue(out, valueObject, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMExpressionConnection#pushByte(byte)
	 */
	public void pushByte(byte abyte) throws IOException {
		if (isConnected())
			ExpressionCommands.sendByte(out, abyte);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMExpressionConnection#pushInt(int)
	 */
	public void pushInt(int anInt) throws IOException {
		if (isConnected())
			ExpressionCommands.sendInt(out, anInt);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMExpressionConnection#pushString(java.lang.String)
	 */
	public void pushString(String aString) throws IOException {
		if (isConnected())
			ExpressionCommands.sendString(out, aString);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMExpressionConnection#pushBoolean(boolean)
	 */
	public void pushBoolean(boolean aBool) throws IOException {
		if (isConnected())
			ExpressionCommands.sendBoolean(out, aBool);
	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMExpressionConnection#getFinalValue(org.eclipse.jem.internal.proxy.common.remote.Commands.ValueObject)
	 */
	public void getFinalValue(ValueObject result) throws CommandException {
		if (isConnected())
			Commands.readBackValue(in, result, Commands.NO_TYPE_CHECK);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMExpressionConnection#pullValue(int, org.eclipse.jem.internal.proxy.common.remote.Commands.ValueObject)
	 */
	public void pullValue(int expressionID, ValueObject proxyids, ValueSender sender) throws CommandException {
		if (isConnected())
			ExpressionCommands.sendPullValueCommand(expressionID, out, in, proxyids, sender);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMExpressionConnection#sync(int, org.eclipse.jem.internal.proxy.common.remote.Commands.ValueObject)
	 */
	public void sync(int expressionID, ValueObject proxyids, ValueSender sender) throws CommandException {
		if (isConnected())
			ExpressionCommands.sendSyncCommand(expressionID, out, in, proxyids, sender);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jem.internal.proxy.remote.IREMConnection#readProxyArrayValues(org.eclipse.jem.internal.proxy.common.remote.Commands.ValueObject, org.eclipse.jem.internal.proxy.common.remote.Commands.ValueSender, boolean)
	 */
	public void readProxyArrayValues(Commands.ValueObject returnValue, Commands.ValueSender valueSender, boolean allowFlag) throws CommandException {
		if (isConnected())
			Commands.readArray(in, returnValue.anInt, valueSender, returnValue, allowFlag);
	}

	public void transferExpression(int expressionID, ValueObject expController) throws CommandException {
		if (isConnected())
			ExpressionCommands.sendTransferExpressionCommand(expressionID, out, in, expController);
	}

	public void resumeExpression(int expressionID, ValueObject expController) throws CommandException {
		if (isConnected())
			ExpressionCommands.sendResumeExpressionCommand(expressionID, out, expController);
	}

}

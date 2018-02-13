/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.javaee.ejb.internal.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.jst.javaee.ejb.ActivationConfigProperty;

import org.eclipse.jst.javaee.ejb.internal.metadata.EjbPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Activation Config Property</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jst.javaee.ejb.internal.impl.ActivationConfigPropertyImpl#getActivationConfigPropertyName <em>Activation Config Property Name</em>}</li>
 *   <li>{@link org.eclipse.jst.javaee.ejb.internal.impl.ActivationConfigPropertyImpl#getActivationConfigPropertyValue <em>Activation Config Property Value</em>}</li>
 *   <li>{@link org.eclipse.jst.javaee.ejb.internal.impl.ActivationConfigPropertyImpl#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ActivationConfigPropertyImpl extends EObjectImpl implements ActivationConfigProperty {
	/**
	 * The default value of the '{@link #getActivationConfigPropertyName() <em>Activation Config Property Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActivationConfigPropertyName()
	 * @generated
	 * @ordered
	 */
	protected static final String ACTIVATION_CONFIG_PROPERTY_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getActivationConfigPropertyName() <em>Activation Config Property Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActivationConfigPropertyName()
	 * @generated
	 * @ordered
	 */
	protected String activationConfigPropertyName = ACTIVATION_CONFIG_PROPERTY_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getActivationConfigPropertyValue() <em>Activation Config Property Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActivationConfigPropertyValue()
	 * @generated
	 * @ordered
	 */
	protected static final String ACTIVATION_CONFIG_PROPERTY_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getActivationConfigPropertyValue() <em>Activation Config Property Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActivationConfigPropertyValue()
	 * @generated
	 * @ordered
	 */
	protected String activationConfigPropertyValue = ACTIVATION_CONFIG_PROPERTY_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ActivationConfigPropertyImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EjbPackage.Literals.ACTIVATION_CONFIG_PROPERTY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getActivationConfigPropertyName() {
		return activationConfigPropertyName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setActivationConfigPropertyName(String newActivationConfigPropertyName) {
		String oldActivationConfigPropertyName = activationConfigPropertyName;
		activationConfigPropertyName = newActivationConfigPropertyName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EjbPackage.ACTIVATION_CONFIG_PROPERTY__ACTIVATION_CONFIG_PROPERTY_NAME, oldActivationConfigPropertyName, activationConfigPropertyName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getActivationConfigPropertyValue() {
		return activationConfigPropertyValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setActivationConfigPropertyValue(String newActivationConfigPropertyValue) {
		String oldActivationConfigPropertyValue = activationConfigPropertyValue;
		activationConfigPropertyValue = newActivationConfigPropertyValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EjbPackage.ACTIVATION_CONFIG_PROPERTY__ACTIVATION_CONFIG_PROPERTY_VALUE, oldActivationConfigPropertyValue, activationConfigPropertyValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EjbPackage.ACTIVATION_CONFIG_PROPERTY__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ACTIVATION_CONFIG_PROPERTY_NAME:
				return getActivationConfigPropertyName();
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ACTIVATION_CONFIG_PROPERTY_VALUE:
				return getActivationConfigPropertyValue();
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ID:
				return getId();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ACTIVATION_CONFIG_PROPERTY_NAME:
				setActivationConfigPropertyName((String)newValue);
				return;
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ACTIVATION_CONFIG_PROPERTY_VALUE:
				setActivationConfigPropertyValue((String)newValue);
				return;
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ID:
				setId((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ACTIVATION_CONFIG_PROPERTY_NAME:
				setActivationConfigPropertyName(ACTIVATION_CONFIG_PROPERTY_NAME_EDEFAULT);
				return;
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ACTIVATION_CONFIG_PROPERTY_VALUE:
				setActivationConfigPropertyValue(ACTIVATION_CONFIG_PROPERTY_VALUE_EDEFAULT);
				return;
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ID:
				setId(ID_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ACTIVATION_CONFIG_PROPERTY_NAME:
				return ACTIVATION_CONFIG_PROPERTY_NAME_EDEFAULT == null ? activationConfigPropertyName != null : !ACTIVATION_CONFIG_PROPERTY_NAME_EDEFAULT.equals(activationConfigPropertyName);
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ACTIVATION_CONFIG_PROPERTY_VALUE:
				return ACTIVATION_CONFIG_PROPERTY_VALUE_EDEFAULT == null ? activationConfigPropertyValue != null : !ACTIVATION_CONFIG_PROPERTY_VALUE_EDEFAULT.equals(activationConfigPropertyValue);
			case EjbPackage.ACTIVATION_CONFIG_PROPERTY__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (activationConfigPropertyName: "); //$NON-NLS-1$
		result.append(activationConfigPropertyName);
		result.append(", activationConfigPropertyValue: "); //$NON-NLS-1$
		result.append(activationConfigPropertyValue);
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //ActivationConfigPropertyImpl
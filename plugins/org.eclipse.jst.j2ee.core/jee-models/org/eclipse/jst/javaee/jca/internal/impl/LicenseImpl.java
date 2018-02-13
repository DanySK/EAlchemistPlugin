/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.javaee.jca.internal.impl;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.jst.javaee.core.Description;

import org.eclipse.jst.javaee.jca.License;

import org.eclipse.jst.javaee.jca.internal.metadata.JcaPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>License</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jst.javaee.jca.internal.impl.LicenseImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.jst.javaee.jca.internal.impl.LicenseImpl#isLicenseRequired <em>License Required</em>}</li>
 *   <li>{@link org.eclipse.jst.javaee.jca.internal.impl.LicenseImpl#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LicenseImpl extends EObjectImpl implements License {
	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected EList<Description> description;

	/**
	 * The default value of the '{@link #isLicenseRequired() <em>License Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isLicenseRequired()
	 * @generated
	 * @ordered
	 */
	protected static final boolean LICENSE_REQUIRED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isLicenseRequired() <em>License Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isLicenseRequired()
	 * @generated
	 * @ordered
	 */
	protected boolean licenseRequired = LICENSE_REQUIRED_EDEFAULT;

	/**
	 * This is true if the License Required attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean licenseRequiredESet;

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
	protected LicenseImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return JcaPackage.Literals.LICENSE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<Description> getDescription() {
		if (description == null) {
			description = new EObjectContainmentEList<Description>(Description.class, this, JcaPackage.LICENSE__DESCRIPTION);
		}
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isLicenseRequired() {
		return licenseRequired;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLicenseRequired(boolean newLicenseRequired) {
		boolean oldLicenseRequired = licenseRequired;
		licenseRequired = newLicenseRequired;
		boolean oldLicenseRequiredESet = licenseRequiredESet;
		licenseRequiredESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JcaPackage.LICENSE__LICENSE_REQUIRED, oldLicenseRequired, licenseRequired, !oldLicenseRequiredESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetLicenseRequired() {
		boolean oldLicenseRequired = licenseRequired;
		boolean oldLicenseRequiredESet = licenseRequiredESet;
		licenseRequired = LICENSE_REQUIRED_EDEFAULT;
		licenseRequiredESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JcaPackage.LICENSE__LICENSE_REQUIRED, oldLicenseRequired, LICENSE_REQUIRED_EDEFAULT, oldLicenseRequiredESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetLicenseRequired() {
		return licenseRequiredESet;
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
			eNotify(new ENotificationImpl(this, Notification.SET, JcaPackage.LICENSE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case JcaPackage.LICENSE__DESCRIPTION:
				return ((InternalEList<?>)getDescription()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case JcaPackage.LICENSE__DESCRIPTION:
				return getDescription();
			case JcaPackage.LICENSE__LICENSE_REQUIRED:
				return isLicenseRequired();
			case JcaPackage.LICENSE__ID:
				return getId();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case JcaPackage.LICENSE__DESCRIPTION:
				getDescription().clear();
				getDescription().addAll((Collection<? extends Description>)newValue);
				return;
			case JcaPackage.LICENSE__LICENSE_REQUIRED:
				setLicenseRequired((Boolean)newValue);
				return;
			case JcaPackage.LICENSE__ID:
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
			case JcaPackage.LICENSE__DESCRIPTION:
				getDescription().clear();
				return;
			case JcaPackage.LICENSE__LICENSE_REQUIRED:
				unsetLicenseRequired();
				return;
			case JcaPackage.LICENSE__ID:
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
			case JcaPackage.LICENSE__DESCRIPTION:
				return description != null && !description.isEmpty();
			case JcaPackage.LICENSE__LICENSE_REQUIRED:
				return isSetLicenseRequired();
			case JcaPackage.LICENSE__ID:
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
		result.append(" (licenseRequired: "); //$NON-NLS-1$
		if (licenseRequiredESet) result.append(licenseRequired); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //LicenseImpl

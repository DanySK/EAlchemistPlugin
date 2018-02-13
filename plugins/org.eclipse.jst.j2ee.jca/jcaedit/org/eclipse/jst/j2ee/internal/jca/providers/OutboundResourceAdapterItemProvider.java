/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.j2ee.internal.jca.providers;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.jst.j2ee.jca.JcaFactory;
import org.eclipse.jst.j2ee.jca.JcaPackage;
import org.eclipse.jst.j2ee.jca.OutboundResourceAdapter;
import org.eclipse.jst.j2ee.jca.internal.plugin.JCAResourceHandler;
import org.eclipse.jst.j2ee.jca.internal.plugin.JcaPlugin;

/**
 * This is the item provider adpater for a {@link com.ibm.etools.jca.OutboundResourceAdapter}
 * object. <!-- begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class OutboundResourceAdapterItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public OutboundResourceAdapterItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addReauthenticationSupportPropertyDescriptor(object);
			addTransactionSupportPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Reauthentication Support feature. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addReauthenticationSupportPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(new ItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getString("_UI_OutboundResourceAdapter_reauthenticationSupport_feature"), //$NON-NLS-1$
					getString("_UI_PropertyDescriptor_description", "_UI_OutboundResourceAdapter_reauthenticationSupport_feature", "_UI_OutboundResourceAdapter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					JcaPackage.eINSTANCE.getOutboundResourceAdapter_ReauthenticationSupport(), true, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE));
	}

	/**
	 * This adds a property descriptor for the Transaction Support feature. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addTransactionSupportPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(new ItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getString("_UI_OutboundResourceAdapter_transactionSupport_feature"), //$NON-NLS-1$
					getString("_UI_PropertyDescriptor_description", "_UI_OutboundResourceAdapter_transactionSupport_feature", "_UI_OutboundResourceAdapter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					JcaPackage.eINSTANCE.getOutboundResourceAdapter_TransactionSupport(), true, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE));
	}

	/**
	 * This specifies how to implement {@link #getChildren}and
	 * { @link org.eclipse.emf.edit.command.AddCommand}and
	 * { @link org.eclipse.emf.edit.command.RemoveCommand}support in {@link #createCommand}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Collection getChildrenReferences(Object object) {
		if (childrenReferences == null) {
			super.getChildrenReferences(object);
			childrenReferences.add(JcaPackage.eINSTANCE.getOutboundResourceAdapter_ConnectionDefinitions());
		}
		return childrenReferences;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EReference getChildReference(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildReference(object, child);
	}


	/**
	 * This returns OutboundResourceAdapter.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return getResourceLocator().getImage("full/obj16/OutboundResourceAdapter"); //$NON-NLS-1$
	}

	/**
	 * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		//		OutboundResourceAdapter outboundResourceAdapter = (OutboundResourceAdapter) object;

		return JCAResourceHandler._UI_OutboundResourceAdapter_type; 
		//return getString("_UI_OutboundResourceAdapter_type") /*+ " " +
		// outboundResourceAdapter.isReauthenticationSupport()*/; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * This handles notification by calling {@link #fireNotifyChanged fireNotifyChanged}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		switch (notification.getFeatureID(OutboundResourceAdapter.class)) {
			case JcaPackage.OUTBOUND_RESOURCE_ADAPTER__REAUTHENTICATION_SUPPORT :
			case JcaPackage.OUTBOUND_RESOURCE_ADAPTER__TRANSACTION_SUPPORT :
			case JcaPackage.OUTBOUND_RESOURCE_ADAPTER__CONNECTION_DEFINITIONS :
			case JcaPackage.OUTBOUND_RESOURCE_ADAPTER__AUTHENTICATION_MECHANISMS : {
				fireNotifyChanged(notification);
				return;
			}
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s
	 * describing all of the children that can be created under this object. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add(createChildParameter(JcaPackage.eINSTANCE.getOutboundResourceAdapter_ConnectionDefinitions(), JcaFactory.eINSTANCE.createConnectionDefinition()));

		newChildDescriptors.add(createChildParameter(JcaPackage.eINSTANCE.getOutboundResourceAdapter_AuthenticationMechanisms(), JcaFactory.eINSTANCE.createAuthenticationMechanism()));
	}

	/**
	 * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return JcaPlugin.getPlugin();
	}
}

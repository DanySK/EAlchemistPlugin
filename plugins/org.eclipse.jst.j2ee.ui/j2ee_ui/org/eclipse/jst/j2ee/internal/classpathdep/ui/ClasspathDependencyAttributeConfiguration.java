/*******************************************************************************
 * Copyright (c) 2005, 2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     rfrost@bea.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.j2ee.internal.classpathdep.ui;

import java.net.URL;

import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.j2ee.classpathdep.IClasspathDependencyConstants;
import org.eclipse.jst.j2ee.internal.plugin.J2EEPlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

public class ClasspathDependencyAttributeConfiguration extends ClasspathAttributeConfiguration {

	private static ImageDescriptor descriptor = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration#canEdit(org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration.ClasspathAttributeAccess)
	 */
	@Override
	public boolean canEdit(ClasspathAttributeAccess attribute) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration#canRemove(org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration.ClasspathAttributeAccess)
	 */
	@Override
	public boolean canRemove(ClasspathAttributeAccess attribute) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration#getImageDescriptor(org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration.ClasspathAttributeAccess)
	 */
	@Override
	public ImageDescriptor getImageDescriptor(ClasspathAttributeAccess attribute) {
		if (descriptor == null) {
			final URL gifImageURL = (URL) J2EEPlugin.getPlugin().getImage("CPDep"); //$NON-NLS-1$
			if (gifImageURL != null) {
				descriptor = ImageDescriptor.createFromURL(gifImageURL);
			}
		}
		return descriptor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration#getNameLabel(org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration.ClasspathAttributeAccess)
	 */
	@Override
	public String getNameLabel(ClasspathAttributeAccess attribute) {
		return Resources.nameLabel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration#getValueLabel(org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration.ClasspathAttributeAccess)
	 */
	@Override
	public String getValueLabel(ClasspathAttributeAccess attribute) {
		final IClasspathAttribute attrib = attribute.getClasspathAttribute();
		if (attrib != null) {
			final String value = attrib.getValue();
			if (value != null) {
				if (value.equals(IClasspathDependencyConstants.RUNTIME_MAPPING_INTO_CONTAINER)) {
					return Resources.containerMapping;
				}
				return value;
			}
		}
		return Resources.unspecified;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration#performEdit(org.eclipse.swt.widgets.Shell, org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration.ClasspathAttributeAccess)
	 */
	@Override
	public IClasspathAttribute performEdit(Shell shell,
			ClasspathAttributeAccess attribute) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration#performRemove(org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration.ClasspathAttributeAccess)
	 */
	@Override
	public IClasspathAttribute performRemove(ClasspathAttributeAccess attribute) {
		return JavaCore.newClasspathAttribute(IClasspathDependencyConstants.CLASSPATH_COMPONENT_DEPENDENCY, null);
	}

	private static final class Resources extends NLS {
        public static String nameLabel;
        public static String unspecified;
        public static String containerMapping;
        static
        {
            initializeMessages( ClasspathDependencyAttributeConfiguration.class.getName(), 
                                Resources.class );
        }
    }
}

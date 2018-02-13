/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.*;

import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jem.util.logger.proxyrender.EclipseLogger;

import org.eclipse.jem.internal.proxy.core.IConfigurationContributionInfo.ContainerPaths;

/**
 * The plugin class for the org.eclipse.jem.internal.proxy.core plugin.
 */

public class ProxyPlugin extends Plugin {
	
	/**
	 * This interface is for a listener that needs to know if this plugin (ProxyPlugin) is being shutdown. 
	 * It is needed because there are some extensions that get added dynamically that need to know when the
	 * plugin is being shutdown. Can't use new bundle listener for this because it notifies AFTER shutdown.
	 * 
	 * @since 1.0.0
	 */
	public interface IProxyPluginShutdownListener {
		/**
		 * ProxyPlugin is in shutdown.
		 * 
		 * @since 1.0.0
		 */
		public void shutdown();
	}
	
	private static ProxyPlugin PROXY_PLUGIN = null;
	public static final String PREFERENCES_VM_NOVERIFY_KEY = "JEM_PREFERENCES_VM_NOVERIFY_KEY"; // Key for NOVERIFY option of VM //$NON-NLS-1$
	public static final String PDE_NATURE_ID = "org.eclipse.pde.PluginNature" ; //$NON-NLS-1$
		
	// If this is set to true, then in development mode and it will try for proxy jars in directories.
	private boolean devMode;
	
	private ListenerList shutdownListeners;

	public ProxyPlugin() {
		super();
		PROXY_PLUGIN = this;
		devMode = Platform.inDevelopmentMode();  	
	}
	
	public boolean isDevMode() {
		return devMode;
	}

	/**
	 * Access the singleton
	 * @return the singleton plugin
	 * 
	 * @since 1.0.0
	 */
	public static ProxyPlugin getPlugin() {
		return PROXY_PLUGIN;
	}
	
	private Logger logger;
	public Logger getLogger() {
		if (logger == null)
			logger = EclipseLogger.getEclipseLogger(this);
		return logger;
	}	

	/**
	 * This will take the bundle and file name and make it local and return that
	 * fully qualified. It will look in fragments, but only returns first found. If there can be multiples use
	 * the one for bundles and it fragments.
	 * <p>
	 * If we are in development and it will pick it up from the path
	 * that is listed in the proxy.jars file located in the bundle passed in. This allows development code to be
	 * used in place of the actual runtime jars. If the runtime jars are found,
	 * they will be used.
	 * <p>
	 * For example if looking for file runtime/xyz.jar in bundle abc, then in bundle directory for abc,
	 * there should be a file called proxy.jars. This should only be in development, this file should not
	 * be distributed for production. It would be distributed in the SDK environment when testing is desired.
	 * <p>
	 * The format of the file is:
	 * 	runtimefile=/projectname/builddirectory
	 * <p>
	 * For this to work when the actual jar is not found, the Eclipse must of been started in 
	 * dev mode (i.e. the bundle location will be a project within the developer Eclipse. That way
	 * we can go up one level for the current install location and assume the above projectname
	 * will be found relative to the directory.
	 * <p>
	 * For the above example:
	 * 	runtime/xyz.jar=/xyzproject/bin
	 * <p>
	 * It will return "." if file can't be found. It means nothing but it won't cause jvm to crash.
	 * 
	 * @param bundle
	 * @param filenameWithinBundle
	 * @return the path to the file or <code>"."</code> if not found.
	 * 
	 * @since 1.0.0
	 */
	public String localizeFromBundleOnly(Bundle bundle, String filenameWithinBundle) {
		URL url = urlLocalizeFromBundleOnly(bundle, filenameWithinBundle);
		return url != null ? getFileFromURL(url) : "."; //$NON-NLS-1$
	}
	public String localizeFromBundleAndFragments(Bundle bundle, String filenameWithinBundle) {
		URL url = urlLocalizeFromBundleAndFragments(bundle, filenameWithinBundle);
		return url != null ? getFileFromURL(url) : "."; //$NON-NLS-1$
	}
	
	/**
	 * Just like localizeFromBundle except it will return an array of Strings. It will look for the filename
	 * within the bundle and any fragments of the bundle. If none are found, an empty array will be returned.
	 * <p>
	 * To find the files in the fragments that are in the runtime path (i.e. libraries), it will need to use a suffix,
	 * This is because the JDT will get confused if a runtime jar in a fragment has the same name
	 * as a runtime jar in the main bundle.
	 * NOTE: This is obsolete. JDT no longer has this problem. So we can find libraries in fragments that have the
	 * same file path. 
	 * <p>
	 * So we will use the following search pattern:
	 * <ol>
	 * <li>Find in all of the fragments those that match the name exactly in the same paths if paths are supplied.</li>
	 * <li>Find in all of the fragments, in their runtime path (library stmt), those that match the name 
	 *    but have a suffix the same as the uniqueid of the fragment (preceeded by a period). This is so that it can be easily
	 *    found but yet be unique in the entire list of fragments. For example if looking for "runtime/xyz.jar"
	 *    and we have fragment "a.b.c.d.frag", then in the runtime path we will look for the file
	 *    "runtime/xyz.a.b.c.d.frag.jar". Note: This is obsolete. Still here only for possible old code. Will go 
	 *    away in future.</li>
	 * <p>
	 * If the files in the fragments are not in the fragments library path then it can have the same name. NOTE: Obsolete,
	 * JDT can now handle same name.
	 * <p>
	 * This is useful for nls where the nls for the filename will be in one or more of the fragments of the plugin.
	 * 
	 * @param bundle
	 * @param filenameWithinBundle
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public String[] localizeAllFromBundleAndFragments(Bundle bundle, String filenameWithinBundle) {
		URL[] urls = urlLocalizeAllFromBundleAndFragments(bundle, filenameWithinBundle);
		String[] result = new String[urls.length];
		for (int i = 0; i < urls.length; i++) {
			result[i] = getFileFromURL(urls[i]);
		}
		return result;
	}
	
	public static String getFileFromURL(URL url) {
		// We need to do this in a device independent way. The URL will always put a leading '/' in the
		// file part of the URL, but on Windows we need to have this '/' removed. Some JRE's don't understand it.
		return new File(url.getFile()).getAbsolutePath();

	}

	public URL urlLocalizeFromBundleAndFragments(Bundle bundle, String filenameWithinBundle) {
		return urlLocalizeFromBundleAndFragments(bundle, new Path(filenameWithinBundle));
	}
	public URL urlLocalizeFromBundleAndFragments(Bundle bundle, IPath filenameWithinBundle) {
		try {
			URL pvm = FileLocator.find(bundle, filenameWithinBundle, null);
			if (pvm != null)
				return FileLocator.toFileURL(pvm);
		} catch (IOException e) {
		}
		if (devMode) {
			URL[] urls = findDevAllFromBundleAndFragments(bundle, filenameWithinBundle.toString());
			if (urls.length > 0)
				return urls[0];
			else
				return null;
		} else
			return null;
	}
	
	/**
	 * Like <code>localizeFromBundleAndFragments</code> except it returns URL's instead.
	 * 
	 * @param bundle
	 * @param filenameWithinBundle
	 * @return
	 * 
	 * @see ProxyPlugin#localizeFromBundleAndFragments(Bundle, String)
	 * @since 1.0.0
	 */
	public URL[] urlLocalizeAllFromBundleAndFragments(Bundle bundle, String filenameWithinBundle) {
		return urlLocalizeAllBundleAndFragments(bundle, Platform.getFragments(bundle), filenameWithinBundle);
	}
	public URL[] urlLocalizeAllFromBundleAndFragments(Bundle bundle, IPath filenameWithinBundle) {
		return urlLocalizeAllBundleAndFragments(bundle, Platform.getFragments(bundle), filenameWithinBundle.toString());
	}
	
	/**
	 * @param bundle
	 * @param filenameWithinBundle
	 * @param fragments
	 * @return
	 * 
	 * @since 1.0.0
	 */
	private URL[] urlLocalizeAllBundleAndFragments(Bundle bundle, Bundle[] fragments, String filenameWithinBundle) {

		ArrayList urls = new ArrayList((fragments == null ? 0 : fragments.length) + 1);
		URL url = internalUrlLocalizeFromBundleOnly(bundle, filenameWithinBundle);
		if (url != null)
			urls.add(url);
		if (fragments != null) {
			for (int i = 0; i < fragments.length; i++) {
				Bundle fragment = fragments[i];
				url = internalUrlLocalizeFromBundleOnly(fragment, filenameWithinBundle);
				if (url != null)
					urls.add(url);
			}
		}
		return (URL[]) urls.toArray(new URL[urls.size()]);
	}

	private static final String PROXYJARS = "proxy.jars";	//$NON-NLS-1$
	
	/**
	 * @see ProxyPlugin#localizeFromBundle(Bundle, String)
	 * 
	 * This is just a helper to return a url instead.
	 * 
	 * @param bundle
	 * @param filenameWithinBundle
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public URL urlLocalizeFromBundleOnly(Bundle bundle, String filenameWithinBundle) {
		// If the filenameWithinBundle begins with one of these special characters,
		// it might be in a fragment.
		if (filenameWithinBundle.charAt(0) == '$'
				&& (filenameWithinBundle.regionMatches(true, 0, "$nl$", 0, "$nl$".length()) //$NON-NLS-1$ //$NON-NLS-2$
						|| filenameWithinBundle.regionMatches(true, 0, "$os$", 0, "$os$".length()) || filenameWithinBundle.regionMatches(true, 0, //$NON-NLS-1$ //$NON-NLS-2$
						"$ws$", 0, "$ws$".length()))) //$NON-NLS-1$ //$NON-NLS-2$
			return urlLocalizeFromBundleAndFragments(bundle, filenameWithinBundle);
		try {
			URL pvm = new URL(bundle.getEntry("/"), filenameWithinBundle); //$NON-NLS-1$
			pvm = verifyFound(FileLocator.toFileURL(pvm));
			if (pvm != null)
				return pvm;
		} catch (IOException e) {
		}
		return findDev(bundle, filenameWithinBundle);

	}
	
	protected URL internalUrlLocalizeFromBundleOnly(Bundle bundle, String filenameWithinBundle) {
		try {
			URL pvm = bundle.getEntry(filenameWithinBundle);
			if (pvm != null)
				return FileLocator.toFileURL(pvm);
		} catch (IOException e) {
		}
		return findDev(bundle, filenameWithinBundle);		
		
	}	
	
	private URL verifyFound(URL pvm) throws IOException {
		if (devMode) {
			// Need to test if found in devmode. Otherwise we will just assume it is found. If not found on remote and moved to cache, an IOException would be thrown.
			if (pvm != null) {
				InputStream ios = null;
				try {
					ios = pvm.openStream();
					if (ios != null)
						return pvm; // Found it, so return it.
				} finally {
					if (ios != null)
						ios.close();
				}
			}
		} else
			return pvm;
		return null;
	}

	/**
	 * @see ProxyPlugin#localizeFromBundle(bundle, String)
	 * 
	 * This is just a helper to return a url instead.
	 * 
	 * @param bundle
	 * @param filenameWithinBundle
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public URL urlLocalizeFromBundleOnly(Bundle bundle, IPath filenameWithinBundle) {					
		return urlLocalizeFromBundleOnly(bundle, filenameWithinBundle.toString());
	}
	
	private URL findDev(Bundle bundle, String filenameWithinBundle) {
		if (devMode) {
			// Got this far and in dev mode means it wasn't found, so we'll try for development style.
			// It is assumed that in dev mode, we are running with the IDE as local and any 
			// build outputs will be local so local file protocol will be returned
			// from Platform.resolve(). We won't be running in dev mode with our entireplugin being in a jar,
			// or on a separate system.
			try {
				URL pvm = bundle.getEntry(PROXYJARS);
				if (pvm != null) {
					InputStream ios = null;
					try {
						ios = pvm.openStream();
						Properties props = new Properties();
						props.load(ios);
						String pathString = props.getProperty(filenameWithinBundle.toString());
						if (pathString != null) {
							URL url = FileLocator.resolve(bundle.getEntry("/"));	// It is assumed that if in debug mode, then this plugin is an imported plugin within the developement workspace. //$NON-NLS-1$
							if (url.getProtocol().equals("file")) { //$NON-NLS-1$
								File file = new File(url.getFile()).getParentFile();	// This gets us to workspace root of development workspace.
								file = new File(file, pathString);
								return file.toURL();
							}
						}
					} finally {
						if (ios != null)
							ios.close();
					}
				}
			} catch (IOException e) {
			}
		}
		
		return null;

	}
	private URL[] findDevAllFromBundleAndFragments(Bundle bundle, String filenameWithinBundle) {
		Bundle [] fragments = Platform.getFragments(bundle);
		ArrayList urls = new ArrayList((fragments == null ? 0 : fragments.length) + 1);
		URL url = findDev(bundle, filenameWithinBundle);
		if (url != null)
			urls.add(url);
		if (fragments != null) {
			for (int i = 0; i < fragments.length; i++) {
				Bundle fragment = fragments[i];
				url = findDev(fragment, filenameWithinBundle);
				if (url != null)
					urls.add(url);
			}
		}
		return (URL[]) urls.toArray(new URL[urls.size()]);
	}
	
	/**
	 * Get the urls for the bundle and all fragments. This is used when bundles/fragments are jarred. It won't work correctly if not 
	 * a jarred bundle and fragments. This would most likely be used for NLS
	 * purposes to bring in the bundle and all of the nls. If a specific fragment was wanted use {@link #urlLocalizeBundle(Bundle)} instead.
	 * 
	 * @param bundle
	 * @return urls for bundle and all fragments.
	 * 
	 * @since 1.2.0
	 */
	public URL[] urlLocalizeBundleAndFragments(Bundle bundle) {
		Bundle[] fragments = Platform.getFragments(bundle);
		List urls = new ArrayList((fragments == null ? 0 : fragments.length) + 1);
		URL[] burls = urlLocalizeBundle(bundle);
		if (burls != null) {
			urls.addAll(Arrays.asList(burls));
		}
		if (fragments != null) {
			for (int i = 0; i < fragments.length; i++) {
				burls = urlLocalizeBundle(fragments[i]);
				if (burls != null)
					urls.addAll(Arrays.asList(burls));
			}
		}

		return (URL[]) urls.toArray(new URL[urls.size()]);

	}
	
	/**
	 * Get the urls for the bundle libraries only. If a fragment is wanted, then pass in the fragment instead.
	 * If bundle and all fragments are wanted use {@link #urlLocalizeBundleAndFragments(Bundle)} instead.
	 * <p>
	 * If in dev mode, it will use the binary output directory for the plugin libraries from the build.properties file. 
	 * 
	 * @param bundle
	 * @return URL array of local library references for the bundle or null if can't resolve to local.
	 * 
	 * @since 1.2.0
	 */
	public URL[] urlLocalizeBundle(Bundle bundle) {
		URL[] pvms;
		try {
			pvms = new URL[] {FileLocator.resolve(bundle.getEntry("/"))}; //$NON-NLS-1$
			if (pvms[0].getProtocol().equals("jar")) { //$NON-NLS-1$
				// The bundle is a jar, so use as is. 
				pvms[0] = getFilePath(pvms[0]);
				return pvms;
			}
		} catch (IOException e) {
			pvms = null;
		}
		try {
			// It is a directory. We could be in either development mode or not. Walk the manifest classpath and find the libraries.
			Properties buildProps = null;
			String libraries = (String) bundle.getHeaders().get(Constants.BUNDLE_CLASSPATH);
			ManifestElement[] elements = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, libraries);
			List urls = new ArrayList();
			if (elements != null) {
				for (int i = 0; i < elements.length; i++) {
					try {
						URL pvm = bundle.getEntry(elements[i].getValue());
						if (pvm != null) {
							urls.add(FileLocator.toFileURL(pvm));
							continue;
						}
					} catch (IOException e) {
					}				
					if (devMode) {
						// Not found as a jar, so see if can be found in devmode.
						if (buildProps == null) {
							buildProps = new Properties();
							try {
								URL bp = bundle.getEntry("build.properties"); //$NON-NLS-1$
								if (bp != null) {
									InputStream ios = null;
									try {
										ios = bp.openStream();
										buildProps.load(ios);
									} finally {
										if (ios != null)
											ios.close();
									}
								}
							} catch (IOException e) {
							}
						}
						String pathString = buildProps.getProperty("output."+elements[i].getValue()); //$NON-NLS-1$
						if (pathString != null) {
							try {
								urls.add(FileLocator.toFileURL(bundle.getEntry(pathString)));
							} catch (IOException e) {
							}
						}
					}
				}
				return (URL[]) urls.toArray(new URL[urls.size()]);
			} else if (devMode) {
				// If dev mode then we would look in the build.properties to find the output.. entry. This tells where the binaries are 
				// for project. If there is no "output.." entry then either it is not a runnable plugin or the rare case is it is only
				// classfiles in a folder. In that case see if there is a "source.." entry and use that.
				try {
					URL bp = bundle.getEntry("build.properties"); //$NON-NLS-1$
					if (bp != null) {
						InputStream ios = null;
						try {
							ios = bp.openStream();
							Properties props = new Properties();
							props.load(ios);
							String pathString = props.getProperty("output.."); //$NON-NLS-1$
							if (pathString != null) {
								return new URL[] {FileLocator.resolve(bundle.getEntry(pathString))};
							} else if ((pathString = props.getProperty("source..")) != null) { //$NON-NLS-1$
								// Probably a class folder, so use the source instead.
								return new URL[] {FileLocator.resolve(bundle.getEntry(pathString))};
							} else
								return pvms;	// Try the root of the plugin.
						} finally {
							if (ios != null)
								ios.close();
						}
					}
				} catch (IOException e) {
				}
				
			}
		} catch (BundleException e) {
		}
		return null;
	}
	
	/**
	 * Returns a url as file url if it can. If it is already a file url, it will just return it.
	 * If it is "jar:file:...." type protocol, then it will strip it down to the file part, which is
	 * the jar itself, and not the file within the jar. 
	 * @param l
	 * @return
	 * 
	 * @since 1.2.0
	 */
	public static URL getFilePath(URL l) {
		if (l != null) {
			if (l.getProtocol().equals("file")) //$NON-NLS-1$
			     return l;
			else if (l.getProtocol().equals("jar")) { //$NON-NLS-1$
				String f = l.getFile();
				int idx = f.lastIndexOf('!');
				if (idx>=0)
					f = f.substring(0,idx);
				try {
					return getFilePath(new URL(f));
				} catch (MalformedURLException e) {}
			}
		}
		return null;
	}
	
	/**
	 * A helper to order the plugins into pre-req order. 
	 * If A eventually depends on B, then B will be ahead of A in the
	 * list of plugins. (I.e. B is a pre-req somewhere of A).
	 *  
	 * @param bundlesToOrder - Bundles of interest. The results will have these in thiee correct order.
	 * @return An array of the Bundlers in there order from no prereqs in set to the leaves.
	 * 
	 * @since 1.0.0
	 */
	public static Bundle[] orderPlugins(final Set bundlesToOrder) {
		Map prereqsMap = new HashMap(bundlesToOrder.size()*3);
		int ndx = bundlesToOrder.size();
		Bundle[] result = new Bundle[ndx];
		Map dependents = getDependentCounts(false, bundlesToOrder, prereqsMap);	// We want the inactive ones too. That way have complete order. They can be ignored later if necessary.
		// keep iterating until all have been visited. This will actually find them in reverse order from what we
		// want, i.e. it will find the leaves first. So we will build result array in reverse order.
		while (!dependents.isEmpty()) {
			// loop over the dependents list.  For each entry, if there are no dependents, visit
			// the bundle and remove it from the list.  Make a copy of the keys so we don't end up
			// with concurrent accesses (since we are deleting the values as we go)
			Iterator ib = dependents.entrySet().iterator();
			while (ib.hasNext()) {
				Map.Entry entry = (Map.Entry) ib.next();
				Bundle bundle = (Bundle) entry.getKey() ;
				int[] count = (int[]) entry.getValue();
				if (count != null && count[0] <= 0) {
					if (bundlesToOrder.contains(bundle)) {
						result[--ndx] = bundle;
						if (ndx == 0)
							return result;	// We've ordered all that we care about. Anything left over is unimportant.
					}
					ib.remove();
					// decrement the dependent count for all of the prerequisites.
					Bundle[] requires = getPrereqs(bundle, prereqsMap);
					for (int j = 0; j < requires.length; j++) {
						Bundle prereq = requires[j];
						int[] countPrereq = (int[]) dependents.get(prereq);
						if (countPrereq != null)
							--countPrereq[0];
					}
				}
			}
		}		
		return result;
	}
	
	/**
	 * Get all of the prereqs for this bundle, all of the way down to the root.
	 * They will be in top-down depth-first order. There won't be duplicates. They will show up
	 * only once the first time they are found.
	 * 
	 * @param bundle
	 * @return list of all pre-reqs.
	 * 
	 * @since 1.0.0
	 */
	public static List getAllPrereqs(Bundle bundle) {
		List prereqs = new ArrayList();
		getAllPrereqs(bundle, prereqs, new HashMap());
		return prereqs;
	}
	
	private static void getAllPrereqs(Bundle bundle, List prereqs, Map prereqsMap) {
		Bundle[] prs = getPrereqs(bundle, prereqsMap);
		for (int i = 0; i < prs.length; i++) {
			Bundle pre = prs[i];
			if (prereqsMap.containsKey(pre))
				continue;	// Already processed this one once.
			prereqs.add(pre);	// Add to the list of pre-reqs accumulated so far.
			getAllPrereqs(pre, prereqs, prereqsMap);
		}
	}
	
	private static Bundle[] getPrereqs(Bundle bundle, Map prereqsMap) {
		Bundle[] prereqs = (Bundle[]) prereqsMap.get(bundle);
		if (prereqs == null) {
			prereqs = getPrereqs(bundle);
			prereqsMap.put(bundle, prereqs);
		}
		return prereqs;
	}
	
	public static Bundle[] getPrereqs(Bundle bundle) {
		Bundle[] l = (Bundle[]) pluginRequiredMap.get(bundle.getSymbolicName());
		if (l == null) {
			BundleSpecification specs[] = Platform.getPlatformAdmin().getState(false).getBundle(bundle.getBundleId()).getRequiredBundles();
			ArrayList bundles = new ArrayList(specs.length);
			for (int i = 0; i < specs.length; i++) {
				Bundle b = Platform.getBundle(specs[i].getName());
				if (b != null)
					bundles.add(b);
			}
			l = (Bundle[]) bundles.toArray(new Bundle[bundles.size()]);
			pluginRequiredMap.put(bundle.getSymbolicName(), l);
		}
		return l;
	}
	
	private static Map getDependentCounts(boolean activeOnly, Set startingSet, Map prereqsMap) {
		// Try to maintain independents in order from the starting set (which happens to be ordered, and hopefully reversed.).
		// Trying to have leaves show up in same order they are found in the Eclipse extensions, so we should
		// have the starting set be in reverse order from that. The actual process
		// builds them in reverse order from the starting set, so we expect the startingSet to be in  reverse order so they will be in forward order
		// hopefully. This is just a heuristic. There is no guarentee it will actually produce the 
		// desired output.
		Map dependents = new LinkedHashMap(startingSet.size());	
		// build a table of all dependent counts.  The table is keyed by descriptor and
		// the value the integer number of dependent plugins.
		List processNow = new ArrayList(startingSet);
		List processNext = new ArrayList(processNow.size());
		if (!processNow.isEmpty()) {
			// Go through the first time from the starting set to get an entry into the list.
			// If there is an entry, then it won't be marked for processNext. Only new entries
			// are added to processNext in the following loop.
			int pnSize = processNow.size();
			for (int i = 0; i < pnSize; i++) {
				Bundle bundle = (Bundle) processNow.get(i);
				if (activeOnly && bundle.getState() != Bundle.ACTIVE)
					continue;
				// ensure there is an entry for this descriptor (otherwise it will not be visited)
				int[] entry = (int[]) dependents.get(bundle);
				if (entry == null)
					dependents.put(bundle, new int[1]);
			}
		}
		
		// Now process the processNow to find the requireds, increment them, and add to processNext if never found before.
		while (!processNow.isEmpty()) {
			processNext.clear();
			int pnSize = processNow.size();
			for (int i = 0; i < pnSize; i++) {
				Bundle bundle = (Bundle) processNow.get(i);
				if (activeOnly && bundle.getState() != Bundle.ACTIVE)
					continue;			
				Bundle[] requires = getPrereqs(bundle, prereqsMap);
				for (int j = 0; j < requires.length; j++) {
					Bundle prereq = requires[j];
					if (prereq == null || activeOnly
							&& bundle.getState() != Bundle.ACTIVE)
						continue;
					int[] entry = (int[]) dependents.get(prereq);
					if (entry == null) {
						dependents.put(prereq, new int[]{1});
						processNext.add(prereq); // Never processed before, so we add it to the next process loop.
					} else
						++entry[0];
				}
			}
			
			// Now swap the lists so that we processNext will be now and visa-versa.
			List t = processNext;
			processNext = processNow;
			processNow = t;
		}
		return dependents;
	}
		
	/**
	 * Add a shutdown listener
	 * @param listener
	 * 
	 * @since 1.0.0
	 */
	public void addProxyShutdownListener(IProxyPluginShutdownListener listener) {
		if (shutdownListeners == null)
			shutdownListeners = new ListenerList();
		shutdownListeners.add(listener);
	}

	/**
	 * Remove a shutdown listener
	 * @param listener
	 * 
	 * @since 1.0.0
	 */
	public void removeProxyShutdownListener(IProxyPluginShutdownListener listener) {
		if (shutdownListeners != null)
			shutdownListeners.remove(listener);
	}
	
	private ILaunchConfigurationListener launchListener = new ILaunchConfigurationListener() {
		public void launchConfigurationAdded(ILaunchConfiguration configuration) {
			try {
				if (!configuration.isWorkingCopy() && IProxyConstants.ID_PROXY_LAUNCH_GROUP.equals(configuration.getCategory()))
					startCleanupJob();
			} catch (Exception e) {
			}
		}

		public void launchConfigurationChanged(ILaunchConfiguration configuration) {
			try {
				if (!configuration.isWorkingCopy() && IProxyConstants.ID_PROXY_LAUNCH_GROUP.equals(configuration.getCategory()))
					startCleanupJob();
			} catch (Exception e) {
			}
		}

		public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
			try {
				// On delete you can't tell the category or anything because all of that info has already removed.
				if (!configuration.isWorkingCopy())
					startCleanupJob();
			} catch (Exception e) {
			}
		}
	};
	
	private Job cleanupJob = new Job(ProxyMessages.ProxyPlugin_CleanupDefaultProxyLaunchConfigurations) { 
		{
			setSystem(true);	// So it doesn't show up in progress monitor. No need to interrupt user.
			setPriority(Job.SHORT);	// A quick running job.
		}
		protected IStatus run(IProgressMonitor monitor) {
			synchronized (this) {
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
			}
			// all we want to do is find out if any launch configurations (from proxy launch group) exist for
			// a project. If they don't, then unset the project's property. If they do, and the property is not
			// set, then set it to NOT_SET to indicate not set, but there are some configs for it.
			// We just gather the project names that have launch configurations.
			try {
				Set projectNames = new HashSet();
				ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();
				for (int i = 0; i < configs.length; i++) {
					if (IProxyConstants.ID_PROXY_LAUNCH_GROUP.equals(configs[i].getCategory())
						&& (ProxyLaunchSupport.ATTR_PRIVATE == null || !configs[i].getAttribute(ProxyLaunchSupport.ATTR_PRIVATE, false)))
						projectNames.add(configs[i].getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "")); //$NON-NLS-1$
				}

				IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
				IJavaElement[] children = model.getChildren();
				int cancelCount = 10;
				for (int j = 0; j < children.length; j++) {
					if (children[j].getElementType() == IJavaElement.JAVA_PROJECT) {
						if (--cancelCount <= 0)
							synchronized (this) {
								cancelCount = 10;	// Rest for next set of ten.
								// Checking on every 10 projects because they may be many projects, while only few configs.
								// This way it will stop sooner.
								if (monitor.isCanceled())
									return Status.CANCEL_STATUS;
							}						
						IProject p = ((IJavaProject) children[j]).getProject();
						if (projectNames.contains(p.getName())) {
							// This project has a launch config. If it has a setting, then do nothing, else need to put on not set. 
							if (p.getPersistentProperty(ProxyPlugin.PROPERTY_LAUNCH_CONFIGURATION) == null)
								p.getProject().setPersistentProperty(
									ProxyPlugin.PROPERTY_LAUNCH_CONFIGURATION,
									ProxyLaunchSupport.NOT_SET);
						} else {
							// This project has no launch configs. Remove any setting if it exists.
							p.setPersistentProperty(ProxyPlugin.PROPERTY_LAUNCH_CONFIGURATION, (String) null);
						}
					}
				}
				return Status.OK_STATUS;
			} catch (CoreException e) {
				return e.getStatus();
			}
		}
	};
	
	private void startCleanupJob() {
		cleanupJob.cancel();	// Stop what we are doing.
		cleanupJob.schedule(1000l);	// Schedule to start in one second.
	}

	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		DebugPlugin.getDefault().getLaunchManager().addLaunchConfigurationListener(launchListener);
		context.addBundleListener(new BundleListener() {
			public void bundleChanged(BundleEvent event) {
				if (event.getBundle() != ProxyPlugin.this.getBundle())
					return;	// Not of interest to us.
				switch (event.getType()) {
					case BundleEvent.STARTED:
						context.removeBundleListener(this);	// Since we don't care anymore
						startCleanupJob();
						break;
					case BundleEvent.STOPPED:
					case BundleEvent.UNINSTALLED:
					case BundleEvent.UNRESOLVED:
						context.removeBundleListener(this);	// We stopped before we started, so remove ourselves.
						break;
				}
			}
		});
		getPluginPreferences().setDefault(PREFERENCES_VM_NOVERIFY_KEY, true);
	}
	

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// Handle case where debug plugin shuts down before we do since order not guarenteed.
		if (DebugPlugin.getDefault() != null)
			DebugPlugin.getDefault().getLaunchManager().removeLaunchConfigurationListener(launchListener);
		cleanupJob.cancel();	// Stop what we are doing.		
		if (shutdownListeners != null) {
			Object[] listeners = shutdownListeners.getListeners();
			for (int i = 0; i < listeners.length; i++) {
				((IProxyPluginShutdownListener) listeners[i]).shutdown();
			}
		}		
		super.stop(context);
	}
	
	public static final String PI_CONFIGURATION_CONTRIBUTION_EXTENSION_POINT = "org.eclipse.jem.proxy.contributors"; //$NON-NLS-1$
	public static final String PI_EXTENSION_REGISTRATION_EXTENSION_POINT = "org.eclipse.jem.proxy.extensions"; //$NON-NLS-1$
	public static final String PI_CONTAINER = "container"; //$NON-NLS-1$
	public static final String PI_PLUGIN = "plugin"; //$NON-NLS-1$
	public static final String PI_CLASS = "class"; //$NON-NLS-1$
	public static final String PI_REGISTRY_TYPE = "registryType";	//$NON-NLS-1$
	public static final Map pluginRequiredMap = new HashMap(50);
	
	/*
	 * Processed extension point info for contributors.
	 */
	protected ContributorExtensionPointInfo contributions;
		
	/*
	 * Processed extension point info for registry extensions.
	 */
	protected ContributorExtensionPointInfo extensions;


	/**
	 * These are public only so that jem.ui can access this constant. Not meant to be accessed by others.
	 */
	public static final QualifiedName PROPERTY_LAUNCH_CONFIGURATION = new QualifiedName("org.eclipse.jem.proxy", "proxyLaunchConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
	
	/**
	 * Return the plugin ordered array of configuration elements for the given container, or <code>null</code> if not contributed.
	 * 
	 * @param containerid the first segment of all of the container paths is the container id.
	 * @param containerPaths array of container paths to match against for contributions. The paths must all be of the same container id.
	 * @return Array of configuration elements. 
	 * 
	 * @since 1.0.0
	 */
	public synchronized IConfigurationElement[] getContainerConfigurations(String containerid, String[] containerPaths) {
		if (contributions == null)
			processProxyContributionExtensionPoint();
		return (IConfigurationElement[]) contributions.containerPathContributions.getContributors(containerid, containerPaths);
	}

	/**
	 * Return the plugin ordered array of configuration elements for the given plugin, or <code>null</code> if not contributed.
	 * 
	 * @param pluginid
	 * @return Array of configuration elements or <code>null</code> if this plugin has no contributions.
	 * 
	 * @since 1.0.0
	 */
	public synchronized IConfigurationElement[] getPluginConfigurations(String pluginid) {
		if (contributions == null)
			processProxyContributionExtensionPoint();
		return (IConfigurationElement[]) contributions.pluginToContributions.get(pluginid);
	}

	/**
	 * Return the plugin ordered array of configuration elements for the given container, or <code>null</code> if not contributed.
	 * 
	 * @param containerid the first segment of all of the container paths is the container id.
	 * @param containerPaths array of container paths to match against for contributions. The paths must all be of the same container id.
	 * @return Array of configuration elements.
	 * 
	 * @since 1.0.0
	 */
	public synchronized IConfigurationElement[] getContainerExtensions(String containerid, String[] containerPaths) {
		if (extensions == null)
			processProxyExtensionExtensionPoint();
		return (IConfigurationElement[]) extensions.containerPathContributions.getContributors(containerid, containerPaths);
	}

	/**
	 * Return the plugin ordered array of configuration elements for the given plugin, or <code>null</code> if not contributed.
	 * 
	 * @param pluginid
	 * @return Array of configuration elements or <code>null</code> if this plugin has no contributions.
	 * 
	 * @since 1.0.0
	 */
	public synchronized IConfigurationElement[] getPluginExtensions(String pluginid) {
		if (extensions == null)
			processProxyExtensionExtensionPoint();
		return (IConfigurationElement[]) extensions.pluginToContributions.get(pluginid);
	}

	protected synchronized void processProxyContributionExtensionPoint() {
		contributions = processContributionExtensionPoint(PI_CONFIGURATION_CONTRIBUTION_EXTENSION_POINT);
	}
	
	protected synchronized void processProxyExtensionExtensionPoint() {
		extensions = processContributionExtensionPoint(PI_EXTENSION_REGISTRATION_EXTENSION_POINT);
	}	
	
	/**
	 * Process the extension point looking contributors. It will find entries that have the "container" or "plugin" attributes
	 * set on them.
	 * 
	 * @param extensionPoint fully-qualified extension point id, including plugin id of the extension point.
	 * @return the contributor info record.
	 * 
	 * @since 1.0.0
	 */
	public static ContributorExtensionPointInfo processContributionExtensionPoint(String extensionPoint) {	
		// We are processing this once because it is accessed often (once per vm per project).
		// This can add up so we get it together once here.
		IExtensionPoint extp = Platform.getExtensionRegistry().getExtensionPoint(extensionPoint);
		ContributorExtensionPointInfo result = new ContributorExtensionPointInfo();
		result.containerPathContributions = new ContainerPathContributionMapping(IConfigurationElement.class);
		if (extp == null) {
			result.pluginToContributions = Collections.EMPTY_MAP;
			return result;
		}
		
		IExtension[] extensions = extp.getExtensions();
		if (extensions.length > 0) {
			// Need to be in plugin order so that first ones processed have no dependencies on others.
			// Gather in extension order.
			// We want the list in reverse order of found extensions for a bundle. This is a heuristic to try
			// to get leaves ordered in the order found from the extension list. Since the orderPlugins actually
			// reverses the leaf order, hopefully this will set it back to what we want at the end.
			HashMap bundlesToExtensions = new LinkedHashMap(extensions.length);
			for (int i = extensions.length-1; i >= 0; i--) {
				Bundle bundle = Platform.getBundle(extensions[i].getContributor().getName());
				IExtension[] ext = (IExtension[]) bundlesToExtensions.get(bundle);
				if (ext == null)
					bundlesToExtensions.put(bundle, new IExtension[] { extensions[i]});
				else {
					// More than one extension defined in this plugin.
					IExtension[] newExt = new IExtension[ext.length + 1];
					System.arraycopy(ext, 0, newExt, 0, ext.length);
					newExt[newExt.length - 1] = extensions[i];
					bundlesToExtensions.put(bundle, newExt);
				}
			}

			// Now order them so we process in required order.
			Bundle[] ordered = ProxyPlugin.orderPlugins(bundlesToExtensions.keySet());
			Map patternStringToID_Pattern = new HashMap(); // Map of string patterns to the {container id, compiled pattern}. This so that we use the same compiled pattern everywhere.
			result.pluginToContributions = new HashMap(ordered.length);
			for (int i = 0; i < ordered.length; i++) {
				IExtension[] exts = (IExtension[]) bundlesToExtensions.get(ordered[i]);
				for (int j = 0; j < exts.length; j++) {
					IConfigurationElement[] configs = exts[j].getConfigurationElements();
					// Technically we expect the config elements to have a name of "contributor", but since that
					// is all that can be there, we will ignore it. The content is what is important.
					for (int k = 0; k < configs.length; k++) {
						String containerPattern = configs[k].getAttribute(PI_CONTAINER);
						if (containerPattern != null) {
							Object[] id_Pattern = (Object[]) patternStringToID_Pattern.get(containerPattern);
							if (id_Pattern == null) {
								int slash = containerPattern.indexOf('/');
								String containerID = slash != -1 ? containerPattern.substring(0, slash) : containerPattern;
								// The pattern becomes for the containerPattern "SWT_CONTAINER" becomes "SWT_CONTAINER(/.*)*". This
								// means to match the string must start with "SWT_CONTAINER" and it must have either nothing after this
								// or it must have a "/" and any characters after that. So this means it will not match "SWT_CONTAINERXZ"
								// but it will match "SWT_CONTAINER/XYZ".
								id_Pattern = new Object[] { containerID, Pattern.compile(containerPattern + "(/.*)*")}; //$NON-NLS-1$
								patternStringToID_Pattern.put(containerPattern, id_Pattern);
							}
							result.containerPathContributions.addContribution((String) id_Pattern[0], (Pattern) id_Pattern[1], configs[k]);
						}

						String plugin = configs[k].getAttribute(PI_PLUGIN);
						if (plugin != null) {
							List contributions = (List) result.pluginToContributions.get(plugin);
							if (contributions == null) {
								contributions = new ArrayList(1);
								result.pluginToContributions.put(plugin, contributions);
							}
							contributions.add(configs[k]);
						}
					}
				}
			}

			// Finalize the mappings for both container paths and plugins.
			result.containerPathContributions.finalizeMapping();
			for (Iterator iter = result.pluginToContributions.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();
				entry.setValue(((List) entry.getValue()).toArray(new IConfigurationElement[((List) entry.getValue()).size()]));
			}
		} else
			result.pluginToContributions = Collections.EMPTY_MAP;
		return result;
	}
	
	/**
	 * The result of the {@link ProxyPlugin#getIDsFound(IJavaProject)}.
	 * 
	 * @since 1.2.0
	 */
	public static class FoundIDs {
		
		/**
		 * Map of (containerIds(String)->{@link ContainerPaths}) of containers classpaths found in the project's classpath.
		 * 
		 */
		public Map containerIds = new HashMap(2);
		
		/**
		 * Map of (containers({@link IClasspathContainer})->Boolean) of containers found in the project's classpath. The value will be <code>true</code> if the container is visible to the top-level project.
		 */
		public Map containers = new HashMap(2);
		
		/**
		 * Map of (pluginIds(String)->Boolean) of plugin ids found in the project's classpath if the project is a plugin project. The value will be <code>true</code> if the plugin is visible to the top-level project.
		 */
		public Map pluginIds;
		
		/**
		 * Map of (projects({@link IPath}->Boolean) of project found in the project's classpath. The value will be <code>true</code> if the project is visible to the top-level project.
		 */
		public Map projects = new HashMap(2);
		
		FoundIDs() {
			// not meant to be instantiated or subclassed by clients.
		}
	}
	
	/**
	 * For the given java project, return the maps of found ids.
	 *
	 * @param jproject
	 * @return the found ids.
	 * 
	 * @since 1.0.0
	 */
	public FoundIDs getIDsFound(IJavaProject jproject) throws JavaModelException {		
		IPath projectPath = jproject.getProject().getFullPath();
		FoundIDs foundIDs = new FoundIDs();
		foundIDs.projects.put(projectPath, Boolean.TRUE);		
		expandProject(projectPath, foundIDs, true, true);
		
		// The containerIds values will actually be an Map[] {Map(visibleIPath->pathString), Map(hiddenIPath->pathString)}. Needs to be converted to a ContainerPaths now
		// that we are done.
		for (Iterator itr = foundIDs.containerIds.entrySet().iterator(); itr.hasNext();) {
			Map.Entry entry = (Entry) itr.next();
			Map[] value = (Map[]) entry.getValue();
			entry.setValue(new ContainerPaths((String) entry.getKey(), (String[]) value[0].values().toArray(new String[value[0].size()]), (String[]) value[1].values().toArray(new String[value[1].size()])));
		}
		
		if (foundIDs.pluginIds == null)
			foundIDs.pluginIds = Collections.EMPTY_MAP;
		
		foundIDs.projects.remove(projectPath);	// Don't need to include itself now, was needed for testing so if ciruclar we don't get into a loop.
		if (foundIDs.projects.isEmpty())
			foundIDs.projects = Collections.EMPTY_MAP;
		
		return foundIDs;

	}
	
	private static final IPath JRE_CONTAINER_PATH = new Path(JavaRuntime.JRE_CONTAINER);
	
	/*
	 * The passed in visible flag tells if this project is visible and its contents are visible if they are exported.
	 * Only exception is if first is true, then all contents are visible to the top level project.
	 */
	private void expandProject(IPath projectPath, FoundIDs foundIds, boolean visible, boolean first) throws JavaModelException {
		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(projectPath.lastSegment());
		if (res == null)
			return;	// Not exist so don't delve into it.
		IJavaProject project = (IJavaProject)JavaCore.create(res);
		if (project == null || !project.exists() || !project.getProject().isOpen())
			return;	// Not exist as a java project or not open, so don't delve into it.

		IClasspathEntry[] entries = project.getRawClasspath();
		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			Boolean currentFlag = null;	// Current setting value.
			boolean newFlag;	// The new setting value. 
			switch (entry.getEntryKind()) {
				case IClasspathEntry.CPE_PROJECT:
					// Force true if already true, or this is the first project, or this project is visible and the entry is exported. These override a previous false.
					currentFlag = (Boolean) foundIds.projects.get(entry.getPath());
					newFlag = (currentFlag != null && currentFlag.booleanValue()) || first || (visible && entry.isExported());
					if (currentFlag == null || currentFlag.booleanValue() != newFlag)
						foundIds.projects.put(entry.getPath(),  newFlag ? Boolean.TRUE : Boolean.FALSE );
					if (currentFlag == null)
						expandProject(entry.getPath(), foundIds, visible && entry.isExported(), false);
					break;
				case IClasspathEntry.CPE_CONTAINER:
					if (!first && JavaRuntime.JRE_CONTAINER.equals(entry.getPath().segment(0))) //$NON-NLS-1$
						break;	// The first project determines the JRE, so any subsequent ones can be ignored.
					Map[] paths = (Map[]) foundIds.containerIds.get(entry.getPath().segment(0));
					if (paths == null) {
						paths = new Map[] {new HashMap(2), new HashMap(2)};
						foundIds.containerIds.put(entry.getPath().segment(0), paths);
					}
					currentFlag = null;
					if (paths[0].containsKey(entry.getPath()))
						currentFlag = Boolean.TRUE;
					else if (paths[1].containsKey(entry.getPath()))
						currentFlag = Boolean.FALSE;
					newFlag = (currentFlag != null && currentFlag.booleanValue()) || first || (visible && entry.isExported());					
					if (currentFlag == null || currentFlag.booleanValue() != newFlag) {
						if (newFlag) {
							// It is visible, remove from hidden, if there, and add to visible.
							paths[1].remove(entry.getPath());
							paths[0].put(entry.getPath(), entry.getPath().toString());
						} else {
							// It is hidden, remove from visible, if there, and add to hidden.
							paths[0].remove(entry.getPath());
							paths[1].put(entry.getPath(), entry.getPath().toString());
						}
					}

					IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), project);
					// Force true if already true, or this is the first project, or this project is visible and the entry is exported. These override a previous false.
					currentFlag = (Boolean) foundIds.containers.get(container);
					newFlag = (currentFlag != null && currentFlag.booleanValue()) || first || (visible && entry.isExported());
					if (currentFlag == null || currentFlag.booleanValue() != newFlag)					
						foundIds.containers.put(container,  newFlag ? Boolean.TRUE : Boolean.FALSE );
					break;
				case IClasspathEntry.CPE_VARIABLE:
					// We only care about JRE_LIB. If we have that, then we will treat it as JRE_CONTAINER. Only
					// care about first project too, because the first project is the one that determines the JRE type.
					if (first && JavaRuntime.JRELIB_VARIABLE.equals(entry.getPath().segment(0))) { //$NON-NLS-1$
						paths = (Map[]) foundIds.containerIds.get(JavaRuntime.JRE_CONTAINER);
						if (paths == null) {
							paths = new Map[] {new HashMap(2), new HashMap(2)};
							foundIds.containerIds.put(JavaRuntime.JRE_CONTAINER, paths);
						}
						currentFlag = null;
						if (paths[0].containsKey(JRE_CONTAINER_PATH))
							currentFlag = Boolean.TRUE;
						else if (paths[1].containsKey(JRE_CONTAINER_PATH))
							currentFlag = Boolean.FALSE;
						newFlag = (currentFlag != null && currentFlag.booleanValue()) || first || (visible && entry.isExported());					
						if (currentFlag == null || currentFlag.booleanValue() != newFlag) {
							if (newFlag) {
								// It is visible, remove from hidden, if there, and add to visible.
								paths[1].remove(JRE_CONTAINER_PATH);
								paths[0].put(JRE_CONTAINER_PATH, JavaRuntime.JRE_CONTAINER);
							} else {
								// It is hidden, remove from visible, if there, and add to hidden.
								paths[0].remove(JRE_CONTAINER_PATH);
								paths[1].put(JRE_CONTAINER_PATH, JavaRuntime.JRE_CONTAINER);
							}
						}
					}
					break;
				default:
					break;
			}
		}		
		
		findPlugins(foundIds, visible, first, project);
	}
	
	/**
	 * Find the plugins that the given project references, either directly or indirectly.
	 * <p>
	 * The map will be of plugin ids to a Boolean. If the boolean is <code>BooleanTRUE</code>,
	 * then the plugin is visible to the given project. the visible and first flags
	 * will modify this. If first is true, then all direct plugins will be visible,
	 * else only exported plugins will be visible. If visible is false and first is false, then it doesn't matter, all of the
	 * plugins will not be visible. 
	 * <p>
	 * Visible means that classes in the plugin can be referenced directly from code. Not visible
	 * means that they can only be referenced from some other plugin in the list. In other words,
	 * visible ones can be directly referenced, but invisible ones can only be referenced from
	 * plugins that can see it.
	 * <p>
	 * For most uses, first and visible should be true. Then it will treat the project as the toplevel
	 * project and will return true for those that are visible to it, either directly or indirectly.
	 * These flags were added for more special cases where may be calling on a project that is deeper
	 * down in the classpath were visibilty has already been decided.
	 * <p>
	 * Note: PDE must be installed for this to return anything, otherwise it will leave
	 * the map alone.
	 * 
	 * @param foundIds foundIds structure to get plugin info from.
	 * @param visible <code>true</code> means this project is visible, so any plugins visible to it will be visible, else none will be visible.
	 * @param first <code>true</code> if this is the top project of interest. This means that all plugins within the project are visible. Else only exported projects will be visible.
	 * @param project project to start looking from
	 * 
	 * @since 1.0.2
	 */
	public void findPlugins(FoundIDs foundIds, boolean visible, boolean first, IJavaProject project) {
		try {
			// To prevent unnecessary loading of the PDE plugin, find the plugins only if this project is a PDE plugin project.
			if (isPDEProject(project)) {
				IPDEProcessForPlugin pdeprocess = getPDEProcessForPlugin();
				if (pdeprocess != null) {
					if (foundIds.pluginIds == null)
						foundIds.pluginIds = new HashMap();
					pdeprocess.findPlugins(project, foundIds, visible, first); // expand the plugins for this project, if any.
				}
			}
		} catch (CoreException e) {
		}
	}

	/**
	 * 
	 * @param project
	 * 
	 * @return true if this is a Plugin project or false if not
	 * @throws CoreException
	 * 
	 * @since 1.1.0
	 */
	public static boolean isPDEProject(IJavaProject project) throws CoreException {
		return project.getProject().hasNature(PDE_NATURE_ID);
	}


	/*
	 * Interface for processing Plugins. Used when PDE plugin is present in the installation. 
	 * 
	 * @since 1.0.2
	 */
	interface IPDEProcessForPlugin {

		/*
		 * Go through the project and find all of the plugins it references, either directly or through
		 * the referenced plugins, and mark them as visible or not.
		 */
		public abstract void findPlugins(IJavaProject project, FoundIDs foundIds, boolean visible, boolean first);
	}
	
	/*
	 * Try to get the pde process for plugin. If already tried once and not found, then forget it.
	 * <package-protected> because PDEContributeClasspath needs it too.
	 */
	private IPDEProcessForPlugin pdeProcessForPlugin;
	private boolean triedPDEProcess;
	IPDEProcessForPlugin getPDEProcessForPlugin() {
		if (!triedPDEProcess) {
			triedPDEProcess = true;
			if (Platform.getBundle("org.eclipse.pde.core") != null) { //$NON-NLS-1$
				try {
					Class classPDEProcess = Class.forName("org.eclipse.jem.internal.proxy.core.PDEProcessForPlugin"); //$NON-NLS-1$
					pdeProcessForPlugin = (IPDEProcessForPlugin) classPDEProcess.newInstance();
				} catch (ClassNotFoundException e) {
					// Not found, do nothing.
				} catch (InstantiationException e) {
					getLogger().log(e, Level.WARNING);
				} catch (IllegalAccessException e) {
					getLogger().log(e, Level.WARNING);
				}
			}
		}
		return pdeProcessForPlugin;
	}
	
	/**
	 * This tries to find a jar in the bundle specified, and the attached source using the
	 * PDE source location extension point. The jar must exist for source to be attachable.
	 * The source must be in the standard PDE source plugin. I.e. it must be in a directory
	 * of the name "bundlename_bundleversion", and in the same path from there as in the
	 * jar, plus the name must be "jarnamesrc.zip".
	 * <p>
	 * The returned URL's will not be Platform.resolve(). They will be in form returned from
	 * Platform.find().
	 * 
	 * @param bundle bundle to search, will search fragments too.
	 * @param filepath filepath from the root of the bundle/fragment where the jar will be found. 
	 * @return two URL's. [0] is the URL to the jar, <code>null</code> if not found, [2] is the URL to the source zip, <code>null</code> if not found.
	 * 
	 * @since 1.0.0
	 */
	public URL[] findPluginJarAndAttachedSource(Bundle bundle, IPath filepath) {
		// This is a bit kludgy, but the algorithm is to find the file first, and then get the root url of the bundle/fragment
		// that matches the found file. This will be used to calculate the name of the directory under the source. From there
		// all of the source extensions will be searched for the source zip file.
		// This is assuming that find returns a url where the file part of the url is a standard path and doesn't have
		// things like special chars to indicate within a jar. That would appear when it is resolved, but I think that the
		// unresolved ones from find are typically "jarbundle://nnn/path" or something like that. This is a gray area.
		URL jarURL = FileLocator.find(bundle, filepath, null);
		if (jarURL == null)
			return new URL[2];
		
		// Found it, so let's try to find which bundle/fragment it was found in.
		String jarString = jarURL.toExternalForm();
		// First the bundle itself.
		String installLoc = bundle.getEntry("/").toExternalForm(); //$NON-NLS-1$
		URL sourceURL = null;
		if (jarString.startsWith(installLoc))
			sourceURL = getSrcFrom(bundle, installLoc, jarString);
		else {
			// Now look in the fragments.
			Bundle[] frags = Platform.getFragments(bundle);
			for (int i = 0; i < frags.length; i++) {
				installLoc = frags[i].getEntry("/").toExternalForm(); //$NON-NLS-1$
				if (jarString.startsWith(installLoc)) {
					sourceURL = getSrcFrom(frags[i], installLoc, jarString);
					break;
				}
			}
		}
		return new URL[] {jarURL, sourceURL};
	}
	
	private URL getSrcFrom(Bundle bundle, String installLoc, String jarString) {
		// format of path in a PDE source plugin is (under the "src" directory from the extension point),
		// "bundlename_bundleversion/pathOfJar/jarnamesrc.zip". However there is no way to know
		// which extension has the source in it, so we need to search them all.
		
		IPath srcPath = new Path(bundle.getSymbolicName()+"_"+ (String) bundle.getHeaders("").get(Constants.BUNDLE_VERSION)); //$NON-NLS-1$ //$NON-NLS-2$ $NON-NLS-2$
		srcPath = srcPath.append(new Path(jarString.substring(installLoc.length())));
		if (srcPath.segmentCount() < 2)
			return null;	// Something is not right. No jar name.
		srcPath = srcPath.removeFileExtension();	// Remove the .jar.
		String jarName = srcPath.lastSegment();	// This should be the jar name.
		srcPath = srcPath.removeLastSegments(1).append(jarName+"src.zip"); //$NON-NLS-1$
		
		// Now look through all of the src extensions. Can't tell if the extension is from a fragment or a bundle, so we need to
		// use Platform.find() to look in the bundle and fragment. So we may get a dup search if there is a fragment source 
		// (for example platform source and win32 platform source (which is a fragment of platform source).
		IConfigurationElement[] ces = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.pde.core.source"); //$NON-NLS-1$
		for (int i = 0; i < ces.length; i++) {
			IPath srcsrch = new Path(ces[i].getAttribute("path")).append(srcPath); //$NON-NLS-1$
			Bundle srcBundle = Platform.getBundle(ces[i].getDeclaringExtension().getContributor().getName());
			URL srcUrl = FileLocator.find(srcBundle, srcsrch, null);
			if (srcUrl != null) {
				return srcUrl;
			}
		}
		return null;
	}
}

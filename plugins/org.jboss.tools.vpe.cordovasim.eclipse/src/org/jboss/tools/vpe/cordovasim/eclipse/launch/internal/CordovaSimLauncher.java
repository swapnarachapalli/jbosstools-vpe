/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.launch.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cordavasim.eclipse.callbacks.CordovaSimRestartCallback;
import org.jboss.tools.vpe.browsersim.eclipse.Activator;
import org.jboss.tools.vpe.browsersim.eclipse.callbacks.JsLogCallback;
import org.jboss.tools.vpe.browsersim.eclipse.callbacks.LogCallback;
import org.jboss.tools.vpe.browsersim.eclipse.callbacks.OpenFileCallback;
import org.jboss.tools.vpe.browsersim.eclipse.callbacks.ViewSourceCallback;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.BrowserSimLauncher;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.ExternalProcessCallback;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.ExternalProcessLauncher;
import org.jboss.tools.vpe.browsersim.eclipse.preferences.BrowserSimPreferencesPage;
import org.jboss.tools.vpe.browsersim.util.BrowserSimUtil;

/**
 * @author "Yahor Radtsevich (yradtsevich)"
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class CordovaSimLauncher {
	public static final String CORDOVASIM_CLASS_NAME = "org.jboss.tools.vpe.cordovasim.CordovaSimRunner"; //$NON-NLS-1$
	private static final List<ExternalProcessCallback> CORDOVASIM_CALLBACKS = Arrays.asList(
			new ViewSourceCallback(),
			new OpenFileCallback(),
			new LogCallback(),
			new JsLogCallback(),
			new CordovaSimRestartCallback()
		);


	private static final List<String> RESOURCES_BUNDLES = BrowserSimLauncher.RESOURCES_BUNDLES;
	
	//if you change this parameter, see also @org.jbosstools.browsersim.ui.BrowserSim
	private static final String NOT_STANDALONE = BrowserSimLauncher.NOT_STANDALONE;	
	
	public static void launchCordovaSim(String projectString, String rootFolderString, String startPageString,
			Integer port) {
		List<String> parameters = new ArrayList<String>();
		parameters.add(NOT_STANDALONE);

		IContainer rootFolder = null;
		IProject project = null;
		String cordovaEngineLocation = null;
		String cordovaVersion = null;
		
		if (projectString != null) {
			project = CordovaSimLaunchParametersUtil.getProject(projectString);
			
			if (project != null) {
				cordovaEngineLocation = CordovaSimLaunchParametersUtil.getCordovaEngineLocation(project);
				cordovaVersion = CordovaSimLaunchParametersUtil.getCordovaVersion(project);			
			}
			
			if (rootFolderString != null) {
				rootFolder = CordovaSimLaunchParametersUtil.getRootFolder(project, rootFolderString);
			} else {
				rootFolder = CordovaSimLaunchParametersUtil.getDefaultRootFolder(project);
			}
		}
		
		String actualStartPageString = null;
		if (startPageString != null) {
			actualStartPageString = startPageString;
		} else {
			IResource startPage = CordovaSimLaunchParametersUtil.getDefaultStartPage(project, rootFolder);
			IPath startPagePath = CordovaSimLaunchParametersUtil.getRelativePath(rootFolder, startPage);
			if (startPagePath != null) {
				actualStartPageString = startPagePath.toString();
			}
		}
		
		if (rootFolder != null && actualStartPageString != null) {
			parameters.add(rootFolder.getLocation().toString());
			parameters.add(actualStartPageString);
			
			if (port != null) {
				parameters.add("-port"); //$NON-NLS-1$
				parameters.add(String.valueOf(port));
			}
			
			if (cordovaEngineLocation != null) {
				parameters.add("-engine"); //$NON-NLS-1$
				parameters.add(cordovaEngineLocation);
			}
			
			if (cordovaVersion != null) {
				parameters.add("-version"); //$NON-NLS-1$
				parameters.add(cordovaVersion);
			}

			launchCordovaSim(parameters);
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					Shell parentShell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
					if (parentShell == null) {
						parentShell = PlatformUI.getWorkbench().getDisplay().getShells()[0]; // Hot fix for gtk3
					}	
					MessageDialog.openError(parentShell,
							Messages.CordovaSimLauncher_CANNOT_RUN_CORDOVASIM, Messages.CordovaSimLauncher_CANNOT_FIND_ROOT_FOLDER);					
				}
			});
		}
	}
	
	public static List<String> getBundles() {
		List<String> bundles = BrowserSimLauncher.getBundles();
		bundles.addAll(Arrays.asList(
				"org.jboss.tools.vpe.cordovasim", //$NON-NLS-1$
				"org.jboss.tools.vpe.cordovasim.ripple", //$NON-NLS-1$
				"org.jboss.tools.vpe.browsersim.debugger", //$NON-NLS-1$
				"org.eclipse.jetty.continuation", //$NON-NLS-1$
				"org.eclipse.jetty.continuation", //$NON-NLS-1$
				"org.eclipse.jetty.http", //$NON-NLS-1$
				"org.eclipse.jetty.io", //$NON-NLS-1$
				"org.eclipse.jetty.security", //$NON-NLS-1$
				"org.eclipse.jetty.server", //$NON-NLS-1$
				"org.eclipse.jetty.servlet", //$NON-NLS-1$
				"org.eclipse.jetty.util", //$NON-NLS-1$
				"org.eclipse.jetty.client", //$NON-NLS-1$
				"org.eclipse.jetty.servlets", //$NON-NLS-1$
				"org.eclipse.jetty.rewrite", //$NON-NLS-1$
				"javax.servlet" //$NON-NLS-1$
		));

		return bundles;
	}
	
	public static void launchCordovaSim(List<String> parameters) {
		IVMInstall jvm = BrowserSimLauncher.getSelectedVM();
		
		String jvmPath = jvm.getInstallLocation().getAbsolutePath();
		String jrePath = jvm.getInstallLocation().getAbsolutePath() + File.separator + "jre"; //$NON-NLS-1$
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		List<String> bundles = getBundles();
		
		if ((IPreferenceStore.FALSE.equals(store.getString(BrowserSimPreferencesPage.BROWSERSIM_GTK_2))
				|| (!BrowserSimUtil.isJavaFxAvailable(jvmPath) && !BrowserSimUtil.isJavaFxAvailable(jrePath)))) {
			bundles.add("org.jboss.tools.vpe.browsersim.javafx.mock"); //$NON-NLS-1$
		}
		
		ExternalProcessLauncher.launchAsExternalProcess(bundles, RESOURCES_BUNDLES,
				CORDOVASIM_CALLBACKS, CORDOVASIM_CLASS_NAME, parameters, Messages.CordovaSimLauncher_CORDOVASIM, jvm);
	}
}

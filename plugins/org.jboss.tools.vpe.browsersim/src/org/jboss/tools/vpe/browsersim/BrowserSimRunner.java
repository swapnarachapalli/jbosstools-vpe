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
package org.jboss.tools.vpe.browsersim;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.vpe.browsersim.browser.PlatformUtil;
import org.jboss.tools.vpe.browsersim.browser.javafx.JavaFXBrowser;
import org.jboss.tools.vpe.browsersim.devtools.DevToolsDebuggerServer;
import org.jboss.tools.vpe.browsersim.ui.BrowserSim;
import org.jboss.tools.vpe.browsersim.ui.CocoaUIEnhancer;
import org.jboss.tools.vpe.browsersim.ui.ExceptionNotifier;
import org.jboss.tools.vpe.browsersim.ui.Messages;
import org.jboss.tools.vpe.browsersim.util.BrowserSimUtil;

/**
 * @author Konstantin Marmalyukov (kmarmaliykov)
 */

public class BrowserSimRunner {
	public static final String PLUGIN_ID = "org.jboss.tools.vpe.browsersim"; //$NON-NLS-1$
	
	
	public static final String NOT_STANDALONE = "-not-standalone"; //$NON-NLS-1$
	public static final String ABOUT_BLANK = "about:blank"; //"http://www.w3schools.com/js/tryit_view.asp?filename=try_nav_useragent"; //$NON-NLS-1$
	
	private static boolean isJavaFxAvailable;
	static {
		if (PlatformUtil.OS_MACOSX.equals(PlatformUtil.getOs())) {
			CocoaUIEnhancer.initializeMacOSMenuBar(Messages.BrowserSim_BROWSER_SIM);
		}
	}
	
	static { 
		isJavaFxAvailable = false;
		boolean isLinux = PlatformUtil.OS_LINUX.equals(PlatformUtil.getOs());
		
		// Trying to load javaFx libs except Linux GTK3 case
		if (!(isLinux && !BrowserSimUtil.isRunningAgainstGTK2())) {
			isJavaFxAvailable = BrowserSimUtil.loadJavaFX();
		}
	}
	
	public static void main(String[] args) {
		Display display = null;
		try {
			BrowserSimArgs browserSimArgs = BrowserSimArgs.parseArgs(args);
			
			String path = browserSimArgs.getPath();
			String url;
			if (path != null) {
				try {
					new URI(path); // validate URL
					url = path;
				} catch (URISyntaxException e) {
					url = ABOUT_BLANK;
				}
			} else {
				url = ABOUT_BLANK;
			}
	
			Shell parent = null;
			if (!PlatformUtil.OS_MACOSX.equals(PlatformUtil.getOs())) {
				parent = new Shell(Display.getDefault(), SWT.NO_TRIM);
				parent.setSize(0, 0);
				BrowserSimUtil.setShellAttributes(parent);
				parent.open();
			}

			BrowserSim browserSim = new BrowserSim(url, parent);
			browserSim.open(isJavaFxAvailable);
			
            if (browserSim.getBrowser() instanceof JavaFXBrowser) {
                DevToolsDebuggerServer.startDebugServer(((JavaFXBrowser)browserSim.getBrowser()).getDebugger());
            }

			display = Display.getDefault();
			while (!display.isDisposed() && BrowserSim.getInstances().size() > 0) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (SWTError e) {
			ExceptionNotifier.showBrowserSimLoadError(new Shell(Display.getDefault()), e, Messages.BrowserSim_BROWSER_SIM);
		} catch (Throwable t) {
			BrowserSimLogger.logError(t.getMessage(), t);
		} finally {
			if (display != null) {
				display.dispose();
			}
		}
	}
}

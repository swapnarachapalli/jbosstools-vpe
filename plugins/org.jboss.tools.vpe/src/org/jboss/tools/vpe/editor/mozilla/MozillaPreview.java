/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.vpe.editor.mozilla;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.jst.web.ui.internal.editor.bundle.BundleMap;
import org.jboss.tools.vpe.editor.VpeEditorPart;
import org.jboss.tools.vpe.editor.VpePreviewDomBuilder;
import org.jboss.tools.vpe.editor.VpeSourceDomBuilder;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.mozilla.listener.EditorLoadWindowListener;
import org.jboss.tools.vpe.editor.mozilla.listener.MozillaDndListener;
import org.jboss.tools.vpe.editor.template.VpeTemplateManager;
import org.jboss.tools.vpe.xulrunner.PlatformIsNotSupportedException;
import org.jboss.tools.vpe.xulrunner.XulRunnerBundleNotFoundException;
import org.jboss.tools.vpe.xulrunner.XulRunnerException;
import org.mozilla.xpcom.XPCOMException;

/**
 * a class implementation of mozilla preview 
 * @author A. Yukhovich
 */
public class MozillaPreview extends MozillaEditor {

	private EditorLoadWindowListener editorLoadWindowListener;
	private MozillaDndListener editorDomEventListener;
	private VpeTemplateManager templateManager;	
	private VpePageContext pageContext;
	private StructuredTextEditor sourceEditor;
	private VpeEditorPart editPart;
	private IDOMDocument sourceDocument;

	public MozillaPreview(VpeEditorPart editPart, StructuredTextEditor sourceEditor) {
		setTemplateManager(VpeTemplateManager.getInstance());
		setSourceEditor(sourceEditor);
		setEditPart(editPart);
	}
	

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(final Composite parent) {
		try {
			setXulRunnerEditor(new XulRunnerPreview(parent, this));
			setInitialContent();
			getXulRunnerEditor().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		} catch (XulRunnerBundleNotFoundException e) {
			// TODO add page with explanation what to do
			showXulRunnerError(parent, e);
		} catch (PlatformIsNotSupportedException e) {
			// TODO add page with explanation what platforms supported and what tod o
			showXulRunnerError(parent, e);
		} catch (XulRunnerException e) {
			showXulRunnerError(parent, e);
		} catch(XPCOMException e) {
			showXulRunnerError(parent, e);
		}
	}

	@Override
	public void setEditorLoadWindowListener(EditorLoadWindowListener listener) {
		editorLoadWindowListener = listener;
	}


	/**
	 * 
	 */
	public void onLoadWindow() {
		setContentArea(findContentArea());
		attachMozillaEventAdapter();
		if (editorLoadWindowListener != null) {
			editorLoadWindowListener.load();
		}
	}
	@Override
	public void showResizer() {
	}

	@Override
	public void hideResizer() {
	}

	//TODO Max Areshkau logic error (we should first call buildDom and only then we can call rebuildDom)
	public void rebuildDom() {
		if(getPageContext()!=null&&getPageContext().getVisualBuilder()!=null&&getSourceDocument()!=null) {
			
		getPageContext().getVisualBuilder().rebuildDom(getSourceDocument());
		}
	}

	//TODO Max Areshkau logic error (we should first call buildDom and only then we can call rebuildDom)
	public void buildDom() {
		BundleMap bundle = new BundleMap();
		bundle.init(getSourceEditor().getEditorInput());
		//Fix for https://jira.jboss.org/jira/browse/JBIDE-5639 - mareshkau
		// in preview should be closer to view in browser. So all
		// bundles should be showed as messages, no as el.
		bundle.setShowBundleUsageAsEL(false);
		
		setPageContext(new VpePageContext(bundle, getEditPart()));
		
		VpeDomMapping domMapping = new VpeDomMapping(getPageContext());
		VpeSourceDomBuilder sourceBuilder = new VpeSourceDomBuilder(domMapping, null, getTemplateManager(), getSourceEditor(), getPageContext());		
		VpeVisualDomBuilder visualBuilder = new VpePreviewDomBuilder(domMapping, null, getTemplateManager(), this, getPageContext());
		getPageContext().setSourceDomBuilder(sourceBuilder);
		getPageContext().setVisualDomBuilder(visualBuilder);

		IDOMModel sourceModel = (IDOMModel)getSourceEditor().getModel();
		setSourceDocument(sourceModel.getDocument());

		visualBuilder.buildDom(getSourceDocument());		
	}

	/**
	 * @return the sourceDocument
	 */
	protected IDOMDocument getSourceDocument() {
		return sourceDocument;
	}

	/**
	 * @param sourceDocument the sourceDocument to set
	 */
	protected void setSourceDocument(IDOMDocument sourceDocument) {
		this.sourceDocument = sourceDocument;
	}



	/**
	 * @return the pageContext
	 */
	private VpePageContext getPageContext() {
		return pageContext;
	}



	/**
	 * @param pageContext the pageContext to set
	 */
	private void setPageContext(VpePageContext pageContext) {
		this.pageContext = pageContext;
	}



	/**
	 * @return the editPart
	 */
	private VpeEditorPart getEditPart() {
		return editPart;
	}



	/**
	 * @param editPart the editPart to set
	 */
	private void setEditPart(VpeEditorPart editPart) {
		this.editPart = editPart;
	}



	/**
	 * @return the templateManager
	 */
	private VpeTemplateManager getTemplateManager() {
		return templateManager;
	}



	/**
	 * @param templateManager the templateManager to set
	 */
	private void setTemplateManager(VpeTemplateManager templateManager) {
		this.templateManager = templateManager;
	}



	/**
	 * @return the sourceEditor
	 */
	private StructuredTextEditor getSourceEditor() {
		return sourceEditor;
	}



	/**
	 * @param sourceEditor the sourceEditor to set
	 */
	private void setSourceEditor(StructuredTextEditor sourceEditor) {
		this.sourceEditor = sourceEditor;
	}

	public void dispose() {
		setEditorLoadWindowListener(null);
		if (pageContext != null) {
			pageContext.dispose();
			pageContext=null;
		}
		if (getXulRunnerEditor() != null) {
			getXulRunnerEditor().dispose();
			setXulRunnerEditor(null);
		}
		sourceDocument=null;
		sourceEditor=null;
		editPart=null;
		
		super.dispose();
	}

	@Override
	protected MozillaEventAdapter createMozillaEventAdapter() {
		return new PreviewDomEventListener();
	}
}

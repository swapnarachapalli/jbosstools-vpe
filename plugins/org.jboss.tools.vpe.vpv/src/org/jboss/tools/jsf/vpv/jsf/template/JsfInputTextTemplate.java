/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.vpv.jsf.template;

import org.jboss.tools.vpe.vpv.mapping.AttributeData;
import org.jboss.tools.vpe.vpv.mapping.VpeElementData;
import org.jboss.tools.vpe.vpv.template.VpeCreationData;
import org.jboss.tools.vpe.vpv.template.util.HTML;
import org.jboss.tools.vpe.vpv.template.util.VisualDomUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JsfInputTextTemplate extends AbstractEditableJsfTemplate {

	public VpeCreationData create(Node sourceNode, Document visualDocument) {
		Element sourceElement = (Element) sourceNode;
		Element input = visualDocument.createElement(HTML.TAG_INPUT);
		/*
         * https://jira.jboss.org/jira/browse/JBIDE-3225
         * Component should render its children.
         */
		VpeCreationData creationData = VisualDomUtil
				.createTemplateWithTextContainer(sourceElement,
						input, HTML.TAG_SPAN, visualDocument);
		copyGeneralJsfAttributes(sourceElement, input);
		ComponentUtil.copyDisabled(sourceElement, input);
		copyAttribute(input, sourceElement, JSF.ATTR_VALUE, HTML.ATTR_VALUE);
		copyAttribute(input, sourceElement, JSF.ATTR_SIZE, HTML.ATTR_SIZE);
		copyAttribute(input, sourceElement, JSF.ATTR_DIR, HTML.ATTR_DIR);
		VpeElementData elementData = new VpeElementData();
		if (sourceElement.hasAttribute(JSF.ATTR_VALUE)) {
			Attr attr = sourceElement.getAttributeNode(JSF.ATTR_VALUE);
			elementData.addNodeData(new AttributeData(attr, input, true));
		} else {
			elementData.addNodeData(new AttributeData(JSF.ATTR_VALUE, input, true));
		}
		creationData.setElementData(elementData);
		return creationData;
	}
}

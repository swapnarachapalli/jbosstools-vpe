/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.vpe.vpv.template.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.tools.vpe.vpv.template.VpeChildrenInfo;
import org.jboss.tools.vpe.vpv.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class VisualDomUtil {
	
	public static String JSF_CORE_URI = "http://java.sun.com/jsf/core"; //$NON-NLS-1$
    public static String JSF_HTML_URI = "http://java.sun.com/jsf/html"; //$NON-NLS-1$
    public static String RICH_FACES_URI = "http://richfaces.org/rich"; //$NON-NLS-1$
    public static String A4J_URI = "http://richfaces.org/a4j"; //$NON-NLS-1$
    public static String FACELETS_URI = "http://java.sun.com/jsf/facelets"; //$NON-NLS-1$
    public static final String VPE_FACET = "VPE-FACET"; //$NON-NLS-1$
    
    /**
     * Flag for correct JSF components
     */
     public static String FACET_JSF_TAG = "FACET-JSF-TAG"; //$NON-NLS-1$
     /**
      * Flag for RF, FACELETS, A4J tags
      */
     public static String FACET_ODD_TAGS = "FACET-ODD-TAGS"; //$NON-NLS-1$
     /**
      * Flag for plain HTML tags and text nodes
      */
     public static String FACET_HTML_TAGS = "FACET-HTML-TAGS"; //$NON-NLS-1$

//	private static Reference<nsIAccessibilityService> accessibilityServiceCache = null;
	
	private static Set<String> escapedTags;
	
	static {
		escapedTags = new HashSet<String>();
		escapedTags.add("f:facet"); //$NON-NLS-1$
		escapedTags.add("f:selectItem"); //$NON-NLS-1$
		escapedTags.add("f:selectItems"); //$NON-NLS-1$
	}

	static public Node getAncestorNode(Node visualNode, String tagName){
		if (tagName == null) return null;
		Node element = visualNode;
		while (true){
			if (tagName.equalsIgnoreCase(element.getNodeName())) {
				return element;
			}
			element = element.getParentNode();
			if (element == null) {
				break;
			}
		}
		return null;
	}

	
	public static long getChildCount(Node node) {
		long count = 0;
		NodeList children = node.getChildNodes();
		if (children != null) {
			count = children.getLength();
		}
		return count;
	}
	
	public static Node getChildNode(Node node, int index) {
		Node child = null;
		NodeList children = node.getChildNodes();
		if (children != null && index >= 0 && index < children.getLength()) {
			child = children.item(index);
		}
		return child;
	}
	
	public static long getOffset(Node node) {
		long offset = 0;
		Node previousSibling = node;
		while ((previousSibling = previousSibling.getPreviousSibling()) != null) {
			offset++;
		}
		return offset;
	}
	
	/**
	 * Appends all children of the {@code node} to its parent at the point before the {@code node}
	 * and removes the {@code node} from the list of its parent's children.
	 * 
	 * @param node should be not null
	 */
	public static void replaceNodeByItsChildren(Node node) {
		final NodeList subTableContainerChildren = node.getChildNodes();
		final Node containerParent = node.getParentNode();
		if (subTableContainerChildren != null) {
			final int length = (int) subTableContainerChildren.getLength();
			for (int i = 0; i < length; i++) {
				final Node child = subTableContainerChildren.item(i);
				node.removeChild(child);
				containerParent.insertBefore(child, node);
			}
		}
		containerParent.removeChild(node);
	}
	
    /**
     * Sets the style of the attribute of the element to the value.
     * If the sub-attribute is present already, it replaces its value.
     * <P/>
     * Example: {@code <element attributeName="subAttributeName : subAttributeValue;">}
     * <P/>
     * Should be used mainly to set HTML STYLE attribute:
     *  {@code setSubAttribute(div, "STYLE", "width", "100%")} 
     */
	public static void setSubAttribute(Element element,
			String attributeName, String subAttributeName,
			String subAttributeValue) {
		String attributeValue = element.getAttribute(attributeName);
		if (attributeValue == null) {
		    attributeValue = ""; //$NON-NLS-1$
		} else {// remove old sub-attribute from the attributeValue
		    attributeValue = deleteFromString(attributeValue, 
		    		subAttributeName, Constants.SEMICOLON);
		}
		if (attributeValue.length() > 0) {
		    if (!attributeValue.endsWith(Constants.SEMICOLON))
			attributeValue += Constants.SEMICOLON;
		}

		attributeValue += subAttributeName + Constants.COLON
			+ subAttributeValue + Constants.SEMICOLON;

		element.setAttribute(attributeName, attributeValue);
	}
	
	// selects parameter from attribute style
	public static String deleteFromString(String data, String begin, String end) {
		int startPosition = data.indexOf(begin);

		if (startPosition < 0)
			return data;

		int endPosition = data.indexOf(end, startPosition);

		String result = data.substring(0, startPosition).trim();
		if (endPosition > 0) {
			result += data.substring(endPosition + 1, data.length()).trim();
		}

		return result;
	}

	/**
	 * Copies all attributes from source node to visual node.
	 * 
	 * @param sourceNode the source node
	 * @param visualElement the visual element
	 */
	public static void copyAttributes(Node sourceNode, Element visualElement) {
	    NamedNodeMap namedNodeMap = sourceNode.getAttributes();
	    for (int i = 0; i < namedNodeMap.getLength(); i++) {
	        Node attribute = namedNodeMap.item(i);
	        // added by Max Areshkau fix for JBIDE-1568
            visualElement.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
	    }
	}
	
	/**
	 * Copies all attributes from source node to visual node.
	 * 
	 * @param sourceElement the source element
	 * @param sourceAttrName the name of source attribute 
	 * @param visualElement the visual element
	 * @param visualAttrName the resulting name of visual attribute 
	 */
	public static void copyAttribute(Element sourceElement, String sourceAttrName,
			Element visualElement, String visualAttrName) {
		if (sourceElement.hasAttribute(sourceAttrName)) {
			String attrValue = sourceElement.getAttribute(sourceAttrName);
			visualElement.setAttribute(visualAttrName, attrValue);
		}
	}

	/**
	 * Copies all attributes from source node to visual node.
	 * 
	 * @param sourceElement the source element
	 * @param visualElement the visual element
	 * @param attributes list names of attributes which will copy
	 */
	public static void copyAttributes(Element sourceElement, Element visualElement, List<String> attributes) {
	    for (String attributeName : attributes) {
	        copyAttribute(sourceElement, attributeName, visualElement, attributeName);
	    }
	}

	/**
	 * Copies all attributes from source node to visual node.
	 * 
	 * @param sourceElement the source element
	 * @param visualElement the visual element
	 * @param sourceToVisualMap mapping for attributes' names.
	 */
	public static void copyAttributes(Element sourceElement,
			Element visualElement, Map<String, String> sourceToVisualMap) {
		for (Entry<String, String> sourceToVisual : sourceToVisualMap.entrySet()) {
			String sourceAttrName = sourceToVisual.getKey();
			String visualAttrName = sourceToVisual.getValue();
			copyAttribute(sourceElement, sourceAttrName, visualElement, visualAttrName);
		}
	}
	
	/**
	 * Returns instance of {@link nsIAccessibilityService}.
	 * 
	 * @author yradtsevich
	 */
//	public static nsIAccessibilityService getAccessibilityService() {
//		nsIAccessibilityService accessibilityService = null;
//		if (accessibilityServiceCache != null) {
//			// get accessibilityService from cache
//			accessibilityService = accessibilityServiceCache.get();
//		}
//		if (accessibilityService == null) {
//			accessibilityService = (nsIAccessibilityService) Mozilla.getInstance()
//				.getServiceManager()
//				.getServiceByContractID(ACCESSIBILITY_SERVICE_CONTRACT_ID,
//	        		nsIAccessibilityService.NS_IACCESSIBILITYSERVICE_IID);
//			
//			// cache accessibilityService
//			accessibilityServiceCache = new SoftReference<nsIAccessibilityService>(accessibilityService);
//		}
//		return accessibilityService;
//	}
	
//	XULRunner 1.9 throws an error when we try to get nsIAccessible.
//	Use XulRunnerVpeUtils#getElementBounds(Node) instead.
//	/**
//	 * Returns on screen bounds of the {@code node}
//	 * 
//	 * @param node cannot be {@code null}
//	 * @return bounds of the {@code node} or {@code null} if it is not accessible
//	 * 
//	 * @author yradtsevich
//	 */
//	public static Rectangle getBounds(Node node) {
//		Rectangle bounds = null;
//
//		nsIAccessible accessible = queryInterface(node, nsIAccessible.class);
//		if (accessible != null) {
//			int[] xArray      = new int[1]; // Left hand corner of the node
//			int[] yArray      = new int[1]; // Top corner of the node
//			int[] widthArray  = new int[1]; // Width of the node
//			int[] heightArray = new int[1]; // Height of the node
//			
//			accessible.getBounds(xArray, yArray, widthArray, heightArray);
//			bounds = new Rectangle(xArray[0], yArray[0], widthArray[0], heightArray[0]);
//		}
//		
//		return bounds;
//	}
	
	/**
	 * Creates HTML container (element) borders of that are
	 * complete invisible to user.
	 * <P>
	 * It can be used to wrap visual HTML elements and text nodes without
	 * changing of their view in VPE.
	 * <P>
	 * Tag <code>span</code> is used as a default container.
	 *   
	 * @param visualDocument the visual document. It is used to create the container.
	 * @return created borderless container
	 * @author yradtsevich
	 * @see #createBorderlessContainer(Document, String)
	 */
	public static Element createBorderlessContainer(Document visualDocument) {
		return createBorderlessContainer(visualDocument, HTML.TAG_SPAN);
	}
	
	/**
	 * Creates a HTML tag that is used as a borderless container 
	 * for other elements. 
	 * 
	 * @param visualDocument the visual document.
	 * @param containerName the name of the tag, that will be used as a container.
	 * 
	 * @return created borderless container
	 */
	public static Element createBorderlessContainer(
			Document visualDocument, String containerName) {
		Element element = visualDocument.createElement(containerName);
	    element.setAttribute(HTML.ATTR_CLASS, HTML.CLASS_VPE_TEXT);
		return element;
	}

	/**
	 * Appends a container for {@code source}'s children
	 * to {@code target} and adds a new object of {@link VpeChildrenInfo} 
	 * in a way that all children will be placed in this container.
	 * @param source source element, cannot be {@code null}
	 * @param target target element, cannot be {@code null}
	 * @param creationData the creation data, cannot be {@code null}
	 * @param visualDocument the visual document, cannot be {@code null}
	 */
	public static void appendChildrenInsertionPoint(Element source,
			Element target, VpeCreationData creationData, Document visualDocument) {
		Element childrenContainer = createBorderlessContainer(visualDocument);
		target.appendChild(childrenContainer);
		
		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(childrenContainer);

		NodeList childNodes = source.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			childrenInfo.addSourceChild(childNodes.item(i));
		}

		creationData.addChildrenInfo(childrenInfo);
	}

	/**
	 * Creates visual tag with additional text container
	 * 
	 * @param sourceElement
	 *            the source element
	 * @param templateContainer
	 *            visual tag that contains template element
	 * @param borderlessContainerName
	 *            the name of the borderless container
	 * @param visualDocument
	 *            the visual document
	 * @return created {@code VpeCreationData} based on specified container name
	 *         or template container.
	 */
	public static VpeCreationData createTemplateWithTextContainer(
			Element sourceElement, Element templateContainer,
			String borderlessContainerName, Document visualDocument) {
		List<Node> children = new ArrayList<Node>();
		VpeCreationData creationData = null;
        NodeList nodeList = sourceElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            /*
             * Do not display escaped tags.
             * These tags are correct and should be processed 
             * by templates themselves. 
             */
            if (!escapedTags.contains(child.getNodeName()) ) {
                children.add(child);
            }
        }
		if (children != null && children.size() > 0) {
	        Element topContainer = createBorderlessContainer(visualDocument, borderlessContainerName);
	        Element textContainer = createBorderlessContainer(visualDocument, borderlessContainerName);
			topContainer.appendChild(textContainer);
			topContainer.appendChild(templateContainer);
			creationData = new VpeCreationData(topContainer);
			VpeChildrenInfo textInfo = new VpeChildrenInfo(textContainer);
			creationData.addChildrenInfo(textInfo);
			for (Node child : children) {
				textInfo.addSourceChild(child);
			}
		} else {
			creationData = new VpeCreationData(templateContainer);
		}
		return creationData;
	}
	
	/**
 	 * Finds visual tag with 'VPE-FACET' attribute in it.
 	 * Facet element should be rendered into this tag. 
 	 * 
 	 * @param facetsParentNode node for which its facet should be rendered. 
 	 * @param facetName facet's name to compare to 'VPE-FACET' attribute value.
 	 * @return found visual tag or 'null' otherwise.
 	 */
	public static Element findVisualTagWithFacetAttribute(
			Node facetsParentNode, String facetName) {
		
     	Element tagForFacet = null;
     	if (null != facetsParentNode) {
     		NodeList nodeList = facetsParentNode.getChildNodes();
     		for (int i = 0; i < nodeList.getLength(); i++) {
     			Element element = null;
     			if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
     				element = (Element) nodeList.item(i);
     			} else {
     				return null;
     			}
     			/*
     			 * If current tag has 'VPE-FACET' attribute 
     			 * with the corresponding  facet name.
     			 * Then return this tag.
     			 */
     			if (element.hasAttribute(VPE_FACET)) {
     				String facetAttributeName = element.getAttribute(VPE_FACET);
     				/*
     				 * In some cases there could be several footer in one visual node.
     				 * For instance, header and footer could be in single column cell.
     				 * Thus VPE-FACET can contain several facet names
     				 * separated by whitespace, i.e. "header footer".
     				 */
     				if (facetAttributeName.indexOf(facetName) >= 0) {
     					return element;
     				}
     			}
     			/*
     			 * Else search in children
     			 */
     			tagForFacet = findVisualTagWithFacetAttribute(element, facetName);
     			/*
     			 * When tag is found in children it will be returned 
     			 */
     			if (null != tagForFacet) {
     				return tagForFacet;
     			}
     		}
		}
     	/*
     	 * If nothing matched return null
     	 */
     	return tagForFacet;
     }
     
 	/**
 	 * Clarifies JSF facet element's children: JSF tags, other tags, HTML, text.
 	 * (For JSF Facet cannot display more than one JSF component).
 	 *  Results are put into the map with keys:
 	 *  <P> 'FACET-JSF-TAG' - for suitable JSF element
 	 *  <P> 'FACET-ODD-TAGS' - for superfluous elements
 	 *  <P> 'FACET-HTML-TAG' - for HTML elements and plain text
 	 * 
 	 * @param facet the facet
 	 * @param pageContext the page context 
 	 * @return map with arranged elements or empty map if nothing was found.
 	 */
//     public static Map<String, List<Node>> findFacetElements(Node facet, VpePageContext pageContext) {
//    	Map<String, List<Node>> facetChildren = new HashMap<String, List<Node>>();
//     	List<Node> jsfTag = new ArrayList<Node>(0);
//     	List<Node> oddTags = new ArrayList<Node>(0);
//     	List<Node> htmlTags = new ArrayList<Node>(0);
//     	facetChildren.put(FACET_JSF_TAG, jsfTag);
//     	facetChildren.put(FACET_ODD_TAGS, oddTags);
//     	facetChildren.put(FACET_HTML_TAGS, htmlTags);
//     	if (null != facet) {
//     		NodeList children = facet.getChildNodes();
//     		Node lastJSFComponent = null;
//     		for (int i = 0; i < children.getLength() ; i++) {
//     			Node child = children.item(i);
//     			String sourcePrefix = child.getPrefix();
//     			List<TaglibData> taglibs = XmlUtil.getTaglibsForNode(child,
//     					pageContext);
//     			TaglibData sourceNodeTaglib = XmlUtil.getTaglibForPrefix(
//     					sourcePrefix, taglibs);
//     			/*
//     			 * Here will be nodes with taglibs
//     			 * Plain html tags and text - will not.
//     			 */
//     			if (null != sourceNodeTaglib) {
//     				String sourceNodeUri = sourceNodeTaglib.getUri();
//     				if ((JSF_CORE_URI.equalsIgnoreCase(sourceNodeUri)
//     								|| JSF_HTML_URI.equalsIgnoreCase(sourceNodeUri)
//     								|| RICH_FACES_URI.equalsIgnoreCase(sourceNodeUri) 
//     								|| A4J_URI.equalsIgnoreCase(sourceNodeUri)
//     								|| FACELETS_URI.equalsIgnoreCase(sourceNodeUri))) {
//     					/*
//     					 * Mark the correct jsf component
//     					 * and add it to the odd list for further correction.
//     					 */
//     					lastJSFComponent = child;
//     					oddTags.add(child);
//     				} else {
//     					/*
//     					 * All other tags: JSF, RF, FACELETS, A4J.
//     					 */
//     					oddTags.add(child);
//     				} 
//     			} else {
//     				/*
//     				 * Plain html and text
//     				 */
//     				if (child instanceof Text) {
//     					/*
//     					 * For text nodes we should omit empty strings
//     					 */
//     					Text textNode = (Text) child;
//     					if (textNode.getNodeValue().trim().length() > 0) {
//     						htmlTags.add(child);
//						}
//                    } else {
//                    	/*
//                    	 * If it is not text then it is normal html tag
//                    	 */
//                    	htmlTags.add(child);
//                    }
//     			}
//     		}
//     		/*
//     		 * Fill the correct JSF component:
//     		 * remove it from the odd list and add to the jsf list.
//     		 */
//     		if (null != lastJSFComponent) {
//     			oddTags.remove(lastJSFComponent);
//     			jsfTag.add(lastJSFComponent);
//			}
// 		}
//     	return facetChildren;
//     }

	/**
	 * Returns {@code true} if and only if {@code potentialAscendant}
	 * is an ascendant of {@code potentialDescendant}.
	 * 
	 * @param potentialAscendant must not be {@code null}
	 * @param potentialDescendant may be {@code null}
	 */
	public static boolean isAscendant(Node potentialAscendant,
			Node potentialDescendant) {
		while (potentialDescendant != null) {
			potentialDescendant = potentialDescendant.getParentNode();
			if (potentialAscendant.equals(potentialDescendant)) {
				return true;
			}
		}
		return false;
	}
}

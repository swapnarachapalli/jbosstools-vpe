/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpv.jsf.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jboss.tools.vpe.vpv.template.util.Constants;
import org.jboss.tools.vpe.vpv.template.util.HTML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utilities for jsf templates
 * 
 * @author Dzmitry Sakovich (dsakovich@exadel.com)
 * 
 */
public class ComponentUtil {

    /**
     * Returns value of attribute.
     * 
     * @param sourceElement
     * @param attributeName
     * @return
     */
    public static String getAttribute(Element sourceElement,
	    String attributeName) {
	String attribute = sourceElement.getAttribute(attributeName);
	if (attribute == null) {
	    attribute = ""; //$NON-NLS-1$
	}
	return attribute;
    }


    /**
     * Returns {@code true} if the {@code element} has attribute {@code
     * disabled} and its value is {@link #string2boolean(String) true}
     */
    public static boolean isDisabled(Element element) {
	return ComponentUtil.string2boolean(ComponentUtil.getAttribute(element,
		JSF.ATTR_DISABLED));
    }

    /**
     * Sets attribute {@code "disabled"} of the {@code element} to {@code
     * "disabled"} if the parameter {@code disabled} is {@code true}, <br/>
     * otherwise removes attribute {@code "disabled"} from the {@code element}
     * if it is present.
     */
    public static void setDisabled(Element visualElement, boolean disabled) {
	if (disabled) {
	    visualElement.setAttribute(HTML.ATTR_DISABLED, HTML.ATTR_DISABLED);
	} else {
	    visualElement.removeAttribute(HTML.ATTR_DISABLED);
	}
    }

    /**
     * Copies {@code "disabled"} attribute from JSF {@code sourceElement} to
     * HTML {@code targetElement}.
     * 
     * @see #isDisabled(Element)
     * @see #setDisabled(nsIDOMElement, boolean)
     */
    public static void copyDisabled(Element sourceElement,
	    Element targetElement) {
	boolean disabled = ComponentUtil.isDisabled(sourceElement);
	ComponentUtil.setDisabled(targetElement, disabled);
    }

    /**
     * Parses string value retrieved from sourceElement.getAttribure(..) method
     * to its boolean value.
     * <p>
     * <code>true</code> is returned only if it specified explicitly, otherwise
     * <code>false</code> is returned.
     * 
     * @param str
     *            the string to parse
     * @return boolean value from string
     */
    public static boolean string2boolean(String str) {
	return Boolean.parseBoolean(str);
    }


    /**
     * Gets the children.
     * 
     * @param sourceElement
     *            the source element
     * 
     * @return the children
     */
    public static List<Node> getChildren(Element sourceElement) {
	ArrayList<Node> children = new ArrayList<Node>();
	NodeList nodeList = sourceElement.getChildNodes();
	for (int i = 0; i < nodeList.getLength(); i++) {
	    Node child = nodeList.item(i);
	    children.add(child);
	}
	return children;
    }

    /**
     * Checks if is blank.
     * 
     * @param value
     *            the value
     * 
     * @return true, if is blank
     */
    public static boolean isBlank(String value) {
	return ((value == null) || (value.trim().length() == 0));
    }

    /**
     * Checks if is not blank.
     * 
     * @param value
     *            the value
     * 
     * @return true, if is not blank
     */
    public static boolean isNotBlank(String value) {
	return !isBlank(value);
    }
    
	/**
	 * Creates the locale.
	 * <p>If the locale string could be parsed into language and country -
	 * creates Locale for this arguments.
	 * <p> By default - locale for empty string is created.
	 * 
	 * @param localeString the locale string
	 * @return Locale object
	 */
	public static Locale createLocale(String localeString) {
		Locale newLocale = null;
		if (null != localeString) {
			if (localeString.length() == 2) {
				newLocale = new Locale(localeString);
			} else if ((localeString.length() == 5) && (localeString.indexOf("_") == 2)) { //$NON-NLS-1$
				newLocale = new Locale(localeString.substring(0, 2), localeString.substring(3));
			} else {
				newLocale = new Locale(Constants.EMPTY);
			}
		} else {
			newLocale = new Locale(Constants.EMPTY);
		}
		return newLocale;	
	}
}

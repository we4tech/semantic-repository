/* $Id$ */
/*
 ******************************************************************************
 *   Copyright (C) 2007 IDEASense, (hasin & hasan) 
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 ******************************************************************************
*/
package com.ideabase.repository.webservice.request.xml;

import com.ideabase.repository.webservice.request.RequestProcessor;
import com.ideabase.repository.webservice.controller.RESTfulAction;
import com.ideabase.repository.common.object.AbstractObjectBase;
import com.ideabase.repository.common.object.GenericItem;
import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.common.object.User;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import nu.xom.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.sql.Timestamp;
import java.text.ParseException;

import static com.ideabase.repository.common.XmlConstants.*;
import com.ideabase.repository.common.exception.ServiceException;
import com.ideabase.repository.common.DateFormatter;
import com.ideabase.repository.common.serializer.SerializerFactory;

/**
 * Xml based reuqest processor for web service client.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class XmlRequestProcessorImpl<T extends AbstractObjectBase>
    implements RequestProcessor {

  /**
   * Request objects are converted to an implementation of {@see ObjectBase}.
   * if user define {@code user} as target-class name it will use {@see User}
   * object internally.
   */
  private static final String TARGET_CLASS_USER = "user";

  /**
   * Logger instance for this class.
   */
  private final Logger LOG = LogManager.getLogger(getClass());

  /**
   * {@inheritDoc}
   */
  public List<T> processRequest(final RESTfulAction pAction,
                                final Reader pContentReader) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Process webservice client request from action - " + pAction);
      final StringBuilder xmlOut = new StringBuilder();
      try {
        int c;
        while ((c = pContentReader.read()) != -1) {
          xmlOut.append((char) c);
        }
        LOG.debug("Xml string - " + xmlOut);
        pContentReader.reset();
      } catch (Exception e) {
        LOG.debug("Exception raised during debug out the requested xml stream", e);
      }
    }
    try {
      final Builder builder = new Builder();
      final Document document = builder.build(pContentReader);
      final Element rootElement = document.getRootElement();

      // Verify whether root element is <request>
      if (!ELEMENT_REQUEST.equals(rootElement.getQualifiedName())) {
        throw ServiceException.aNew(rootElement, "Root element must be " +
            "<request/> element.");
      }

      // Returnable item list.
      final List<T> itemList = new ArrayList<T>();

      // looking for <item/> element
      LOG.debug("Looking for <item/> elements.");
      final Elements itemElements = rootElement.getChildElements(ELEMENT_ITEM);
      if (itemElements != null && itemElements.size() > 0) {
        for (int i = 0; i < itemElements.size(); i++) {
          final Element itemElement = itemElements.get(i);
          // process each <item/> elements
          final T item = processItemElement(itemElement);
          if (item != null) {
            itemList.add(item);
          }
        }
      } else {
        throw ServiceException.aNew(pAction, "Invalid request format, " +
            "it doesn't contain any <item/> element.");
      }

      // Return list of items
      return itemList;
    } catch (Exception e) {
      throw new ServiceException(pAction, "Invalid request input stream.", e);
    }
  }

  private T processItemElement(final Element pItemElement)
      throws ParseException, ClassNotFoundException, IllegalAccessException,
      InstantiationException {
    LOG.debug("Processing item element");
    final ObjectBase objectBase = SerializerFactory.getInstance().
        deserializeObject(SerializerFactory.XML, pItemElement.toXML());
    return (T) objectBase;
  }
}

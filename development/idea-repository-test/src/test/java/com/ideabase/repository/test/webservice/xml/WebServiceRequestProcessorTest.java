/* $Id: WebServiceRequestProcessorTest.java 250 2008-01-07 10:18:29Z hasan $ */
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
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.test.webservice.xml;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.List;

import com.ideabase.repository.webservice.request.xml.XmlRequestProcessorImpl;
import com.ideabase.repository.webservice.controller.RESTfulAction;
import com.ideabase.repository.common.object.GenericItem;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * Parse content from web service request, the content could be in request/json etc..
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class WebServiceRequestProcessorTest extends TestCase {

  private static final Logger LOG =
      LogManager.getLogger(WebServiceRequestProcessorTest.class);

  public void testXmlRequest() throws IOException {
    final Reader contentStream = loadContent();
    final XmlRequestProcessorImpl<GenericItem> xmlRequestProcessor =
        new XmlRequestProcessorImpl<GenericItem>();
    final RESTfulAction action = new RESTfulAction();
    final List<GenericItem> items =
        xmlRequestProcessor.processRequest(action, contentStream);

    assertNotNull("Object couldn't be retrieved from request context.", items);
    assertFalse("Object list must be not empty.", items.isEmpty());
    LOG.debug("Object - " + items);

    // verify object by object
    final GenericItem item = items.get(0);

    // verify basic fields
    assertNotNull("Title is empty.", item.getTitle());
    assertNotNull("Created on is empty.", item.getCreatedOn());
    assertNotNull("Last updated on is empty.", item.getLastUpdatedOn());

    // Verify fields.
    assertFalse("Fields are empty",
        item.getFields() == null || item.getFields().isEmpty());

    verifyRelatedItems(item);
  }

  private void verifyRelatedItems(final GenericItem pItem) {
    // Verify relation types
    assertFalse("Relation types are empty.",
        pItem.getRelationTypes() == null || pItem.getRelationTypes().isEmpty());

    for (final String relationType : pItem.getRelationTypes()) {
      final List<Integer> relatedItems =
          pItem.getRelatedItemsByRelationType(relationType);
      assertFalse("No related pItem fround for group - " + relationType,
          (relatedItems == null || relatedItems.isEmpty()));
      LOG.debug("Related items - " + relatedItems);
    }
  }

  private Reader loadContent() throws IOException {
    return new InputStreamReader(getClass().getClassLoader().
        getResourceAsStream("save-format.xml"));
  }
}

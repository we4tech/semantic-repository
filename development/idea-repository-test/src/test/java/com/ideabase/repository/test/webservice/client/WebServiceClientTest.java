/*
 * $Id: WebServiceClientTest.java 250 2008-01-07 10:18:29Z hasan $
 ******************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 ******************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.test.webservice.client;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.jmock.MockObjectTestCase;

import java.io.InputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.ideabase.repository.common.Query;
import com.ideabase.repository.common.InvocationSupport;
import com.ideabase.repository.common.serializer.SerializerFactory;
import com.ideabase.repository.common.object.GenericItem;
import com.ideabase.repository.webservice.client.*;

/**
 * Test web service client.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class WebServiceClientTest extends MockObjectTestCase {

  private static final String XML_MIME_TYPE = "text/xml";
  private static final String METHOD_SEND_REQUEST = "sendServiceRequest";
  private static final String METHOD_FIND_CONTENT = "findContent";

  private final Logger LOG = LogManager.getLogger(WebServiceClientTest.class);
  private WebServiceManager mWebServiceManagerImpl = new WebServiceManager("/");

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testLoginOperation() throws Exception {
    // fix parsable xml content
    fixFindContent("login-success.xml");

    final String user = "we4tech";
    final String password = "123456";
    final ExecutionResponse response =
        mWebServiceManagerImpl.login(XML_MIME_TYPE, user, password);

    // verify
    assertTrue("Not logged on", response.getState());
    assertNotNull("No auth key found", response.getAuthKey());
  }

  public void testGetOperation() throws IOException {
    // Fix parsable xml content.
    fixFindContent("get-success.xml");

    final GenericItem item =
        mWebServiceManagerImpl.getItemById(XML_MIME_TYPE, 33);
    assertNotNull("NO item found.", item);
    assertNotNull("No Related item found.", item.getRelatedItemsMap());
    assertNotNull("NO field eixsts.", item.getFields());
    LOG.debug("Result - " + item);
  }

  private void fixFindContent(final String pResourceFile) {
    mWebServiceManagerImpl.mInvocationSupport = new InvocationSupport() {
      public Object execute(final Object... pArguments) {
        try {
          final WebServiceResponse response = new WebServiceResponse();
          response.setContentType("text/xml");
          response.setResponseStatus(200);
          response.setResponseContent(findContent(pResourceFile));
          response.setServiceUri("/service/bang/bang");
          return response;
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  private String findContent(final String pResource) throws IOException {
    final InputStream inStream =
        getClass().getClassLoader().getResourceAsStream(pResource);
    final byte[] data = new byte[inStream.available()];
    inStream.read(data, 0, data.length);
    return new String(data);
  }

  public void testFindOperation() {
    fixFindContent("find-success.xml");

    final SearchResult result = mWebServiceManagerImpl.
        findContent(XML_MIME_TYPE, new Query().and("test", "hasan"));
    assertNotNull("No search result found", result);
    assertNotNull("No page count.", result.getPageCount());
    assertNotNull("No max rows found.", result.getMaxRows());
    assertFalse("NO item contents.",
        result.getItems() == null || result.getItems().isEmpty());
    LOG.debug("Items - " + result);
  }

  public void testSaveOperation() {
    fixFindContent("save-success.xml");
    // create new item object
    final GenericItem item = new GenericItem() {{
      setCreatedOn(new Timestamp(System.currentTimeMillis()));
      setLastUpdatedOn(new Timestamp(System.currentTimeMillis()));
      setTitle("test document");

      // define fields.
      final Map<String, String> fields = new HashMap<String, String>();
      fields.put("field-1", "field value - 1");
      fields.put("field-2", "field value - 2");
      setFields(fields);

      // define related items
      final Map<String, List<Integer>> items =
          new HashMap<String, List<Integer>>();
      final List<Integer> itemRefs = new ArrayList<Integer>();
      itemRefs.add(321);
      items.put("user", itemRefs);
      setRelatedItemsMap(items);
    }};

    // store object and translate service response.
    final ExecutionResponse response =
        mWebServiceManagerImpl.saveItem(XML_MIME_TYPE, item);
    assertNotNull("No response received", response);
    LOG.debug("Response - " + response);
  }

  public void testUpdateOperation() {
    fixFindContent("update-success.xml");
    // create new item object
    final GenericItem item = new GenericItem() {{
      setCreatedOn(new Timestamp(System.currentTimeMillis()));
      setLastUpdatedOn(new Timestamp(System.currentTimeMillis()));
      setTitle("test document");

      // define fields.
      final Map<String, String> fields = new HashMap<String, String>();
      fields.put("field-1", "field value - 1");
      fields.put("field-2", "field value - 2");
      setFields(fields);

      // define related items
      final Map<String, List<Integer>> items =
          new HashMap<String, List<Integer>>();
      final List<Integer> itemRefs = new ArrayList<Integer>();
      itemRefs.add(321);
      items.put("user", itemRefs);
      setRelatedItemsMap(items);
    }};

    final StringBuilder builder = new StringBuilder();
    builder.append(SerializerFactory.getInstance().
                   serializeObject(SerializerFactory.XML, item));
    LOG.debug("Serialized item - " + builder.toString());

    // perform request.
    final ExecutionResponse response =
        mWebServiceManagerImpl.saveItem(XML_MIME_TYPE, item);
    assertNotNull("Response not found", response);
    LOG.debug("Response - " + response);
  }

  public void testDeleteOperation() {
    fixFindContent("delete-success.xml");
    final ExecutionResponse response =
        mWebServiceManagerImpl.deleteItem(XML_MIME_TYPE, 321);
    assertNotNull("No response found.", response);
    LOG.debug(response);
  }
}

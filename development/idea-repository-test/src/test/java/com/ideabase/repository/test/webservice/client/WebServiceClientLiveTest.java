/* $Id: WebServiceClientLiveTest.java 252 2008-01-18 09:06:45Z hasan $ */
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
 * $LastChangedDate: 2008-01-18 15:06:45 +0600 (Fri, 18 Jan 2008) $
 * $LastChangedRevision: 252 $
 ******************************************************************************
*/
package com.ideabase.repository.test.webservice.client;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.test.TestCaseRepositoryHelper;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.APIServiceKey;
import com.ideabase.repository.api.UserService;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.common.object.GenericItem;

import static com.ideabase.repository.test.TestCaseRepositoryHelper.*;
import com.ideabase.repository.common.Query;
import com.ideabase.repository.webservice.client.SearchResult;
import com.ideabase.repository.webservice.client.ItemRef;
import com.ideabase.repository.webservice.client.WebServiceManagerInterface;
import com.ideabase.repository.webservice.client.WebServiceRequest;
import com.ideabase.repository.webservice.client.ExecutionResponse;
import com.ideabase.repository.webservice.client.WebServiceManager;

/**
 * Container based web service test.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class WebServiceClientLiveTest extends BaseTestCase {
  private final Logger LOG =
      LogManager.getLogger(WebServiceClientLiveTest.class);
  private static final String XML_MIME = "text/xml";

  private WebServiceManagerInterface mWebServiceManager =
      new WebServiceManager("http://localhost:8080/repository/rest/");
  private RepositoryService mRepositoryService;
  private UserService mUserService;


  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mRepositoryService =
        (RepositoryService) mContext.getBean(APIServiceKey.REPOSITORY_SERVICE);
    mUserService = (UserService) mContext.getBean(APIServiceKey.USER_SERVICE);
  }

  /**
   * Test user authentication
   */
  public void testLogin() {
    authenticate();
  }

  private void authenticate() {
    final String userName = "we4tech5-" + System.currentTimeMillis();
    final String password = "new-password";

    // create userName
    final User user = new User();
    user.setUser(userName);
    user.setPassword(password);
    user.setRole(User.FIELD_ROLE_ADMIN, true);
    user.setRole(User.FIELD_ROLE_READ, true);
    user.setRole(User.FIELD_ROLE_WRITE, true);
    mUserService.registerUser(user);

    final ExecutionResponse response =
        mWebServiceManager.login(XML_MIME, userName, password);

    assertNotNull("No response found", response);
    assertTrue("Login failed", response.getState());
    assertNotNull("No auth key found", response.getAuthKey());

    LOG.debug("Response - " + response);
  }

  /**
   * Test invalid authentication
   */
  public void testInvalidLogin() {
    final ExecutionResponse response =
        mWebServiceManager.login(XML_MIME, "gmapd", "gmapd");
    assertNotNull("Web service error", response);
    assertFalse("Login not suppose to succeed", response.getState());
    assertNotNull("No message found", response.getMessage());

    LOG.debug("Response - " + response);
  }

  /**
   * Test get operation
   */
  public void testGetOperation() {
    authenticate();

    // create new item.
    final List<Integer> itemIds = fixCreateItems(mRepositoryService, 1);
    final Integer itemId = itemIds.get(0);
    final GenericItem item = mWebServiceManager.getItemById(XML_MIME, itemId);

    // verify content
    assertNotNull("NO item found", item);
    assertNotNull("No item id is defined", item.getId());
    assertFalse("No item fields are defined",
                item.getFields() == null || item.getFields().isEmpty());
    assertNotNull("No item title is defined", item.getTitle());
    assertNotNull("NO created on timestamp set", item.getCreatedOn());
    assertNotNull("NO last modified on timestamp set", item.getLastUpdatedOn());

    LOG.debug("Object  - " + item);
  }

  /**
   * Test get operation with related items.
   */
  public void testGetOperationWithRelatedItems() {
    authenticate();

    // create items
    final String relationType = "related";
    final List<Integer> itemIds = fixCreateItems(mRepositoryService, 10);
    final List<Integer> itemIds2 =
        fixCreateItems(mRepositoryService, 1, relationType, itemIds);

    // find item object
    final GenericItem item = mWebServiceManager.getItemByIdAndRelatedItems(
        XML_MIME, itemIds2.get(0), relationType, 0, 2);

    LOG.debug("Object - " + item);
    
    // verify
    assertNotNull("NO item found", item);
    assertNotNull("No item id is defined", item.getId());
    assertFalse("No item fields are defined",
                item.getFields() == null || item.getFields().isEmpty());
    assertNotNull("No item title is defined", item.getTitle());
    assertNotNull("NO created on timestamp set", item.getCreatedOn());
    assertNotNull("NO last modified on timestamp set", item.getLastUpdatedOn());
    assertFalse("No related items found", item.getRelatedItemsMap() == null
                || item.getRelatedItemsMap().isEmpty());
  }

  /**
   * Find items with specific keyword.
   */
  public void testFindOperation() {
    authenticate();

    // create items
    final List<Integer> itemIds = fixCreateItems(mRepositoryService, 10);
    // search keyword
    final String keyword = "brown jump";
    // build query
    final Query query = new Query().or("name", keyword);
    query.maxRows(2);
    
    // perform search
    final SearchResult searchResult =
        mWebServiceManager.findContent(XML_MIME, query);

    // verify
    assertNotNull("No search result found", searchResult);
    assertTrue("No pagination info provided.",
      searchResult.getMaxRows() != null && searchResult.getPageCount() != null);

    // verify items
    final List<ItemRef> itemRefs = searchResult.getItems();
    LOG.debug("List of Items - " + itemRefs);
    assertNotNull("NO item referene found.", itemRefs);
    assertFalse("No item reference defined", itemRefs.isEmpty());
    assertTrue("Object length doesn't match",
               itemRefs.size() == query.getMaxRows());

    LOG.debug("Search result - " + searchResult);
    LOG.debug("ItemRef size - " + itemRefs.size());
  }

  public void testSaveOperation() {
    authenticate();

    // create new item
    final GenericItem item = new GenericItem();
    item.setCreatedOn(new Timestamp(System.currentTimeMillis()));
    item.setLastUpdatedOn(new Timestamp(System.currentTimeMillis()));
    item.setTitle("my item title");
    // build fields
    final Map<String, String> fields = new HashMap<String, String>();
    fields.put("email", "kana@bang.com");
    fields.put("description", "Total 6 table, 3 table are Otobi & 3 normal. Call: 01712102206");
    item.setFields(fields);

    // store item over web service request
    final ExecutionResponse response =
        mWebServiceManager.saveItem(XML_MIME, item);

    // verify
    assertNotNull("NO response found", response);
    assertTrue("State returns false", response.getState());
    final List<ItemRef> itemRefs = response.getItemRef();

    assertFalse("NO item reference found",
                itemRefs == null || itemRefs.isEmpty());

    // debug out
    LOG.debug("Response out - " + response);
  }

  public void testUpdateOperation() {
    authenticate();

    // create new item
    final List<Integer> items = fixCreateItems(mRepositoryService, 1);
    final Integer itemId = items.get(0);

    // testable content
    final String newTitle = "New bright title";

    // find item
    final GenericItem item = mWebServiceManager.getItemById(XML_MIME, itemId);

    // slighly modify the content structure.
    item.setTitle(newTitle);

    // update item object
    final ExecutionResponse response =
        mWebServiceManager.saveItem(XML_MIME, item);

    // verify
    assertNotNull("NO response found", response);
    assertTrue("State returns false", response.getState());
    final List<ItemRef> itemRefs = response.getItemRef();
    assertFalse("NO item reference found",
                itemRefs == null || itemRefs.isEmpty());

    // retrieve the item
    final WebServiceRequest updatedItemRequest =
        response.getItemRef().get(0).getItemContentRequest();
    updatedItemRequest.
        setServiceUri(updatedItemRequest.getServiceUri() + ".xml");
    final GenericItem updatedItem =
        mWebServiceManager.getItem(XML_MIME, updatedItemRequest);

    // verify
    assertEquals("Updated item doesn't hold the same title",
                 newTitle, updatedItem.getTitle());
  }

  public void testUpdatePartialContent() {
    authenticate();

    // create new updatedItem
    final Integer itemId = fixCreateItems(mRepositoryService, 1).get(0);
    final String newValue = "my field here";

    // update updatedItem
    final GenericItem updatedItem = new GenericItem();
    updatedItem.setId(itemId);
    final Map<String, String> updatedFields = new HashMap<String, String>();
    updatedFields.put("name", newValue);
    updatedItem.setFields(updatedFields);

    // retrieve before updating
    final GenericItem nonUpdatedItem =
        mWebServiceManager.getItemById(XML_MIME, itemId);
    // verify
    assertNotNull("Object not found", nonUpdatedItem);
    assertFalse("Object somehow match with new title",
        nonUpdatedItem.getFields().get("name").equals(newValue));

    // update through web service.
    mWebServiceManager.saveItem(XML_MIME, updatedItem);

    // retrieve updated item
    final GenericItem afterUpdateItem =
        mWebServiceManager.getItemById(XML_MIME, itemId);
    // verify
    assertNotNull("Object not found", afterUpdateItem);
    assertTrue("Object doesn't match with my updated field",
        afterUpdateItem.getFields().get("name").equals(newValue));
  }

  public void testDeleteOperation() {
    authenticate();

    // create new item
    final Integer itemId = fixCreateItems(mRepositoryService, 1).get(0);

    // delete through web service request.
    final ExecutionResponse response =
        mWebServiceManager.deleteItem(XML_MIME, itemId);

    assertNotNull("NO response found", response);
    assertTrue("State returns false", response.getState());
    assertNotNull("No message defined", response.getMessage());

    // try to retrieve this object through web service
    final GenericItem item = mWebServiceManager.getItemById(XML_MIME, itemId);
    assertNull("Somehow item found, but it is not intended.", item);
  }

  public void testRegister() {
    final User user = new User();
    user.setUser("hasankhan" + System.currentTimeMillis());
    user.setPassword("nhmthk");
    user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
    user.setLastUpdatedOn(new Timestamp(System.currentTimeMillis()));
    user.setRole(User.FIELD_ROLE_ADMIN, true);
    user.setRole(User.FIELD_ROLE_READ, true);
    user.setRole(User.FIELD_ROLE_WRITE, true);

    final ExecutionResponse response =
        mWebServiceManager.register(XML_MIME, user);

    assertNotNull("NO response found", response);
    assertTrue("Response state is false", response.getState());
    assertFalse("NO item created", response.getItemRef() == null
                || response.getItemRef().isEmpty());
  }

  public void testFindRelatedItems() {
    authenticate();

    // create fixed items
    final List<Integer> fixedItems =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService,  11);

    // update object relation
    final Integer baseItemId = fixedItems.get(0);

    // attach related items
    final String relationType = "category";
    mRepositoryService.addRelatedItems(
        relationType, baseItemId, fixedItems.subList(1, fixedItems.size()));

    // send request
    final SearchResult result = mWebServiceManager.
        getRelatedItems(XML_MIME, relationType, baseItemId, 0,
                        Integer.MAX_VALUE);
    // verify
    assertNotNull("Result not found", result);
    assertTrue("NO such item found",
        result.getItems() != null && !result.getItems().isEmpty());
    assertTrue("Count doesn't match", result.getItems().size() == 10);
    assertTrue("NO meta info found",
        result.getMaxRows() != null && result.getPageCount() != null);
  }

  public void testAddRelatedItems() {
    authenticate();

    // create fixed items
    final List<Integer> fixedItems =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService,  11);

    // update object relation
    final Integer baseItemId = fixedItems.get(0);

    // attach related items
    final String relationType = "category";

    final GenericItem item = new GenericItem();
    item.setId(baseItemId);
    item.addRelatedItems(relationType,
                         fixedItems.subList(1, fixedItems.size()));
    // send web service request
    mWebServiceManager.addRelatedItems(XML_MIME, item);

    // send request
    final SearchResult result = mWebServiceManager.
        getRelatedItems(XML_MIME, relationType, baseItemId, 0,
                        Integer.MAX_VALUE);
    // verify
    assertNotNull("Result not found", result);
    assertTrue("NO such item found",
        result.getItems() != null && !result.getItems().isEmpty());
    assertTrue("Count doesn't match", result.getItems().size() == 10);
    assertTrue("NO meta info found",
        result.getMaxRows() != null && result.getPageCount() != null);
  }

  public void testDeleteRelatedItems() {
    authenticate();

    // create fixed items
    final List<Integer> fixedItems =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService,  11);

    // update object relation
    final Integer baseItemId = fixedItems.get(0);

    // attach related items
    final String relationType = "category";

    final GenericItem item = new GenericItem();
    item.setId(baseItemId);
    item.addRelatedItems(relationType,
                         fixedItems.subList(1, fixedItems.size()));
    // send web service request
    mWebServiceManager.addRelatedItems(XML_MIME, item);

    // retrieve all list of related items.
    final SearchResult result = mWebServiceManager.
        getRelatedItems(XML_MIME, relationType, baseItemId, 0,
                        Integer.MAX_VALUE);
    // verify
    assertNotNull("Result not found", result);
    assertTrue("NO such item found",
        result.getItems() != null && !result.getItems().isEmpty());
    assertTrue("Count doesn't match", result.getItems().size() == 10);
    assertTrue("NO meta info found",
        result.getMaxRows() != null && result.getPageCount() != null);

    // send request to delete related items
    mWebServiceManager.deleteRelatedItems(XML_MIME, item);

    // retrieve all list of related items.
    final SearchResult result2 = mWebServiceManager.
        getRelatedItems(XML_MIME, relationType, baseItemId, 0,
                        Integer.MAX_VALUE);

    // veirfy
    assertNotNull("Result not found", result2);
    assertTrue("Related item found",
        result2.getItems() == null || result2.getItems().isEmpty());
    assertFalse("Count doesn't match", result2.getItems().size() == 10);
    assertTrue("NO meta info found",
        (result2.getMaxRows() == null || result2.getMaxRows() == 0)
            && (result2.getPageCount() == null || result2.getPageCount() == 0));
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
//    fixRemoveAllItems(mRepositoryService);
  }
}
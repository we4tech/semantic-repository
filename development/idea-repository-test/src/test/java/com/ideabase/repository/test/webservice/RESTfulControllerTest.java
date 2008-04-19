/* $Id: RESTfulControllerTest.java 260 2008-03-19 05:38:06Z hasan $ */
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
 * $LastChangedDate: 2008-03-19 11:38:06 +0600 (Wed, 19 Mar 2008) $
 * $LastChangedRevision: 260 $
 ******************************************************************************
*/
package com.ideabase.repository.test.webservice;

import javax.security.auth.Subject;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ideabase.repository.api.API;
import com.ideabase.repository.api.APIServiceKey;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.UserService;
import com.ideabase.repository.common.Query;
import com.ideabase.repository.common.WebConstants;
import com.ideabase.repository.common.XmlConstants;
import com.ideabase.repository.common.object.GenericItem;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.common.object.UserCredential;
import com.ideabase.repository.common.serializer.SerializerFactory;
import com.ideabase.repository.test.TestCaseRepositoryHelper;
import static com.ideabase.repository.test.TestCaseRepositoryHelper.fixRemoveAllItems;
import com.ideabase.repository.test.helper.MockHttpServletRequestFixed;
import com.ideabase.repository.webservice.controller.RESTfulController;
import com.ideabase.repository.webservice.controller.RESTfulControllerImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

/**
 * Test restful controller.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class RESTfulControllerTest extends BaseRestfullControllerTestcase {

  private static final String RESTFUL_CONTROLLER = "restfulWebService";
  private static final String METHOD_GET = "GET";
  private static final String METHOD_POST = "POST";
  private static final String METHOD_DELETE = "DELETE";

  private RESTfulController mRestfulController;
  private RepositoryService mRepositoryService;
  private UserService mUserService;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mRestfulController =
        (RESTfulController) mContext.getBean(RESTFUL_CONTROLLER);
    mRepositoryService =
        (RepositoryService) mContext.getBean(APIServiceKey.REPOSITORY_SERVICE);
    mUserService = (UserService) mContext.getBean(APIServiceKey.USER_SERVICE);
    LOG.debug("Controller - " + mRestfulController);

    TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
  }

  /**
   * Test authentication.
   * @throws Exception
   */
  public void testLogin() throws Exception {
    final Integer userId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, "hasan", "hasankhan");

    try {
      // Prepare http request
      getSubjectFromASuccessfulRequest();
    } finally {
      TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
    }

  }

  /**
   * Test get request for retrieving an item.
   * @throws Exception
   */
  public void testGetOperation() throws Exception {
    final Integer userId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, "hasan", "hasankhan");
    final List<Integer> parentItemIds = new ArrayList<Integer>();
    parentItemIds.add(userId);
    final List<Integer> itemIds = TestCaseRepositoryHelper.
        fixCreateItems(mRepositoryService, 10, "blog", parentItemIds);
    
    // Execute successful login request.
    final Subject subject = getSubjectFromASuccessfulRequest();

    // Create a new servlet request.
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/service/get/" + itemIds.get(0) + ".xml");
    request.setMethod(METHOD_GET);
    // set parameters
    request.addParameter(WebConstants.PARAM_LOAD_RELATED_ITEMS, "true");
    request.addParameter(WebConstants.PARAM_RELATION_TYPES, "blog, user");
    request.addParameter(WebConstants.PARAM_MAX, "1");

    // set session id
    final MockHttpSession session = new MockHttpSession();
    session.setAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT, subject);
    request.setSession(session);
    final MockHttpServletResponse response = new MockHttpServletResponse();

    // Execute controller
    final ModelAndView modelAndView =
        mRestfulController.handleRequest(request, response);

    // Verify response
    assertNull("Model and view is ignored.", modelAndView);
    LOG.debug("Response - " + response.getContentAsString());
    final boolean stateTrue =
        response.getContentAsString().indexOf("false") == -1;
    assertTrue("This action should not return false",
               stateTrue);
    assertEquals("Response status is not 200.",
        RESTfulControllerImpl.STATUS_OK_200, response.getStatus());
  }

  /**
   * Test search request for findout a list of items.
   * @throws Exception
   */
  public void testFindOperation() throws Exception {
    // Create dummy user object
    final Integer dummyUserId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, "hasan", "hasankhan");

    // Create fixture data
    final List<Integer> createdItemIdList =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 5);
    // Set dummy user with the deletable list of items
    createdItemIdList.add(dummyUserId);

    try {
      // Execute successful login request
      final Subject subject = getSubjectFromASuccessfulRequest();

      // Create a new servlet request
      final MockHttpServletRequest request = new MockHttpServletRequest();
      request.setRequestURI("/service/find/q.xml");
      final Query query =
          new Query();
      query.and("email", "hasan@somewherein.net");
      LOG.debug("New query - " + query.buildQuery());
      request.addParameter("q", query.buildQuery().toString());
      // set pagination
      request.addParameter(WebConstants.PARAM_OFFSET, String.valueOf(0));
      request.addParameter(WebConstants.PARAM_MAX, String.valueOf(2));
      request.setMethod(METHOD_GET);

      // set session id
      final MockHttpSession session = new MockHttpSession();
      session.setAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT, subject);
      request.setSession(session);
      final MockHttpServletResponse response = new MockHttpServletResponse();

      // Execute controller
      final ModelAndView modelAndView =
          mRestfulController.handleRequest(request, response);

      // Verify response
      assertNull("Model or view is returned", modelAndView);
      final String responseContent = response.getContentAsString();
      assertTrue("Response content is empty.",
          responseContent.indexOf("true") != -1);
      assertEquals("Resonse status is not 200",
          RESTfulControllerImpl.STATUS_OK_200, response.getStatus());
      LOG.debug("Response content - " + responseContent);

    } finally {
      // clean up all fixed items.
      TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
    }
  }

  public void testDeleteOperation() throws Exception {
    final List<Integer> createdItems =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 1);
    // create new user object
    final Integer userId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, "hasan", "hasankhan");
    createdItems.add(userId);
    try {
      final Subject subject = getSubjectFromASuccessfulRequest();
      final MockHttpServletRequest request = new MockHttpServletRequest();
      request.setRequestURI("/service/delete/" + createdItems.get(0) + ".xml");
      request.setMethod(METHOD_DELETE);
      request.getSession().
          setAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT, subject);
      final MockHttpServletResponse response = new MockHttpServletResponse();

      // Execute command
      mRestfulController.handleRequest(request, response);

      // Verify impacts
      final String content = response.getContentAsString();
      assertTrue("No response request generated.", content.length() > 0);
      final boolean stateTrue = content.indexOf("true") != -1;
      assertTrue("No response state found", stateTrue);
      assertEquals("Return status 202",
                 RESTfulControllerImpl.STATUS_ACCEPTED_202, 202);
      LOG.debug("Content - " + content);
    } finally {
      TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
    }
  }

  public void testCleanup() {
    TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
  }

  /**
   * Request for storing new object.
   * @throws Exception
   */
  public void testSaveOperation() throws Exception {
    // Create dummy user object
    final Integer dummyUserId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, "hasan", "hasankhan");

    // Create fixture data
    final List<Integer> createdItemIdList =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 1);
    // Set dummy user with the deletable list of items
    createdItemIdList.add(dummyUserId);

    try {
      final Subject subject = getSubjectFromASuccessfulRequest();
      // build mock request
      final MockHttpServletRequest request = new MockHttpServletRequest();
      request.setRequestURI("/service/save/item.xml");
      request.setMethod(METHOD_POST);
      request.getSession().
          setAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT, subject);
      request.addParameter("item", fixContent());

      // Build mock response
      final MockHttpServletResponse response = new MockHttpServletResponse();

      // send request
      mRestfulController.handleRequest(request, response);

      // verify response
      final String content = response.getContentAsString();
      assertFalse("Response Content is empty", content.length() == 0);
      assertTrue("State is false.", content.indexOf("true") != -1);
      LOG.debug("Response - " + content);
    } finally {
      TestCaseRepositoryHelper.
          fixRemoveItems(mRepositoryService, createdItemIdList);
    }
  }

  /**
   * Request for updating an exsiting object.
   * @throws Exception
   */
  public void testUpdateOperation() throws Exception {
    // Create dummy user object
    final Integer dummyUserId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, "hasan", "hasankhan");

    // Create fixture data
    final List<Integer> createdItemIdList =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 1);
    // Set dummy user with the deletable list of items
    createdItemIdList.add(dummyUserId);

    try {
      final Subject subject = getSubjectFromASuccessfulRequest();
      // build mock request
      final MockHttpServletRequest request = new MockHttpServletRequest();
      request.setRequestURI("/service/update/item.xml");
      request.setMethod(METHOD_POST);
      request.getSession().
          setAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT, subject);
      request.addParameter("item", fixUpdateRequest(createdItemIdList.get(0)));

      // Build mock response
      final MockHttpServletResponse response = new MockHttpServletResponse();

      // send request
      mRestfulController.handleRequest(request, response);

      // verify response
      final String content = response.getContentAsString();
      assertFalse("Response Content is empty", content.length() == 0);
      LOG.debug("Response - " + content);
      assertTrue("Response state false.", content.indexOf("false") == -1);

      // verify updated object
      final GenericItem item =
          mRepositoryService.getItem(createdItemIdList.get(0),
                                     GenericItem.class);
      LOG.debug("Updated item - " + item);
      assertEquals("New title doesn't match",
                   "hasan-bang", item.getTitle());
    } finally {
      TestCaseRepositoryHelper.
          fixRemoveAllItems(mRepositoryService);
    }
  }

  private String fixUpdateRequest(final Integer pId) throws IOException {
    final InputStream inputStream = getClass().getClassLoader().
        getResourceAsStream("update-format.xml");
    byte[] data = new byte[inputStream.available()];
    inputStream.read(data, 0, data.length);
    final String text = new String(data);
    return text.replaceAll("#ID#", String.valueOf(pId));
  }

  private String fixContent() throws IOException {
    final InputStream inputStream = getClass().getClassLoader().
        getResourceAsStream("save-format.xml");
    byte[] data = new byte[inputStream.available()];
    inputStream.read(data, 0, data.length);
    return new String(data);
  }

  private Subject getSubjectFromASuccessfulRequest() throws Exception {
    final MockHttpServletRequestFixed request =
        new MockHttpServletRequestFixed();
    request.setRequestURI("/service/login/user&p.xml");
    request.setMethod(METHOD_POST);
    request.addParameter("user", "hasan");
    request.addParameter("p", "hasankhan");
    final MockHttpServletResponse response = new MockHttpServletResponse();

    // Execute controller
    final ModelAndView modelAndView =
        mRestfulController.handleRequest(request, response);

    // Verify response.
    assertNull("model or view is returned.", modelAndView);
    LOG.debug("Response - " + response.getContentAsString());
    assertTrue("No request response is generated.",
               response.getContentAsString().length() > 0);
    final Subject subject = (Subject) request.getSession().
        getAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT);
    assertNotNull("Subject didn't set in the session.", subject);
    return subject;
  }

  public void testUser() {
    TestCaseRepositoryHelper.fixCreateUser(mUserService, "hasan", "hasankhan");
    assertNotNull("Login failed", API.giveMe().userService().
        login(new UserCredential("hasan", "hasankhan")));
  }

  public void testRegisterUser() throws Exception {
    final User user = new User();
    user.setUser("hasankhan");
    user.setPassword("nhmthk");
    user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
    user.setLastUpdatedOn(new Timestamp(System.currentTimeMillis()));
    user.setRole(User.FIELD_ROLE_ADMIN, true);
    user.setRole(User.FIELD_ROLE_READ, true);
    user.setRole(User.FIELD_ROLE_WRITE, true);

    final MockHttpServletRequest request = new MockHttpServletRequest();
    final String serviceUri = "/service/register/admin.xml";
    request.setRequestURI(serviceUri);
    final String serializedContent = SerializerFactory.getInstance().
        serializeObject(SerializerFactory.XML, user);
    String requestString = "<" + XmlConstants.ELEMENT_REQUEST + ">";
    requestString += serializedContent;
    requestString += "</" + XmlConstants.ELEMENT_REQUEST + ">";
    request.addParameter("admin", requestString);

    LOG.debug("Request - " + requestString);
    final MockHttpServletResponse response = new MockHttpServletResponse();

    // execute controller
    mRestfulController.handleRequest(request, response);

    final String responseContent = response.getContentAsString();
    assertTrue("No content found", responseContent.length() > 0);
    LOG.debug("content - " + responseContent);
  }

  public void testAddRelatedItem() throws Exception {
    // Create dummy user object
    final Integer dummyUserId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, "hasan", "hasankhan");

    // authenticate user.
    final Subject subject = getSubjectFromASuccessfulRequest();

    // fix data
    final List<Integer> fixedItems =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService,  2);

    // relation types
    final String categoryType = "category";

    // request through xml format
    final GenericItem item = new GenericItem();
    item.setId(fixedItems.get(1));
    item.addRelatedItem(categoryType, fixedItems.get(0));

    // prepare request command
    final StringBuilder requestString = new StringBuilder();
    requestString.append("<" + XmlConstants.ELEMENT_REQUEST + ">");
    requestString.append(SerializerFactory.getInstance().
                         serializeObject(SerializerFactory.XML, item));
    requestString.append("</" + XmlConstants.ELEMENT_REQUEST + ">");

    // create request uri
    final String requestUri = "/service/add-related-items/id.xml";
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI(requestUri);
    request.setMethod(METHOD_POST);
    request.addParameter("id", requestString.toString());
    request.getSession().
        setAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT, subject);
    final MockHttpServletResponse response = new MockHttpServletResponse();

    // execute restful service
    mRestfulController.handleRequest(request, response);

    // verify
    final String responseString = response.getContentAsString();
    LOG.debug("Response string - " + responseString);
    assertFalse("No response found",
                responseString == null || responseString.length() == 0);
    assertFalse("Response state false",
                responseString.indexOf("false") != -1);

  }

  public void testDeleteRelatedItem() throws Exception {
    // Create dummy user object
    final Integer dummyUserId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, "hasan", "hasankhan");

    // authenticate user.
    final Subject subject = getSubjectFromASuccessfulRequest();

    // fix data
    final List<Integer> fixedItems =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService,  2);

    // relation types
    final String categoryType = "category";

    // add related items
    mRepositoryService.
        addRelatedItem(categoryType, fixedItems.get(0), fixedItems.get(1));
    // verify
    final List<Integer> relatedItems = mRepositoryService.
        getRelatedItems(fixedItems.get(0), categoryType, 0, Integer.MAX_VALUE);
    assertTrue("No related item found",
               relatedItems != null && !relatedItems.isEmpty());

    // request through xml format
    final GenericItem item = new GenericItem();
    item.setId(fixedItems.get(0));
    item.addRelatedItem(categoryType, fixedItems.get(1));


    // prepare request command
    final StringBuilder requestString = new StringBuilder();
    requestString.append("<" + XmlConstants.ELEMENT_REQUEST + ">");
    requestString.append(SerializerFactory.getInstance().
                         serializeObject(SerializerFactory.XML, item));
    requestString.append("</" + XmlConstants.ELEMENT_REQUEST + ">");

    // create request uri
    final String requestUri = "/service/delete-related-items/id.xml";
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI(requestUri);
    request.setMethod(METHOD_POST);
    request.addParameter("id", requestString.toString());
    request.getSession().
        setAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT, subject);
    final MockHttpServletResponse response = new MockHttpServletResponse();

    // execute restful service
    mRestfulController.handleRequest(request, response);

    // verify
    final String responseString = response.getContentAsString();
    LOG.debug("Response string - " + responseString);
    assertFalse("No response found",
                responseString == null || responseString.length() == 0);
    assertFalse("Response state false",
                responseString.indexOf("false") != -1);

    // verify related items
    final List<Integer> relatedItems2 = mRepositoryService.
        getRelatedItems(fixedItems.get(0), categoryType, 0, Integer.MAX_VALUE);
    assertFalse("Related item found",
                relatedItems2 != null && !relatedItems2.isEmpty());
  }

  public void testListOfRelatedItems() throws Exception {
    // Create dummy user object
    final Integer dummyUserId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, "hasan", "hasankhan");

    // authenticate user.
    final Subject subject = getSubjectFromASuccessfulRequest();

    // create fixed items
    final List<Integer> fixedItems =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService,  11);

    // update object relation
    final Integer baseItemId = fixedItems.get(0);

    // attach related items
    final String relationType = "category";
    mRepositoryService.addRelatedItems(
        relationType, baseItemId, fixedItems.subList(1, fixedItems.size()));

    // send restful request
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/service/find-related-items/" +
                          relationType + "&" + String.valueOf(baseItemId) +
                          ".xml");
    request.setParameter(WebConstants.PARAM_MAX, 4 + "");
    request.setMethod(METHOD_POST);
    request.getSession().
        setAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT, subject);
    final MockHttpServletResponse response = new MockHttpServletResponse();

    // execute restful controller
    mRestfulController.handleRequest(request, response);

    final String responseString = response.getContentAsString();
    LOG.debug("Response content - " + responseString);

    assertFalse("No response found",
                responseString == null || responseString.length() == 0);
    assertFalse("Response state false",
                responseString.indexOf("false") != -1);
  }

  public void testFindOperationInPHPResponseFormat() throws Exception {
    // Create dummy user object
    final Integer dummyUserId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, "hasan", "hasankhan");

    // Create fixture data
    final List<Integer> createdItemIdList =
        TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 5);
    // Set dummy user with the deletable list of items
    createdItemIdList.add(dummyUserId);

    try {
      // Execute successful login request
      final Subject subject = getSubjectFromASuccessfulRequest();

      // Create a new servlet request
      final MockHttpServletRequest request = new MockHttpServletRequest();
      request.setRequestURI("/service/find/q.php");
      final Query query =
          new Query();
      query.and("email", "hasan@somewherein.net");
      LOG.debug("New query - " + query.buildQuery());
      request.addParameter("q", query.buildQuery().toString());
      // set pagination
      request.addParameter(WebConstants.PARAM_OFFSET, String.valueOf(0));
      request.addParameter(WebConstants.PARAM_MAX, String.valueOf(2));
      request.setMethod(METHOD_GET);

      // set session id
      final MockHttpSession session = new MockHttpSession();
      session.setAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT, subject);
      request.setSession(session);
      final MockHttpServletResponse response = new MockHttpServletResponse();

      // Execute controller
      final ModelAndView modelAndView =
          mRestfulController.handleRequest(request, response);

      // Verify response
      assertNull("Model or view is returned", modelAndView);
      final String responseContent = response.getContentAsString();
      assertTrue("Response content is empty.",
          responseContent.indexOf("true") != -1);
      assertEquals("Resonse status is not 200",
          RESTfulControllerImpl.STATUS_OK_200, response.getStatus());
      LOG.debug("Response content - " + responseContent);

    } finally {
      // clean up all fixed items.
      TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
    }
  }

  public void testGetItemInPHPResponse() throws Exception {
    final Integer userId = TestCaseRepositoryHelper.
        fixCreateUser(mUserService, "hasan", "hasankhan");
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    request.setRequestURI("/service/get/" + userId + ".php");
    request.setMethod(METHOD_GET);

    // fix login state
    final Subject subject = getSubjectFromASuccessfulRequest();
    final MockHttpSession session = new MockHttpSession();
    session.setAttribute(WebConstants.SESSION_ATTR_USER_SUBJECT, subject);
    request.setSession(session);

    // execute action
    final ModelAndView modelAndView =
        mRestfulController.handleRequest(request, response);

    // verify response
    final String responseContent = response.getContentAsString();
    assertNotNull(responseContent);
    LOG.debug("Response content - " + responseContent);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    fixRemoveAllItems(mRepositoryService);
  }
}

/* $Id: RESTfulControllerImpl.java 173 2007-06-23 21:00:21Z hasan $ */
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
 * $LastChangedDate: 2007-06-23 14:00:21 -0700 (Sat, 23 Jun 2007) $
 * $LastChangedRevision: 173 $
 ******************************************************************************
*/
package com.ideabase.repository.webservice.controller;

import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.StateManager;
import com.ideabase.repository.api.UserService;
import static com.ideabase.repository.common.ActionConstants.*;
import static com.ideabase.repository.common.CommonConstants.INDEX_DEFAULT;
import com.ideabase.repository.common.GroupConstants;
import com.ideabase.repository.common.Query;
import com.ideabase.repository.common.RequestState;
import static com.ideabase.repository.common.WebConstants.*;
import static com.ideabase.repository.common.XmlConstants.*;
import com.ideabase.repository.common.exception.ServiceException;
import com.ideabase.repository.common.object.*;
import com.ideabase.repository.core.auth.RepositoryUserPrincipal;
import com.ideabase.repository.webservice.helper.ResponseBuilder;
import com.ideabase.repository.webservice.helper.ResponseElement;
import com.ideabase.repository.webservice.object.EmptyObject;
import com.ideabase.repository.webservice.object.HitListObject;
import com.ideabase.repository.webservice.request.WebServiceRequestHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.*;

/**
 * Authenticate user credential. user id is taken from request url. <br>
 * i.e. /service/login/hasan [here user is 'hasan']<br>
 * password is taken from POST parameter 'password'<br>
 *
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class RESTfulControllerImpl implements RESTfulController {

  /**
   * Logger instance for this class.
   */
  private static final Logger LOG =
      LogManager.getLogger(RESTfulControllerImpl.class);

  /**
   * Take an instance of current logger status.
   */
  private static final boolean DEBUG = LOG.isDebugEnabled();

  /**
   * Resolve all error or warning message from message properties file.
   */
  private final MessageSourceAccessor mMessageAccessor;

  /**
   * Instance of implemented {@see StateManager}
   */
  private final StateManager mStateManager;

  /**
   * Default response format is used whenever processErrorAction is invoked
   * without RESTful action.
   */
  private static final String DEFAULT_RESPONSE_FORMAT = "request";

  /**
   * Mapping for content mime type, all mime types are mapped with a single
   * typed name. for example -<br>
   * [text => 'text/request']<br>
   */
  private static final Map<String, String> CONTENT_TYPE_MAP =
      new HashMap<String, String>();

  /**
   * Static initialization of {@see RESTfulControllerImpl#CONTENT_TYPE_MAP}
   */
  static {
    CONTENT_TYPE_MAP.put("xml", "text/xml");
    CONTENT_TYPE_MAP.put("json", "application/json");
    CONTENT_TYPE_MAP.put("html", "text/html");
    CONTENT_TYPE_MAP.put("text", "text/plain");
  }

  /**
   * Default locale is set ot US.
   */
  private static final Locale DEFAULT_LOCALE = Locale.US;

  /**
   * All error message keys are started with "error" prefix.
   */
  private static final String KEY_ERROR_PREFIX = "error.";

  /**
   * All success message keys are started with "success" prefix.
   */
  public static final String KEY_SUCCESS_PREFIX = "success.";

  /**
   * message key, error.action.invalid.
   */
  private static final String KEY_INVALID_ACTION =
                              KEY_ERROR_PREFIX + "action.invalid";

  /**
   * message key, success.action.save.
   */
  private static final String KEY_SAVE_SUCCESSFUL =
                              KEY_SUCCESS_PREFIX + "action.save";

  /**
   * message key, error.action.failure.
   */
  private static final String KEY_ACTION_FAILURE =
                              KEY_ERROR_PREFIX + "action.failure";

  /**
   * Message key, error.action.login.failure. used when authentication failed.
   */
  private static final String KEY_INVALID_USER =
                              KEY_ERROR_PREFIX + "action.login.failure";

  /**
   * Message key, error.action.authorized, used when specific action is not
   * permitted.
   */
  public static final String KEY_UNAUTHORIZED_ACTION =
                             KEY_ERROR_PREFIX + "action.unauthorized";

  /**
   * Message key, error.action.authentication required.
   */
  public static final String KEY_AUTHENTICATION_REQUIRED =
                             KEY_ERROR_PREFIX + "action.authentication";

  /**
   * Message key, error.action.resource_not_available, used when specific
   * resource is not available.
   */
  public static final String KEY_RESOURCE_NOT_AVAILABLE =
                             KEY_ERROR_PREFIX + "action.resource_not_available";

  public static final String KEY_DELETE_SUCCESSFUL =
                             KEY_SUCCESS_PREFIX + "action.delete";
  /**
   * Send response status 404, it means resource doesn't exists.
   */
  public static final int STATUS_NOT_FOUND_404 = 404;

  /**
   * Send out this status 500, when internal service error occured.
   */
  public static final int STATUS_500 = 500;

  /**
   * Send out this status, when the request has be executed successfully.
   */
  public static final int STATUS_OK_200 = 200;

  /**
   * Send out this status 201, when a new object is created.
   */
  public static final int STATUS_CREATED_201 = 201;

  /**
   * Send out this status, when user credential is accepted.
   */
  public static final int STATUS_ACCEPTED_202 = 202;

  /**
   * Send out this status, when user is not authenticated.
   */
  public static final int STATUS_UNAUTHORIZED_401 = 401;

  /**
   * Send out this status, when user action is not authorized.
   */
  public static final int STATUS_FORBIDDEN_403 = 403;

  /**
   * Send out this status, when requested format is not supported.
   */
  public static final int STATUS_UNSUPPORTED_FORMAT_415 = 415;

  /**
   * When user requested for retrieving some specific id content. usually
   * this requst is used to perform retrieval operation.
   */
  private static final String ACTION_GET = "get";

  /**
   * When user requested for attaching a list of items to an specific item.
   */
  private static final String ACTION_ADD_RELATED_ITEMS = "add-related-items";

  /**
   * When user requested for remove the attached related items.
   */
  private static final String ACTION_DELETE_RELATED_ITEMS =
                              "delete-related-items";

  /**
   * When user requested for retrieve the list of related items
   */
  private static final String ACTION_FIND_RELATED_ITEMS =
                              "find-related-items";

  /**
   * When user requested for a list of content reference. usually this
   * request is used to perform search operation.
   */
  private static final String ACTION_FIND = "find";

  /**
   * Delete request for removing an item.
   */
  private static final String ACTION_DELETE = "delete";

  /**
   * Save request for storing an item.
   */
  private static final String ACTION_SAVE = "save";

  /**
   * Update request for updating an existing item.
   */
  private static final String ACTION_UPDATE = "update";

  /**
   * No string is set.
   */
  private static final String EMPTY_STRING = "";

  /**
   * Value is set to true.
   */
  private static final String TRUE = "true";

  /**
   * Separate string with comma (,)
   */
  private static final String SEPARATOR_COMMA = ",";

  /**
   * Item uri prefix, this constant is used when an item has been created.
   */
  private static final String ITEM_URI_PREFIX = "/service/get/";

  /**
   * Dependency on {@see UserService}. which is resolved over constructor
   * injection.
   */
  private final UserService mUserService;

  /**
   * Core repsitory API, which is used to perform repository related action.
   */
  private final RepositoryService mRepositoryService;

  /**
   * Convert string query to Lucene query.
   */
  private final QueryParser mQueryParser;

  /**
   * Web service request processor.
   */
  private final WebServiceRequestHandler mWebServiceRequestHandler;


  /**
   * Default constructor, {@see RepositoryService}, {@see UserService},
   * {@see MEssageSourceAccessor} and {@see QueryParser} are the default
   * depdendency. <Br>
   *
   * @param pRepositoryService required depedency. 
   * @param pUserService required dependency.
   * @param pSourceAccessor depenency on {@see MessageSourceAccessor}.
   * @param pQueryParser dependency on {@see QueryParser}.
   * @param pRequestHandler web service request handler.
   * @param pStateManager dependency on {@see StateManager} implementation.
   */
  public RESTfulControllerImpl(final RepositoryService pRepositoryService,
                               final UserService pUserService,
                               final MessageSourceAccessor pSourceAccessor,
                               final QueryParser pQueryParser,
                               final WebServiceRequestHandler pRequestHandler,
                               final StateManager pStateManager) {
    mRepositoryService = pRepositoryService;
    mUserService = pUserService;
    mMessageAccessor = pSourceAccessor;
    mQueryParser = pQueryParser;
    mWebServiceRequestHandler = pRequestHandler;
    mStateManager = pStateManager;
  }

  /**
   * Default controller dispatcher. this method is proxied through aspectJ
   * around advice. so this part of code won't be executed.
   *
   * @param pHttpServletRequest servlet request object
   * @param pHttpServletResponse servlet response object
   * @return {@see ModelAndView} which is returned null.
   * @throws Exception if any exception is raised.
   */
  public ModelAndView handleRequest(
      final HttpServletRequest pHttpServletRequest,
      final HttpServletResponse pHttpServletResponse) throws Exception {
    LOG.debug("Handle request should't be executed. if you get it executed. " +
        "it means, the AspectJ proxy didn't work with around advice.");
    // Empty method, this method won't be executed.
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public void processErrorAction(final RESTfulAction pAction,
                                 final ErrorCode pErrorCode,
                                 final HttpServletRequest pRequest,
                                 final HttpServletResponse pResponse) {
    if (DEBUG) {
      LOG.debug("Process error action for error code - " + pErrorCode);
    }
    try {
      switch (pErrorCode) {
        /*
         * When Login failed occured.
         */
        case LOGIN_FAILED:
          final String loginErrormessage =
            mMessageAccessor.getMessage(KEY_INVALID_USER, getActiveLocale());
          generateErrorResponse(pAction, pResponse, loginErrormessage,
                                STATUS_UNAUTHORIZED_401);
          break;
        
        /*
         * When invalid action is triggered.
         */
        case INVALID_ACTION:
          invalidActionResponse(pAction, KEY_INVALID_ACTION, pResponse);
          break;

        /**
        * Failed to execute an action.
        */
        case FAILURE_EXECUTION:
          invalidActionResponse(pAction, KEY_ACTION_FAILURE, pResponse);
          break;

        /**
         * When user is authenticated successfully, but the requested action is
         * not permitted.
         */
        case UNAUTHORIZED_ACTION:
          final String unauthorizedActionMessage = mMessageAccessor.
              getMessage(KEY_UNAUTHORIZED_ACTION,
                  new Object[] { pAction.getUri(), pAction.getRequestMethod() },
                  getActiveLocale());
          generateErrorResponse(pAction, pResponse, unauthorizedActionMessage,
                                STATUS_FORBIDDEN_403);
          break;

          /**
           * When user is authenticated successfully, but the requested resource
           * is not available.
           */
        case RESOURCE_NOT_AVAILABLE:
          final String resourceNotAvailableMessage = mMessageAccessor.
              getMessage(KEY_RESOURCE_NOT_AVAILABLE,
                  new Object[] { pAction.getUri() },
                  getActiveLocale());
          generateErrorResponse(pAction, pResponse, resourceNotAvailableMessage,
                                STATUS_NOT_FOUND_404);
          break;
        case AUTHENTICATION_REQUIRED:
          final String authRequired = mMessageAccessor.
              getMessage(KEY_AUTHENTICATION_REQUIRED,
                  new Object[] { pAction.getUri(), pAction.getRequestMethod() },
                  getActiveLocale());
          generateErrorResponse(pAction, pResponse, authRequired,
                                STATUS_UNAUTHORIZED_401);
          break;
      }
    } catch (Throwable t) {
      throw new ServiceException(pErrorCode,
              "Failed to generate error response.", t);
    }
  }

  /**
   * Generate error response, error response hold state and message.
   * 
   * @param pAction restful action object
   * @param pResponse http servlet response object.
   * @param pMessage error / warning message.
   * @param pStatus response status, 500 or 404
   * @throws java.io.IOException if any error raised.
   */
  private void generateErrorResponse(final RESTfulAction pAction,
                                     final HttpServletResponse pResponse,
                                     final String pMessage,
                                     final int pStatus) throws IOException {
    if (DEBUG) {
      LOG.debug("Error response message - " + pMessage);
    }
    if (pAction == null) {
      throw new NullPointerException(
            "RESTfulAction has passed with empty reference.");
    }
    // Determine response format
    final String responseFormat;
    if (pAction != null) {
      responseFormat = pAction.getResponseFormat();
    } else {
      responseFormat = DEFAULT_RESPONSE_FORMAT;
    }
    // Prepare response builder
    final ResponseBuilder builder =
        new ResponseBuilder(responseFormat, false);
    // Add response object.
    builder.addObject(new ResponseElement(ELEMENT_MESSAGE, pMessage));
    // set response status
    pResponse.setStatus(pStatus);
    // set content type
    pResponse.setContentType(findContentMimeType(pAction.getResponseFormat()));
    // Set rendered output to the servlet response.
    pResponse.getWriter().println(builder.buildResponse());
  }

  public void processAuthorizedAction(final RESTfulAction pAction,
                                      final HttpServletRequest pRequest,
                                      final HttpServletResponse pResponse)
      throws Exception {
    if (DEBUG) {
      LOG.debug("Process authorized action." + pAction);
    }
    // handle action type
    // handle get request
    if (ACTION_GET.equals(pAction.getAction())) {
      handleGetAction(pAction, pRequest, pResponse);
    }
    // handle find request
    else if (ACTION_FIND.equals(pAction.getAction())) {
      handleFindAction(pAction, pRequest, pResponse);
    }
    // handle delete request.
    else if (ACTION_DELETE.equals(pAction.getAction())) {
      handleDeleteAction(pAction, pRequest, pResponse);
    }
    // handle save request
    else if (ACTION_SAVE.equals(pAction.getAction())) {
      handleSaveAction(pAction, pRequest, pResponse);
    }
    // handle update request
    else if (ACTION_UPDATE.equals(pAction.getAction())) {
      handleUpdateAction(pAction, pRequest, pResponse);
    }
    // handle add related items
    else if (ACTION_ADD_RELATED_ITEMS.equals(pAction.getAction())) {
      handleAddRelatedItemsAction(pAction, pRequest, pResponse);
    }
    // handle delete related items
    else if (ACTION_DELETE_RELATED_ITEMS.equals(pAction.getAction())) {
      handleDeleteRelatedItemsAction(pAction, pRequest, pResponse);
    }
    // handle find related items
    else if (ACTION_FIND_RELATED_ITEMS.equals(pAction.getAction())) {
      handleFindRelatedItemsAction(pAction, pRequest, pResponse);
    }
    // handle unknown action
    else {
      invalidActionResponse(pAction, KEY_INVALID_ACTION, pResponse);
    }
  }

  private void handleFindRelatedItemsAction(final RESTfulAction pAction,
                                            final HttpServletRequest pRequest,
                                            final HttpServletResponse pResponse)
      throws IOException {
    LOG.debug("Find A list of related items.");
    final String param = pAction.getParameter();
    final String[] params = param.split(PARAMETER_SPLITER);
    final String relationType = params[0];
    final Integer baseItemId = Integer.parseInt(params[1]);

    // looking for pagination hints
    Integer skipRows = 0;
    Integer maxRows = Integer.MAX_VALUE;

    // override skip rows and maximum number from the request parameter.
    final String parameterOffset = pRequest.getParameter(PARAM_OFFSET);
    final String parameterMax = pRequest.getParameter(PARAM_MAX);

    if (parameterOffset != null) {
      skipRows = Integer.parseInt(parameterOffset);
    }
    if (parameterMax != null) {
      maxRows = Integer.parseInt(parameterMax);
    }

    // retrieve the result
    final List<Integer> relatedItems = mRepositoryService.
        getRelatedItems(baseItemId, relationType, skipRows, maxRows);
    // find the number of available right side item
    final int rowCount =
        mRepositoryService.getRelatedItemsCount(baseItemId, relationType);

    // TODO: count total number of approx. pages.
    int pageCount = -1;
    if (parameterMax != null) {
      pageCount = (int) Math.round((double) rowCount / (double) maxRows);
      if (pageCount == 0 && rowCount > 0) {
        pageCount = 1;
      }
    }

    // send out the response
    final List<String> relatedItemsUri =
        new ArrayList<String>(relatedItems.size());
    for (final Integer itemId : relatedItems) {
      relatedItemsUri.add(ITEM_URI_PREFIX + String.valueOf(itemId));
    }

    // generate response content
    final ResponseElement itemResponseElement =
        new ResponseElement(ELEMENT_ITEM, relatedItemsUri);
    final ResponseElement itemsResponseElement =
        new ResponseElement(ELEMENT_ITEMS, itemResponseElement);
    itemsResponseElement.addResponseElement(
        new ResponseElement(ELEMENT_MAX_ROWS, rowCount));
    itemsResponseElement.addResponseElement(
        new ResponseElement(ELEMENT_PAGES_COUNT, pageCount));

    generateResponse(true, pAction, pRequest, pResponse,
                     itemsResponseElement, STATUS_OK_200);
  }

  /**
   * Delete a list of items from a specific object. <br>
   * if only group is specified, all items under that group will be wiped out.
   * <br> otherwise only the specific items will be removed.
   * @param pAction restful action object.
   * @param pRequest servlet request.
   * @param pResponse servlet response.
   * @throws IOException if exception raised during sending out the response
   *         to the client.
   */
  private void handleDeleteRelatedItemsAction(
      final RESTfulAction pAction, final HttpServletRequest pRequest,
      final HttpServletResponse pResponse) throws IOException {

    LOG.debug("Handle delete related items action.");
    final String parameterName = pAction.getParameter();
    final String requestContent = pRequest.getParameter(parameterName);
    final Reader requestContentReader = new StringReader(requestContent);
    final List<ObjectBase> items =
        mWebServiceRequestHandler.handleRequest(pAction, requestContentReader);

    // store related items
    for (final ObjectBase item : items) {
      final Integer itemId = item.getId();
      final Map<String, List<Integer>> relatedItems = item.getRelatedItemsMap();
      for (final Map.Entry<String, List<Integer>> entry
           : relatedItems.entrySet()) {
        final String relationType = entry.getKey();
        final List<Integer> itemIds = entry.getValue();
        // delete related items.
        if (itemIds == null || itemIds.isEmpty()) {
          if (DEBUG) {
            LOG.debug("Deleting all items from the specific group - " +
                      relationType);
          }
          mRepositoryService.deleteRelatedItems(relationType, itemId);
        } else {
          if (DEBUG) {
            LOG.debug("Delete specific item from the group of - " +
                      relationType);
          }
          for (final Integer rightSideItemId : itemIds) {
            mRepositoryService.
                deleteRelatedItem(relationType, itemId, rightSideItemId);
          }
        }
      }
    }

    // send response
    final String message = mMessageAccessor.getMessage(KEY_DELETE_SUCCESSFUL);
    final ResponseElement responseElement =
        new ResponseElement(ELEMENT_MESSAGE, message);
    generateResponse(
        true, pAction, pRequest, pResponse, responseElement, STATUS_OK_200);
  }

  /**
   * Parse user request. and find an item with the requested related item list
   * which are needs to be added with the main item.
   * @param pAction restful action.
   * @param pRequest http servlet request.
   * @param pResponse http servlet response.
   * @throws java.io.IOException if exception raised during sending out the
   *         response to the client.
   */
  private void handleAddRelatedItemsAction(final RESTfulAction pAction,
                                           final HttpServletRequest pRequest,
                                           final HttpServletResponse pResponse)
      throws IOException {
    LOG.debug("Handle add related items action.");
    final String parameterName = pAction.getParameter();
    final String requestContent = pRequest.getParameter(parameterName);
    final Reader requestContentReader = new StringReader(requestContent);
    final List<ObjectBase> items =
        mWebServiceRequestHandler.handleRequest(pAction, requestContentReader);

    // store related items
    for (final ObjectBase item : items) {
      final Integer itemId = item.getId();
      final Map<String, List<Integer>> relatedItems = item.getRelatedItemsMap();
      for (final Map.Entry<String, List<Integer>> entry
           : relatedItems.entrySet()) {
        final String relationType = entry.getKey();
        final List<Integer> itemIds = entry.getValue();
        // store relation
        mRepositoryService.addRelatedItems(relationType, itemId, itemIds);
      }
    }

    // send response
    final String message = mMessageAccessor.getMessage(KEY_SAVE_SUCCESSFUL);
    final ResponseElement responseElement =
        new ResponseElement(ELEMENT_MESSAGE, message);
    generateResponse(
        true, pAction, pRequest, pResponse, responseElement, STATUS_OK_200);
  }

  private void handleUpdateAction(final RESTfulAction pAction,
                                  final HttpServletRequest pRequest,
                                  final HttpServletResponse pResponse)
      throws IOException {
    LOG.debug("Handle update action.");
    final String requestedIndexRepository =
        pRequest.getParameter(PARAM_INDEX_REPOSITORY);
    final String contentParam = pAction.getParameter();
    final String content = pRequest.getParameter(contentParam);
    final StringReader contentStream = new StringReader(content);
    final List<ObjectBase> items =
        mWebServiceRequestHandler.handleRequest(pAction, contentStream);

    if (items != null && !items.isEmpty()) {
      if (DEBUG) {
        LOG.debug("Request has a list of items - " + items);
      }
      final List<String> newlyCreatedItemIds = new ArrayList<String>();
      for (final ObjectBase item : items) {
        // set user requested index repository
        item.setIndexRepository(requestedIndexRepository);
        // load the existing object
        final ObjectBase existingItem;
        // Determine type of existing object item.
        existingItem =
            mRepositoryService.getItem(item.getId(), item.getClass());

        if (existingItem == null) {
          throw new ServiceException(item.getId(), "Invalid update request," +
              " item doesn't exist in repository.");
        }
        
        // active user id.
        final Integer authUserId = findActiveUser(pRequest);

        // trasform new object to old object
        transferNewToOld(existingItem, item, authUserId);

        // update object
        final Integer itemId = mRepositoryService.save(existingItem);
        newlyCreatedItemIds.add(ITEM_URI_PREFIX + itemId);
      }

      // generate response
      final ResponseElement responseElement =
          new ResponseElement(ELEMENT_ITEM, newlyCreatedItemIds);
      generateResponse(true, pAction, pRequest, pResponse,
                       responseElement, STATUS_CREATED_201);
    }
  }

  private void transferNewToOld(final ObjectBase pExistingItem,
                                final ObjectBase pNewItem,
                                final Integer pAuthUserId) {
    // update index repository name
    if (pNewItem.getIndexRepository() != null) {
      pExistingItem.setIndexRepository(pNewItem.getIndexRepository());
    } else if (pExistingItem.getIndexRepository() == null) {
      pExistingItem.setIndexRepository(INDEX_DEFAULT);
    }

    // update title
    if (pNewItem.getTitle() != null) {
      pExistingItem.setTitle(pNewItem.getTitle());
    }

    // update basic fields
    if (pNewItem.getFields() != null && !pNewItem.getFields().isEmpty()) {
      final Map<String, String> existingFields = pExistingItem.getFields();
      final Map<String, String> updatedFields = pNewItem.getFields();

      for (final Map.Entry<String, String> entry : updatedFields.entrySet()) {
        final String fieldName = entry.getKey();
        final String fieldValue = entry.getValue();
        existingFields.put(fieldName, fieldValue);
      }
      pExistingItem.setFields(existingFields);
    }

    // update last updated time stamp
    final Timestamp lastUpdatedOn;
    if (pNewItem.getLastUpdatedOn() != null) {
      LOG.debug("Last updated timestamp was defined.");
      lastUpdatedOn = pNewItem.getLastUpdatedOn();
    } else {
      LOG.debug("Taking updated timestamp");
      lastUpdatedOn = new Timestamp(System.currentTimeMillis());
    }
    pExistingItem.setLastUpdatedOn(lastUpdatedOn);

    // update created on timestamp
    if (pNewItem.getCreatedOn() != null) {
      pExistingItem.setCreatedOn(pNewItem.getCreatedOn());
    }

    // TODO: update related items
/*
    if (!pNewItem.getRelationTypes().isEmpty()) {
      pExistingItem.setRelatedItemsMap(pNewItem.getRelatedItemsMap());
    }

*/
    // set author group.
    List<Integer> authorGroup = pExistingItem.
        getRelatedItemsByRelationType(GroupConstants.GROUP_AUTHOR);
    if (authorGroup != null) {
      if (!authorGroup.contains(pAuthUserId)) {
        authorGroup.add(pAuthUserId);
      }
    } else {
      authorGroup = new ArrayList<Integer>();
      authorGroup.add(pAuthUserId);
    }
  }

  /**
   * Find requested content. parse the user requested content. <br>
   * Retrieve the list of requested items.<br>
   * If any item id was specified that will be ignored by resetting
   * {@code item.setId(null)}<br>
   * set or create a new group of {@code author}. and add currently logged
   * on user id.<br>
   * Generate a successful response.<br>
   *
   * @param pAction restful action object
   * @param pRequest servlet request.
   * @param pResponse servlet response.
   * @throws IOException if any exception raised during sending off the content.
   */
  private void handleSaveAction(final RESTfulAction pAction,
                                final HttpServletRequest pRequest,
                                final HttpServletResponse pResponse)
      throws IOException {
    LOG.debug("Handle save action.");
    String indexRepository = pRequest.getParameter(PARAM_INDEX_REPOSITORY);
    if (indexRepository == null) {
      indexRepository = INDEX_DEFAULT;
    }

    final String contentParam = pAction.getParameter();
    final String content = pRequest.getParameter(contentParam);
    final StringReader contentStream = new StringReader(content);
    final List<ObjectBase> items =
        mWebServiceRequestHandler.handleRequest(pAction, contentStream);

    if (items != null && !items.isEmpty()) {
      if (DEBUG) {
        LOG.debug("Request has a list of items - " + items);
      }
      final List<String> newlyCreatedItemIds = new ArrayList<String>();
      for (final ObjectBase item : items) {
        // on 'save' action there is no way user can set 'id' number.
        item.setId(null);
        // set index repository name
        item.setIndexRepository(indexRepository);
        // set author group.
        List<Integer> authorGroup =
            item.getRelatedItemsByRelationType(GroupConstants.GROUP_AUTHOR);
        if (authorGroup != null) {
          authorGroup.add(findActiveUser(pRequest));
        } else {
          authorGroup = new ArrayList<Integer>();
          authorGroup.add(findActiveUser(pRequest));
        }
        final Integer itemId = mRepositoryService.save(item);
        newlyCreatedItemIds.add(ITEM_URI_PREFIX + itemId);
      }

      // generate response
      final ResponseElement responseElement =
          new ResponseElement(ELEMENT_ITEM, newlyCreatedItemIds);
      generateResponse(true, pAction, pRequest, pResponse,
                       responseElement, STATUS_CREATED_201);
    }
  }

  /**
   * Find {@see RepositoryUserPrincipal} from {@see Subject} object which must
   * be in {@see HttpSession} attribute list. if no such principal found an
   * unchecked exception will be raised.
   *
   * @param pRequest http servlet request.
   * @return the user id.
   */
  private Integer findActiveUser(final HttpServletRequest pRequest) {
    final String authToken = pRequest.getParameter(PARAM_AUTH_TOKEN);
    final Subject subject;
    if (mStateManager != null && authToken != null) {
      final RequestState requestState =
          mStateManager.getRequestStateForToken(authToken);
      subject = requestState.getSubject();
    } else {
      subject = (Subject) pRequest.getSession().
          getAttribute(SESSION_ATTR_USER_SUBJECT);
    }
    final Set<RepositoryUserPrincipal> principals =
        subject.getPrincipals(RepositoryUserPrincipal.class);
    if (principals != null && !principals.isEmpty()) {
      final RepositoryUserPrincipal principal = principals.iterator().next();
      return principal.getId();
    } else {
      throw ServiceException.aNew(subject,
          "Subject doesn't contain the RepositoryUserPrincipal.");
    }
  }

  /**
   * Delete an item from repository. it matches the patter
   * of {@code .+/delete/.+}.
   * @param pAction restful action
   * @param pRequest servlet request.
   * @param pResponse servlet response.
   */
  private void handleDeleteAction(final RESTfulAction pAction,
                                  final HttpServletRequest pRequest,
                                  final HttpServletResponse pResponse)
      throws IOException {

    // Find item id from REST parameter.
    final Integer itemId = Integer.parseInt(pAction.getParameter());
    if (DEBUG) {
      LOG.debug("Removing item id - " + itemId);
    }

    // Delete this item from repository
    mRepositoryService.delete(itemId);

    // Prepare successful message
    final String removeSuccessfulMessage = mMessageAccessor.
        getMessage(KEY_DELETE_SUCCESSFUL, new Object[] {itemId},
                   getActiveLocale());

    // Generate new response 
    final ResponseElement responseElement =
        new ResponseElement(ELEMENT_MESSAGE, removeSuccessfulMessage);
    generateResponse(true, pAction, pRequest, pResponse,
                     responseElement, STATUS_ACCEPTED_202);
  }

  /**
   * Handle {@code .+/find/.+} type request.
   */
  private void handleFindAction(final RESTfulAction pAction,
                                final HttpServletRequest pRequest,
                                final HttpServletResponse pResponse)
      throws ParseException, IOException {
    // Find query expression
    final String parameter = pAction.getParameter();
    final String queryString = pRequest.getParameter(parameter);

    if (queryString == null) {
      throw new ServiceException(parameter, "Parameter has no such value.");
    }

    // determine skipRows and length.
    final String paramOffset = pRequest.getParameter(PARAM_OFFSET);
    final String paramMax = pRequest.getParameter(PARAM_MAX);
    final Integer skip;
    if (paramOffset == null) {
      skip = 0;
    } else {
      skip = Integer.parseInt(paramOffset);
    }

    final Integer max;
    if (paramMax == null) {
      // Integer maximum number is the limit of rows.
      max = Integer.MAX_VALUE;
    } else {
      max = Integer.parseInt(paramMax);
    }

    if (DEBUG) {
      LOG.debug("Offset - " + skip + " max rows - " + max);
    }

    // find index repository
    String indexRepository =
        pRequest.getParameter(PARAM_INDEX_REPOSITORY);
    if (indexRepository == null) {
      indexRepository = INDEX_DEFAULT;
    }
    // build query
    final Query query =
        new Query(mQueryParser.parse(queryString)).index(indexRepository);

    // Set skipRows and maximum number of rows.
    query.maxRows(max).skipRows(skip);

    // find sortable field related parameters
    final String paramSortBy = pRequest.getParameter(PARAM_SORT_BY);

    if (paramSortBy != null) {
      // determine sorting order
      final String paramOrder = pRequest.getParameter(PARAM_ORDER);
      Boolean order = Boolean.FALSE;
      if (paramOrder != null && TRUE.equals(paramOrder)) {
        order = Boolean.TRUE;
      }

      // split sortable fields by comma (,)
      final String[] splittedSortableFields =
          paramSortBy.split(SORTABLE_FIELDS_SEPARATOR);
      for (final String sortableField : splittedSortableFields) {
        query.sortBy(sortableField, order);
      }
    }

    // perform lucene based search
    final PaginatedList<Hit> results =
        mRepositoryService.getItemsByQuery(query);
    final Object object;
    if (results != null && !results.isEmpty()) {
      object = new HitListObject(results);
    } else {
      object = EmptyObject.EMPTY;
    }
    // Response element.
    final ResponseElement responseElement =
        new ResponseElement(ELEMENT_ITEMS, object);
    // Add pagination hits
    final ResponseElement maximumRows =
        new ResponseElement(ELEMENT_MAX_ROWS, results.size());
    // Add probable page number
    final ResponseElement probablePages =
        new ResponseElement(ELEMENT_PAGES_COUNT, results.getPageCount());

    // add to the top response element
    responseElement.addResponseElement(maximumRows).
                    addResponseElement(probablePages);

    // Generate response
    generateResponse(true, pAction, pRequest, pResponse,
                     responseElement, STATUS_OK_200);
  }

  /**
   * Find an object from {@see RepositoryService}. if object doesn't exist
   * forward to processErrorAction.
   *
   * @param pAction restful action parameters.
   * @param pRequest servlet request.
   * @param pResponse servlet response.
   */
  private void handleGetAction(final RESTfulAction pAction,
                               final HttpServletRequest pRequest,
                               final HttpServletResponse pResponse)
      throws IOException {
    final Integer resourceId = Integer.parseInt(pAction.getParameter());
    final GenericItem genericItem =
        mRepositoryService.getItem(resourceId, GenericItem.class);

    // Send error message when resource doesn't exist.
    if (genericItem == null) {
      if (DEBUG) {
        LOG.debug("Item id - " + resourceId + " doesn't exists.");
      }
      processErrorAction(pAction, ErrorCode.RESOURCE_NOT_AVAILABLE,
                         pRequest, pResponse);
    } else {
      // Load related objects if parameter says it is intentional.
      loadRelatedItems(pRequest, genericItem);

      // convert object to request format (request/htm/json/text etc...).
      final ResponseElement responseElement =
          new ResponseElement(ELEMENT_ITEM, genericItem);
      generateResponse(true, pAction, pRequest, pResponse,
                       responseElement, STATUS_OK_200);
    }
  }

  /**
   * Load all right side related items and attach them with the target object.
   * @param pRequest servlet request
   * @param pGenericItem generic item object.
   */
  private void loadRelatedItems(final HttpServletRequest pRequest,
                                final GenericItem pGenericItem) {
    final String paramLoadRelatedItems =
        pRequest.getParameter(PARAM_LOAD_RELATED_ITEMS);
    final String paramRelatedItemsOffset =
        pRequest.getParameter(PARAM_OFFSET);
    final String paramRelatedItemsMax =
        pRequest.getParameter(PARAM_MAX);
    final String paramRelationTypes =
        pRequest.getParameter(PARAM_RELATION_TYPES);

    final boolean relatedItemLoadingEnabled =
        (paramLoadRelatedItems != null)
            && (EMPTY_STRING.equals(paramLoadRelatedItems)
            || TRUE.equals(paramLoadRelatedItems));
    if (relatedItemLoadingEnabled) {
      LOG.debug("Loading related items.");
      // Determine the skipRows
      final Integer offset;
      if (paramRelatedItemsOffset != null) {
        offset = Integer.parseInt(paramRelatedItemsOffset);
      } else {
        offset = 0;
      }

      // Determine the maximum number of items
      final Integer max;
      if (paramRelatedItemsMax != null) {
        max = Integer.parseInt(paramRelatedItemsMax);
      } else {
        max = Integer.MAX_VALUE;
      }

      if (DEBUG) {
        LOG.debug("Determined offset - " + offset + ", max rows - " + max);
      }
      // Relation type is not explicity defined.
      if (paramRelationTypes == null) {
        LOG.debug("No relation type defined.");
        final Map<String, List<Integer>> relatedItemsMap =
            mRepositoryService.getItems(pGenericItem.getId(), offset, max);
        pGenericItem.setRelatedItemsMap(relatedItemsMap);
      }
      // Multiple relation types can be defined using comma separator.
      else {
        if (DEBUG) {
          LOG.debug("Relation type defined - " + paramRelationTypes);
        }
        final Map<String, List<Integer>> relatedItemsMap =
            new HashMap<String, List<Integer>>();
        // split relation types
        final String[] splitedTypes = paramRelationTypes.split(SEPARATOR_COMMA);
        final Integer parentItemId = pGenericItem.getId();
        // Query for each relation type.
        for (String type : splitedTypes) {
          type = type.trim();
          if (DEBUG) {
            LOG.debug("Retrieving items for relation type - " + type);
          }
          final List<Integer> relatedItems =
            mRepositoryService.getRelatedItems(parentItemId, type, offset, max);
          relatedItemsMap.put(type, relatedItems);
        }
        if (DEBUG) {
          LOG.debug("Related items map - " + relatedItemsMap);
        }
        // Add relation type map in to the target object.
        pGenericItem.setRelatedItemsMap(relatedItemsMap);
      }
    } else {
      LOG.debug("No related items are loading.");
    }
  }

  public void processUnauthenticatedAction(
      final RESTfulAction pAction, final HttpServletRequest pRequest,
      final HttpServletResponse pResponse) throws Exception {
    if (DEBUG) {
      LOG.debug("Process unauthenticated action - " + pAction);
    }
    // Verify type of action.
    if (ACTION_LOGIN.equals(pAction.getAction())) {
      // Handle login request.
      handleLoginAction(pAction, pRequest, pResponse);
    } else if (ACTION_REGISTER.equals(pAction.getAction())) {
      // handle register request
      handleRegisterAction(pAction, pRequest, pResponse);
    } else {
      LOG.debug("NO action handler found.");
      // Send out a not allowed error response
      invalidActionResponse(pAction, KEY_INVALID_ACTION, pResponse);
    }
  }

  /**
   * User suppose to request registration process through the following URI-<Br>
   * {@code
   *  /service/register/user.xml or
   *  /service/register/admin.xml
   * } <br>
   * This function will look up {@code user|admin} post field from the request
   * parameters. if request parameter doesn't contain any exception it will
   * through the exeption.<br>
   * otherwise user will be registered and it will generate a response with a
   * newly created object reference.
   *
   * @param pAction restful action
   * @param pRequest http servlet request.
   * @param pResponse http servelt response.
   * @throws java.io.IOException if exception raised during trasfering content
   *          to the client.
   */
  private void handleRegisterAction(final RESTfulAction pAction,
                                    final HttpServletRequest pRequest,
                                    final HttpServletResponse pResponse)
      throws IOException {
    LOG.debug("Handle user registration action.");
    // find index repository name
    String indexRepository = pRequest.getParameter(PARAM_INDEX_REPOSITORY);
    if (indexRepository == null) {
      indexRepository = INDEX_DEFAULT;
    }
    // find parameter name
    final String paramName = pAction.getParameter();
    // lookup content from parameter
    final String content = pRequest.getParameter(paramName);

    // verify content
    if (content == null || content.length() == 0) {
      throw ServiceException.aNew(pAction, "Invalid registration request",null);
    }
    // process new content
    final List<ObjectBase> objects = mWebServiceRequestHandler.
        handleRequest(pAction, new StringReader(content));
    if (objects != null && !objects.isEmpty()) {
      final List<String> newlyCreatedItemIds = new ArrayList<String>();
      for (final ObjectBase objectBase : objects) {
        objectBase.setIndexRepository(indexRepository);
        if (objectBase instanceof User) {
          LOG.debug("Register new user object");
          final Integer id = mUserService.registerUser((User) objectBase);
          newlyCreatedItemIds.add(ITEM_URI_PREFIX + String.valueOf(id));
        } else {
          LOG.debug("Creating new item object");
          final Integer id = mRepositoryService.save(objectBase);
          newlyCreatedItemIds.add(ITEM_URI_PREFIX + String.valueOf(id));
        }
      }

      if (!newlyCreatedItemIds.isEmpty()) {
        // generate response
        final ResponseElement responseElement =
            new ResponseElement(ELEMENT_ITEM, newlyCreatedItemIds);
        generateResponse(true, pAction, pRequest, pResponse,
                         responseElement, STATUS_CREATED_201);
      } else {
        invalidActionResponse(pAction, KEY_INVALID_ACTION, pResponse);
      }
    }
  }

  /**
   * Generate this response when any action is not allowed or invalid.
   * @param pAction restful action object.
   * @param pMessageKey message key.
   * @param pResponse servelt response.
   */
  private void invalidActionResponse(final RESTfulAction pAction,
                                     final String pMessageKey,
                                     final HttpServletResponse pResponse)
      throws IOException {
    final String uri;
    if (pAction == null) {
      uri = "EMPTY";
    } else {
      uri = pAction.getUri();
    }
    final String message = mMessageAccessor.
        getMessage(pMessageKey, new Object[] {uri}, getActiveLocale());
    generateErrorResponse(pAction, pResponse, message, STATUS_FORBIDDEN_403);
  }

  private Locale getActiveLocale() {
    return DEFAULT_LOCALE;
  }

  /**
   * Parse url, and find the name of user and password parameter. <br>
   * perform login request, if {@see Subject} is returned, store it in
   * session context and response status is set to 202.<br>
   * Otherwise raise {@see ServiceException}, which is meant to be a symbol of
   * login failure.<br>
   * @param pAction restful action object.
   * @param pRequest servlet request object.
   * @param pResponse servelt response object.
   * @throws java.io.IOException if exception is raised during transfering
   *         content to the client.
   */
  private void handleLoginAction(final RESTfulAction pAction,
                                 final HttpServletRequest pRequest,
                                 final HttpServletResponse pResponse)
      throws IOException {
    // Determine requested user name and password.
    final String parameters = pAction.getParameter();
    final String[] splitParameters = parameters.split(PARAMETER_SPLITER);

    // throw exception if parameter is few.
    if (splitParameters.length != 2) {
      throw new ServiceException(parameters, "No such user or password request " +
                                             "parameter found.");
    }
    // proceed if parameter length is acceptable.
    final String userParamName = splitParameters[0];
    final String passwordParamName = splitParameters[1];

    // find user and password value from the request parameter.
    final String user = pRequest.getParameter(userParamName);
    final String password = pRequest.getParameter(passwordParamName);

    // Attempt for authentication.
    final UserCredential userCredential = new UserCredential(user, password);
    // generate authentication token for the current authentication request.
    final String stateId = mStateManager.generateRequestStateToken();
    userCredential.setStateId(stateId);

    // attempt for authentication
    final Subject subject = mUserService.login(userCredential);

    // If successful authentication, the subject won't be null.
    if (subject != null) {
      // keep subject with in the session context
      pRequest.getSession().
          setAttribute(SESSION_ATTR_USER_SUBJECT, subject);
      // generate response
      final ResponseElement responseElement =
         new ResponseElement(ELEMENT_AUTH_TOKEN, stateId);
      generateResponse(true, pAction, pRequest, pResponse, responseElement,
                       STATUS_ACCEPTED_202);
    } else {
      LOG.debug("User - " + user + ", attempt failed.");
    }
  }

  /**
   * Generate response using {@see ResponseBuilder}. response builder is
   * responsible to select appropriate content formatter.
   * @param pState state of the response.
   * @param pAction restulf action object.
   * @param pRequest servlet request.
   * @param pResponse servlet response.
   * @param pResponseElement response element.
   * @param pStatus http response status.
   * @throws java.io.IOException if exception is raised during transfering
   *         response to the client.
   */
  private void generateResponse(
      final boolean pState, final RESTfulAction pAction,
      final HttpServletRequest pRequest, final HttpServletResponse pResponse,
      final ResponseElement pResponseElement, final int pStatus)
      throws IOException {
    // Build response content.
    final ResponseBuilder responseBuilder =
        new ResponseBuilder(pAction.getResponseFormat(), pState);
    // add response object.
    responseBuilder.addObject(pResponseElement);
    // set response status
    pResponse.setStatus(pStatus);
    // set content type.
    pResponse.setContentType(findContentMimeType(pAction.getResponseFormat()));
    // send out response content.
    pResponse.getWriter().println(responseBuilder.buildResponse());
  }

  /**
   * Find content mime type by the short name of the content format. <br>
   * for example: {@code request} for mime type of {@code text/request}
   * @param pResponseFormat short name of content format.
   * @return the mime type of the content format.
   */
  private String findContentMimeType(final String pResponseFormat) {
    return CONTENT_TYPE_MAP.get(pResponseFormat.toLowerCase());
  }

}

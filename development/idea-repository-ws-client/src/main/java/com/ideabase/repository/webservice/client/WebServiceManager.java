/*
 * $Id: WebServiceManager.java 250 2008-01-07 10:18:29Z hasan $
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
package com.ideabase.repository.webservice.client;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.commons.codec.digest.DigestUtils;
import com.ideabase.repository.webservice.client.impl.HttpWebServiceControllerImpl;
import com.ideabase.repository.webservice.client.processor.ResponseProcessor;
import com.ideabase.repository.webservice.client.processor.impl.XmlResponseProcessorImpl;
import com.ideabase.repository.common.Query;
import com.ideabase.repository.common.InvocationSupport;
import com.ideabase.repository.common.WebConstants;
import com.ideabase.repository.common.XmlConstants;
import com.ideabase.repository.common.serializer.SerializerFactory;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.common.object.GenericItem;
import com.ideabase.repository.common.formatter.XmlSerializable;
import com.ideabase.repository.common.exception.ServiceException;
import static com.ideabase.repository.common.WebConstants.*;

import java.util.Map;
import java.util.HashMap;

/**
 * This class holds all the implementation of web service client and
 * related classes.<br>
 * this class also delegate the implementation of {@see WebServiceController}
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan
 *         (hasan)</a>
 */
public class WebServiceManager implements WebServiceManagerInterface {

  private static final String METHOD_POST = "POST";
  private static final String METHOD_GET = "GET";
  private static final String METHOD_DELETE = "DELETE";
  private static final String XML_CONTENT_TYPE = "text/xml";
  private static final String TEXT_CONTENT_TYPE = "text/plain";
  private static final String JSON_CONTENT_TYPE = "application/json";

  /**
   * This package private property is kept for supporting mockable
   * method abstraction.
   */
  public InvocationSupport mInvocationSupport;

  private static final Map<String, String> SERVICE_FORMATS =
      new HashMap<String, String>();

  static {
    SERVICE_FORMATS.put(XML_CONTENT_TYPE, "xml");
    SERVICE_FORMATS.put(TEXT_CONTENT_TYPE, "txt");
    SERVICE_FORMATS.put(JSON_CONTENT_TYPE, "json");
  }

  /* service action uri */
  private static final String SERVICE_FIND = "/service/find/query";
  private static final String SERVICE_GET = "/service/get/";
  private static final String SERVICE_SAVE = "/service/save/item";
  private static final String SERVICE_UPDATE = "/service/update/item";
  private static final String SERVICE_DELETE = "/service/delete/";
  private static final String SERVICE_LOGIN = "/service/login/user&password";
  private static final String SERVICE_REGISTER_ADMIN =
                              "/service/register/admin";
  private static final String SERVICE_ADD_RELATED_ITEMS =
                              "/service/add-related-items/item";
  private static final String SERVICE_DELETE_RELATED_ITEMS =
                              "/service/delete-related-items/item";
  private static final String SERVICE_FIND_RELATED_ITEMS =
                              "/service/find-related-items";
  private static final String SERVICE_REGISTER_USER = "/service/register/user";
  private static final String SERVICE_PARAM_ITEM = "item";
  private static final String SERVICE_PARAM_USER = "user";
  private static final String SERVICE_PARAM_PASSWORD = "password";
  private static final String SERVICE_PARAM_ADMIN = "admin";
  private static final String SLASH = "/";
  private static final String DOT = ".";
  private static final String AND = "&";

  /**
   * xml based web service response processor
   */
  private final ResponseProcessor mXmlResponseProcessor =
      new XmlResponseProcessorImpl();

  private final Logger LOG = LogManager.getLogger(getClass());
  private final WebServiceController mWebServiceController;
  private final String mBaseUrl;
  private String mDAuthKey;
  private String mCookies;

  /**
   * Default constructor, it initate the instance of web service controller.
   * @param pBaseUrl base url of remote service.
   */
  public WebServiceManager(final String pBaseUrl) {
    mBaseUrl = pBaseUrl;
    // create implementation instance of WebServiceController.
    // Default implementation is HttpWebServiceControllerImpl, later this could
    // be changed with other form of media.
    mWebServiceController =
        new HttpWebServiceControllerImpl(pBaseUrl, mCookies);
  }

  public WebServiceResponse sendServiceRequest(final WebServiceRequest pRequest)
  {
    if (mInvocationSupport != null) {
      return (WebServiceResponse) mInvocationSupport.execute(pRequest);
    } else {
      return mWebServiceController.sendServiceRequest(pRequest);
    }
  }

  /**
   * {@inheritDoc}
   */
  public SearchResult findContent(final String pMimeType, final Query pQuery) {
    final WebServiceRequest request = new WebServiceRequest();
    request.setRequestMethod(METHOD_GET);
    request.setServiceUri(buildServiceUri(SERVICE_FIND, pMimeType));
    request.setCookies(mCookies);
    request.setAuthKey(mDAuthKey);
    // FIXME: pQuery needs to be validated first or this block should be aware
    // of NPE
    request.addParameter("query", pQuery.buildQuery().toString());

    // build pagination meta info.
    if (pQuery.getMaxRows() != null) {
      request.addParameter(PARAM_MAX, String.valueOf(pQuery.getMaxRows()));
    }

    if (pQuery.getSkipRows() != null) {
      request.addParameter(PARAM_OFFSET, String.valueOf(pQuery.getSkipRows()));
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Sending /find/ request through web service request - " +
                request);
    }

    // Received response from web service.
    final WebServiceResponse response = sendServiceRequest(request);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Received /find/ response through web service - " + response);
    }
    
    // process response with appropriate processor.
    if (response != null) {
      return processResponse(pMimeType, response, SearchResult.class);
    } else {
      throw ServiceException.
            aNew(pQuery, "Request has ended with no response.");
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T extends ObjectBase> T getItem(final String pMimeType,
                                          final WebServiceRequest pRequest) {
    // set session key
    pRequest.setCookies(mCookies);
    pRequest.setAuthKey(mDAuthKey);
    final WebServiceResponse response = sendServiceRequest(pRequest);
    if (response != null) {
      return (T) processResponse(pMimeType, response, ObjectBase.class);
    } else {
      throw ServiceException.
            aNew(pRequest, "Request has ended with no response");
    }
  }

  /**
   * Find item by item id.
   * @param pMimeType web sercie response type.
   * @param pItemId item id.
   * @return item object.
   */
  public <T extends ObjectBase> T getItemById(final String pMimeType,
                                              final Integer pItemId) {
    final WebServiceRequest request = new WebServiceRequest();
    // set session key
    request.setCookies(mCookies);
    request.setAuthKey(mDAuthKey);
    request.setServiceUri(new StringBuilder()
        .append(SERVICE_GET).append(pItemId)
        .append(DOT).append(SERVICE_FORMATS.get(pMimeType)).toString());
    request.setRequestMethod(METHOD_GET);

    return (T) getItem(pMimeType, request);
  }

  /**
   * Process response with appropriate mime type based response processor.
   */
  private <T> T processResponse(final String pMimeType,
                                final WebServiceResponse pResponse,
                                final Class<T> pClass) {
    if (pMimeType.equalsIgnoreCase(XML_CONTENT_TYPE)) {
      return mXmlResponseProcessor.processResponse(pResponse, pClass);
    } else {
      throw new UnsupportedOperationException("Other mime type - " +
            pMimeType + " is not yet supported.");
    }
  }

  private String buildServiceUri(final String pUri, final String pMimeType) {
    return pUri + DOT + SERVICE_FORMATS.get(pMimeType);
  }

  /**
   * {@inheritDoc}
   */
  public <T extends GenericItem> ExecutionResponse saveItem(
      final String pMimeType, final T pItem) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Storing item object - " + pItem);
    }
    if (pMimeType == null || pItem == null) {
      throw new IllegalArgumentException("Mime type or item object " +
                                         "is not defined.");
    }
    // build web service request object.
    final WebServiceRequest request = buildWebServiceRequest(pMimeType, pItem);
    // send request to the web service.
    final WebServiceResponse response = sendServiceRequest(request);
    // verify response content.
    if (response == null) {
      throw ServiceException.aNew(request, "No response generated.");
    }

    // process response.
    return processResponse(pMimeType, response, ExecutionResponse.class);
  }

  /**
   * {@inheritDoc}
   */
  public ExecutionResponse addRelatedItems(final String pMime,
                                           final ObjectBase pItem) {
    final String format = SERVICE_FORMATS.get(pMime);

    // build web service request
    final WebServiceRequest request = new WebServiceRequest();
    request.setCookies(mCookies);
    request.setAuthKey(mDAuthKey);
    request.setServiceUri(new StringBuilder().append(SERVICE_ADD_RELATED_ITEMS).
                          append(DOT).append(format).toString());
    request.setRequestMethod(METHOD_POST);
    request.addParameter(SERVICE_PARAM_ITEM, buildRequestString(format, pItem));

    // execute remote service
    final WebServiceResponse response = sendServiceRequest(request);

    // send exception if no such response found.
    if (response == null) {
      throw ServiceException.aNew(request, "No response found");
    }

    // process the response
    return processResponse(pMime, response, ExecutionResponse.class);
  }

  private String buildRequestString(final String pFormat,
                                    final ObjectBase pItem) {
    final StringBuilder builder = new StringBuilder();
    builder.append("<" + XmlConstants.ELEMENT_REQUEST + ">");
    builder.append(SerializerFactory.
                  getInstance().serializeObject(pFormat, pItem));
    builder.append("</" + XmlConstants.ELEMENT_REQUEST + ">");
    return builder.toString();
  }

  private <T extends GenericItem> WebServiceRequest buildWebServiceRequest(
      final String pMimeType, final T pItem) {
    final String format = SERVICE_FORMATS.get(pMimeType);
    final WebServiceRequest request = new WebServiceRequest();
    // set session key
    request.setCookies(mCookies);
    request.setAuthKey(mDAuthKey);
    request.setRequestMethod(METHOD_POST);
    if (pItem.getId() == null) {
      request.setServiceUri(
          SERVICE_SAVE + DOT + format);
    } else {
      request.setServiceUri(
          SERVICE_UPDATE + DOT + format);
    }

    // set parameters
    final Map<String, String> parameters = new HashMap<String, String>();
    parameters.put(SERVICE_PARAM_ITEM, formatObject(format, pItem));
    request.setParameters(parameters);

    // return newly built request.
    return request;
  }

  private <T extends GenericItem> String formatObject(final String pFormat,
                                                      final T pItem) {
    final StringBuilder builder = new StringBuilder();
    builder.append(XmlConstants.ELEMENT_START).
            append(XmlConstants.ELEMENT_REQUEST).
            append(XmlConstants.ELEMENT_END);
    builder.append(SerializerFactory.getInstance().
            serializeObject(pFormat, pItem));
    builder.append(XmlConstants.ELEMENT_START).
            append(SLASH).
            append(XmlConstants.ELEMENT_REQUEST).
            append(XmlConstants.ELEMENT_END);
    return builder.toString();
  }

  /**
   * {@inheritDoc}
   */
  public ExecutionResponse deleteItem(final String pMimeType,
                                      final Integer pItemId) {
    final WebServiceRequest request = new WebServiceRequest();
    request.setCookies(mCookies);
    request.setAuthKey(mDAuthKey);
    request.setRequestMethod(METHOD_DELETE);
    request.setServiceUri(
        new StringBuilder().append(SERVICE_DELETE).
            append(String.valueOf(pItemId)).append(DOT).
            append(SERVICE_FORMATS.get(pMimeType)).toString());

    final WebServiceResponse response = sendServiceRequest(request);
    if (response == null) {
      throw ServiceException.aNew(request, "Response is returned empty.");
    }

    return processResponse(pMimeType, response, ExecutionResponse.class);
  }

  /**
   * {@inheritDoc}
   */
  public ExecutionResponse login(final String pMimeType,
                                 final String pUser,
                                 final String pPassword) {
    // build web service request object
    final WebServiceRequest request = new WebServiceRequest();
    request.setRequestMethod(METHOD_GET);
    final String uri = new StringBuilder()
        .append(SERVICE_LOGIN).append(DOT)
        .append(SERVICE_FORMATS.get(pMimeType)).toString();
    LOG.debug("Prepared uri - " + uri);
    request.setServiceUri(uri);

    // set parameters
    final Map<String, String> params = new HashMap<String, String>();
    params.put(SERVICE_PARAM_USER, pUser);
    params.put(SERVICE_PARAM_PASSWORD, pPassword);
    request.setParameters(params);

    final WebServiceResponse response = sendServiceRequest(request);
    if (response == null) {
      throw ServiceException.aNew(request, "Response is returned empty.");
    }

    final ExecutionResponse executionResponse =
        processResponse(pMimeType, response, ExecutionResponse.class);
    if (executionResponse != null && executionResponse.getState()) {
      mDAuthKey = executionResponse.getAuthKey();
      mCookies = response.getCookies();
      if (LOG.isDebugEnabled()) {
        LOG.debug("Session key - " + mDAuthKey);
      }
    }
    return executionResponse;
  }

  /**
   * {@inheritDoc}
   */
  public ExecutionResponse register(final String pMimeType, final User pUser) {
    // build web service request
    final WebServiceRequest request = new WebServiceRequest();
    request.setCookies(mCookies);
    request.setAuthKey(mDAuthKey);
    // find format
    final String format = SERVICE_FORMATS.get(pMimeType);
    if (pUser.isAdmin()) {
      request.setServiceUri(
          new StringBuilder().append(SERVICE_REGISTER_ADMIN).append(DOT).
              append(format).toString());
      // serialize content
      final String serializedContent =
          SerializerFactory.getInstance().serializeObject(format, pUser);
      final StringBuilder requestString = new StringBuilder();
      requestString.append("<" + XmlConstants.ELEMENT_REQUEST + ">");
      requestString.append(serializedContent);
      requestString.append("</" + XmlConstants.ELEMENT_REQUEST + ">");
      request.addParameter(SERVICE_PARAM_ADMIN, requestString.toString());
    } else {
      request.setServiceUri(
          new StringBuilder().append(SERVICE_REGISTER_USER).append(DOT).
              append(format).toString());
      final String serializedContent =
          SerializerFactory.getInstance().serializeObject(format, pUser);
      final StringBuilder requestString = new StringBuilder();
      requestString.append("<" + XmlConstants.ELEMENT_REQUEST + ">");
      requestString.append(serializedContent);
      requestString.append("</" + XmlConstants.ELEMENT_REQUEST + ">");
      request.addParameter(SERVICE_PARAM_USER, requestString.toString());
    }
    request.setRequestMethod(METHOD_POST);

    // execute the request
    final WebServiceResponse response = sendServiceRequest(request);
    if (response == null) {
      throw ServiceException.aNew(request, "Response is returned empty.");
    }

    // process response
    return processResponse(pMimeType, response, ExecutionResponse.class);
  }

  public SearchResult getRelatedItems(final String pMimeType, 
                                      final String pRelationType,
                                      final Integer pItemId,
                                      final Integer pSkip,
                                      final Integer pMaxRows) {
    LOG.debug("Retrieve the list of related items.");
    final WebServiceRequest request = new WebServiceRequest();
    request.setCookies(mCookies);
    request.setAuthKey(mDAuthKey);
    final String format = SERVICE_FORMATS.get(pMimeType);
    request.setServiceUri(new StringBuilder()
        .append(SERVICE_FIND_RELATED_ITEMS).append(SLASH).append(pRelationType)
        .append(AND).append(String.valueOf(pItemId)).append(DOT)
        .append(format).toString());
    request.setRequestMethod(METHOD_POST);
    request.addParameter(WebConstants.PARAM_OFFSET, String.valueOf(pSkip));
    request.addParameter(WebConstants.PARAM_MAX, String.valueOf(pMaxRows));

    // send request
    final WebServiceResponse response = sendServiceRequest(request);
    if (response == null) {
      throw ServiceException.aNew(request, "Invalid response returned.", null);
    } else {
      return processResponse(pMimeType, response, SearchResult.class);
    }
  }



  /**
   * {@inheritDoc}
   */
  public <T extends ObjectBase> T getItemByIdAndRelatedItems(
      final String pMimeType, final Integer pItemId,
      final String pRelationTypes, final Integer pSkip,
      final Integer pMaxValue) {
    // build web service request.
    final WebServiceRequest request = new WebServiceRequest();
    // set session key
    request.setCookies(mCookies);
    request.setAuthKey(mDAuthKey);
    request.setServiceUri(new StringBuilder()
        .append(SERVICE_GET).append(pItemId)
        .append(DOT).append(SERVICE_FORMATS.get(pMimeType)).toString());
    request.setRequestMethod(METHOD_GET);

    // enable related items loading
    request.addParameter(PARAM_LOAD_RELATED_ITEMS, String.valueOf(true));

    // set optional parameters
    // set relation types.
    if (pRelationTypes != null) {
      request.addParameter(PARAM_RELATION_TYPES, pRelationTypes);
    }

    // set number of skipped rows
    if (pSkip != null) {
      request.addParameter(PARAM_OFFSET, String.valueOf(pSkip));
    }

    // set number of max rows
    if (pMaxValue != null) {
      request.addParameter(PARAM_MAX, String.valueOf(pMaxValue));
    }

    return (T) getItem(pMimeType, request);
  }

  public String getCookies() {
    return mCookies;
  }

  public String getBaseUrl() {
    return mBaseUrl;
  }

  /**
   * {@inheritDoc}
   */
  public ExecutionResponse deleteRelatedItems(final String pMime, 
                                              final GenericItem pItem) {
    final String format = SERVICE_FORMATS.get(pMime);

    // build web service request
    final WebServiceRequest request = new WebServiceRequest();
    request.setCookies(mCookies);
    request.setAuthKey(mDAuthKey);
    request.setServiceUri(new StringBuilder().
                          append(SERVICE_DELETE_RELATED_ITEMS).
                          append(DOT).append(format).toString());
    request.setRequestMethod(METHOD_POST);
    request.addParameter(SERVICE_PARAM_ITEM, buildRequestString(format, pItem));

    // execute remote service
    final WebServiceResponse response = sendServiceRequest(request);

    // send exception if no such response found.
    if (response == null) {
      throw ServiceException.aNew(request, "No response found");
    }

    // process the response
    return processResponse(pMime, response, ExecutionResponse.class);
  }
}

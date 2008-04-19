/*
 * $Id: XmlResponseProcessorImpl.java 250 2008-01-07 10:18:29Z hasan $
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
package com.ideabase.repository.webservice.client.processor.impl;

import com.ideabase.repository.webservice.client.processor.ResponseProcessor;
import com.ideabase.repository.webservice.client.*;
import static com.ideabase.repository.common.XmlConstants.*;
import com.ideabase.repository.common.exception.ServiceException;
import com.ideabase.repository.common.DateFormatter;
import com.ideabase.repository.common.serializer.SerializerFactory;
import com.ideabase.repository.common.object.ObjectBase;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.sql.Timestamp;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * Implementation of xml typed web service response {@see ResponseProcessor}.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class XmlResponseProcessorImpl implements ResponseProcessor {

  private final Logger LOG = LogManager.getLogger(getClass());
  
  private static final String URI_SEPARATOR = "/";
  private static final String METHOD_GET = "GET";
  private static final String TRUE = "true";

  /**
   * {@inheritDoc}
   */
  public <T> T processResponse(final WebServiceResponse pResponse,
                               final Class<T> pExpectedClass) {

    try {
      // parse xml document
      final Builder builder = new Builder();
      final Document document = builder.build(findReader(pResponse));
      final Element rootElement = document.getRootElement();

      // convert response to an object.
      if (pExpectedClass == SearchResult.class) {
        // process search result
        return (T) processSearchResult(rootElement);
      }
      // find single item object
      else if (pExpectedClass == ObjectBase.class) {
        return (T) processItem(rootElement);
      }
      // find execution response object
      else if (pExpectedClass == ExecutionResponse.class) {
        return (T) processExecutionResponse(rootElement);
      }
    } catch (Throwable e) {
      throw ServiceException.aNew(pResponse, "Failed to process response.", e);
    }
    return null;
  }

  private ExecutionResponse processExecutionResponse(final Element pRootElement)
  {
    final ExecutionResponse response = new ExecutionResponse();
    response.setState(findBoolean(pRootElement.getAttributeValue(ATTR_STATE)));
    response.setMessage(findValue(pRootElement, ELEMENT_MESSAGE));
    response.setItemRef(findItemRef(pRootElement));
    response.setAuthKey(findValue(pRootElement, ELEMENT_AUTH_TOKEN));
    return response;
  }

  private List<ItemRef> findItemRef(final Element pRootElement) {
    final Elements itemElements = pRootElement.getChildElements(ELEMENT_ITEM);
    if (itemElements != null && itemElements.size() > 0) {
      final List<ItemRef> itemRefs = new ArrayList<ItemRef>();
      for (int i = 0; i < itemElements.size(); i++) {
        final Element itemElement = itemElements.get(i);
        final String resourceUri = itemElement.getValue();
        // find item id by spliting string with "/" and take the last one.
        final String[] split = resourceUri.split(URI_SEPARATOR);
        final Integer itemId = Integer.valueOf(split[split.length - 1]);
        // build item ref. object
        final ItemRef itemRef = new ItemRef();
        itemRef.setId(itemId).
                setItemContentRequest(buildWebServiceRequest(resourceUri));
        itemRefs.add(itemRef);
      }
      return itemRefs;
    }
    return Collections.emptyList();
  }

  private boolean findBoolean(final String pAttributeValue) {
    return pAttributeValue.equalsIgnoreCase(TRUE);
  }

  private Object processItem(final Element pRootElement) throws ParseException {
    final Element itemElement = pRootElement.getFirstChildElement(ELEMENT_ITEM);
    if (itemElement != null) {
      return SerializerFactory.getInstance().
             deserializeObject(SerializerFactory.XML, itemElement.toXML());
    } else {
      return null;
    }
  }

  private Object processSearchResult(final Element pRootElement) {
    final SearchResult result = new SearchResult();
    result.setMaxRows(
        Integer.parseInt(findValue(pRootElement, ELEMENT_MAX_ROWS)));
    result.setPageCount(
        Integer.parseInt(findValue(pRootElement, ELEMENT_PAGES_COUNT)));
    result.setItems(findItems(pRootElement, ELEMENT_ITEMS));
    return result;
  }

  private List<ItemRef> findItems(final Element pRootElement,
                                  final String pElementName) {
    LOG.debug("Find items - " + pRootElement +
              " - element name - " + pElementName);
    final Element itemsElement =
        pRootElement.getFirstChildElement(pElementName);
    return findItems(itemsElement);
  }

  private List<ItemRef> findItems(final Element pItemsElement) {
    LOG.debug("Items element - "  + pItemsElement);
    if (pItemsElement != null) {
      final Elements itemElements =
          pItemsElement.getChildElements(ELEMENT_ITEM);
      if (itemElements != null && itemElements.size() > 0) {
        final List<ItemRef> itemRefs = new ArrayList<ItemRef>();
        for (int i = 0; i < itemElements.size(); i++) {
          final Element itemElement = itemElements.get(i);
          final String resourceUri = itemElement.getValue();
          // Every itemRef response comes with a predefined formatted form.
          // for example: /service/get/32323
          // here /server/get is prefix and 32323 is itemRef id.
          // we need to parse the integer itemRef id.
          // the quick trick is to split the string with "/" and read the last
          // one.
          final String[] split = resourceUri.split(URI_SEPARATOR);
          // If any exception raised it does mean the invalid response format.
          final Integer itemId = Integer.valueOf(split[split.length - 1]);
          // build the itemRef object.
          final ItemRef itemRef = new ItemRef();
          itemRef.setId(itemId);
          itemRef.setItemContentRequest(buildWebServiceRequest(resourceUri));
          // add itemRef to the list of itemRef list.
          itemRefs.add(itemRef);
        }
        return itemRefs;
      }
    }
    return Collections.emptyList();
  }

  private WebServiceRequest buildWebServiceRequest(final String pResourceUri) {
    final WebServiceRequest request = new WebServiceRequest() {{
      setRequestMethod(METHOD_GET);
      setServiceUri(pResourceUri);
      // Session key is set from the request handler.
    }};
    return request;
  }

  private String findValue(final Element pRootElement,
                           final String pElementName) {
    final Element element = pRootElement.getFirstChildElement(pElementName);
    if (element != null) {
      return element.getValue();
    }
    return null;
  }

  private Reader findReader(final WebServiceResponse pResponse) {
    if (pResponse.getResponseContent() == null) {
      throw new IllegalArgumentException("No response content found.");
    }
    return new StringReader(pResponse.getResponseContent());
  }
}

/* $Id: XmlObjectBaseSerializer.java 260 2008-03-19 05:38:06Z hasan $ */
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
package com.ideabase.repository.common.serializer.impl;

import com.ideabase.repository.common.CommonConstants;
import com.ideabase.repository.common.DateFormatter;
import static com.ideabase.repository.common.DateFormatter.parseTimestamp;
import static com.ideabase.repository.common.XmlConstants.*;
import com.ideabase.repository.common.exception.ServiceException;
import com.ideabase.repository.common.object.GenericItem;
import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.common.serializer.ObjectBaseSerializer;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import java.io.StringReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * Convert {@see ObjectBase} object to xml serialized form.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class XmlObjectBaseSerializer implements ObjectBaseSerializer {

  public <T extends ObjectBase> String serializeObject(final T pObject) {
    if (pObject == null) {
      throw new IllegalArgumentException("No null value is accepted.");
    }
    final StringBuilder builder = new StringBuilder();
    // add root <item> element
    startElement(builder, ELEMENT_ITEM);
    // set target class
    addElement(builder, ELEMENT_TARGET_CLASS,
               pObject.getClass().getSimpleName());
    // add id
    if (pObject.getId() != null) {
      addElement(builder, ELEMENT_ID, pObject.getId());
    }
    // add title
    final String title = pObject.getTitle();
    if (title != null) {
      addElement(builder, ELEMENT_TITLE, title, true);
    }

    // add created on
    if (pObject.getCreatedOn() != null) {
      addElement(builder, ELEMENT_CREATED_ON, pObject.getCreatedOn(), true);
    }
    // add last updated on
    if (pObject.getLastUpdatedOn() != null) {
      addElement(builder, ELEMENT_LAST_UPDATED_ON,
                 pObject.getLastUpdatedOn(), true);
    }
    // add fields
    if (pObject.getFields() != null && !pObject.getFields().isEmpty()) {
      addElements(builder, ELEMENT_FIELDS, ELEMENT_FIELD, pObject.getFields());
    }
    // add related objects
    if (pObject.getRelationTypes() != null
        && !pObject.getRelationTypes().isEmpty()) {
      addRelatedItems(builder, pObject);
    }

    // add index repository
    final String indexRepository;
    if (pObject.getIndexRepository() == null) {
      indexRepository = CommonConstants.INDEX_DEFAULT;
    } else {
      indexRepository = pObject.getIndexRepository();
    }
    addElement(builder, ELEMENT_INDEX_REPOSITORY, indexRepository);

    // end root </item> element
    endElement(builder, ELEMENT_ITEM);

    return builder.toString();
  }

  public <T extends ObjectBase> T deserializeObject(final String pContent) {
    if (pContent == null) {
      throw new IllegalArgumentException("Content must be none empty.");
    }
    try {
      final Builder builder = new Builder();
      final StringReader reader = new StringReader(pContent);
      final Document document = builder.build(reader);
      final Element rootElement = document.getRootElement();

      // look for <item> element
      if (!ELEMENT_ITEM.equals(rootElement.getQualifiedName())) {
        throw new IllegalArgumentException(
              "Content doesn't hold any <item/> element.");
      }
      // look for target object type
      final ObjectBase targetObject = findTargetObject(rootElement);
      populateTargetObject(rootElement, targetObject);
      return (T) targetObject;
    } catch (Exception e) {
      throw ServiceException.aNew(pContent, "Invalid content found", e);
    }
  }

  private void populateTargetObject(final Element pRootElement,
                                    final ObjectBase pTargetObject)
      throws ParseException {
    // find id
    pTargetObject.setId(findInteger(pRootElement, ELEMENT_ID));
    // find title
    pTargetObject.setTitle(findValue(pRootElement, ELEMENT_TITLE));
    // find created on
    pTargetObject.setCreatedOn(findTimestamp(pRootElement, ELEMENT_CREATED_ON));
    // find last modified on
    pTargetObject.setLastUpdatedOn(
                  findTimestamp(pRootElement, ELEMENT_LAST_UPDATED_ON));
    // find fields
    pTargetObject.setFields(findFields(pRootElement));
    // find related items
    pTargetObject.setRelatedItemsMap(findRelatedItems(pRootElement));
  }

  private Integer findInteger(final Element pRootElement,
                              final String pElementName) {
    final String value = findValue(pRootElement, pElementName);
    if (value != null) {
      return Integer.parseInt(value);
    }
    return null;
  }

  private Map<String, List<Integer>> findRelatedItems(
      final Element pRootElement) {
    final Element relatedItemsElement =
        pRootElement.getFirstChildElement(ELEMENT_RELATED_ITEMS);
    if (relatedItemsElement != null) {
      final Elements childElements = relatedItemsElement.getChildElements();
      if (childElements != null && childElements.size() > 0) {
        final Map<String, List<Integer>> relatedItems =
            new HashMap<String, List<Integer>>();
        for (int i = 0; i < childElements.size(); i++) {
          final Element childElement = childElements.get(i);
          final String relationTypeName = childElement.getQualifiedName();
          relatedItems.put(relationTypeName, findItems(childElement));
        }
        return relatedItems;
      }
    }
    return Collections.emptyMap();
  }

  private List<Integer> findItems(final Element pChildElement) {
    final Elements itemElements = pChildElement.getChildElements(ELEMENT_ITEM);
    if (itemElements != null && itemElements.size() > 0) {
      final List<Integer> items = new ArrayList<Integer>();
      for (int i = 0; i < itemElements.size(); i++) {
        final Element item = itemElements.get(i);
        final Integer itemId = Integer.valueOf(item.getValue());
        items.add(itemId);
      }
      return items;
    }
    return Collections.emptyList();
  }

  private Map<String, String> findFields(final Element pRootElement) {
    final Element fieldsElement =
        pRootElement.getFirstChildElement(ELEMENT_FIELDS);
    if (fieldsElement != null) {
      final Elements fieldElements =
          fieldsElement.getChildElements(ELEMENT_FIELD);
      if (fieldElements != null && fieldElements.size() > 0) {
        final Map<String, String> fields = new HashMap<String, String>();
        for (int i = 0; i < fieldElements.size(); i++) {
          final Element fieldElement = fieldElements.get(i);
          final String name = fieldElement.getAttributeValue(ATTR_NAME);
          final String value = fieldElement.getValue();
          fields.put(name, value);
        }
        return fields;
      }
    }
    return Collections.emptyMap();
  }

  private Timestamp findTimestamp(final Element pRootElement,
                                  final String pELementName)
      throws ParseException {
    final String value = findValue(pRootElement, pELementName);
    if (value != null) {
      return parseTimestamp(value);
    }
    return null;
  }

  private String findValue(final Element pRootElement,
                           final String pElementName) {
    final Element element = pRootElement.getFirstChildElement(pElementName);
    if (element != null) {
      return element.getValue();
    }
    return null;
  }

  private ObjectBase findTargetObject(final Element pRootElement) {
    final Element targetObject =
        pRootElement.getFirstChildElement(ELEMENT_TARGET_CLASS);
    if (targetObject != null) {
      if (TARGET_CLASS_USER.equalsIgnoreCase(targetObject.getValue())) {
        return new User();
      }
    }
    return new GenericItem();
  }

  private <T extends ObjectBase> void addRelatedItems(
      final StringBuilder pBuilder, final T pObject) {
    startElement(pBuilder, ELEMENT_RELATED_ITEMS);
    for (final String relationType : pObject.getRelationTypes()) {
      // relation type element started
      startElement(pBuilder, relationType);
      final List<Integer> relatedItems =
          pObject.getRelatedItemsByRelationType(relationType);
      if (relatedItems != null && !relatedItems.isEmpty()) {
        for (final Integer relatedItemId : relatedItems) {
          addElement(pBuilder, ELEMENT_ITEM, relatedItemId);
        }
      }
      // relation type element ended.
      endElement(pBuilder, relationType);
    }
    endElement(pBuilder, ELEMENT_RELATED_ITEMS);
  }

  private void addElements(final StringBuilder pBuilder,
                           final String pElementRoot,
                           final String pElementChild,
                           final Map<String, String> pValues) {
    startElement(pBuilder, pElementRoot);
    for (final Map.Entry<String, String> entry : pValues.entrySet()) {
      final String key = entry.getKey();
      final String value = entry.getValue();
      pBuilder.append("<").append(pElementChild).append(" ").append(ATTR_NAME).
          append("=\"").append(key).append("\"").append(">").
          append(BLOCK_CDATA_START).append(value).append(BLOCK_CDATA_END);
      endElement(pBuilder, pElementChild);
    }
    endElement(pBuilder, pElementRoot);
  }

  private void addElement(final StringBuilder pBuilder,
                          final String pElementName,
                          final Object pValue, final Boolean... pOptions) {
    startElement(pBuilder, pElementName, pOptions);
    if (pValue instanceof Timestamp) {
      pBuilder.append(DateFormatter.formatDate((Timestamp) pValue));
    } else {
      pBuilder.append(String.valueOf(pValue));
    }
    endElement(pBuilder, pElementName, pOptions);
  }

  private void endElement(final StringBuilder pBuilder,
                          final String pElementName,
                          final Boolean... pOptions) {
    if (pOptions != null && pOptions.length > 0 && pOptions[0]) {
      pBuilder.append(BLOCK_CDATA_END);
    }
    pBuilder.append("</").append(pElementName).append(">");
  }

  private void startElement(final StringBuilder pBuilder,
                            final String pElementName,
                            final Boolean... pOptions) {
    pBuilder.append("<").append(pElementName).append(">");
    if (pOptions != null && pOptions.length > 0 && pOptions[0]) {
      pBuilder.append(BLOCK_CDATA_START);
    }
  }
}

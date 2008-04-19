/**
 * $Id$
 * *****************************************************************************
 *    Copyright (C) 2005 - 2007 somewhere in .Net ltd.
 *    All Rights Reserved.  No use, copying or distribution of this
 *    work may be made except in accordance with a valid license
 *    agreement from somewhere in .Net LTD.  This notice must be included on
 *    all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 * *****************************************************************************
 */

package com.ideabase.repository.common.serializer.impl;

import com.ideabase.repository.common.CommonConstants;
import static com.ideabase.repository.common.XmlConstants.*;
import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.common.serializer.ObjectBaseSerializer;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Serialize object into php
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class PHPObjectBaseSerializer implements ObjectBaseSerializer {

  public <T extends ObjectBase> String serializeObject(final T pObject) {
    final StringBuilder builder = new StringBuilder();
    builder.append("array(");
    addItem(builder, ELEMENT_TARGET_CLASS,
            pObject.getClass().getSimpleName(), true, true);
    addItem(builder, ELEMENT_ID, String.valueOf(pObject.getId()), true, true);
    addItem(builder, ELEMENT_TITLE, pObject.getTitle(), true, true);
    addItem(builder, ELEMENT_CREATED_ON,
            formattedDate(pObject.getCreatedOn()), true, false);
    addItem(builder, ELEMENT_LAST_UPDATED_ON,
            formattedDate(pObject.getLastUpdatedOn()), true, false);
    addItem(builder, ELEMENT_INDEX_REPOSITORY,
            pObject.getIndexRepository(), true, true);
    addItems(builder, ELEMENT_FIELDS, pObject.getFields());
    builder.append(")");
    return builder.toString();
  }

  private void addItems(final StringBuilder pBuilder,
                        final String pItemName,
                        final Map<String, String> pFields) {
    pBuilder.append("'").append(pItemName).append("' => ");
    if (pFields != null && !pFields.isEmpty()) {
      pBuilder.append("array(");
      boolean canAddComma = pFields.size() > 1;
      int mapSize = pFields.size();
      int currentIndex = 0;
      for (final Map.Entry<String, String> entry : pFields.entrySet()) {
        if (currentIndex == (mapSize - 1)) {
          canAddComma = false;
        }
        addItem(pBuilder, entry.getKey(), entry.getValue(), canAddComma, true);
        currentIndex++;
      }
      pBuilder.append(")");
    } else {
      pBuilder.append("array()");
    }
  }

  private String formattedDate(final Timestamp pTimestamp) {
    if (pTimestamp != null) {
      final StringBuilder builder = new StringBuilder();
      builder.append("time(").append(pTimestamp.getTime()).append(")");
      return builder.toString();
    }
    return CommonConstants.EMPTY_STRING;
  }

  private void addItem(final StringBuilder pBuilder,
                       final String pItemName,
                       final String pValue, final boolean pComma,
                       final boolean pQuote) {
    pBuilder.append("'").append(pItemName).append("' => ");
    if (pQuote) {
      pBuilder.append("'");
      pBuilder.append(escapeIllegalText(pValue));
      pBuilder.append("'");
    } else {
      pBuilder.append(pValue);
    }

    if (pComma) {
      pBuilder.append(",");
    }
  }

  private String escapeIllegalText(final String pValue) {
    if (pValue != null) {
      return pValue.replaceAll("(['])", "\\\\$1");
    }
    return pValue;
  }

  public <T extends ObjectBase> T deserializeObject(final String pContent) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }
}

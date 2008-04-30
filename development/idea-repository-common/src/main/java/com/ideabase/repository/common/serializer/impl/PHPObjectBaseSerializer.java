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
import static com.ideabase.repository.common.StringUtil.*;
import static com.ideabase.repository.common.CommonConstants.*;
import static com.ideabase.repository.common.XmlConstants.*;
import static com.ideabase.repository.common.PHPConstants.*;
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
    builder.append(ARRAY_START);
    addItem(builder, ELEMENT_TARGET_CLASS,
            pObject.getClass().getSimpleName(), true, true);
    addItem(builder, ELEMENT_ID, String.valueOf(pObject.getId()), true, true);
    addItem(builder, ELEMENT_TITLE,
            escapeIllegalText(pObject.getTitle()), true, true);
    addItem(builder, ELEMENT_CREATED_ON,
            formattedDate(pObject.getCreatedOn()), true, false);
    addItem(builder, ELEMENT_LAST_UPDATED_ON,
            formattedDate(pObject.getLastUpdatedOn()), true, false);

    String indexRepository = pObject.getIndexRepository();
    if (indexRepository == null) { indexRepository = INDEX_DEFAULT; }
    
    addItem(builder, ELEMENT_INDEX_REPOSITORY,
            indexRepository, true, true);
    addItems(builder, ELEMENT_FIELDS, pObject.getFields());
    builder.append(END_BRACE);

    return builder.toString();
  }

  private void addItems(final StringBuilder pBuilder,
                        final String pElementName,
                        final Map<String, String> pFields) {

    pBuilder.append(QUOTE).
        append(escapeIllegalText(pElementName)).append(QUOTE).append(ASSIGN);

    if (pFields != null && !pFields.isEmpty()) {
      pBuilder.append(ARRAY_START);
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
      pBuilder.append(END_BRACE);
    } else {
      pBuilder.append(ARRAY_EMPTY);
    }
  }

  private String formattedDate(final Timestamp pTimestamp) {
    if (pTimestamp != null) {
      final StringBuilder builder = new StringBuilder();
      builder.append(TIME_START).append(pTimestamp.getTime()).append(END_BRACE);
      return builder.toString();
    }
    return CommonConstants.EMPTY_STRING;
  }

  private void addItem(final StringBuilder pBuilder,
                       final String pItemName,
                       final String pValue, final boolean pComma,
                       final boolean pQuote) {
    pBuilder.append(QUOTE).
        append(escapeIllegalText(pItemName)).append(QUOTE).append(ASSIGN);
    if (pQuote) {
      pBuilder.append(QUOTE);
      pBuilder.append(escapeIllegalText(pValue));
      pBuilder.append(QUOTE);

    } else {
      pBuilder.append(pValue);
    }

    if (pComma) {
      pBuilder.append(COMMA);
    }
  }

  public <T extends ObjectBase> T deserializeObject(final String pContent) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }
}

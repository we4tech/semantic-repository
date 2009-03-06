/* $Id$ */
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
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 ******************************************************************************
*/
package com.ideabase.repository.webservice.object;

import com.ideabase.repository.common.formatter.XmlSerializable;
import com.ideabase.repository.common.formatter.PHPSerializable;

import java.util.List;
import java.util.ArrayList;

import static com.ideabase.repository.common.XmlConstants.*;
import static com.ideabase.repository.common.PHPConstants.*;
import static com.ideabase.repository.common.CommonConstants.*;
import static com.ideabase.repository.common.StringUtil.*;
import com.ideabase.repository.common.object.Hit;
import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.common.object.AbstractObjectBase;
import com.ideabase.repository.common.object.GenericItem;
import com.ideabase.repository.api.RepositoryService;

/**
 * Convert object to Xml, JSON, text or other formats.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class HitListObject implements XmlSerializable, PHPSerializable {

  private final List<Hit> mItems;
  private static final String URI_PREFIX = "/service/get/";
  private final List<String> mSelectableFields = new ArrayList();
  private RepositoryService mRepositoryService;

  /**
   * Default constructor, it requires a list of {@code Integer} items.
   * @param pItems list of {@code Integer} items.
   */
  public HitListObject(final List<Hit> pItems) {
    if (pItems == null || pItems.isEmpty()) {
      throw new IllegalArgumentException("Invalid list of integer items. " +
        "this argument must be not null");
    }
    mItems = pItems;
  }

  public void addSelectableField(final String pField) {
    mSelectableFields.add(pField);
  }

  public void toXml(final StringBuilder pBuilder) {
    final boolean selectableFieldsAreDefined = !mSelectableFields.isEmpty();
    for (final Hit hit : mItems) {
      pBuilder.append(ELEMENT_START).append(ELEMENT_ITEM)
          .append(SPACE).append(ATTR_SCORE)
          .append("='").append(String.valueOf(hit.getScore())).append("' ")
          .append(ATTR_URI).append("='")
          .append(URI_PREFIX).append(String.valueOf(hit.getId()))
          .append("'").append(ELEMENT_END);

      // add selectable fields is exists
      pBuilder.append(ELEMENT_START).append(ELEMENT_FIELDS).append(ELEMENT_END);
      if (selectableFieldsAreDefined) {
        final GenericItem item = mRepositoryService.
                                       getItem(hit.getId(), GenericItem.class);
        for(final String field : mSelectableFields) {
          final String value = item.getField(field);
          if (value != null) {
            pBuilder.append(ELEMENT_START).append(ELEMENT_FIELD).append(SPACE).
                     append(ATTR_NAME).append("='").
                     append(escapeIllegalText(field)).
                     append(QUOTE).append(ELEMENT_END);
            pBuilder.append(BLOCK_CDATA_START).
                     append(String.valueOf(value)).
                     append(BLOCK_CDATA_END);
            pBuilder.append(ELEMENT_START).append("/").append(ELEMENT_FIELD).
                     append(ELEMENT_END);
          }
        }
      }
      pBuilder.append(ELEMENT_START).append(SLASH).append(ELEMENT_FIELDS).
               append(ELEMENT_END);
      pBuilder.append(ELEMENT_START).append(SLASH).append(ELEMENT_ITEM).
               append(ELEMENT_END);
    }
  }

  public void toPHP(final StringBuilder pBuilder) {
    final boolean selectableFieldsAreDefined = !mSelectableFields.isEmpty();
    boolean started = false;
    pBuilder.append(ARRAY_START);
    for (final Hit hit : mItems) {
      if (started) {
        pBuilder.append(COMMA);
      } else {
        started = true;
      }
      pBuilder.append(ARRAY_START).append(QUOTE).append(URI_PREFIX).
          append(String.valueOf(hit.getId())).append("', ");
      pBuilder.append(String.valueOf(hit.getScore())).
               append(COMMA).append(ARRAY_START);

      // iterating thorugh all defiend selectable fields
      if (selectableFieldsAreDefined) {
        final GenericItem item =
            mRepositoryService.getItem(hit.getId(), GenericItem.class);

        boolean commaNeeded = false;
        for (final String field : mSelectableFields) {
          final String value = item.getField(field);
          if (value != null) {
            if (commaNeeded) {
              pBuilder.append(COMMA);
            } else {
              commaNeeded = true;
            }
            pBuilder.append(QUOTE).
                     append(escapeIllegalText(field)).append(QUOTE).
                     append(ASSIGN).append(QUOTE).
                     append(escapeIllegalText(value)).append(QUOTE);
          }
        }
      }
      pBuilder.append(END_BRACE).append(END_BRACE);
    }
    pBuilder.append(END_BRACE);
  }

  public void setRepositoryService(final RepositoryService pRepositoryService) {
    mRepositoryService = pRepositoryService;
  }
}

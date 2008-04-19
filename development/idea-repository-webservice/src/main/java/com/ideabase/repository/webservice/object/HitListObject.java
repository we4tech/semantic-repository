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
import static com.ideabase.repository.common.XmlConstants.*;
import com.ideabase.repository.common.object.Hit;

/**
 * Convert object to Xml, JSON, text or other formats.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class HitListObject implements XmlSerializable, PHPSerializable {

  private final List<Hit> mItems;
  private static final String URI_PREFIX = "/service/get/";

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

  public void toXml(final StringBuilder pBuilder) {
    for (final Hit hit : mItems) {
      pBuilder.append(ELEMENT_START).append(ELEMENT_ITEM)
          .append(" ").append(ATTR_SCORE)
          .append("='").append(String.valueOf(hit.getScore())).append("'")
          .append(ELEMENT_END);
      pBuilder.append(URI_PREFIX).append(String.valueOf(hit.getId()));
      pBuilder.append(ELEMENT_START).append("/").append(ELEMENT_ITEM).
               append(ELEMENT_END);
    }
  }

  public void toPHP(final StringBuilder pBuilder) {
    boolean started = false;
    pBuilder.append("array(");
    for (final Hit hit : mItems) {
      if (started) {
        pBuilder.append(",");
      } else {
        started = true;
      }
      pBuilder.append("array('").append(URI_PREFIX).
          append(String.valueOf(hit.getId())).append("', ");
      pBuilder.append(String.valueOf(hit.getScore())).append(")");
    }
    pBuilder.append(")");
  }
}

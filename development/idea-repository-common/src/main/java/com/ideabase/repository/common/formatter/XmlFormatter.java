/* $Id: XmlFormatter.java 260 2008-03-19 05:38:06Z hasan $ */
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
package com.ideabase.repository.common.formatter;

import com.ideabase.repository.common.exception.ServiceException;

import java.util.List;
import static com.ideabase.repository.common.XmlConstants.*;

/**
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class XmlFormatter implements ResponseFormatter {

  /**
   * {@inheritDoc}
   */
  public String format(final boolean pState, final List<Object> pResponseObjects) {
    if (pResponseObjects == null || pResponseObjects.isEmpty()) {
      throw new ServiceException(null, "No such response object. you must " +
          "define a list of objects before invoking *Formatter::format");
    }

    final StringBuilder builder = new StringBuilder();
    // <response state="true|false">
    builder.append(ELEMENT_START).append(ELEMENT_RESPONSE).
            append(" ").append(ATTR_STATE).append("=\"").append(pState).
            append("\"").append(ELEMENT_END);

    buildXmlFromList(builder, pResponseObjects);

    builder.append(ELEMENT_START).append("/").append(ELEMENT_RESPONSE).
            append(ELEMENT_END);
    // </response>
    return builder.toString();
  }

  private void buildXmlFromList(final StringBuilder pBuilder,
                                final List<Object> pResponseObjects) {
    for (final Object object : pResponseObjects) {
      XmlSerializable xmlSerializable = (XmlSerializable) object;
      xmlSerializable.toXml(pBuilder);
    }
  }
}

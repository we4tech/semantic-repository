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
package com.ideabase.repository.webservice.helper;

import java.util.List;
import java.util.ArrayList;

import static com.ideabase.repository.common.XmlConstants.*;
import static com.ideabase.repository.common.PHPConstants.*;
import static com.ideabase.repository.common.CommonConstants.*;
import static com.ideabase.repository.common.StringUtil.*;
import com.ideabase.repository.common.formatter.XmlSerializable;
import com.ideabase.repository.common.formatter.PHPSerializable;
import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.common.serializer.SerializerFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ResponseElement implements XmlSerializable, PHPSerializable {

  private static final Logger LOG = LogManager.getLogger(ResponseElement.class);
  private final String mName;
  private final Object mValue;
  private final List<ResponseElement> mResponseElements =
      new ArrayList<ResponseElement>();

  /**
   * Default constructor.
   * @param pName root element name
   * @param pValue element value object
   */
  public ResponseElement(final String pName, final Object pValue) {
    mName = pName;
    mValue = pValue;
  }

  public String getName() { return mName; }
  public Object getValue() { return mValue; }

  public ResponseElement addResponseElement(
      final ResponseElement pResponseElement) {
    mResponseElements.add(pResponseElement);
    return this;
  }

  public void removeResponseElement(final ResponseElement pResponseElement) {
    mResponseElements.remove(pResponseElement);
  }

  public void toXml(final StringBuilder pBuilder) {
    if (mValue instanceof ObjectBase) {
      final String serializedString = SerializerFactory.getInstance().
          serializeObject(SerializerFactory.XML, (ObjectBase) mValue);
      pBuilder.append(serializedString);
    } else if (mValue instanceof XmlSerializable) {
      pBuilder.append(ELEMENT_START).append(mName).
               append(ELEMENT_END);
      ((XmlSerializable) mValue).toXml(pBuilder);
      pBuilder.append(ELEMENT_START).append(SLASH).append(mName).
               append(ELEMENT_END);
    } else if (mValue instanceof List) {
      for (final Object value : (List) mValue) {
        pBuilder.append(ELEMENT_START).append(mName).
                 append(ELEMENT_END);
        pBuilder.append(BLOCK_CDATA_START);
        pBuilder.append(value);
        pBuilder.append(BLOCK_CDATA_END);
        pBuilder.append(ELEMENT_START).append(SLASH).append(mName).
                 append(ELEMENT_END);
      }
    } else {
      pBuilder.append(ELEMENT_START).append(mName).
               append(ELEMENT_END);
      pBuilder.append(BLOCK_CDATA_START);
      pBuilder.append(mValue);
      pBuilder.append(BLOCK_CDATA_END);
      pBuilder.append(ELEMENT_START).append(SLASH).append(mName).
               append(ELEMENT_END);
    }

    // Let's other response element be included.
    for (final ResponseElement responseElement : mResponseElements) {
      responseElement.toXml(pBuilder);
    }
  }

  public void toPHP(final StringBuilder pBuilder) {
    if (mValue instanceof ObjectBase) {
      pBuilder.append(SerializerFactory.getInstance().
          serializeObject(SerializerFactory.PHP, (ObjectBase) mValue));
      appendComma(pBuilder);
    }
    // php serialized value
    else if (mValue instanceof PHPSerializable) {
      pBuilder.append(QUOTE).append(mName).append(QUOTE).append(ASSIGN);
      ((PHPSerializable) mValue).toPHP(pBuilder);
      appendComma(pBuilder);
    }
    // list of values
    else if (mValue instanceof List) {
      pBuilder.append(QUOTE).append(mName).append(QUOTE).
               append(ASSIGN).append(ARRAY_START);
      boolean started = false;
      for (final Object value : (List) mValue) {
        if (started) {
          pBuilder.append(COMMA);
        } else {
          started = true;
        }
        pBuilder.append(QUOTE).
            append(escapeIllegalText(String.valueOf(value))).append(QUOTE);
      }
      pBuilder.append(END_BRACE);
      appendComma(pBuilder);
    }
    // default value
    else {
      pBuilder.append(QUOTE).append(escapeIllegalText(mName)).append(QUOTE).
          append(ASSIGN).append(QUOTE).
          append(escapeIllegalText(String.valueOf(mValue))).append(QUOTE);
      appendComma(pBuilder);
    }

    // Let's other response element be included.
    boolean started = false;
    for (final ResponseElement responseElement : mResponseElements) {
      if (started) {
        pBuilder.append(COMMA);
      } else {
        started = true;
      }
      responseElement.toPHP(pBuilder);
    }
  }

  private void appendComma(final StringBuilder pBuilder) {
    if (mResponseElements != null && !mResponseElements.isEmpty()) {
      pBuilder.append(COMMA);
    }
  }
}

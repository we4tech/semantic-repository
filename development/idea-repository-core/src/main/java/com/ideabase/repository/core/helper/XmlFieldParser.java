/* $Id:XmlFieldParser.java 249 2007-12-02 08:32:47Z hasan $ */
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
 * $LastChangedBy:hasan $
 * $LastChangedDate:2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision:249 $
 ******************************************************************************
*/
package com.ideabase.repository.core.helper;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Document;
import nu.xom.Elements;

import java.util.Map;
import java.util.HashMap;
import java.io.StringReader;

import com.ideabase.repository.common.exception.ServiceException;
import static com.ideabase.repository.common.XmlConstants.*;

/**
 * Parse request document.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class XmlFieldParser {

  private final Map<String, String> mFieldValueMap =
      new HashMap<String, String>();

  private final String mFieldXml;

  /**
   * Default constructor, it accepts required dependency request field content.
   * @param pFieldXml request field content.
   */
  public XmlFieldParser(final String pFieldXml) {
    mFieldXml = pFieldXml;
    parse();
  }

  private void parse() {
    try {
      // Build request document from string reader.
      final Builder builder = new Builder();
      final Document document = builder.build(new StringReader(mFieldXml));

      // Find <fields/> element
      final Element fieldsElement = document.getRootElement();

      // Find <field/> from parent element
      final Elements fieldElements = fieldsElement.getChildElements(ELEMENT_FIELD);
      for (int i = 0; i < fieldElements.size(); i++) {
        final Element fieldElement = fieldElements.get(i);
        final String name = fieldElement.getAttributeValue(ATTR_NAME);
        final String content = fieldElement.getValue();
        // set to map object
        mFieldValueMap.put(name, content);
      }
    } catch (Exception e) {
      throw new ServiceException(null, "Failed to parse a request document.", e);
    }
  }

  public Map<String, String> getFieldValueMap() {
    return mFieldValueMap;
  }
}

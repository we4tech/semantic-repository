/* $Id:ObjectPopulateHelper.java 249 2007-12-02 08:32:47Z hasan $ */
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

import com.ideabase.repository.core.dao.Item;
import com.ideabase.repository.common.object.ObjectBase;

import java.lang.reflect.InvocationTargetException;

/**
 * Populate an object instance from an item instance..
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ObjectPopulateHelper {
  private static final String SETTER_METHOD_PREFIX = "set";

  /**
   * Retrieve {@code CacheModel} and parse Xml content. traverse all fields
   * and set the field value from request content.
   */
  public static <T> void populateObjectInstance(
      final Item pItem, final ObjectBase pObjectInstance)
      throws NoSuchMethodException, IllegalAccessException,
             InvocationTargetException {

    // parse request content.
    final XmlFieldParser parser = new XmlFieldParser(pItem.getDocument());
    pObjectInstance.setFields(parser.getFieldValueMap());
  }

  private static String prepareSetterMethodFor(final String pFieldName) {
    return SETTER_METHOD_PREFIX + pFieldName.substring(0, 1).toUpperCase() +
           pFieldName.substring(1, pFieldName.length());
  }
}

/* $Id:FieldMethodConverter.java 249 2007-12-02 08:32:47Z hasan $ */
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

import java.lang.reflect.Method;

/**
 * Convert field name to setter or getter method name.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class FieldMethodConverter {
  private static final String PREFIX_METHOD_SETTER = "set";
  private static final String PREFIX_METHOD_GETTER = "get";

  public static Method getSetterMethod(final String pField,
                                       final Class pTargetClass,
                                       final Class<?>... pParams)
      throws NoSuchMethodException {
    String setterMethodName = prepareMethod(PREFIX_METHOD_SETTER, pField);
    return pTargetClass.getDeclaredMethod(setterMethodName, pParams);
  }

  private static String prepareMethod(final String pPrefixMethodSetter,
                                      final String pField) {
    return pPrefixMethodSetter + pField.substring(0, 1).toUpperCase() +
           pField.substring(1, pField.length());
  }

  public static Method getGetterMethod(final String pField,
                                       final Class pTargetClass,
                                       final Class<?>... pParams)
      throws NoSuchMethodException {
    String methodName = prepareMethod(PREFIX_METHOD_GETTER, pField);
    return pTargetClass.getMethod(methodName, pParams);
  }
}

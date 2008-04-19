/*
 * $Id: ObjectToString.java 250 2008-01-07 10:18:29Z hasan $
 ******************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 ******************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.common;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.lang.reflect.Method;

/**
 * Helper class to simply Object to string,
 * @author hasan &lt;hasan@somewherein&gt;
 */
public class ObjectToString {

  private static final Logger LOG = LogManager.getLogger(ObjectToString.class);

  /**
   * Match method prefix by this constants. <br>
   * <b>for example:</b> if method name is {@code getFoo} it is invoked and
   * returned value is set on our output message.<br>
   * otherwise it is skipped.<br>
   */
  private static final String METHOD_PREFIX = "get";

  /**
   * Message block prefix '{'. for example: {id: 11, name: '121'}
   */
  private static final String MESSAGE_BLOCK_START = "{";

  /**
   * Message block suffix '}'. for example: {id: 11, name: '121'}
   */
  private static final String MESSAGE_BLOCK_END = "}";

  /**
   * Separate message properties.
   */
  private static final char MESSAGE_VALUE_SEPARATOR = ',';

  /**
   * Property name and value separator. for example {id : 'value'}
   */
  private static final String MESSAGE_NAME_VALUE_SEPARATOR = ": ";

  /**
   * <b>DON't call {@code LOG.debug(pObject)}, it will start
   * an infinite loop.</b><br><br>
   * Retreive all public properties and make a string message
   * for example:<br>
   * <b>{id:21, name:'hasan'}</b>
   *
   * @param pObject the instance of the object,
   *        which needs to be serialized in a string form.<br>
   * @param pClass the object class type.<br>
   * @return the string message.<br>
   */
  public static String toString(Object pObject, Class pClass) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("\r\n").append(MESSAGE_BLOCK_START);
    try {
      for (Method method : pClass.getMethods()) {
        try {
          boolean methodHasGetPrefix = method.getName().
                                       startsWith(METHOD_PREFIX);
          if (methodHasGetPrefix) {
            Object returnValue = method.invoke(pObject);
            stringBuilder.append("\t").
                          append(formatMethodName(method.getName())).
                          append(MESSAGE_NAME_VALUE_SEPARATOR).
                          append("\"").append(returnValue).append("\", \r\n");
          }
        } catch (Exception e) {
          // LOG.debug("Reflection failed for method name - " + method.getName());
        }
      }
      // remove extra "," if exists.
      if (stringBuilder.charAt((stringBuilder.length()-2)) == MESSAGE_VALUE_SEPARATOR) {
        stringBuilder.deleteCharAt((stringBuilder.length() - 2));
      }
    } catch (Exception e) {
      LOG.warn("Failed to convert object to string", e);
    }
    stringBuilder.append(MESSAGE_BLOCK_END).append("\r\n");
    return stringBuilder.toString();
  }

  /**
   * Object to string by specified object instance
   * @param pObject the object instance
   * @return toString output
   */
  public static String toString(Object pObject) {
    return toString(pObject, pObject.getClass());
  }

  /**
   * remove "get" from method name, and convert all string to lower case.
   * @param pMethodName method name.
   * @return formatted method name.
   */
  private static String formatMethodName(String pMethodName) {
    pMethodName = pMethodName.replaceAll(METHOD_PREFIX, "").toLowerCase();
    return pMethodName;
  }
}
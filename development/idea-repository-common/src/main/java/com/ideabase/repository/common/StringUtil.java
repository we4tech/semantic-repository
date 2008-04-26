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

package com.ideabase.repository.common;

/**
 * Provide handful utility to perform a specific job with {@code String} class.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class StringUtil {

  /**
   * Escape illegal characters from the string object (such as: ').
   * @param pValue string value
   * @return illegal text escaped string
   */
  public static String escapeIllegalText(final String pValue) {
    if (pValue != null) {
      return pValue.replaceAll("(['])", "\\\\$1");
    }
    return pValue;
  }
}

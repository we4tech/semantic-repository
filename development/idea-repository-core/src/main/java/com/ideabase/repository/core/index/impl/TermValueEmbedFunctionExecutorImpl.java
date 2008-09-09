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

package com.ideabase.repository.core.index.impl;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * Execute embed function from the search term.
 * ie. title:(x_functionName_term) 
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermValueEmbedFunctionExecutorImpl {

  private static final Logger LOGGER =
      LogManager.getLogger(TermValueEmbedFunctionExecutorImpl.class);
  private static final String FUNCTION_NAME_PREFIX = "x_";
  private static final String FUNCTION_SEPARATOR = "_";

  public String eval(final String pTermString) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("looking up embed function for the term - " + pTermString);
    }
    // ensure whether this value has mark for using embed function
    // typically 'x_' prefix is used before the value
    // ie. title:(x_phoneticEncode_kokia~)
    if (pTermString.startsWith(FUNCTION_NAME_PREFIX)) {
      LOGGER.debug("embed function is required to execute.");
      // separate value by '_' separator
      // second part must be the function name
      // third part must be the value
      final String[] parts = pTermString.split(FUNCTION_SEPARATOR);
      if (parts.length != 3) {
        throw new IllegalArgumentException("Invalid embed function is " +
                                           "used on value - " + pTermString);
      }

      final String functionName = parts[1];
      final String value = parts[2];

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("embed function name - '" +
                   functionName + "' for value - '" + value + "'");
      }
    }
    return pTermString;
  }
}

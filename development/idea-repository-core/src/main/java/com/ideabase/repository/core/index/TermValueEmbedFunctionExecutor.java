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

package com.ideabase.repository.core.index;

import com.ideabase.repository.core.index.function.TermValueEmbedFunction;

import java.util.Map;

/**
 * Execute embed function from the search term.
 * ie. title:(x_functionName_term)
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public interface TermValueEmbedFunctionExecutor {

  /**
   * Term string will be returned after executing the embed function
   * if no embed function is mentioned {@CODE PTermString} will be returned.
   * @param pTermString term string which may have embed function
   * @return the value after executing embed function or the this value
   *         (if no embed function is mentioned)
   */
  String eval(final String pTermString);

  /**
   * Set supported embed function, where key is used for function name
   * and {@see TermValueEmbedFunction} implementation instance is used
   * for mapping the implementation.
   * @param pEmbedFunctions map of supported embed function name
   *        and implementation.
   */
  void setEmbedFunctions(
      final Map<String, TermValueEmbedFunction> pEmbedFunctions);
}

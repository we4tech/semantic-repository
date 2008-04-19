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

package com.ideabase.repository.common.formatter;

import com.ideabase.repository.common.exception.ServiceException;

import java.util.List;

/**
 * PHP response formatter.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class PHPFormatter implements ResponseFormatter {

  private static final String TOKEN_STATE = "state";
  private static final String TOKEN_ASSIGN = " => ";

  public String format(final boolean pState,
                       final List<Object> pResponseObjects) {
    if (pResponseObjects == null || pResponseObjects.isEmpty()) {
      throw new ServiceException(null, "No such response object. you must " +
          "define a list of objects before invoking *Formatter::format");
    }
    final StringBuilder builder = new StringBuilder();
    builder.append("return array('")
        .append(TOKEN_STATE).append("'").append(TOKEN_ASSIGN).append(String.valueOf(pState));
    builder.append(", ");
    if (pResponseObjects != null && !pResponseObjects.isEmpty()) {
      boolean started = false;
      for (final Object eObject : pResponseObjects) {
        if (started) {
          builder.append(",");
        } else {
          started = true;
        }
        if (eObject instanceof PHPSerializable) {
          ((PHPSerializable) eObject).toPHP(builder);
        }
      }
    } else {
      builder.append("array()");
    }
    builder.append(");");
    return builder.toString();
  }
}

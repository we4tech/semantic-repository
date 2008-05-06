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

import org.apache.lucene.search.SortComparator;
import org.apache.lucene.document.NumberTools;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Support number sorting through custom implementation
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class NumberSortComparator extends SortComparator {
  private final Logger mLog = LogManager.getLogger(getClass());

  protected Comparable getComparable(final String s) {
    if (s != null && s.length() > 10) {
      return NumberTools.stringToLong(s);
    } else if (s != null && s.length() < 10) {
      return Long.parseLong(s);
    } else {
      return Long.MIN_VALUE;
    }
  }
}

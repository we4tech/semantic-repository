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

package com.ideabase.repository.core.helper;

import static com.ideabase.repository.common.CommonConstants.FieldNames.*;

/**
 * Helper functions for index related tasks
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class IndexHelper {

  public static boolean isAcceptableFieldNameForTokenCollection(
      final String pFieldName) {
    return (pFieldName != null &&
            !pFieldName.endsWith(FIELD_SUFFIX_DATE)     &&
            !pFieldName.equals(FIELD_ID)                &&
            !pFieldName.endsWith(FIELD_SUFFIX_ID)       &&
            !pFieldName.endsWith(FIELD_SUFFIX_NUMBER))  &&
            !pFieldName.endsWith(FIELD_SUFFIX_IGNORE)   &&
            !pFieldName.startsWith(FIELD_PREFIX_PRICE)  &&
            !pFieldName.toLowerCase().endsWith(FIELD_SUFFIX_ON);
  }
}

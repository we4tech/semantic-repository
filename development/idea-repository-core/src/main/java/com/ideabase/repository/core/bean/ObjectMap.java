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

package com.ideabase.repository.core.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Object map is extend version of {@see HashMap} which implemented setter
 * method to enable through spring bean configuration layer.
 *
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class ObjectMap extends HashMap {
  public void setValues(final Map<String, Object> pValues) {
    putAll(pValues);
  }
}

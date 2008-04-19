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

/**
 * Mixin type for certifing php serializbility
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public interface PHPSerializable {
  void toPHP(final StringBuilder pBuilder);
}

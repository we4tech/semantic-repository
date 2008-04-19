/* $Id: MethodExecutionTimeCalcuator.java 250 2008-01-07 10:18:29Z hasan $ */
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
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.test;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class MethodExecutionTimeCalcuator {

  public static <T> T executeMethod(final Executable pExecutable,
                                    final Logger pLogger) {
    if (pExecutable == null) {
      throw new IllegalArgumentException("No executable code is attached.");
    }
    final long __startTime = System.currentTimeMillis();
    T result = (T) pExecutable.execute();
    final long __endTime = System.currentTimeMillis();
    pLogger.debug("Executing time - (" + ( __endTime - __startTime ) + ") ");
    return result;
  }

  public static interface Executable {
    public Object execute();
  }
}

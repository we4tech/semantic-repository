/* $Id:FieldInvocationHelper.java 249 2007-12-02 08:32:47Z hasan $ */
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
 * $LastChangedBy:hasan $
 * $LastChangedDate:2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision:249 $
 ******************************************************************************
*/
package com.ideabase.repository.core.helper;

import com.ideabase.repository.common.exception.ServiceException;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * Invoke specific method to perform some specific task.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class FieldInvocationHelper {

  private static final Logger LOG =
      LogManager.getLogger(FieldInvocationHelper.class);
  private static final boolean DEBUG = LOG.isDebugEnabled();


  /**
   * Retrive the {@code Method} for the specified mutator method name.<Br>
   * if no such method exists, exception is returned. otherwise method is
   * invoked with field value.
   *
   * @param pObjectInstance target object instance.
   * @param pSetterMethod setter method name.
   * @param pFieldValue parameter that will be passed during the invocation of
   *        the method.
   */
  public static <T> void invokeMethodByName(final T pObjectInstance,
                                            final String pSetterMethod,
                                            final String pFieldValue) {
    try {
      // find the method by name.
      final Method method = pObjectInstance.getClass().
          getMethod(pSetterMethod, String.class);
      
      // invoke method.
      method.invoke(pObjectInstance, pFieldValue);
    } catch (Exception e) {
      throw new ServiceException(pSetterMethod,
          "Failed to inject value on setter method.", e);
    }
  }
}

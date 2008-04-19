/* $Id: DataAccessEventAdvice.java 249 2007-12-02 08:32:47Z hasan $ */
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
 * $LastChangedDate: 2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision: 249 $
 ******************************************************************************
*/
package com.ideabase.repository.core.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import com.ideabase.repository.api.EventManager;
import com.ideabase.repository.api.event.Event;
import com.ideabase.repository.core.dao.ItemDAO;
import com.ideabase.repository.core.dao.ItemMappingDAO;
import com.ideabase.repository.core.dao.Item;
import com.ideabase.repository.core.dao.ItemMapping;

import static com.ideabase.repository.api.event.Event.EventType;

/**
 *
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan
 *        (hasan)</a>
 */
@Aspect
public class DataAccessEventAdvice {
  private final EventManager mEventManager;
  private static final String METHOD_PREFIX_CREATE = "create";
  private static final String METHOD_PREFIX_UPDATE = "update";
  private static final String METHOD_PREFIX_DELETE = "delete";

  public DataAccessEventAdvice(final EventManager pEventManager) {
    mEventManager = pEventManager;
  }

  @AfterReturning(
      pointcut = "com.ideabase.repository.core.aspect.ArchitecturePointcuts.dataAccessOperation()",
      returning = "pReturnValue"
  )
  public void publishSaveEvent(final Object pReturnValue) {
    //TODO: System.out.println("Return value - " + pReturnValue);
  }

  /**
   * Find Signature class. now publish event based on the specific class and
   * method signature.
   */
  @Around(
      "com.ideabase.repository.core.aspect.ArchitecturePointcuts.dataAccessOperation()"
  )
  public Object aroundOperation(final ProceedingJoinPoint pProceedingJoinPoint)
      throws Throwable {
    // Determine source class and mehtod.
    final Signature signature = pProceedingJoinPoint.getSignature();
    final Class signatureClass = signature.getDeclaringType();
    final String signatureMethod = signature.getName();
    final Object[] arguments = pProceedingJoinPoint.getArgs();

    // Execute the operation
    final Object returned = pProceedingJoinPoint.proceed();

    // publish event
    addEvent(returned, signatureClass, signatureMethod, arguments);

    // Return the executed output.
    return returned;
  }

  private void addEvent(final Object pReturned,
                        final Class pSignatureClass,
                        final String pSignatureMethod,
                        final Object[] pArguments) {
    if (pSignatureClass == ItemDAO.class
        || pSignatureClass == ItemMappingDAO.class) {
      handleDAOEvent(pReturned, pSignatureClass, pSignatureMethod, pArguments);
    }
  }

  private void handleDAOEvent(final Object pReturned,
                              final Class pSignatureClass,
                              final String pSignatureMethod,
                              final Object[] pArguments) {
    // Create item method
    if (pSignatureMethod.startsWith(METHOD_PREFIX_CREATE)) {
      // publish the events
      mEventManager.publishEvent(newEvent((Integer) pReturned,
                                 EventType.ITEM_CREATED, pSignatureClass));
    }
    // update item
    else if (pSignatureMethod.startsWith(METHOD_PREFIX_UPDATE)) {
      mEventManager.publishEvent(newEvent((Integer) pReturned,
                                 EventType.ITEM_UPDATED, pSignatureClass));
    }
    // delete item
    else if (pSignatureMethod.startsWith(METHOD_PREFIX_DELETE)) {
      final Integer itemId;
      if (pArguments[0] instanceof Item) {
        itemId = ((Item) pArguments[0]).getId();
      } else if (pArguments[0] instanceof ItemMapping) {
        itemId = ((ItemMapping) pArguments[0]).getLeftId();
      } else {
        itemId = null;
      }
      mEventManager.publishEvent(newEvent(itemId,
                                 EventType.ITEM_DELETED, pSignatureClass));
    }
  }

  private Event newEvent(final Integer pItemId,
                         final EventType pEventType,
                         final Class pSource) {
    return new Event(pItemId, pEventType, pSource);
  }
}

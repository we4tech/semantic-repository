/* $Id: IndexEventAdvice.java 249 2007-12-02 08:32:47Z hasan $ */
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
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import com.ideabase.repository.api.EventManager;
import com.ideabase.repository.api.event.Event;
import com.ideabase.repository.core.index.RepositoryItemIndex;

/**
 * Advice for sending out event from indexer.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
@Aspect
public class IndexEventAdvice {

  private final EventManager mEventManager;
  private static final String METHOD_ADD = "add";
  private static final String METHOD_UPDATE = "update";
  private static final String METHOD_DELETE = "delete";

  public IndexEventAdvice(final EventManager pEventManager) {
    mEventManager = pEventManager;
  }

  @Around("com.ideabase.repository.core.aspect.ArchitecturePointcuts.indexOperation()")
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
    addEvent(signatureClass, signatureMethod, arguments);

    // Return the executed output.
    return returned;
  }

  private void addEvent(final Class pSignatureClass,
                        final String pSignatureMethod,
                        final Object[] pArguments) {
    if (pSignatureClass == RepositoryItemIndex.class) {
      handleIndexEvent(pSignatureClass, pSignatureMethod, pArguments);
    }
  }

  private void handleIndexEvent(final Class pSignatureClass,
                                final String pSignatureMethod,
                                final Object[] pArguments) {
    // add method
    if (pSignatureMethod.startsWith(METHOD_ADD)) {
      final Document document = (Document) pArguments[0];
      mEventManager.publishEvent(newEvent(document,
          Event.EventType.ITEM_INDEX_ADDED, pSignatureClass));
    }
    // update method
    else if (pSignatureMethod.startsWith(METHOD_UPDATE)) {
      final Term idTerm = (Term) pArguments[0];
      mEventManager.publishEvent(newEvent(idTerm,
          Event.EventType.ITEM_INDEX_UPDATED, pSignatureClass));
    }
    // Delete method
    else if (pSignatureMethod.startsWith(METHOD_DELETE)) {
      final Object object = pArguments[0];
      if (object instanceof Term) {
        final Term idTerm = (Term) object;
        mEventManager.publishEvent(newEvent(idTerm,
            Event.EventType.ITEM_INDEX_DELETED, pSignatureClass));
      }
    }
  }

  private Event newEvent(final Object pItem,
                         final Event.EventType pEventType,
                         final Class pSource) {
    return new Event(pItem, pEventType, pSource);
  }
}

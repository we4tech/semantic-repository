/* $Id: EventManagerImpl.java 249 2007-12-02 08:32:47Z hasan $ */
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
package com.ideabase.repository.core.service;

import com.ideabase.repository.api.EventManager;
import com.ideabase.repository.common.exception.ServiceException;
import com.ideabase.repository.api.event.Event;
import com.ideabase.repository.api.event.EventListener;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * Implementation of {@see EventManager}.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class EventManagerImpl implements EventManager {

  /**
   * Maximum 100 events can be queued. when this 100 is filled up,
   * automatically every thread will be invoked.
   */
  private static final int CORE_THREAD_POOL_SIZE = 100;

  private final Logger LOG = LogManager.getLogger(EventManagerImpl.class);
  private final boolean DEBUG = LOG.isDebugEnabled();
  private final ScheduledExecutorService mExecutorService =
      Executors.newSingleThreadScheduledExecutor();

  private final List<EventListener> mEventListeners =
      new ArrayList<EventListener>();


  public void subscribe(final EventListener pEventListener) {
    if (DEBUG) {
      LOG.debug("Subscribing new event listener - " + pEventListener);
    }
    mEventListeners.add(pEventListener);
  }

  public void unsubscribe(final EventListener pEventListener) {
    if (DEBUG) {
      LOG.debug("Unsbscribing new event listener - " + pEventListener);
    }
    mEventListeners.remove(pEventListener);
  }

  public Iterator<EventListener> eventListenerIterator() {
    return mEventListeners.iterator();
  }

  public void publishEvent(final Event pEvent) {
    if (DEBUG) {
      LOG.debug("Publish new event to the subscriber - " + pEvent);
    }
    final Runnable runnable = new Runnable() {
      public void run() {
        for (final EventListener eventListener : mEventListeners) {
          if (DEBUG) {
            LOG.debug("Publishing new event to - " + eventListener);
          }
          eventListener.onEvent(pEvent);
        }
      }
    };
    mExecutorService.execute(runnable);
  }

  public void setEventListeners(final List<EventListener> pEventListeners) {
    if (pEventListeners == null || pEventListeners.isEmpty()) {
      throw new ServiceException(null,
          "Failed to add specified event listerners, " +
          "because no event listener are found in the list.");
    }
    for (final EventListener listener : pEventListeners) {
      subscribe(listener);
    }
  }
}

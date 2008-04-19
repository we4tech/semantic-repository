/* $Id: ObjectIntegrityTestAdvice.java 249 2007-12-02 08:32:47Z hasan $ */
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
import org.aspectj.lang.annotation.Before;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.common.object.Hit;
import com.ideabase.repository.common.object.PaginatedList;
import com.ideabase.repository.common.Query;
import com.ideabase.repository.api.RepositoryService;

/**
 * This advice is used to verify the integrity of a new object which will be
 * created over {@see RepositoryService#save()} method.<Br>
 * this advice is guled before invoking {@see RepositoryService#save()}
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
@Aspect
public class ObjectIntegrityTestAdvice {

  /**
   * Logger instance for the current class.
   */
  private final Logger LOG = LogManager.getLogger(getClass());
  private final RepositoryService mRepositoryService;

  /**
   * Default constructor, it requires default constructor,
   * {@see RepositoryItemSearch} to perform integrity test.
   * @param pRepositoryService required dependency to perform integrity test.
   */
  public ObjectIntegrityTestAdvice(
      final RepositoryService pRepositoryService) {
    mRepositoryService = pRepositoryService;
  }

  /**
   * Perform integrity verification if target object has no <b>ID</b> defined
   * and {@see ObjectBase#getIntegrityVerificationQuery()) doesn't return {@code null}.
   */
  @Before("com.ideabase.repository.core.aspect.ArchitecturePointcuts." +
          "repositorySaveOperation() && args(objectBase)")
  public void verifyIntegrityBeforeSave(final ObjectBase objectBase) {
    LOG.debug("Verify integrity before storing an object.");
    if (objectBase != null) {
      verifyIntegrity(objectBase);
    }
  }

  private void verifyIntegrity(final ObjectBase pObjectBase) {
    final Query query = pObjectBase.getIntegrityVerificationQuery();
    LOG.debug("Verify integrity.");
    // when object in new state and integrity is required.
    if (pObjectBase.getId() == null && query != null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Execute integrity verification query - " + query);
      }
      testIntegrity(query, pObjectBase);
    }
    // When object in update state and integrity verification is required.
    else if (pObjectBase.getId() != null && query != null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Execute integrity verification on update state - " + query);
      }
      // if title is defined, please verify the integrity of the new title
      final String newTitle = pObjectBase.getTitle();
      if (newTitle != null) {
        testIntegrity2(query, pObjectBase);
      }
    } else {
      LOG.debug("NO integrity requirement meets.");
    }
  }

  private void testIntegrity(final Query pQuery, final ObjectBase pObjectBase) {
    LOG.debug("Test integrity type - 1");
    PaginatedList<Hit> hits = null;
    try {
      hits = mRepositoryService.getItemsByQuery(pQuery);
      LOG.debug("Hits - " + hits);
    } catch (Throwable e) {
      // ignore this exception, we are not concerning on runtime exception
      // during performing index driven search.
      // this exception may raise when no index already been created.
      // better to log it for later purpose
      LOG.warn("Failed to perform index searching.", e);
    }
    if (hits != null && !hits.isEmpty()) {
      throw new IllegalArgumentException("duplicate item, repository item " +
          "title (" + pObjectBase.getTitle() + ") has already been created.");
    }
  }

  private void testIntegrity2(final Query pQuery, final ObjectBase pObjectBase)
  {
    LOG.debug("Test integrity type - 2");
    PaginatedList<Hit> hits = null;
    try {
      hits = mRepositoryService.getItemsByQuery(pQuery);
      LOG.debug("Hits - " + hits);
    } catch (Throwable e) {
      // ignore this exception, we are not concerning on runtime exception
      // during performing index driven search.
      // this exception may raise when no index already been created.
      // better to log it for later purpose
      LOG.warn("Failed to perform index searching.", e);
    }
    if (hits != null && !hits.isEmpty()) {
      LOG.debug("Duplicate item found, now throwing an exception.");
      final Integer objectId = hits.get(0).getId();
      if (!objectId.equals(pObjectBase.getId())) {
        throw new IllegalArgumentException("duplicate item, repository item " +
            "title (" + pObjectBase.getTitle() + ") has already been created.");
      }
    }
  }
}

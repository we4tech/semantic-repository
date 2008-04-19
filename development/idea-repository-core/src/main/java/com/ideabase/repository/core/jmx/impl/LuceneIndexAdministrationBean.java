/*
 * $Id: LuceneIndexAdministrationBean.java 253 2008-03-10 18:04:01Z hasan $
 * *****************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-03-10 23:34:01 +0530 (Mon, 10 Mar 2008) $
 * $LastChangedRevision: 253 $
 * *****************************************************************************
*/
package com.ideabase.repository.core.jmx.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.ideabase.repository.core.jmx.ILuceneIndexAdministrationBean;
import com.ideabase.repository.core.index.RepositoryItemIndex;
import com.ideabase.repository.core.search.RepositoryItemSearch;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.common.object.GenericItem;
import org.springmodules.lucene.index.support.LuceneIndexSupport;
import org.springmodules.lucene.index.core.LuceneIndexTemplate;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.annotation.ManagedOperation;

/**
 * Implementation of {@see ILuceneIndexAdministrationBean} JMX bean.
 * @author <a href="mailto:hasan@somewherein.net">
 *         nhm tanveer hossain khan (hasan)</a>
 */
@ManagedResource(
    objectName="repository.tools:name=LuceneIndexAdministration",
    description="Administrate lucene based index.",
    log=true,
    logFile="repository.tools.lucene-index-admin.log",
    currencyTimeLimit=15
)
public class LuceneIndexAdministrationBean
    implements ILuceneIndexAdministrationBean {

  /**
   * Logger instance.
   */
  private static Logger LOG =
      LogManager.getLogger(LuceneIndexAdministrationBean.class);

  private final RepositoryService mRepositoryService;
  private Map<String, RepositoryItemIndex> mRepositoryItemIndexMap;
  private Map<String, RepositoryItemSearch> mRepositoryItemSearchMap;

  /**
   * Default constructor, which requires {@see RepositoryService} dependencies
   * @param pRepositoryService required dependencies
   */
  public LuceneIndexAdministrationBean(
      final RepositoryService pRepositoryService) {
    super();
    mRepositoryService = pRepositoryService;
  }

  public void setRepositoryItemIndexServices(
      final Map<String, RepositoryItemIndex> pRepositoryItemIndexMap) {
    if (pRepositoryItemIndexMap == null && pRepositoryItemIndexMap.isEmpty()) {
      throw new RuntimeException("There should be at least one item index services listed.");
    }
    mRepositoryItemIndexMap = pRepositoryItemIndexMap;
  }

  public void setRepositoryItemSearchServices(
      final Map<String, RepositoryItemSearch> pRepositoryItemSearchMap) {
    if (pRepositoryItemSearchMap == null && pRepositoryItemSearchMap.isEmpty()) {
      throw new RuntimeException("There should be at least one item search services listed.");
    }
    mRepositoryItemSearchMap = pRepositoryItemSearchMap;
  }

  /**
   * {@inheritDoc}
   */
  @ManagedOperation(
      description = "Optimize lucene index"
  )
  public boolean optimizeIndics() {
    try {
      for (final Map.Entry<String, RepositoryItemIndex> entry
          : mRepositoryItemIndexMap.entrySet()) {
        entry.getValue().optimize();
      }
      LOG.debug("Optimization has been completed");
      return true;
    } catch (Exception e) {
      LOG.warn("Exception raised during optimizing the index.", e);
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @ManagedOperation(
      description = "Rebuild lucene index"
  )
  public boolean rebuildIndics() {
    LOG.debug("Rebuilding lucene index.");
    throw new NoSuchMethodError("This method wasn't implemented yet.");
    /*try {
      // remove all existing index.
      final LuceneIndexTemplate template = getLuceneIndexTemplate();
      removeIndics(template);

      // rebuild all index
      rebuildIndics(template);

      // invoke optimize
      optimizeIndics();
      return true;
    } catch (Exception e) {
      LOG.warn("Exception raised during rebuilding the index", e);
    }
    return false;*/
  }

  /**
   * {@inheritDoc}
   */
  @ManagedOperation(
      description = "Create indics based on current repository items."
  )
  public boolean createIndics() {
    /*try {
      rebuildIndics(getLuceneIndexTemplate());
      return true;
    } catch (Exception e) {
      LOG.warn("Create index has been failed", e);
    }
    return false;*/
    throw new NoSuchMethodError("This method wasn't implemented yet.");
  }

  private void rebuildIndics(final LuceneIndexTemplate pTemplate) {
    final List<Integer> itemIds =
        mRepositoryService.getAllItems(0, Integer.MAX_VALUE);
    for (final Integer itemId : itemIds) {
      final GenericItem item =
          mRepositoryService.getItem(itemId, GenericItem.class);
      pTemplate.addDocument(item.getDocument());
    }
    LOG.debug("Building new index has bee completed.");
  }

  private void removeIndics(final LuceneIndexTemplate pTemplate) {
    for (int i = 0; i < pTemplate.getNumDocs(); i++) {
      if (!pTemplate.isDeleted(i)) {
        pTemplate.deleteDocument(i);
      }
    }
    LOG.debug("All index removed.");
  }
}

/*
 * $Id:RepositoryItemIndexImpl.java 249 2007-12-02 08:32:47Z hasan $
 ******************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 ******************************************************************************
 * $LastChangedBy:hasan $
 * $LastChangedDate:2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision:249 $
 ******************************************************************************
*/
package com.ideabase.repository.core.index.impl;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.Collection;

import org.springmodules.lucene.index.support.LuceneIndexSupport;
import org.springmodules.lucene.index.factory.LuceneIndexReader;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import com.ideabase.repository.core.index.RepositoryItemIndex;

/**
 * Implementation of {@code RepositoryItemIndex}.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class RepositoryItemIndexImpl extends LuceneIndexSupport
    implements RepositoryItemIndex {

  private final Logger LOG =
      LogManager.getLogger(RepositoryItemIndexImpl.class);

  /**
   * Executor service is used to process all same index related work in a
   * queued manner. so the caller won't wait for the processing instead it
   * will do it afterwords.
   */
  private final Executor mIndexTaskExecutor =
      Executors.newSingleThreadExecutor();

  /**
   * {@inheritDoc}
   * @param pDocument {@inheritDoc}
   */
  public void addDocument(final Document pDocument) {
    mIndexTaskExecutor.execute(
        new Runnable() {
          public void run() {
            getLuceneIndexTemplate().addDocument(pDocument);
          }
        }
    );
  }

  /**
   * {@inheritDoc}
   */
  public void updateDocument(final Term pTerm,
                             final Document pDocument) {
    mIndexTaskExecutor.execute(
        new Runnable() {
          public void run() {
            getLuceneIndexTemplate().deleteDocuments(pTerm);
            getLuceneIndexTemplate().addDocument(pDocument);
          }
        }
    );
  }

  /**
   * {@inheritDoc}
   */
  public void deleteDocument(final Term pIdTerm) {
    mIndexTaskExecutor.execute(
        new Runnable() {
          public void run() {
            getLuceneIndexTemplate().deleteDocuments(pIdTerm);
          }
        }
    );
  }

  /**
   * {@inheritDoc}
   */
  public void optimize() {
    mIndexTaskExecutor.execute(
        new Runnable() {
          public void run() {
            getLuceneIndexTemplate().optimize();
          }
        }
    );
  }

  public void deleteIndexFiles(final boolean pConfirmation) throws IOException {
    throw new UnsupportedOperationException(
              "This method is not yet implemented.");
  }
}

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

import org.springmodules.lucene.index.support.LuceneIndexSupport;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
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
  private Executor mIndexTaskExecutor =
      Executors.newSingleThreadExecutor();

  private boolean mThreadedTaskExecution = true;

  public void setThreadedTaskExecution(final boolean pThreadedTaskExecution) {
    mThreadedTaskExecution = pThreadedTaskExecution;
  }

  public void setTaskExecutor(final Executor pExecutor) {
    mIndexTaskExecutor = pExecutor;
  }

  /**
   * {@inheritDoc}
   * @param pDocument {@inheritDoc}
   */
  public void addDocument(final Document pDocument) {
    final Runnable runnable = new Runnable() {
      public void run() {
        if (getAnalyzer() == null) {
          getLuceneIndexTemplate().addDocument(pDocument);
        } else {
          System.out.println(getAnalyzer());
          getLuceneIndexTemplate().addDocument(pDocument, getAnalyzer());
        }
      }
    };
    if (mThreadedTaskExecution) {
      mIndexTaskExecutor.execute(runnable);
    } else {
      runnable.run();
    }
  }

  /**
   * {@inheritDoc}
   */
  public void updateDocument(final Term pTerm,
                             final Document pDocument) {
    final Runnable runnable = new Runnable() {
      public void run() {
        getLuceneIndexTemplate().deleteDocuments(pTerm);
        if (getAnalyzer() == null) {
          getLuceneIndexTemplate().addDocument(pDocument);
        } else {
          getLuceneIndexTemplate().addDocument(pDocument, getAnalyzer());
        }
      }
    };
    if (mThreadedTaskExecution) {
      mIndexTaskExecutor.execute(runnable);
    } else {
      runnable.run();
    }
  }

  /**
   * {@inheritDoc}
   */
  public void deleteDocument(final Term pIdTerm) {
    final Runnable runnable = new Runnable() {
      public void run() {
        getLuceneIndexTemplate().deleteDocuments(pIdTerm);
      }
    };
    if (mThreadedTaskExecution) {
      mIndexTaskExecutor.execute(runnable);
    } else {
      runnable.run();
    }

  }

  /**
   * {@inheritDoc}
   */
  public void optimize() {
    final Runnable runnable = new Runnable() {
      public void run() {
        getLuceneIndexTemplate().optimize();
      }
    };
    if (mThreadedTaskExecution) {
      mIndexTaskExecutor.execute(runnable);
    } else {
      runnable.run();
    }
  }

  public void deleteIndexFiles(final boolean pConfirmation) throws IOException {
    throw new UnsupportedOperationException(
              "This method is not yet implemented.");
  }
}

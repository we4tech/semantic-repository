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

package com.ideabase.repository.core.index.factory;

import org.springmodules.lucene.index.factory.*;
import com.ideabase.repository.core.index.service.TermUsageService;

/**
 * Term usage aware index factory, which can remove term usages when
 * an index is removed.
 *
 * decorate {@code LuceneIndexWriter} implementation with {@code TermUsageAwareLuceneIndexWriter}
 *
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermUsageAwareIndexFactory implements IndexFactory {

  private SimpleIndexFactory mBaseIndexFactory;
  public static TermUsageService mTermUsageService;

  public TermUsageAwareIndexFactory(
    final SimpleIndexFactory pBaseIndexFactory,
    final TermUsageService pTermUsageService) {
    mBaseIndexFactory = pBaseIndexFactory;
    mTermUsageService = pTermUsageService;
  }

  public LuceneIndexReader getIndexReader() {
    return new TermUsageAwareLuceneIndexReader(
      mBaseIndexFactory.getIndexReader(), mTermUsageService);
  }

  public LuceneIndexWriter getIndexWriter() {
    return new TermUsageAwareLuceneIndexWriter(
      mBaseIndexFactory.getIndexWriter(), mTermUsageService);
  }
}

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

package com.ideabase.repository.test.index;

import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.test.TestCaseRepositoryHelper;
import com.ideabase.repository.core.index.RepositoryItemIndex;
import com.ideabase.repository.core.index.service.TermUsageService;
import com.ideabase.repository.core.index.factory.TermUsageAwareIndexFactory;
import com.ideabase.repository.core.index.impl.RepositoryItemIndexImpl;
import com.ideabase.repository.core.index.filter.TermUsageFilter;
import com.ideabase.repository.core.CoreServiceKey;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.APIServiceKey;
import com.ideabase.repository.common.Query;
import com.ideabase.repository.common.object.GenericItem;
import org.springmodules.lucene.index.factory.SimpleIndexFactory;
import org.springmodules.lucene.index.factory.IndexFactory;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.search.QueryTermVector;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

/**
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermUsageAwareIndexFactoryTest extends BaseTestCase {
  private RepositoryItemIndex mRepositoryItemIndex;
  private RepositoryService mRepositoryService;
  private IndexFactory mIndexFactory;
  private RepositoryItemIndex mItemIndex;
  private TermUsageService mTermUsageService;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mRepositoryItemIndex = (RepositoryItemIndex) mContext.
        getBean(CoreServiceKey.REPOSITORY_ITEM_INDEX);
    mRepositoryService = (RepositoryService) mContext.
        getBean(APIServiceKey.REPOSITORY_SERVICE);
    mIndexFactory = (IndexFactory) mContext.getBean("indexFactory1");
    mItemIndex = (RepositoryItemIndex) mContext.getBean("itemIndex1");
    mTermUsageService = (TermUsageService) mContext.
        getBean(CoreServiceKey.TERM_USAGE_SERVICE);
  }

  public void testShouldSuccessfullyAddNewDocument()
      throws IOException, InterruptedException {

    mItemIndex.setThreadedTaskExecution(false);
    final GenericItem item = new GenericItem();
    item.setTitle("i love to hang out with friends.");
    item.addField("interest", "apple, linux, mac");

    final int itemId = mRepositoryService.save(item);
    assertTrue(itemId != 0);
  }

  // TODO: this are not exactly unit test, mostly used for experimenting.
  public void testShouldSuccessfullyPerformSearch() {
    assertNotNull(mTermUsageService);

    final int maxRows = mRepositoryService.getAllItemsCount();
    final List<Integer> itemIds =
        mRepositoryService.getAllItems(maxRows - 3, maxRows);
    final Map<String, String> tags = mTermUsageService.
        getTags(itemIds, Arrays.asList("interest"), 10);
    LOG.debug(tags);
  }
}

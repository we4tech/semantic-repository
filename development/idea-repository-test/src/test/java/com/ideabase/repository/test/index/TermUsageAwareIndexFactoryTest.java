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

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermUsageAwareIndexFactoryTest extends BaseTestCase {
  private RepositoryItemIndex mRepositoryItemIndex;
  private RepositoryService mRepositoryService;
  private IndexFactory mIndexFactory;
  private RepositoryItemIndex mItemIndex;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mRepositoryItemIndex = (RepositoryItemIndex) mContext.
        getBean(CoreServiceKey.REPOSITORY_ITEM_INDEX);
    mRepositoryService = (RepositoryService) mContext.
        getBean(APIServiceKey.REPOSITORY_SERVICE);
    mIndexFactory = (IndexFactory) mContext.getBean("indexFactory1");
    mItemIndex = (RepositoryItemIndex) mContext.getBean("itemIndex1");
  }

  public GenericItem[] testShouldProperlyCountTokensWhenIndexingNewItem()
    throws IOException, InterruptedException {

    // disable threaded execution for index factory
    mRepositoryItemIndex.setThreadedTaskExecution(false);

    // create item - 1
    final GenericItem item = new GenericItem();
    item.setTitle("token1 token2 token3");
    item.addField("field1", "value1 value2 value3");
    item.addField("field2", "value1 value2 value3 ab abc abcdefghijlm abcdefghijlmn");
    assertNotNull(mRepositoryService.save(item));

    // create item - 2
    final GenericItem item2 = new GenericItem();
    item2.setTitle("token2");
    item2.addField("field1", "value3");
    assertNotNull(mRepositoryService.save(item2));

    // verify the stored tokens and their usage count
    assertFalse(TermUsageAwareIndexFactory.mTermUsageService.map().isEmpty());
    System.out.println(TermUsageAwareIndexFactory.mTermUsageService.map());

    assertEquals(2, TermUsageAwareIndexFactory.mTermUsageService.map().get("value1").intValue());
    assertEquals(2, TermUsageAwareIndexFactory.mTermUsageService.map().get("value2").intValue());
    assertEquals(3, TermUsageAwareIndexFactory.mTermUsageService.map().get("value3").intValue());

    assertEquals(1, TermUsageAwareIndexFactory.mTermUsageService.map().get("token1").intValue());
    assertEquals(2, TermUsageAwareIndexFactory.mTermUsageService.map().get("token2").intValue());
    assertEquals(1, TermUsageAwareIndexFactory.mTermUsageService.map().get("token3").intValue());
    
    return new GenericItem[] {item, item2};
  }

  public void testShouldReduceTheTokenCountWhenAnIndexIsRemoved()
    throws IOException, InterruptedException {

    // disable threaded task execution
    mRepositoryItemIndex.setThreadedTaskExecution(false);

    // create new items
    final GenericItem[] items = testShouldProperlyCountTokensWhenIndexingNewItem();
    System.out.println(TermUsageAwareIndexFactory.mTermUsageService.map());

    // remove recently created items
    for (final GenericItem item : items) {
      System.out.println("Deleting - " + item.getId());
      mRepositoryService.delete(item.getId());
    }

    // these items shouldn't be existing
    for (final GenericItem item : items) {
      assertNull(mRepositoryService.getItem(item.getId(), GenericItem.class));
    }
    System.out.println(TermUsageAwareIndexFactory.mTermUsageService.map());

    // verify the usage of the tokens
    assertEquals(0, TermUsageAwareIndexFactory.mTermUsageService.map().get("value1").intValue());
    assertEquals(0, TermUsageAwareIndexFactory.mTermUsageService.map().get("value2").intValue());
    assertEquals(0, TermUsageAwareIndexFactory.mTermUsageService.map().get("value3").intValue());

    assertEquals(0, TermUsageAwareIndexFactory.mTermUsageService.map().get("token1").intValue());
    assertEquals(0, TermUsageAwareIndexFactory.mTermUsageService.map().get("token2").intValue());
    assertEquals(0, TermUsageAwareIndexFactory.mTermUsageService.map().get("token3").intValue());
    Thread.sleep(4000);
  }

}

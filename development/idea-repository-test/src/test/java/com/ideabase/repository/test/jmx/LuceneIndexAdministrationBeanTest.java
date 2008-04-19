/*
 * $Id: LuceneIndexAdministrationBeanTest.java 250 2008-01-07 10:18:29Z hasan $
 * *****************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 * *****************************************************************************
*/
package com.ideabase.repository.test.jmx;

import java.io.File;
import java.net.MalformedURLException;

import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.test.TestCaseRepositoryHelper;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.APIServiceKey;
import com.ideabase.repository.core.jmx.ILuceneIndexAdministrationBean;
import com.ideabase.repository.core.CoreServiceKey;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * Test the functionalities of {@see ILuceneIndexAdminstrationBean}
 *
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class LuceneIndexAdministrationBeanTest extends BaseTestCase {

  /**
   * Logger instance.
   */
  private final static Logger LOG =
      LogManager.getLogger(LuceneIndexAdministrationBeanTest.class);
  private RepositoryService mRepositoryService;
  private ILuceneIndexAdministrationBean mIndexAdministrationBean;

  /**
   * Setup context configuration and retrieve the {@see RepositoryService}
   * and {@see ILuceneIndexAdministrationBean}.
   * @throws Exception if exception raised during initialization.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mRepositoryService =
        (RepositoryService) mContext.getBean(APIServiceKey.REPOSITORY_SERVICE);
    mIndexAdministrationBean = (ILuceneIndexAdministrationBean) mContext.
        getBean(CoreServiceKey.INDEX_ADMINISTRATION_JMX_BEAN);
    TestCaseRepositoryHelper.fixRemoveAllItems(mRepositoryService);
  }

  /**
   * <b>Invoke - </b> {@see ILuceneIndexAdministrationBean#optimizeIndics()}<br>
   * <b>Expectation - </b> {@code true}
   */
  public void testOptimizeIndex() {
    // create items
    TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 100);
    assertTrue("Optimization failed",
               mIndexAdministrationBean.optimizeIndics());
  }

  /**
   * <b>Invoke - </b>  {@see ILuceneIndexAdministrationBean#rebuildIndics()}<br>
   * <b>Expectation - </b> {@code true}
   */
  public void testRebuildIndex() {
    // create items
    TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 100);
    assertTrue("Rebuilding index has been failed", 
               mIndexAdministrationBean.rebuildIndics());
  }

  public void testJMXService()
      throws InterruptedException, MalformedURLException {
    TestCaseRepositoryHelper.fixCreateItems(mRepositoryService, 100);
    Thread.sleep(Integer.MAX_VALUE);
  }
}

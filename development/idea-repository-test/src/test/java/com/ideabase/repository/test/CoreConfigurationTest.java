/**
 * $Id: CoreConfigurationTest.java 250 2008-01-07 10:18:29Z hasan $
 * *****************************************************************************
 *    Copyright (C) 2005 - 2007 somewhere in .Net ltd.
 *    All Rights Reserved.  No use, copying or distribution of this
 *    work may be made except in accordance with a valid license
 *    agreement from somewhere in .Net LTD.  This notice must be included on
 *    all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 * *****************************************************************************
 */

package com.ideabase.repository.test;

import org.apache.lucene.store.FSDirectory;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Verify the core configuration.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class CoreConfigurationTest extends BaseTestCase {

  private static final String BEAN_FS_DIRECTORY = "fsDirectory";
  private static final String PROPERTY_INDEX_DIRECTORY = "index.directory";
  private static final String BEAN_DATA_SOURCE = "dataSource";
  private FSDirectory mFactoryBean;
  private DataSource mDataSource;

  /**
   * Load spring context.
   *
   * @throws Exception if exception raised during Spring container
   *                   initialization.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    systemPropertiesFixture();
    mFactoryBean = (FSDirectory) mContext.getBean(BEAN_FS_DIRECTORY);
    mDataSource = (DataSource) mContext.getBean(BEAN_DATA_SOURCE);
  }

  private void systemPropertiesFixture() {
    System.setProperty(PROPERTY_INDEX_DIRECTORY,
        "file:////Users/nhmtanveerhossainkhanhasan/java-tmp/index");
  }

  public void testIndexFactoryBean() throws Exception {
    final Object file = mFactoryBean.getFile();
    LOG.info("file - " + file);
    assertNotNull(file);
  }

  public void testDataSource() throws SQLException {
    assertNotNull(mDataSource.getConnection());
  }
}

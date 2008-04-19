/*
 * $Id: ILuceneIndexAdministrationBean.java 249 2007-12-02 08:32:47Z hasan $
 * *****************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision: 249 $
 * *****************************************************************************
*/
package com.ideabase.repository.core.jmx;

import org.springmodules.lucene.index.support.LuceneIndexSupport;

/**
 * Lucene related optimization functionalities exposing through JMX.
 * so server admin can perform periodical optimization by hand. <br>
 * eventually this class comes with rebuild index functionalities. so admin
 * can rebuild the index too.
 * @author <a href="mailto:hasan@somewherein.net">
 *         nhm tanveer hossain khan (hasan)</a>
 */
public interface ILuceneIndexAdministrationBean {

  /**
   * Optimize the existing index by invoking index writer optimize.
   * @return true is returned if no exception found during the process.
   */
  boolean optimizeIndics();

  /**
   * Rebuild the whole index from retrieving content from repository.
   * all old index will be removed.
   * @return true is returned if no exception is raised during the process.
   */
  boolean rebuildIndics();

  /**
   * Create index for all existing document. this is need to mention that
   * if already same indics are existed, this create will add them once again.
   * @return true is returned if no exception raised.
   */
  boolean createIndics();
}

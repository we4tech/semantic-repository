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

package com.ideabase.repository.core.index.bean;

import org.springmodules.lucene.index.support.SimpleIndexFactoryBean;
import org.springmodules.lucene.index.factory.SimpleIndexFactory;
import com.ideabase.repository.core.index.factory.TermUsageAwareIndexFactory;
import com.ideabase.repository.core.index.service.TermUsageService;

/**
 * To remove term usage count when index is removed otherwise term usage
 * count will be increasing forever.
 *
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermUsageAwareIndexFactoryBean extends SimpleIndexFactoryBean {

  private TermUsageAwareIndexFactory mFactory;
  private TermUsageService mTermUsageService;

  public void setTermUsageService(final TermUsageService pTermUsageService) {
    mTermUsageService = pTermUsageService;
  }

  @Override
  public Object getObject() throws Exception {
    return mFactory;
  }

  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();

    mFactory = new TermUsageAwareIndexFactory(
      (SimpleIndexFactory) super.getObject(), mTermUsageService); 
  }
}

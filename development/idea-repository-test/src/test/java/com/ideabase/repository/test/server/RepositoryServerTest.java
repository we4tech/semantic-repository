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

package com.ideabase.repository.test.server;

import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.server.RepositoryServer;
import com.ideabase.repository.server.ServerServiceKey;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class RepositoryServerTest extends BaseTestCase {

  private ApplicationContext mServerContext;
  private RepositoryServer mRepositoryServer;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mServerContext = new ClassPathXmlApplicationContext(new String[] {
        "repositoryServerContext.xml"
    });
    mRepositoryServer = (RepositoryServer) mServerContext.
        getBean(ServerServiceKey.SERVER_IMPL);
  }

  public void testStart() {
    assertTrue(mRepositoryServer.start());
  }
}

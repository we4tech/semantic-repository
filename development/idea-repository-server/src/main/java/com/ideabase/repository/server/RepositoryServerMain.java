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

package com.ideabase.repository.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;

import java.util.Date;
import java.io.File;

/**
 * Server main is used for launching the repository server implementation.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class RepositoryServerMain {
  // this is intentionally kept public so that we can access from 3rd party
  // scripting environment such as jruby
  public RepositoryServer mRepositoryServer;
  public ApplicationContext mServerContext;

  public RepositoryServerMain() {
    mServerContext = new ClassPathXmlApplicationContext(new String[] {
        "repositoryServerContext.xml"
    });
    mRepositoryServer = (RepositoryServer) mServerContext.
        getBean(ServerServiceKey.SERVER_IMPL);
  }

  public static void main(String[] pArgs) throws InterruptedException {
    System.err.println("Starting Repository Server at " + new Date());
    setupApplicationLogger();
    setupRequiredSystemProperties();
    final RepositoryServerMain repositoryServerMain = new RepositoryServerMain();
    if (repositoryServerMain.mRepositoryServer.start()) {
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          System.out.println("Stopping repository server.");
          repositoryServerMain.mRepositoryServer.stop();
          System.out.println("Stopping repository server stopped.");
        }
      });
      Thread.currentThread().join();
    }
  }

  private static void setupApplicationLogger() {
    DOMConfigurator.configure("config/log4j.xml");
  }

  private static void setupRequiredSystemProperties() {
    // add jaas login  module & policy
    System.setProperty("java.security.auth.login.config", "security/jaas.config");
    System.setProperty("java.security.auth.policy", "security/repository.policy");
    System.setProperty("configuration", new File("./config").getAbsolutePath());
    System.setProperty("java.protocol.handler.pkgs", "com.ideabase.repository.core.protocols");
    System.err.println("Configuration path - " + System.getProperty("configuration"));
    if (Logger.getLogger(RepositoryServerMain.class).isDebugEnabled()) {
      System.setProperty("DEBUG", "true");
    }
  }
}

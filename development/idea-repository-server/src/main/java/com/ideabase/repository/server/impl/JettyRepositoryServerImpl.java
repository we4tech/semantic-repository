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

package com.ideabase.repository.server.impl;

import com.ideabase.repository.server.RepositoryServer;
import com.ideabase.repository.server.RepositoryServerConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.jasper.servlet.JspServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.thread.ThreadPool;
import org.mortbay.resource.Resource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.util.Log4jConfigServlet;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.net.MalformedURLException;

/**
 * Jetty based {@see RepositoryServer} implementation
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class JettyRepositoryServerImpl implements RepositoryServer {

  private static final String KEY_CONNECTOR = "connector";
  private static final String KEY_HANDLER = "handler";
  private static final String KEY_THREAD_POOL = "theadPool";
  private static final String KEY_TMPDIR = "tmpDir";
  private static final String CONTEXT_PATH = "/";

  private final Logger mLog = LogManager.getLogger(getClass());
  private RepositoryServerConfiguration mConfiguration;
  private SelectChannelConnector mConnector;
  private Server mServer;
  private Context mContext;
  private ThreadPool mThreadPool;

  public void setConfiguration(
      final RepositoryServerConfiguration pConfiguration) {
    mConfiguration = pConfiguration;
  }

  public boolean start() {
    mLog.debug("Starting jetty based repository server.");

    // find connector
    mConnector = mConfiguration.getProperty(KEY_CONNECTOR);
    // find thread pool
    mThreadPool = mConfiguration.getProperty(KEY_THREAD_POOL);
    if (mConnector == null || mThreadPool == null) {
      throw new RuntimeException("Please verify whether you have set " +
          "Connector or Thread pool configuration on server-beans.xml");
    }

    try {
      // create server instance
      mServer = new Server();
      mServer.addConnector(mConnector);
      mServer.setThreadPool(mThreadPool);

      // create restful web service handler
      mContext = setupWebServiceHandler();

      // start server
      mServer.start();
      mContext.getServletHandler().initialize();
      return true;
    } catch (Exception e) {
      mLog.warn(e);
      e.printStackTrace();
      throw new RuntimeException("Failed to initiate repository server.", e);
    }
  }

  private Context setupWebServiceHandler()
      throws InterruptedException, IOException {
    final String tmpDir = mConfiguration.getProperty(KEY_TMPDIR);
    final File tmpDirFile = new File(tmpDir);
    if (!tmpDirFile.exists()) {
      tmpDirFile.mkdirs();
    }

    final Context context = new Context(mServer, "/", Context.SESSIONS);

    // add jsp support
    context.addServlet(JspServlet.class, "*.jsp");
    context.setBaseResource(Resource.newClassPathResource("webapp"));
    context.addServlet(DefaultServlet.class, "/");

    // add spring servlet
    final ServletHolder servletHolder =
        new ServletHolder(DispatcherServlet.class);
    servletHolder.setInitParameter("contextConfigLocation",
                                   "classpath:/applicationContext.xml");
    servletHolder.setName("webservice-action-servlet");
    context.addServlet(servletHolder, "/rest/*");

    // add spring context listener
    context.getInitParams().put("contextConfigLocation",
                                "classpath:/applicationContext.xml");
    context.addEventListener(new ContextLoaderListener());

    // add log4j servlet
    context.addServlet(Log4jConfigServlet.class, "/log4j");

    return context;
  }

  public boolean stop() {
    mLog.debug("Stopping jetty based repository server.");
    if (mServer != null && mServer.isRunning()) {
      try {
        mContext.stop();
        mServer.stop();
        return true;
      } catch (Exception e) {
        mLog.warn(e);
        throw new RuntimeException("failed to stop a running instance " +
            "of repository server.");
      }
    }
    return false;
  }
}

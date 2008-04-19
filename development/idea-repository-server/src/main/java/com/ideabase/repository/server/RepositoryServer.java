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

/**
 * Repository server api can be used for implementing embed server.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public interface RepositoryServer {

  /**
   * Set repository server related configuration
   * @param pConfiguration server configuration
   */
  void setConfiguration(final RepositoryServerConfiguration pConfiguration);

  /**
   * Start repositroy server if exception found return {@code false}
   * @return return {@code true} if successfully started.
   */
  boolean start();

  /**
   * Stop repository server instance if exception found return {@code false}
   * @return return {@code true} if successfully stopped.
   */
  boolean stop();
}

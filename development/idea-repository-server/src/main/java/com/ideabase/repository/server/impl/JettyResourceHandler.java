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

import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.resource.Resource;

import java.net.MalformedURLException;
import java.io.IOException;

/**
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class JettyResourceHandler extends ResourceHandler {
  @Override
  public Resource getResource(final String s) throws MalformedURLException {
    final Resource realResource = super.getResource(s);
    try {
      return Resource.newSystemResource("/Users/nhmtanveerhossainkhanhasan/projects/ideabase-products/idea-repository/development/idea-repository-webapp/src/main/webapp/index.jsp");
    } catch (IOException e) {
      return null;
    }
  }
}

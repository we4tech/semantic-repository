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

import java.util.Map;

/**
 * Repository server related configuration bean
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class RepositoryServerConfiguration {
  private Map<String, Object> properties;

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(final Map<String, Object> pProperties) {
    properties = pProperties;
  }

  public <T> T getProperty(final String pKey) {
    return (T) properties.get(pKey);
  }
}

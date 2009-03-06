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

import java.io.File;
import java.net.URI;

/**
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class FileBean implements org.springframework.beans.factory.FactoryBean {

  private File mFile;

  public void setFile(final String pFile) {
    mFile = new File(pFile);
  }

  public Object getObject() throws Exception {
    return mFile;
  }

  public Class getObjectType() {
    return mFile.getClass();
  }

  public boolean isSingleton() {
    return true;
  }
}

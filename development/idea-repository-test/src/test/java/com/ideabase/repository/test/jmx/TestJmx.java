/* $Id: TestJmx.java 250 2008-01-07 10:18:29Z hasan $ */
/*
 ******************************************************************************
 *   Copyright (C) 2007 IDEASense, (hasin & hasan) 
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.test.jmx;

import javax.management.*;
import java.lang.management.ManagementFactory;

/**
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class TestJmx {
  public static interface TestBeanMBean {
    public String getName();

    public void setName(final String pName);

    public String getEmail();

    public void setEmail(final String pEmail);

    public void register(TestBeanMBean pTestBeanMBean);
  }

  public static class TestBean implements TestBeanMBean {
    private String name;
    private String email;

    public String getName() {
      return name;
    }

    public void setName(final String pName) {
      name = pName;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(final String pEmail) {
      email = pEmail;
    }

    public void register(TestBeanMBean pTestBeanMBean) {
      System.out.println("Registering bean - " + pTestBeanMBean);
    }
  }
  public static void main(String[] args) throws MalformedObjectNameException,
      NotCompliantMBeanException, MBeanRegistrationException,
      InstanceAlreadyExistsException, InterruptedException {
    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName =
        new ObjectName("com.ideabase.repository.jmx.test:type=Test");
    final TestBeanMBean test = new TestBean();
    test.setName("hasan");

    // register
    server.registerMBean(test, objectName);
    System.out.println("server started.");
    Thread.sleep(Long.MAX_VALUE);
  }
}

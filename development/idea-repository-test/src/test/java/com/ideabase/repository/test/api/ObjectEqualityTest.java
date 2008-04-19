/* $Id: ObjectEqualityTest.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.test.api;

import junit.framework.TestCase;
import com.ideabase.repository.common.object.User;

/**
 * Test object equality.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ObjectEqualityTest extends TestCase {

  public void testUserEquality() {
    final User user1 = new User();
    final User user2 = new User();

    assertFalse("Both object is equal", user1.equals(user2));

    user1.setId(500);
    user2.setId(500);
    assertTrue("Both object are not equal", user1.equals(user2));

    user2.setId(300);
    assertFalse("Both object are not equal", user1.equals(user2));
  }

  public void test1() {
    System.out.println(new Integer(33).hashCode() + " VS " +
                       new Integer(33).hashCode());
  }
}

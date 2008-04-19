/* $Id: SerializerFactoryTest.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.test.common;

import java.sql.Timestamp;

import junit.framework.TestCase;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.common.serializer.SerializerFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * Test serializer factory class.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class SerializerFactoryTest extends TestCase {

  private static final Logger LOG =
      LogManager.getLogger(SerializerFactoryTest.class);

  public void testSerialization() {
    final User user = new User();
    user.setUser("hasankhan");
    user.setPassword("nhmthk");
    user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
    user.setLastUpdatedOn(new Timestamp(System.currentTimeMillis()));
    user.setRole(User.FIELD_ROLE_ADMIN, true);
    user.setRole(User.FIELD_ROLE_READ, true);
    user.setRole(User.FIELD_ROLE_WRITE, true);
    final String relationTypeUser = "user";
    final String relationTypeBlog = "blog";
    user.addRelatedItem(relationTypeUser, 33);
    user.addRelatedItem(relationTypeUser, 44);
    user.addRelatedItem(relationTypeUser, 55);

    user.addRelatedItem(relationTypeBlog, 11);
    user.addRelatedItem(relationTypeBlog, 22);

    final String serializedContent = SerializerFactory.getInstance().
        serializeObject(SerializerFactory.XML, user);

    // verify
    assertNotNull("No such serialized content found", serializedContent);
    LOG.debug("Serialized - " + serializedContent);

    // deserialize
    final User newUser = SerializerFactory.getInstance().
        deserializeObject(SerializerFactory.XML, serializedContent);

    // verify
    assertNotNull("User not null", newUser);
    assertNotNull("User title not defined", newUser.getTitle());
    assertFalse("User timestamps are not defined",
        (newUser.getCreatedOn() == null) || (newUser.getLastUpdatedOn() == null));
    assertFalse("User fields are empty",
        (newUser.getFields() == null || newUser.getFields().isEmpty()));
    assertFalse("No related item found", (newUser.getRelatedItemsMap() == null
        || newUser.getRelatedItemsMap().isEmpty()));
    
    LOG.debug("User - " + newUser);
  }
}

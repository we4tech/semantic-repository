/* $Id: ObjectBaseSerializer.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.common.serializer;

import com.ideabase.repository.common.object.ObjectBase;

/**
 * All implementor will be responsible to serialize {@see ObjectBase} object
 * in a specific format.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface ObjectBaseSerializer {

  /**
   * Serialize all {@see ObjectBase} extended object and send out the output
   * in a formatted string.
   * @param pObject object thats intended for serialization.
   * @return the serialized output.
   */
  <T extends ObjectBase> String serializeObject(final T pObject);

  /**
   * Convert XML/JSON/HTML/TEXT to a specific object.
   * @param pContent the content type can be in XML/JSON/HTML/TEXT
   * @return object of type {@see ObjectBase}
   */
  <T extends ObjectBase> T deserializeObject(final String pContent);
}

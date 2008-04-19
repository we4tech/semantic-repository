/* $Id: SerializerFactory.java 260 2008-03-19 05:38:06Z hasan $ */
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
 * $LastChangedDate: 2008-03-19 11:38:06 +0600 (Wed, 19 Mar 2008) $
 * $LastChangedRevision: 260 $
 ******************************************************************************
*/
package com.ideabase.repository.common.serializer;

import com.ideabase.repository.common.exception.ServiceException;
import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.common.serializer.impl.PHPObjectBaseSerializer;
import com.ideabase.repository.common.serializer.impl.XmlObjectBaseSerializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Serialize object into a specific format.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class SerializerFactory {

  /**
   * Logger instance for the whole class.
   */
  private final Logger LOG = LogManager.getLogger(getClass());

  /**
   * Singleton instance for serialization factory.
   * TODO: this part needs to move on spring container.
   */
  private static final SerializerFactory INSTANCE =
      new SerializerFactory();

  /**
   * xml serializer object reference.
   */
  public static final String XML = "xml";

  /**
   * JSON serializer object reference.
   */
  public static final String JSON = "json";

  /**
   * Text serializer object reference.
   */
  public static final String TEXT = "txt";

  /**
   * HTML serializer object reference.
   */
  public static final String HTML = "html";

  /**
   * PHP serializer object reference.
   */
  public static final String PHP = "php";

  /**
   * List of available serializers. all serializers are registered with the
   * extention of the format. for example "xml,  txt, html, json" these are
   * used for formatting in xml, html and json format.
   */
  private final Map<String, ObjectBaseSerializer> mSerializers =
      new HashMap<String, ObjectBaseSerializer>();

  /**
   * Default constructor, register the predefined supported format.
   */
  private SerializerFactory() {
    registerAvailableSerializers();
  }

  // TODO: register php object serializer
  private void registerAvailableSerializers() {
    LOG.info("Registering available serializer object");
    mSerializers.put(XML, new XmlObjectBaseSerializer());
    mSerializers.put(PHP, new PHPObjectBaseSerializer());
  }

  /**
   * Return the singleton isntance of this class.
   * @return the singleton instance of this class.
   */
  public static SerializerFactory getInstance() {
    return INSTANCE;
  }

  /**
   * Serialize an object and return the serialized value. if no associated
   * serializer available, it will throw an ServiceException.
   * @param pFormat serializer type (xml|json|html|text)
   * @param pObject the object which is need to be serialized.
   * @return Serialized content.
   */
  public <T extends ObjectBase> String serializeObject(final String pFormat,
                                                       final T pObject) {
    if (pFormat == null || pObject == null) {
      throw new IllegalArgumentException("Format or Object is an empty");
    }
    final ObjectBaseSerializer objectBaseSerializer = mSerializers.get(pFormat);
    if (objectBaseSerializer == null) {
      throw ServiceException.aNew(pFormat, "No such format available");
    }
    return objectBaseSerializer.serializeObject(pObject);
  }

  public <T extends ObjectBase> T deserializeObject(final String pFormat,
                                                    final String pContent) {
    if (pFormat == null || pContent == null) {
      throw new IllegalArgumentException("Format or content is an empty");
    }
    final ObjectBaseSerializer objectBaseSerializer = mSerializers.get(pFormat);
    if (objectBaseSerializer == null) {
      throw ServiceException.aNew(pFormat, "No such format available");
    }
    return (T) objectBaseSerializer.deserializeObject(pContent);
  }
}

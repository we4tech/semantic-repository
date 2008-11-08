/**
 * $Id: ExtendedUtilNamespaceHandler.java 263 2008-04-08 11:42:34Z hasan $
 * *****************************************************************************
 *    Copyright (C) 2005 - 2007 somewhere in .Net ltd.
 *    All Rights Reserved.  No use, copying or distribution of this
 *    work may be made except in accordance with a valid license
 *    agreement from somewhere in .Net LTD.  This notice must be included on
 *    all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-04-08 17:42:34 +0600 (Tue, 08 Apr 2008) $
 * $LastChangedRevision: 263 $
 * *****************************************************************************
 */

package com.ideabase.repository.core.namespace;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Element;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Look up system property.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class ExtendedUtilNamespaceHandler extends NamespaceHandlerSupport {

  private static final Logger LOG =
      LogManager.getLogger(ExtendedUtilNamespaceHandler.class);

  private static final String ELEMENT_RESOURCE = "resource";
  private static final String ATTR_FROM_SYSTEM_PROPERTY = "from-property";
  private static final String ATTR_OR_DEFAULT = "or-default";
  private static final String ATTR_SCOPE = "scope";
  private static final String ATTR_PATH_PREFIX = "path-prefix";

  private static final String PROPERTY_FROM_SYSTEM_PROPERTY = "fromSystemProperty";
  private static final String PROPERTY_MAGIC_URL_PREFIX = "__extUrlPrefix";
  private static final String RESOURCE_SUFFIX = ".properties";
  private static final String SCOPE_NAME_FROM_SYSTEM_PROPERTIES_PREFIX = "sys:";

  private static final Map<String, Properties> SCOPE_PROPERTIES_CACHES =
      new HashMap<String, Properties>();

  /**
   * Default constructor, register all supported bean definition.
   */
  public void init() {
    LOG.info("Initialize extended utility namespace handler.");
    this.registerBeanDefinitionParser(
        ELEMENT_RESOURCE, new ResourceBeanDefinitionParser());
  }

  private static class ResourceBeanDefinitionParser
      extends AbstractBeanDefinitionParser {

    protected AbstractBeanDefinition parseInternal(
        final Element pElement, final ParserContext pParserContext) {
      LOG.debug("Parsing resource bean definition.");
      final BeanDefinitionBuilder definitionBuilder =
          BeanDefinitionBuilder.rootBeanDefinition(ResourceFactoryBean.class);
      initProperties(pElement, definitionBuilder);
      return definitionBuilder.getBeanDefinition();
    }

    /**
     * Initiate properties for Resource bean definition. initially properties
     * are located from {@code System} default properties.<br>
     * if {@code scope} attribute is explicitly mentioned, property will be
     * located from specific scope properties. for example - <br>
     * < ext-util:resource ... scope="test"/ > <br>
     * "test.properties" will be located from classpath. from where the
     * property will be returned.
     */
    private void initProperties(final Element pElement,
                                final BeanDefinitionBuilder pResourceBean) {

      // system property name and scope string
      final String systemProperty =
          pElement.getAttribute(ATTR_FROM_SYSTEM_PROPERTY);

      // properties scope
      final String scopeString = pElement.getAttribute(ATTR_SCOPE);
      final String pathPrefix = pElement.getAttribute(ATTR_PATH_PREFIX);

      if (systemProperty != null) {
        final String value =
            findPropertyValue(systemProperty, pathPrefix, scopeString);
        if (value != null) {
          pResourceBean.addPropertyValue(PROPERTY_FROM_SYSTEM_PROPERTY, value);
        } else {
          final String defaultValue = pElement.getAttribute(ATTR_OR_DEFAULT);
          pResourceBean.addPropertyValue(
              PROPERTY_FROM_SYSTEM_PROPERTY, defaultValue);
        }
      } else {
        throw new RuntimeException(
            "No system property or default value was defined.");
      }
    }

    private String findPropertyValue(final String pProperty,
                                     final String pPathPrefix,
                                     final String pScopeString) {
      if (pScopeString == null || pScopeString.length() == 0) {
        // no scope is defined, which refers to the default properties source
        // from System.
        return System.getProperty(pProperty);
      } else {
        LOG.debug("locating scope oriented resource files.");
        // build scoped properties full path
        final String propertiesPath =
            buildPropertiesPath(pPathPrefix, pScopeString);

        // look up cache with scope and prefix.
        Properties scopedProperties =
            findPropertiesFromCache(propertiesPath);

        // if cache doesnt hold any properties, load the properties file
        // and cache.
        if (scopedProperties == null) {
          System.out.println("__PTH - " + propertiesPath);
          scopedProperties = loadPropertiesAndCache(propertiesPath);
        }

        if (scopedProperties != null) {
          return scopedProperties.getProperty(pProperty);
        }
        return null;
      }
    }

    private Properties loadPropertiesAndCache(final String pPropertiesPath) {
      try {
        // load it.
        final URL url = ResourceUtils.getURL(pPropertiesPath);
        final Properties properties = new Properties();
        final InputStream urlStream = url.openConnection().getInputStream();
        properties.load(urlStream);
        urlStream.close();

        // cache it .
        SCOPE_PROPERTIES_CACHES.put(pPropertiesPath, properties);

        // return it.
        return properties;
      } catch (Exception e) {
        LOG.warn("failed to load properties file - " + pPropertiesPath, e);
        throw new IllegalArgumentException("Properties file doesn't exists.", e);
      }
    }

    private String buildPropertiesPath(final String pPathPrefix,
                                       final String pScopeString) {

      // determine the appropriate scope name, if any scope name is prefixed
      // by "sys:" will return the scope name from system properties.
      final String scope;
      if (pScopeString.startsWith(SCOPE_NAME_FROM_SYSTEM_PROPERTIES_PREFIX)) {
        final String[] split = pScopeString.split(":");
        scope = System.getProperty(split[1]);
        if (scope == null) {
          throw new IllegalArgumentException("invalid system property - " +
              split[1] + ", which is not defined. use -Dsys:" +
              split[1] + "=<value> to define scope string.");
        }
      } else {
        scope = pScopeString;
      }

      final String propertiesPath;
      if (pPathPrefix != null && pPathPrefix.length() > 0) {

        String prefix = pPathPrefix;
        if (prefix.startsWith(SCOPE_NAME_FROM_SYSTEM_PROPERTIES_PREFIX)) {
          final String[] split = prefix.split(":");
          final String prefixRef = split[1];
          prefix = System.getProperty(prefixRef);

          if (prefix == null) {
            throw new IllegalArgumentException("invalid system property - " +
              prefixRef + ", which is not defined. use -D" +
              prefixRef + "=<value> to define scope string.");
          }
        }

        // determine the appopriate path prefix, if sys: is prefixed, the path
        // prefix value will be returned from system properties.
        propertiesPath = new StringBuilder()
            .append(prefix)
            .append(File.separator)
            .append(scope)
            .append(RESOURCE_SUFFIX).toString();
      } else {
        propertiesPath = "classpath:" + scope + RESOURCE_SUFFIX;
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("locating properties resource - " + propertiesPath);
      }
      return propertiesPath;
    }

    private Properties findPropertiesFromCache(final String pPropertiesPath) {
      return SCOPE_PROPERTIES_CACHES.get(pPropertiesPath);
    }

    private static class ResourceFactoryBean implements FactoryBean {

      private String mSystemProperty;

      public void setFromSystemProperty(final String pSystemProperty) {
        mSystemProperty = pSystemProperty;
      }

      public Object getObject() throws Exception {
        return mSystemProperty;
      }

      public Class getObjectType() {
        return String.class;
      }

      public boolean isSingleton() {
        return true;
      }
    }
  }
}

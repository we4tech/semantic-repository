/* $Id: Cache.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.common.cache.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.ideabase.repository.common.cache.CacheInvalidator;
import com.ideabase.repository.common.cache.EmptyCacheInvalidatorImpl;
import com.ideabase.repository.common.cache.CacheableObjectValidator;
import com.ideabase.repository.common.cache.EmptyCacheableObjectValidatorImpl;

/**
 * Annotation for defining properties for cache object. this annotation specify
 * cache expire time or cache invalidation rule.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cache {

  /**
   * Default factory method for {@see CacheableObjectValidator} and
   * {@see CacheInvalidator}.
   */
  String FACTORY_METHOD = "getInstance";

  /**
   * Caching rule, default value is {@see CacheType#CACHE_RETURNED_OBJECT}
   * @return cache type is returned.
   */
  CacheType cacheType() default CacheType.CACHE_RETURNED_OBJECT;

  /**
   * Explictly mention the type of parameter object. if nothing is methioned
   * a default value {@code String.class} is used, which means nothing special,
   * rather internally we take the the first parameter from the method.
   * @return parameter class type.
   */
  Class<?> parameterType() default String.class;

  /**
   * Verify whether object is valid for caching or not.
   * @return the cache object validator implemented class.
   */
  Class<? extends CacheableObjectValidator> cachableObjectValidator()
          default EmptyCacheableObjectValidatorImpl.class;

  /**
   * Define factory method to create instance for {@see CacheableObjectValidator}
   * and {@see CacheInvalidator}. implemented class.
   * @return the factory method name.
   */
  String factoryMethod() default FACTORY_METHOD;
  
  /**
   * Declare explicitly the cache key. if this property is not defined,
   * by default the random cache key is generated.
   * @return cache key in string, the default value is an empty string.
   */
  String cacheKey() default "";

  /**
   * Define the expire time of the cache, by default the cache won't be expired.
   * @return cache expire time in milliseconds.
   */
  long expireTimeInMillis() default 0;

  /**
   * Or specifiy the cache invalidating logic implemented class. this class
   * @return cache invalidating logic implemented class name.
   */
  Class<? extends CacheInvalidator> invalidator()
          default EmptyCacheInvalidatorImpl.class;
}

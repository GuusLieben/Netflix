/*
 * Copyright © 2018. Guus Lieben.
 * All rights reserved.
 */

package com.netflix.handles;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import static com.netflix.commons.Commons.exception;

public class PropertiesHandle {

  private static Properties properties = new Properties();
  private static ThreadLocal<InputStream> inputStream = new ThreadLocal<>();

  public static String get(String property) {
    // Read the properties file
    inputStream.set(PropertiesHandle.class.getClassLoader().getResourceAsStream("package.properties"));

    // Make sure we're not reading null
    Objects.requireNonNull(inputStream);

    // Assume none found if exception throws
    try {
      // Load the properties from the file
      properties.load(inputStream.get());
      // Grab the property requested
      return properties.getProperty(property);
    } catch (IOException e) {
      exception(e);
      return null;
    }
  }
}

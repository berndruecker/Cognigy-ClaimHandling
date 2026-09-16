package com.camunda.demo.claimhandling.workers;

import java.util.Map;

/** Null-safe accessors for the loosely-typed JSON maps (customer/claim/conversation/DMN results) job workers receive as process variables. */
final class Vars {

  private Vars() {}

  static String str(Map<String, Object> map, String key, String defaultValue) {
    if (map == null) {
      return defaultValue;
    }
    Object value = map.get(key);
    return value != null ? value.toString() : defaultValue;
  }

  static boolean bool(Map<String, Object> map, String key, boolean defaultValue) {
    if (map == null) {
      return defaultValue;
    }
    Object value = map.get(key);
    return value instanceof Boolean bool ? bool : defaultValue;
  }
}

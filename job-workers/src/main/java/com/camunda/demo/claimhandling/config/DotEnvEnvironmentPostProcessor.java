package com.camunda.demo.claimhandling.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Loads the repository-root {@code .env} file (Camunda client credentials, see
 * {@code .env.example}) into the Spring Environment as {@code camunda.client.*} properties,
 * without ever being committed to the repo or read by tooling other than this loader.
 *
 * <p>The {@code .env} file uses plain {@code key=value} lines with dotted property names
 * (e.g. {@code camunda.client.mode=saas}), which {@link Properties#load(InputStream)} parses
 * directly. Values loaded here take precedence over {@code application.yaml} defaults but not
 * over real OS/process environment variables, so a real deployment env still wins.
 */
public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private static final String PROPERTY_SOURCE_NAME = "dotenv";
  private static final String DOTENV_FILENAME = ".env";
  private static final int MAX_PARENT_SEARCH_DEPTH = 5;

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    findDotEnvFile().ifPresent(file -> {
      Map<String, Object> values = readProperties(file);
      if (!values.isEmpty()) {
        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, values));
      }
    });
  }

  private Map<String, Object> readProperties(Path file) {
    Properties properties = new Properties();
    try (InputStream in = Files.newInputStream(file)) {
      properties.load(in);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read " + file, e);
    }
    Map<String, Object> values = new LinkedHashMap<>();
    for (String name : properties.stringPropertyNames()) {
      String value = properties.getProperty(name);
      if (value != null && !value.isBlank()) {
        values.put(name, value);
      }
    }
    return values;
  }

  /** Walks up from the current working directory so the app finds {@code .env} whether it's run from the repo root or from {@code job-workers/}. */
  private Optional<Path> findDotEnvFile() {
    Path dir = Path.of("").toAbsolutePath();
    for (int i = 0; i <= MAX_PARENT_SEARCH_DEPTH && dir != null; i++) {
      Path candidate = dir.resolve(DOTENV_FILENAME);
      if (Files.isRegularFile(candidate)) {
        return Optional.of(candidate);
      }
      dir = dir.getParent();
    }
    return Optional.empty();
  }
}

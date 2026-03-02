package com.snapspace.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to read application configuration from a {@code config.properties} file.
 *
 * <p>
 * The properties file should be located in the {@code src/main/resources} folder.
 * This class loads the properties once and provides access to them via
 * {@link #get(String)} and {@link #get(String, String)}.
 * </p>
 */
public class PropertiesUtil {

    /**
     * Properties object storing all loaded key-value pairs.
     */
    private static final Properties props = new Properties();

    static {
        try (InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, config.properties not found!");
            } else {
                props.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a property value by key.
     *
     * @param key the property key
     * @return the value, or {@code null} if the key is not present
     */
    public static String get(String key) {
        return props.getProperty(key);
    }

    /**
     * Retrieves a property value by key, falling back to a default if absent.
     *
     * <p>
     * Useful for optional config like {@code hibernate.show_sql} where a
     * sensible default exists and the app shouldn't fail if the key is missing.
     * </p>
     *
     * @param key          the property key
     * @param defaultValue the value to return if the key is not present
     * @return the configured value, or {@code defaultValue} if absent
     */
    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}

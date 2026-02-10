package com.snapspace.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to read application configuration from a {@code config.properties} file.
 * <p>
 * The properties file should be located in the {@code src/main/resources} folder.
 * This class loads the properties once and provides access to them via {@link #get(String)}.
 * </p>
 */
public class PropertiesUtil {

    /**
     * Properties object storing all loaded key-value pairs
     */
    private static Properties props = new Properties();

    // Load properties statically when class is first accessed
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
     * Retrieves a property value by key from the loaded properties.
     *
     * @param key the property key
     * @return the property value as a {@link String}, or {@code null} if the key is not found
     */
    public static String get(String key) {
        return props.getProperty(key);
    }
}

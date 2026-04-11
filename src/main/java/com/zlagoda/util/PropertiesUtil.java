package com.zlagoda.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private PropertiesUtil() {
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("db.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("Файл db.properties не знайдено");
            }

            PROPERTIES.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException("Помилка завантаження db.properties", e);
        }
    }
}
package com.example.petstore.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvConfig {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream f = new FileInputStream("src/test/resources/env.properties")) {
            Properties env = new Properties();
            env.load(f);
            String activeEnv = env.getProperty("env", "dev").trim();
            try (FileInputStream f2 = new FileInputStream("src/test/resources/env-" + activeEnv + ".properties")) {
                props.load(f2);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load environment properties", e);
        }
    }

    public static String getBaseUrl() {
        return props.getProperty("base.url").trim();
    }
}

package com.agrinexus.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:${DB_URL:${DATABASE_URL:}}}")
    private String rawUrl;

    @Value("${spring.datasource.username:${DB_USERNAME:${DB_USER:avnadmin}}}")
    private String rawUsername;

    @Value("${spring.datasource.password:${DB_PASSWORD:${DB_PASS:}}}")
    private String rawPassword;

    @Value("${spring.datasource.driver-class-name:}")
    private String explicitDriver;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        String url = rawUrl != null ? rawUrl.trim() : "";
        String username = rawUsername != null ? rawUsername.trim() : "";
        String password = rawPassword != null ? rawPassword : "";
        String driver = explicitDriver != null ? explicitDriver.trim() : "";

        // Check if H2 test database
        if (url.startsWith("jdbc:h2:")) {
            log.info("Configuring H2 in-memory test database: {}", url);
            config.setJdbcUrl(url);
            config.setUsername(username.isEmpty() ? "sa" : username);
            config.setPassword(password);
            config.setDriverClassName("org.h2.Driver");
            return new HikariDataSource(config);
        }

        // Handle standard MySQL URI format e.g. mysql://user:pass@host:port/dbname?params
        if (url.startsWith("mysql://")) {
            try {
                URI uri = URI.create(url);
                String userInfo = uri.getUserInfo();
                if (userInfo != null && !userInfo.isEmpty()) {
                    String[] parts = userInfo.split(":", 2);
                    if (parts.length > 0 && !parts[0].isEmpty()) {
                        username = parts[0];
                    }
                    if (parts.length > 1) {
                        password = parts[1];
                    }
                }
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 3306;
                String path = uri.getPath();
                String query = uri.getQuery();

                StringBuilder sb = new StringBuilder("jdbc:mysql://")
                        .append(host)
                        .append(":")
                        .append(port)
                        .append(path != null ? path : "");

                sb.append("?sslMode=REQUIRED&allowPublicKeyRetrieval=true");
                if (query != null && !query.isEmpty()) {
                    for (String param : query.split("&")) {
                        String lower = param.toLowerCase();
                        if (!lower.startsWith("ssl-mode") && !lower.startsWith("sslmode") && !lower.startsWith("allowpublickeyretrieval")) {
                            sb.append("&").append(param);
                        }
                    }
                }
                url = sb.toString();
            } catch (Exception e) {
                log.warn("Failed to parse mysql:// URI as standard URI, falling back to prefix prepending: {}", e.getMessage());
                url = "jdbc:" + url;
            }
        }

        // Ensure jdbc: prefix
        if (!url.isEmpty() && !url.startsWith("jdbc:")) {
            url = "jdbc:" + url;
        }

        // Normalize ssl-mode to sslMode for MySQL Connector/J
        if (url.contains("ssl-mode=")) {
            url = url.replace("ssl-mode=REQUIRED", "sslMode=REQUIRED")
                     .replace("ssl-mode=required", "sslMode=REQUIRED")
                     .replace("ssl-mode=PREFERRED", "sslMode=PREFERRED")
                     .replace("ssl-mode=preferred", "sslMode=PREFERRED");
        }

        // If talking to a cloud host and sslMode is not explicitly in URL, add it
        if (!url.isEmpty() && !url.contains("sslMode=") && !url.contains("useSSL=")) {
            String separator = url.contains("?") ? "&" : "?";
            url = url + separator + "sslMode=REQUIRED&allowPublicKeyRetrieval=true";
        }

        if (driver.isEmpty()) {
            driver = "com.mysql.cj.jdbc.Driver";
        }

        // Mask credentials for logging
        String sanitizedUrl = url.replaceAll(":[^/@]+@", ":****@");
        log.info("Configuring MySQL DataSource -> Target JDBC URL: {}, Username: {}, Driver: {}",
                sanitizedUrl, username, driver);

        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);

        config.setConnectionTimeout(30000);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }
}


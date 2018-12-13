package tech.brownbear.mysql;

import com.google.common.base.Preconditions;

public class Configuration {
    private final String host;
    private final int port;
    private final String dbName;

    public static class Builder {
        private String host = "127.0.0.1";
        private int port = 3306;
        private String dbName;

        public Builder setHost(String host) {
            Preconditions.checkNotNull(host);
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setDbName(String dbName) {
            this.dbName = dbName;
            return this;
        }

        public Configuration build() {
            Preconditions.checkNotNull(host);
            Preconditions.checkNotNull(dbName);
            return new Configuration(
                host,
                port,
                dbName);
        }
    }

    private Configuration(String host, int port, String dbName) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDbName() {
        return dbName;
    }
}

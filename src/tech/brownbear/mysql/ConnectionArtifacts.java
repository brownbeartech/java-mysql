package tech.brownbear.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Artifacts from an insert or update query
 */
public class ConnectionArtifacts implements AutoCloseable {
    private PreparedStatement ps;
    private ResultSet rs;

    public void with(PreparedStatement ps) {
        this.ps = ps;
    }

    public void with(ResultSet rs) {
        this.rs = rs;
    }

    public PreparedStatement ps() {
        return ps;
    }

    public ResultSet rs() {
        return rs;
    }

    @Override
    public void close() {
        Closeables.closeQuietly(rs);
        Closeables.closeQuietly(ps);
    }
}

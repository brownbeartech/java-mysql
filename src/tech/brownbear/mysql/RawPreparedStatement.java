package tech.brownbear.mysql;

import com.google.common.base.Preconditions;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RawPreparedStatement {
    private List<Param> params = new ArrayList<>();
    protected final String query;

    private class Param {
        final int type;
        final Object value;

        public Param(
            Object value,
            int type) {
            this.value = value;
            this.type = type;
        }
    }

    public RawPreparedStatement(String query) {
        this.query = query;
    }

    static Pattern pattern = Pattern.compile("\\?");

    public RawPreparedStatement setString(String value) {
        addParam(new Param(value, Types.VARCHAR));
        return this;
    }

    public RawPreparedStatement setInteger(Integer value) {
        addParam(new Param(value, Types.INTEGER));
        return this;
    }

    public RawPreparedStatement setLong(Long value) {
        addParam(new Param(value, Types.BIGINT));
        return this;
    }

    private void addParam(Param param) {
        params.add(param);
    }

    protected String rawQuery() {
        return query;
    }

    private int argumentCount() {
        int i = 0;
        Matcher m = pattern.matcher(query);
        while (m.find()) {
            i++;
        }
        return i;
    }

    public <T> List<T> executeQuery(Connection connection, ConnectionArtifacts artifacts, RowMapper<T> rowMapper) throws SQLException {
        List<T> results = new ArrayList<>();
        artifacts.with(connection.prepareStatement(rawQuery()));
        prepare(artifacts.ps());
        artifacts.with(artifacts.ps().executeQuery());
        int rowNo = 1;
        while (artifacts.rs().next()) {
            results.add(rowMapper.map(artifacts.rs(), rowNo++));
        }
        return results;
    }

    public int executeUpdate(Connection connection, ConnectionArtifacts artifacts) throws SQLException {
        artifacts.with(prepareStatement(connection));
        prepare(artifacts.ps());
        return artifacts.ps().executeUpdate();
    }

    public Integer executeInsert(Connection connection, ConnectionArtifacts artifacts) throws SQLException {
        artifacts.with(connection.prepareStatement(rawQuery(), Statement.RETURN_GENERATED_KEYS));
        prepare(artifacts.ps());
        artifacts.ps().executeUpdate();
        artifacts.with(artifacts.ps().getGeneratedKeys());
        if (artifacts.rs().next()) {
            return artifacts.rs().getInt(1);
        }
        return null;
    }

    public void executeBulkInsert(Connection connection, ConnectionArtifacts artifacts) throws SQLException {
        artifacts.with(connection.prepareStatement(rawQuery()));
        prepare(artifacts.ps());
        artifacts.ps().executeUpdate();
    }

    protected void prepare(PreparedStatement ps) throws SQLException {
        int argumentCount = argumentCount();
        Preconditions.checkArgument(
            params.size() == argumentCount,
            "Parameter list size error. Expected: " + argumentCount + ", Provided : " + params.size());
        int i = 1;
        for (Param param : params) {
            if (param.value != null) {
                if (param.type == Types.VARCHAR) {
                    ps.setString(i, (String) param.value);
                } else if (param.type == Types.INTEGER) {
                    ps.setInt(i, (int) param.value);
                } else if (param.type == Types.BIGINT) {
                    ps.setLong(i, (long) param.value);
                }
            } else {
                ps.setNull(i, param.type);
            }
            i++;
        }
    }

    protected PreparedStatement prepareStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(rawQuery());
    }
}

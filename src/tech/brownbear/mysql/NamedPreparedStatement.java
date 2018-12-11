package tech.brownbear.mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

public class NamedPreparedStatement extends RawPreparedStatement {
    private Map<String, Param> paramsByName = new HashMap<>();

    static Pattern pattern = Pattern.compile(":(\\w+)");

    private class Param {
        final int type;
        final Object value;
        final String name;

        public Param(
            String name,
            Object value,
            int type) {
            this.name = name;
            this.value = value;
            this.type = type;
        }
    }

    public NamedPreparedStatement(String query) {
        super(query);
    }

    public NamedPreparedStatement setString(String name, String value) {
        addParam(new Param(name, value, Types.VARCHAR));
        return this;
    }

    public NamedPreparedStatement setInteger(String name, Integer value) {
        addParam(new Param(name, value, Types.INTEGER));
        return this;
    }

    public NamedPreparedStatement setLong(String name, Long value) {
        addParam(new Param(name, value, Types.BIGINT));
        return this;
    }

    private void addParam(Param param) {
        Preconditions.checkArgument(!paramsByName.containsKey(param.name), "Parameter with key '" + param.name + "' already exists");
        paramsByName.put(param.name, param);
    }

    private Set<String> getOrderedParameterNames() {
        Set<String> names = new LinkedHashSet<>();
        Matcher m = pattern.matcher(query);
        while (m.find()) {
            names.add(m.group(1));
        }
        return names;
    }

    @Override
    protected String rawQuery() {
        return query.replaceAll(":(\\w+)", "?");
    }

    @Override
    protected void prepare(PreparedStatement ps) throws SQLException {
        int i = 1;
        for (String name : getOrderedParameterNames()) {
            Param param = paramsByName.get(name);
            Preconditions.checkNotNull(param, "SQL statement is missing a value for '" + name + "'");
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
}

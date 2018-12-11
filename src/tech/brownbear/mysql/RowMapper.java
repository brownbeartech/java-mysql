package tech.brownbear.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {
    T map(ResultSet rs, int rowNo) throws SQLException;
}

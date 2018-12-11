package tech.brownbear.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * <R> The result of each statement's execution can be stored in a custom container, R
 */
public class Transaction<R> {
    public final List<Statement<R>> statements;

    @FunctionalInterface
    public interface Statement<R> {
        void execute(Connection connection, ConnectionArtifacts artifacts, R result) throws SQLException;
    }
    
    public Transaction(List<Statement<R>> statements) {
        this.statements = ImmutableList.copyOf(statements);
    }

    public void run(Connection connection, ArtifactCollection collection, R result) throws SQLException {
        for (Statement<R> statement : statements) {
            statement.execute(connection, collection.provide(), result);
        }
    }

    public static class Builder<R> {
        public List<Statement<R>> statements = new ArrayList<>();
 
        public Builder<R> addStatement(Statement<R> s) {
            this.statements.add(s);
            return this;
        }

        public Transaction<R> build() {
            return new Transaction<R>(statements);
        }
    }
}

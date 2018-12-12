package tech.brownbear.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class Transaction {
    public final List<Statement> statements;

    @FunctionalInterface
    public interface Statement {
        void execute(Connection connection, ConnectionArtifacts artifacts) throws SQLException;
    }

    public Transaction(List<Statement> statements) {
        this.statements = ImmutableList.copyOf(statements);
    }

    public void run(Connection connection, ArtifactCollection collection) throws SQLException {
        for (Statement statement : statements) {
            statement.execute(connection, collection.provide());
        }
    }

    public static class Builder {
        public List<Statement> statements = new ArrayList<>();

        public Builder addStatement(Statement s) {
            this.statements.add(s);
            return this;
        }

        public Transaction build() {
            return new Transaction(statements);
        }
    }
}

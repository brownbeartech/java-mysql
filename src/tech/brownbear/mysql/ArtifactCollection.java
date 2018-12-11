package tech.brownbear.mysql;

import java.util.ArrayList;
import java.util.List;

/**
 * A closeable collection of connection artifacts
 */
public class ArtifactCollection implements AutoCloseable {
    private List<ConnectionArtifacts> artifacts = new ArrayList<>();

    public ConnectionArtifacts provide() {
        ConnectionArtifacts artifacts = new ConnectionArtifacts();
        this.artifacts.add(artifacts);
        return artifacts;
    }

    @Override
    public void close() {
        for (ConnectionArtifacts artifacts : this.artifacts) {
            Closeables.closeQuietly(artifacts);
        }
    }
}

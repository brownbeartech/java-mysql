package tech.brownbear.mysql;

@FunctionalInterface
public interface CredentialsProvider {
    Credentials get();
}

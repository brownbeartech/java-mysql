load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

maven_jar(
    name = "org_slf4j_api",
    artifact = "org.slf4j:slf4j-api:1.7.22",
)

maven_jar(
    name = "com_google_guava",
    artifact = "com.google.guava:guava:21.0",
)

maven_jar(
    name = "org_json",
    artifact = "org.json:json:20160810",
)

maven_jar(
    name = "com_zaxxer_hikari",
    artifact = "com.zaxxer:HikariCP:2.7.1"
)

maven_jar(
    name = "mysql_connector_java",
    artifact = "mysql:mysql-connector-java:8.0.8-dmr",
)

git_repository(
    name = "tech_brownbear_resources",
    remote = "https://github.com/brownbeartech/java-resources.git",
    commit = "2cb8cb42bf96e61a5737b308fc8286f816f0bd68"
)
package(default_visibility = ["//visibility:public"])

java_library(
    name = "mysql",
    srcs = glob(["tech/brownbear/mysql/*.java"]),
    deps = [
        "@maven//:mysql_mysql_connector_java",
        "@maven//:org_slf4j_slf4j_api",
        "@maven//:com_google_guava_guava",
        "@maven//:com_zaxxer_HikariCP",
        "@maven//:com_google_code_gson_gson",
        "@maven//:org_json_json",
        "@tech_brownbear_resources//src:resources",
    ],
)
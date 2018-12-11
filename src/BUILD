package(default_visibility = ["//visibility:public"])

java_library(
    name = "mysql",
    srcs = glob(["tech/brownbear/mysql/*.java"]),
    deps = [
        "@com_zaxxer_hikari//jar",
        "@com_google_guava//jar",
        "@org_json//jar",
        "@org_slf4j_api//jar",
        "@mysql_connector_java//jar",
        "@tech_brownbear_resources//src:resources",
    ],
)
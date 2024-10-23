package uk.co.kiteframe.habitpal.configuring

import com.zaxxer.hikari.HikariDataSource
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.nonEmptyString
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.net.URI

data class DbConfig(
    val jdbcUrl: URI,
    val username: String,
    val password: String
)

fun DbConfig.toDslContext() = DSL.using(
    toHikariDataSource().apply {
        validate()
    },
    SQLDialect.POSTGRES
)

fun Environment.toDbConfig() = DbConfig(
    jdbcUrl = EnvironmentKey.map(URI::create).required("jdbc.url")(this),
    username = EnvironmentKey.nonEmptyString().required("db.username")(this),
    password = EnvironmentKey.nonEmptyString().required("db.password")(this),
)

fun DbConfig.toHikariDataSource() = HikariDataSource().apply {
    jdbcUrl = this@toHikariDataSource.jdbcUrl.toString()
    username = this@toHikariDataSource.username
    password = this@toHikariDataSource.password
}
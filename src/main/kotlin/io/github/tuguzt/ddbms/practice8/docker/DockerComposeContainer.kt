@file:Suppress("unused")

package io.github.tuguzt.ddbms.practice8.docker

import org.testcontainers.containers.DockerComposeContainer
import java.io.File

const val identifier = "ddbms8"

fun createDockerComposeContainer(): DockerComposeContainer<*> =
    DockerComposeContainer(identifier, File("src/main/resources/docker-compose.yml"))
        .withMongoService(index = 0)
        .withMongoService(index = 1)
        .withMongoService(index = 2)
        .withLocalCompose(true)

private fun DockerComposeContainer<*>.mongoService(index: Int): DockerComposeService =
    exposedServices[index].run {
        DockerComposeService(
            host = getServiceHost(host, port),
            port = getServicePort(host, port),
        )
    }

fun DockerComposeContainer<*>.mongo1Service(): DockerComposeService = mongoService(index = 0)

fun DockerComposeContainer<*>.mongo2Service(): DockerComposeService = mongoService(index = 1)

fun DockerComposeContainer<*>.mongo3Service(): DockerComposeService = mongoService(index = 2)

data class DockerComposeService(val host: String, val port: Int) {
    override fun toString(): String = "$host:$port"
}

private val exposedServices = listOf(
    DockerComposeService("mongo1", 30001),
    DockerComposeService("mongo2", 30002),
    DockerComposeService("mongo3", 30003),
)

private fun DockerComposeContainer<*>.withMongoService(index: Int): DockerComposeContainer<*> =
    exposedServices[index].run { withExposedService(host, port) }

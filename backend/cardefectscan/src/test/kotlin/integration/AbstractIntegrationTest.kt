package integration

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import ru.hse.cardefectscan.CardefectscanApplication


@SpringBootTest(classes = [CardefectscanApplication::class])
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {
    companion object {
        private val redisContainer = GenericContainer<Nothing>("redis")
            .apply {
                withExposedPorts(6379)
                waitingFor(Wait.forListeningPort())
            }

        private val postgresqlContainer = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:16-alpine"))
            .apply {
                withUsername("postgres")
                withPassword("postgres")
            }

        @BeforeAll
        @JvmStatic
        fun startContainers() {
            redisContainer.start()
            postgresqlContainer.start()
        }

        @AfterAll
        @JvmStatic
        fun stopContainers() {
            redisContainer.stop()
            postgresqlContainer.stop()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresqlContainer::getUsername)
            registry.add("spring.datasource.password", postgresqlContainer::getPassword)
        }
    }
}
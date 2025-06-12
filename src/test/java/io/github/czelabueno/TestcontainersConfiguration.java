
package io.github.czelabueno;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    public PostgreSQLContainer<?> pgVectorContainer() {
        return new PostgreSQLContainer<>("pgvector/pgvector:pg17").withStartupTimeout(Duration.ofMinutes(6));
    }

    @Bean
    public DynamicPropertyRegistrar pgVectorProperties(PostgreSQLContainer container) {
        return (properties) -> {
            properties.add("spring.datasource.url", container::getJdbcUrl);
            properties.add("spring.datasource.username", container::getUsername);
            properties.add("spring.datasource.password", container::getPassword);
        };
    }

    @Bean
    JdbcTemplate jdbcTemplate(PostgreSQLContainer<?> pgVectorContainer) throws SQLException {
        DataSource ds = new SimpleDriverDataSource(
                pgVectorContainer.getJdbcDriverInstance(),
                pgVectorContainer.getJdbcUrl(),
                pgVectorContainer.getUsername(),
                pgVectorContainer.getPassword()
        );
        return new JdbcTemplate(ds);
    }

    @Bean
    EmbeddingModel embeddingModel() {
        return new OpenAiEmbeddingModel(
                OpenAiApi.builder()
                        .apiKey(System.getenv("OPENAI_API_KEY"))
                        .build(),
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .model("text-embedding-ada-002")
                        .build()
        );
    }

    @Bean
    public PgVectorStore pgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                        .initializeSchema(true)
                        .build();
    }
}


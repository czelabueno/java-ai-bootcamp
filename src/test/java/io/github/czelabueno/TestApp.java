package io.github.czelabueno;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@Import(TestcontainersConfiguration.class)
public class TestApp {
}

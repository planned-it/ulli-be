package planned.it.ulli.ulli_be.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiFeignConfig {

    @Value("${openai.api-key}")
    private String openaiApiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("Authorization", "Bearer " + openaiApiKey);
            template.header("Content-Type", "application/json");
        };
    }
}


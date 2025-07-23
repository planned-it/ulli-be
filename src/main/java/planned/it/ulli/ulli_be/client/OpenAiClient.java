package planned.it.ulli.ulli_be.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import planned.it.ulli.ulli_be.config.OpenAiFeignConfig;

import java.util.Map;

@FeignClient(name = "openai", url = "https://api.openai.com/v1", configuration = OpenAiFeignConfig.class)
public interface OpenAiClient {
    @PostMapping(value = "/chat/completions", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> chatCompletions(@RequestBody Map<String, Object> request);
}

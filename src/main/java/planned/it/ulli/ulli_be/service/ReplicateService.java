package planned.it.ulli.ulli_be.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
public class ReplicateService {

    @Value("${replicate.key}")
    private String apiKey;

    @Value("${replicate.url}")
    private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ReplicateService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public Mono<String> generateImage(String prompt) {
        System.out.println(prompt);
//        // Stable Diffusion XL 모델 사용 (텍스트 디자인에 적합)
//        Map<String, Object> requestBody = Map.of(
//                "version", "39ed52f2a78e934b3ba6e2a89f5b1c712de7dfea535525255b1aa35c5565e08b",
//                "input", Map.of(
//                        "prompt", prompt,
//                        "width", 1024,
//                        "height", 1024,
//                        "num_inference_steps", 20,
//                        "guidance_scale", 7.5,
//                        "scheduler", "K_EULER"
//                )
//        );
//
//        return webClient.post()
//                .uri(apiUrl)
//                .header("Authorization", "Token " + apiKey)
//                .header("Content-Type", "application/json")
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .flatMap(this::pollForResult)
//                .onErrorMap(throwable -> new RuntimeException("Replicate API 호출 실패: " + throwable.getMessage()));
        // Stable Diffusion XL 1.0 모델 사용 (올바른 버전)
        Map<String, Object> requestBody = Map.of(
                "version", "be04660a5b93ef2aff61e3668dedb4cbeb14941e62a3fd5998364a32d613e35e",
                "input", Map.of(
                        "prompt", prompt.trim(),
                        "width", 1024,
                        "height", 1024,
                        "num_inference_steps", 25,  // 20보다 25가 더 안정적
                        "guidance_scale", 7.5,
                        "scheduler", "K_EULER_ANCESTRAL",  // 더 안정적인 스케줄러
                        "negative_prompt", "blurry, low quality, distorted",  // 품질 향상을 위한 네거티브 프롬프트
                        "num_outputs", 1,
                        "apply_watermark", false
                )
        );

        return webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Token " + apiKey)
                .header("Content-Type", "application/json")
                .header("User-Agent", "YourApp/1.0")  // User-Agent 헤더 추가
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))  // 타임아웃 설정
                .flatMap(this::pollForResult)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))  // 재시도 로직
                        .filter(throwable -> !(throwable instanceof IllegalArgumentException)))
                .onErrorMap(TimeoutException.class,
                        throwable -> new RuntimeException("API 호출 타임아웃: " + throwable.getMessage()))
                .onErrorMap(throwable -> {
                    System.out.println("Replicate API 호출 실패");
                    return new RuntimeException("Replicate API 호출 실패: " + throwable.getMessage());
                });
    }

    private Mono<String> pollForResult(String initialResponse) {
        try {
            JsonNode jsonNode = objectMapper.readTree(initialResponse);
            String predictionId = jsonNode.get("id").asText();

            return pollPrediction(predictionId);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Replicate 응답 파싱 실패", e));
        }
    }

    private Mono<String> pollPrediction(String predictionId) {
        return webClient.get()
                .uri(apiUrl + "/" + predictionId)
                .header("Authorization", "Token " + apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        String status = jsonNode.get("status").asText();

                        if ("succeeded".equals(status)) {
                            JsonNode output = jsonNode.get("output");
                            if (output.isArray() && output.size() > 0) {
                                return Mono.just(output.get(0).asText());
                            }
                        } else if ("failed".equals(status)) {
                            return Mono.error(new RuntimeException("이미지 생성 실패"));
                        }

                        // 아직 처리 중이면 잠시 대기 후 다시 확인
                        return Mono.delay(Duration.ofSeconds(2))
                                .flatMap(delay -> pollPrediction(predictionId));

                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("상태 확인 실패", e));
                    }
                });
    }
}

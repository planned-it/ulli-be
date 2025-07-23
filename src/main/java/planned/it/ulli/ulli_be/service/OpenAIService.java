package planned.it.ulli.ulli_be.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import planned.it.ulli.ulli_be.dto.DesignRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.api-url}")
    private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OpenAIService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

//    public Mono<String> generateImage(String prompt) {
//        Map<String, Object> requestBody = Map.of(
//                "model", "dall-e-3",
//                "prompt", prompt,
//                "n", 1,
//                "size", "1024x1024",
//                "quality", "standard",
//                "style", "vivid"
//        );
//
//        return webClient.post()
//                .uri(apiUrl)
//                .header("Authorization", "Bearer " + apiKey)
//                .header("Content-Type", "application/json")
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .map(this::extractImageUrl)
//                .onErrorMap(throwable -> new RuntimeException("OpenAI API 호출 실패: " + throwable.getMessage()));
//    }
    public Mono<String> generateImage(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", "dall-e-3",
                "prompt", prompt,
                "n", 1,  // DALL·E 3에서는 반드시 1이어야 함
                "size", "1024x1024",
                "quality", "standard",
                "style", "vivid",
                "response_format", "url"  // 이거 꼭 있어야 함
        );
        System.out.println("prompt = " + prompt);
        return webClient.post()
                .uri(apiUrl)  // e.g., "https://api.openai.com/v1/images/generations"
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> System.out.println("OpenAI response: "+response))  // 디버깅용
                .map(this::extractImageUrl)
                .onErrorResume(e -> {
                    System.out.println("OpenAI API 호출 실패"+e.getMessage());
                    return Mono.error(new RuntimeException("OpenAI API 호출 실패: " + e.getMessage()));
                });
    }


//    private String extractImageUrl(String response) {
//        try {
//            JsonNode jsonNode = objectMapper.readTree(response);
//            return jsonNode.get("data").get(0).get("url").asText();
//        } catch (Exception e) {
//            throw new RuntimeException("OpenAI 응답 파싱 실패", e);
//        }
//    }
    private String extractImageUrl(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode data = root.path("data");
            if (data.isArray() && data.size() > 0) {
                return data.get(0).path("url").asText();
            }
            throw new RuntimeException("이미지 URL 없음: " + responseBody);
        } catch (Exception e) {
            throw new RuntimeException("응답 파싱 실패: " + e.getMessage(), e);
        }
    }

    public String generateDesign(DesignRequest request) {
        String styles = String.join(", ", request.getStyles());
        String colors = String.join(", ", request.getCustomColors());
        String prompt = String.format(
                "한글 문구 '%s'를 '%s' 스타일로 디자인하려고 해. '%s' 각 글자별로 글꼴 크기는 기본 80px로 두고, color(hex), fontSize(px) 포함한 JSON 배열로 출력해줘. 다른 대답은 절대 하지 말고 JSON 배열만 답해줘. " +
                        "반드시 다음 JSON 형식으로 상세하게 응답해주세요:\n" +
                        "{\n" +
                        "  \"fontSize\": \"rem 단위\",\n" +
                        "  \"fontFamily\": \"한글 폰트명\",\n" +
                        "  \"fontWeight\": \"100-900\",\n" +
                        "  \"letterSpacing\": \"자간 (em 단위)\",\n" +
                        "  \"lineHeight\": \"행간\",\n" +
                        "  \"color\": \"메인 색상\",\n" +
                        "  \"background\": \"배경 (그라데이션 포함)\",\n" +
                        "  \"textShadow\": \"그림자 효과 (복수 가능)\",\n" +
                        "  \"transform\": \"변형 효과\",\n" +
                        "  \"animation\": \"애니메이션 이름과 속성\",\n" +
                        "  \"effects\": [\n" +
                        "    {\n" +
                        "      \"type\": \"gradient|glow|bubble|outline|neon|3d\",\n" +
                        "      \"subtype\": \"세부 타입\",\n" +
                        "      \"colors\": [\"색상 배열\"],\n" +
                        "      \"intensity\": \"강도\",\n" +
                        "      \"direction\": \"방향\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"backgroundShape\": {\n" +
                        "    \"type\": \"rounded-rect|circle|ellipse\",\n" +
                        "    \"dimensions\": \"크기\",\n" +
                        "    \"style\": \"스타일 속성\"\n" +
                        "  },\n" +
                        "  \"filters\": [\n" +
                        "    {\"type\": \"blur|brightness|contrast|saturate\", \"value\": \"값\"}\n" +
                        "  ],\n" +
                        "  \"decorations\": [\n" +
                        "    {\"type\": \"sparkle|dots|stars|hearts\", \"properties\": \"속성\"}\n" +
                        "  ]\n" +
                        "}\n" +
                        "\n" +
                        "스타일별 특화 요청:\n" +
                        "- 동글동글: 둥근 폰트 + 넓은 자간 + 부드러운 색상 + bubble 효과\n" +
                        "- 네온: 형광색 + glow 효과 + 강한 그림자\n" +
                        "- 빈티지: 세리프 폰트 + 따뜻한 색상 + 3D 효과\n" +
                        "- 글리치: 디지털 색상 + 변형 효과 + 특수 필터\n" +
                        "- 귀여운: 파스텔 색상 + 장식 요소 + 부드러운 애니메이션\n" +
                        "\n" +
                        "모든 속성을 최대한 다양하고 창의적으로 활용해주세요!" +
                        "" +
                        "'%s'",
                request.getText(), styles, colors != null ? colors + "색상 계열로, " : "", request.getCustomPrompt() != null ? request.getCustomPrompt() : ""
        );

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4",
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 한글 타이포그래피 스타일링 디자이너입니다."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
        );
        System.out.println(prompt);

        String result = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 동기 호출
        System.out.println(result);
        return result;
    }
}

//package planned.it.ulli.ulli_be.controller;
//
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import planned.it.ulli.ulli_be.dto.DesignRequest;
//import planned.it.ulli.ulli_be.dto.GenerateRequest;
//import planned.it.ulli.ulli_be.dto.GenerateResponse;
//import planned.it.ulli.ulli_be.service.TextDesignService;
//import reactor.core.publisher.Mono;
//
//import javax.validation.Valid;
//
//@RestController
//@RequestMapping("/api")
//public class TextDesignController {
//
//    private final TextDesignService textDesignService;
//
//    public TextDesignController(TextDesignService textDesignService) {
//        this.textDesignService = textDesignService;
//    }
//
//    @PostMapping("/generate-text-design")
//    public Mono<ResponseEntity<GenerateResponse>> generateTextDesign(@Valid @RequestBody GenerateRequest request) {
//        System.out.println("api 호출");
//        return textDesignService.generateTextDesign(request)
//                .map(ResponseEntity::ok)
//                .onErrorReturn(ResponseEntity.internalServerError().build());
//    }
//
//    @GetMapping("/health")
//    public ResponseEntity<String> health() {
//        return ResponseEntity.ok("Text Design Service is running");
//    }
//
//    @PostMapping("/design")
//    public ResponseEntity<?> generate(@RequestBody DesignRequest request) {
//        String json = textDesignService.generateDesign(request);
//        return ResponseEntity.ok(json);
//    }
//}

package planned.it.ulli.ulli_be.controller;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import planned.it.ulli.ulli_be.client.OpenAiClient;

import java.util.*;

@RestController
@RequestMapping("/api")
public class TextDesignController {

    private final OpenAiClient openAiClient;

    public TextDesignController(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    @PostMapping("/design")
    public Map<String, Object> generateDesign(@RequestBody GenerateRequest request) {
        String prompt = buildPrompt(request);

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "gpt-4");
        payload.put("messages", List.of(
                Map.of("role", "system", "content", "당신은 스타일 키워드를 기반으로 텍스트에 어울리는 디자인 스타일(JSON 형식)을 생성하는 디자이너입니다."),
                Map.of("role", "user", "content", prompt)
        ));
        payload.put("temperature", 0.8);
        System.out.println(payload);
        Map<String, Object> stringObjectMap = openAiClient.chatCompletions(payload);
        System.out.println(stringObjectMap);
        String content = ((Map<String, Object>) ((Map<String, Object>) ((List<?>) stringObjectMap.get("choices")).get(0)).get("message")).get("content").toString();
        System.out.println("content = " + content);
        return Map.of("html", content);
    }

//    private String buildPrompt(GenerateRequest request) {
//        StringBuilder sb = new StringBuilder();
////        sb.append("문구: \"").append(request.getText()).append("\"\n");
////
////        if (!request.getStyles().isEmpty()) {
////            sb.append("스타일 키워드: ").append(String.join(", ", request.getStyles())).append("\n");
////        }
////        if (!request.getCustomColors().isEmpty()) {
////            sb.append("선호 색상: ").append(String.join(", ", request.getCustomColors())).append("\n");
////        }
////        if (request.getCustomPrompt() != null) {
////            sb.append("추가 설명: ").append(request.getCustomPrompt()).append("\n");
////        }
////
////        sb.append("\n위 내용을 반영하여 각 글자에 대해 다음 형식의 JSON 배열로 응답하세요:\n");
////        sb.append("[{ \"text\": \"한\", \"color\": \"#FF0000\", \"fontSize\": \"72\", \"decoration\": \"gradient\" }, ...]");
////        return sb.toString();
//        sb.append("문구: \\\"").append(request.getText()).append("\\\"\n");
//        sb.append("스타일 키워드: ").append(String.join(", ", request.getStyles())).append("\n");
//
//        if (!request.getCustomColors().isEmpty()) {
//            sb.append("선호 색상: ").append(String.join(", ", request.getCustomColors())).append("\n");
//        }
//        if (request.getCustomPrompt() != null) {
//            sb.append("추가 설명: ").append(request.getCustomPrompt()).append("\n");
//        }
//
//        sb.append("\n위 내용을 참고하여, 각 글자에 대해 HTML/CSS로 표현할 수 있도록 스타일 정보를 생성해주세요.\n");
//        sb.append("JSON 배열 형태로 응답하세요. 각 항목은 다음 구조를 따라야 합니다:\n");
//        sb.append("{ \"text\": \"한\", \"inlineStyle\": \"font-size:72px; font-weight:bold; background:linear-gradient(...); -webkit-background-clip:text; -webkit-text-fill-color:transparent;\" }\n");
//        sb.append("줄바꿈은 text 속성 내에 포함되어 표현하세요. 줄바꿈은 \\n 문자 그대로 유지합니다.\n");
//        return sb.toString();
//    }

//    private String buildPrompt(GenerateRequest request) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("\n당신은 웹 타이포그래피 디자이너입니다.\n");
//        sb.append("아래 문구의 각 글자에 대해 스타일 키워드를 시각적으로 표현한 디자인 JSON 배열을 생성하세요.\n");
//
//        sb.append("문구: \"").append(request.getText()).append("\"\n");
//
//        if (!request.getStyles().isEmpty()) {
//            sb.append("스타일 키워드: ").append(String.join(", ", request.getStyles())).append("\n");
//        }
//
//        if (!request.getCustomColors().isEmpty()) {
//            sb.append("사용자가 입력한 색상: ").append(String.join(", ", request.getCustomColors())).append("\n");
//            sb.append("각 줄마다 하나의 색상을 적용하되, 글자마다 색이 다르지 않도록 같은 줄에는 동일한 색상을 사용하세요. 반드시 색상은 입력된 리스트를 사용해야 합니다.\n");
//        } else {
//            sb.append("색상은 스타일 키워드에 맞춤형으로 AI가 제안하되, 줄마다 하나의 색상을 적용하고 한 줄의 모든 글자는 같은 색상을 사용하세요.\n");
//        }
//
//        if (request.getCustomPrompt() != null) {
//            sb.append("추가 설명: ").append(request.getCustomPrompt()).append("\n");
//        }
//
//        sb.append("스타일 키워드는 사용자가 표현하고 싶은 글자의 느낌을 말하며, 키워드에 어울리는 시각 효과(예: gradient, neon, shadow, pattern, underline, metallic 등)와 연결되어야 합니다.\n");
//        sb.append("다양한 decoration 값 중에 선택하세요. decoration은 'gradient', 'neon', 'pattern', 'metallic', 'shadow', 'underline', 'none' 중 선택할 수 있습니다.\n");
//
//        sb.append("\n※ 중요한 규칙:\n");
//        sb.append("- 전체 문구에 하나의 decoration 스타일만 사용하세요 (예: gradient, neon, shadow 등 중 하나).\n");
//        sb.append("- decoration 값은 각 글자에 동일하게 적용해야 합니다.\n");
//        sb.append("- 글자 크기는 통일하세요.\n");
//        sb.append("- 줄바꿈을 유지하세요.\n");
//
//        sb.append("\n아래 형식의 JSON 배열로 응답하세요:\n");
//        sb.append("[{ \"text\": \"한\", \"color\": \"#FF0000\", \"fontSize\": \"72\", \"decoration\": \"gradient\", \"fontFamily\": \"MaruBuri\",  \"borderRadius\": \"50%\", \"backgroundPattern\": \"dots\", }, ...]");
//
//        return sb.toString();
//    }

    private String buildPrompt(GenerateRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("당신은 웹 타이포그래피 UI/UX 디자이너이며, 사용자로부터 받은 문구와 스타일 키워드, 색상 정보를 기반으로 시각적으로 돋보이는 텍스트 디자인을 만들어야 합니다. 3초 정도 생각하고 가장 적합한 응답을 해주세요.\n\n");
        sb.append("아래 입력된 문구에 대해 각 줄별로 스타일을 적용하여 인라인 CSS가 적용된 HTML 코드만 출력하세요.\n\n");
        sb.append("출력 규칙:\n");
        sb.append("- 전체 문구를 구성하는 글자를 <span>으로 감싸고, CSS는 모두 style 속성에 작성\n");
        sb.append("- 줄바꿈은 <br/> 태그로 유지\n");
        sb.append("- 같은 줄의 글자들은 동일한 스타일을 적용\n");
        sb.append("- 가능한 한 스타일 키워드와 조화롭고 통일감 있게, 어울리게, 유사하게 구성\n\n");
        sb.append("활용 가능한 스타일 속성:\n");
        sb.append("- color, font-size, font-family, text-shadow, background, background-clip, animation, font-weight\n");
        sb.append("- gradient나 텍스처가 있을 경우 background-clip: text 와 -webkit-text-fill-color: transparent 사용 가능\n\n");
        sb.append("문구:\n\"\"\"\n").append(request.getText()).append("\n\"\"\"\n\n");

        if (!request.getStyles().isEmpty()) {
            sb.append("스타일 키워드: ").append(String.join(", ", request.getStyles())).append("\n");
        }
        if (!request.getCustomColors().isEmpty()) {
            sb.append("선호 색상: ").append(String.join(", ", request.getCustomColors())).append("\n");
        }
        if (request.getCustomPrompt() != null && !request.getCustomPrompt().isBlank()) {
            sb.append("추가 설명: ").append(request.getCustomPrompt()).append("\n");
        }

        sb.append("참고할 스타일 키워드 해석 기준 예시");
        sb.append("- 귀여운: 둥근 폰트, 파스텔톤 색상, 동글동글한 테두리 활용하면 좋음");
        sb.append("- 화려한: 밝은 네온컬러나 패턴, 그라데이션, 애니메이션 반짝임 활용하면 좋음");
        sb.append("- 시원한: 블루 계열, 물방울 텍스쳐 활용하면 좋음");
        sb.append("- 모던한: 무채색, 정돈된 레이아웃, 세리프 없는 글꼴 활용하면 좋음");
        sb.append("- 이외에도 스타일 키워드의 정의에 어울리는 css를 충분히 적용하세요.");

        sb.append("\n설명 없이 HTML 코드만 출력하세요. <style> 태그는 사용하지 말고 모든 CSS는 style 속성으로만 작성하세요. " +
                "중요: 응답 JSON의 html 키에 HTML을 그대로 넣되, HTML을 문자열로 escape하지 말 것.");
        return sb.toString();
    }


    public static class GenerateRequest {
        private String text;
        private List<String> styles;
        private List<String> customStyles;
        private List<String> customColors;
        private String customPrompt;
        private boolean isRegenerate;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public List<String> getStyles() { return styles; }
        public void setStyles(List<String> styles) { this.styles = styles; }

        public List<String> getCustomStyles() { return customStyles; }
        public void setCustomStyles(List<String> customStyles) { this.customStyles = customStyles; }

        public List<String> getCustomColors() { return customColors; }
        public void setCustomColors(List<String> customColors) { this.customColors = customColors; }

        public String getCustomPrompt() { return customPrompt; }
        public void setCustomPrompt(String customPrompt) { this.customPrompt = customPrompt; }

        public boolean isRegenerate() { return isRegenerate; }
        public void setRegenerate(boolean regenerate) { isRegenerate = regenerate; }
    }
}

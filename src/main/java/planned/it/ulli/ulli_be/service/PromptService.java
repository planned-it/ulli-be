package planned.it.ulli.ulli_be.service;

import org.springframework.stereotype.Service;
import planned.it.ulli.ulli_be.dto.GenerateRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PromptService {

    private final Map<String, String> styleTranslations = Map.of(
            "cute", "cute, adorable, kawaii style",
            "bright", "bright, vibrant, cheerful",
            "luxury", "luxury, premium, elegant, sophisticated",
            "colorful", "colorful, rainbow, vibrant colors",
            "dark", "dark, moody, dramatic",
            "elegant", "elegant, refined, classy",
            "modern", "modern, contemporary, minimalist",
            "vintage", "vintage, retro, classic"
    );

    public String buildPrompt(GenerateRequest request) {
        if (request.getCustomPrompt() != null && !request.getCustomPrompt().trim().isEmpty()) {
            return request.getCustomPrompt() + " Text: \"" + request.getText() + "\"";
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("Create a beautiful text design with the text: \"").append(request.getText()).append("\"");

        // 스타일 추가
        List<String> allStyles = request.getStyles().stream()
                .map(style -> styleTranslations.getOrDefault(style, style))
                .collect(Collectors.toList());

        allStyles.addAll(request.getCustomStyles());

        if (!allStyles.isEmpty()) {
            prompt.append(". Style: ").append(String.join(", ", allStyles));
        }

        // 색상 추가
        if (!request.getCustomColors().isEmpty()) {
            prompt.append(". Colors: ").append(String.join(", ", request.getCustomColors()));
        }

        // 기본 스타일 지침
        prompt.append(". High quality typography, artistic design, professional layout, ");
        prompt.append("creative font styling, visually appealing composition, ");
        prompt.append("suitable for digital use, clean background");

        return prompt.toString();
    }
}

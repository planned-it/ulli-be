package planned.it.ulli.ulli_be.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import planned.it.ulli.ulli_be.dto.DesignRequest;
import planned.it.ulli.ulli_be.dto.GenerateRequest;
import planned.it.ulli.ulli_be.dto.GenerateResponse;
import reactor.core.publisher.Mono;

@Service
public class TextDesignService {

    @Value("${app.ai-provider:openai}") // openai 또는 replicate
    private String aiProvider;

    private final OpenAIService openAIService;
    private final ReplicateService replicateService;
    private final PromptService promptService;

    public TextDesignService(OpenAIService openAIService, ReplicateService replicateService,
                             PromptService promptService) {
        this.openAIService = openAIService;
        this.replicateService = replicateService;
        this.promptService = promptService;
    }

    public Mono<GenerateResponse> generateTextDesign(GenerateRequest request) {
        String prompt = promptService.buildPrompt(request);

        Mono<String> imageUrlMono;
        if ("replicate".equalsIgnoreCase(aiProvider)) {
            System.out.println("replicate");
            imageUrlMono = replicateService.generateImage(prompt);
        } else {
            imageUrlMono = openAIService.generateImage(prompt);
        }

        return imageUrlMono
                .map(imageUrl -> new GenerateResponse(imageUrl, aiProvider, prompt))
                .onErrorMap(throwable -> new RuntimeException("이미지 생성 실패: " + throwable.getMessage()));
    }

    public String generateDesign(DesignRequest request) {
        return openAIService.generateDesign(request);
    }
}

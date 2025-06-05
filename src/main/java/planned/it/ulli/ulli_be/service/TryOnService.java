package planned.it.ulli.ulli_be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TryOnService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${replicate.url}")
    private String REPLICATE_API_URL;

    @Value("${replicate.key}")
    private String REPLICATE_API_TOKEN;

    private final SupabaseUploadService supabaseUploadService;

    public String processTryOn(MultipartFile human, MultipartFile cloth) throws IOException {
        // 1. 이미지 업로드 (Supabase storage)
        String humanImageUrl = supabaseUploadService.uploadImage(human, generateFileName("human", human));
        String clothImageUrl = supabaseUploadService.uploadImage(cloth, generateFileName("cloth", cloth));

        // 2. Replicate 요청 페이로드
        Map<String, Object> requestBody = Map.of(
                "version", "39860afc9f164ce9734d5666d17a771f986dd2bd3ad0935d845054f73bbec447",
                "input", Map.of(
                        "steps", 25,
                        "face_image", humanImageUrl,
                        "commerce_image", clothImageUrl
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + REPLICATE_API_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(REPLICATE_API_URL, request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || responseBody.get("urls") == null) {
            throw new RuntimeException("Replicate 응답 오류");
        }

        Map<String, String> urls = (Map<String, String>) responseBody.get("urls");
        return urls.get("get"); // 결과 이미지 polling URL

        // 임시 리턴
//        return humanImageUrl;
    }

    private String generateFileName(String type, MultipartFile file) {
        String extension = Optional.ofNullable(file.getOriginalFilename())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf(".")))
                .orElse(".jpg");
        return type + "/" + UUID.randomUUID() + extension;
    }
}

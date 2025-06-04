package planned.it.ulli.ulli_be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TryOnService {

    private final RestTemplate restTemplate = new RestTemplate();

//    private static final String REPLICATE_API_URL = "https://api.replicate.com/v1/predictions";
//    private static final String REPLICATE_API_TOKEN = "Token YOUR_REPLICATE_API_KEY";
//
    private final SupabaseUploadService supabaseUploadService;

    public String processTryOn(MultipartFile human, MultipartFile cloth) throws IOException {
        // 1. 이미지 업로드 (Supabase storage)
        String humanImageUrl = supabaseUploadService.uploadImage(human, generateFileName("human", human));
        String clothImageUrl = supabaseUploadService.uploadImage(cloth, generateFileName("cloth", cloth));

        // 2. Replicate 요청 페이로드
//        Map<String, Object> requestBody = Map.of(
//                "version", "모델 버전 ID",
//                "input", Map.of(
//                        "image_path", humanImageUrl,
//                        "cloth_path", clothImageUrl
//                )
//        );
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", REPLICATE_API_TOKEN);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
//        ResponseEntity<Map> response = restTemplate.postForEntity(REPLICATE_API_URL, request, Map.class);
//
//        Map<String, Object> responseBody = response.getBody();
//        if (responseBody == null || responseBody.get("urls") == null) {
//            throw new RuntimeException("Replicate 응답 오류");
//        }
//
//        Map<String, String> urls = (Map<String, String>) responseBody.get("urls");
//        return urls.get("get"); // 결과 이미지 polling URL

        // 임시 리턴
        return humanImageUrl;
    }

    private String generateFileName(String type, MultipartFile file) {
        String extension = Optional.ofNullable(file.getOriginalFilename())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf(".")))
                .orElse(".jpg");
        return type + "/" + UUID.randomUUID() + extension;
    }
}

package planned.it.ulli.ulli_be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SupabaseUploadService {

    @Value("${supabase.url}")
    private String SUPABASE_URL;

    @Value("${supabase.bucket}")
    private String SUPABASE_BUCKET;

    @Value("${supabase.key}")
    private String SUPABASE_API_KEY;

    public String uploadImage(MultipartFile file, String filename) throws IOException {
        WebClient webClient = WebClient.builder()
                .baseUrl(SUPABASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + SUPABASE_API_KEY)
                .build();

        String uploadPath = String.format("/storage/v1/object/%s/%s", SUPABASE_BUCKET, filename);

        System.out.println("Uploading to: " + SUPABASE_URL + uploadPath);
        System.out.println("content type: "+file.getContentType());
        System.out.println();

        String response = webClient.put()
                .uri(uploadPath)
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .bodyValue(file.getBytes())
                .retrieve()
                .bodyToMono(String.class)
                .block()
                ;  // 동기적으로 대기 (비동기로 쓰려면 block 제거하고 Mono<String> 반환)

        return String.format("%s/storage/v1/object/%s/%s", SUPABASE_URL, SUPABASE_BUCKET, filename);
    }
}

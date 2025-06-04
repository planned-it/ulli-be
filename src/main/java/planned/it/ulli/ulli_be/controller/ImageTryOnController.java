package planned.it.ulli.ulli_be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import planned.it.ulli.ulli_be.service.TryOnService;

@RestController
@RequestMapping("/api/tryon")
@RequiredArgsConstructor
public class ImageTryOnController {

    private final TryOnService tryOnService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> tryOnClothes(
            @RequestPart("human") MultipartFile humanImage,
            @RequestPart("cloth") MultipartFile clothImage
    ) {
        try {
            String resultImageUrl = tryOnService.processTryOn(humanImage, clothImage);
            return ResponseEntity.ok(resultImageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("합성 실패: " + e.getMessage());
        }
    }
}

package yu.likelion14th.allligo_was.S3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import yu.likelion14th.allligo_was.S3.dto.S3PresignedUrlResDto;
import yu.likelion14th.allligo_was.S3.dto.UploadDomain;
import yu.likelion14th.allligo_was.exception.CustomException;
import yu.likelion14th.allligo_was.exception.ErrorCode;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;

    /**
     * Presigned URL 발급
     *
     * 프론트는 presignedUrl로 S3에 직접 PUT 업로드하고,
     * fileUrl은 이후 홍보/쿠폰/프로필/콘텐츠 API 요청 시 DB 저장용으로 전달한다.
     */
    public S3PresignedUrlResDto createPresignedUrl(
            Long userId,
            UploadDomain domain,
            String contentType
    ) {
        validateUploadDomain(domain);

        String extension = extractExtension(contentType);
        String key = createKey(domain, userId, extension);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(10))
                .build();

        URL presignedUrl = s3Presigner.presignPutObject(presignRequest).url();
        String fileUrl = convertToUrl(key);

        return S3PresignedUrlResDto.of(
                presignedUrl.toString(),
                fileUrl
        );
    }

    /**
     * DB 저장 전에 fileUrl이 올리고의 S3 URL인지 검증한다.
     */
    public void validateFileUrl(Long userId, UploadDomain domain, String fileUrl) {
        validateUploadDomain(domain);

        if (fileUrl == null || fileUrl.isBlank()) {
            throw new CustomException(ErrorCode.FILE_URL_REQUIRED);
        }

        String allowedPrefix = normalizeBaseUrl()
                + "/"
                + domain.getPath()
                + "/"
                + userId
                + "/";

        if (!fileUrl.startsWith(allowedPrefix)) {
            throw new CustomException(ErrorCode.INVALID_FILE_URL);
        }
    }

    /**
     * 여러 개의 파일 URL 검증
     *
     * 홍보 이미지처럼 1~5장 업로드하는 경우 사용한다.
     */
    public void validateFileUrls(Long userId, UploadDomain domain, Iterable<String> fileUrls) {
        if (fileUrls == null) {
            throw new CustomException(ErrorCode.FILE_URL_REQUIRED);
        }

        for (String fileUrl : fileUrls) {
            validateFileUrl(userId, domain, fileUrl);
        }
    }

    private void validateUploadDomain(UploadDomain domain) {
        if (domain == null) {
            throw new CustomException(ErrorCode.INVALID_UPLOAD_DOMAIN);
        }
    }

    private String createKey(UploadDomain domain, Long userId, String extension) {
        return String.format(
                "%s/%d/%s.%s",
                domain.getPath(),
                userId,
                UUID.randomUUID(),
                extension
        );
    }

    private String extractExtension(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_CONTENT_TYPE);
        }

        return switch (contentType) {
            case "image/png" -> "png";
            case "image/jpeg", "image/jpg" -> "jpg";
            default -> throw new CustomException(ErrorCode.INVALID_IMAGE_CONTENT_TYPE);
        };
    }

    private String convertToUrl(String key) {
        return normalizeBaseUrl() + "/" + key;
    }

    private String normalizeBaseUrl() {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_FILE_URL);
        }

        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }

        return baseUrl;
    }
}
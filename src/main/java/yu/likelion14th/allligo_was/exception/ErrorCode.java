package yu.likelion14th.allligo_was.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식을 맞추어 작성해주세요."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 메일은 사용할 수 없어요."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일 인증 정보를 찾을 수 없습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),

    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 발송에 실패했습니다."),

    EMAIL_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "인증 토큰이 올바르지 않습니다."),
    EMAIL_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었습니다."),

    PASSWORD_INVALID_LENGTH(HttpStatus.BAD_REQUEST, "6자 이상, 12자 이하로 입력해주세요."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    STORE_REQUIRED(HttpStatus.BAD_REQUEST, "가게 정보는 필수 입력입니다."),
    INVALID_STORE_URL(HttpStatus.BAD_REQUEST, "올바른 링크 형식으로 입력해 주세요."),

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "매장 정보를 찾을 수 없습니다."),
    INVALID_LINK(HttpStatus.BAD_REQUEST, "링크가 유효하지 않습니다."),

    INVALID_COUPON_IMAGE_URL(HttpStatus.BAD_REQUEST, "쿠폰 이미지 URL이 유효하지 않습니다."),

    //promotion errorcode
    PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 홍보 요청을 찾을 수 없습니다."),
    PROMOTION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 홍보 요청에 접근할 권한이 없습니다."),
    INVALID_PROMOTION_TITLE(HttpStatus.BAD_REQUEST, "홍보 제목은 3자 이상, 20자 이하로 입력해주세요."),
    INVALID_PROMOTION_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "콘텐츠 타입은 BLOG 또는 VIDEO만 가능합니다."),
    INVALID_PROMOTION_PROMPT(HttpStatus.BAD_REQUEST, "추가 프롬프트는 250자 이하로 입력해주세요."),
    INVALID_PROMOTION_IMAGE_COUNT(HttpStatus.BAD_REQUEST, "이미지는 1장 이상 5장 이하로 등록해야 합니다."),
    INVALID_PROMOTION_TAG_COUNT(HttpStatus.BAD_REQUEST, "해시태그는 10개 미만으로 등록해야 합니다."),
    INVALID_PROMOTION_TAG_LENGTH(HttpStatus.BAD_REQUEST, "해시태그는 7자 이하로 입력해주세요."),
    INVALID_PROMOTION_SCHEDULE(HttpStatus.BAD_REQUEST, "스케줄을 1개 이상 등록해야 합니다."),
    INVALID_PROMOTION_DEADLINE(HttpStatus.BAD_REQUEST, "마감일을 입력해주세요."),
    INVALID_PROMOTION_PUBLISH_TIME(HttpStatus.BAD_REQUEST, "배포 시간은 현재 시간보다 1시간 이후여야 합니다."),
    INVALID_PROMOTION_DAY_OF_WEEK(HttpStatus.BAD_REQUEST, "배포 요일 값이 올바르지 않습니다."),
    INVALID_PROMOTION_MODE(HttpStatus.BAD_REQUEST, "분위기 태그는 따뜻함, 차분함, 밝음 중 하나만 선택할 수 있습니다."),
    

    // 컨텐츠 관련
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "콘텐츠를 찾을 수 없습니다."),
    CONTENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 콘텐츠에 접근할 권한이 없습니다."),
    CONTENT_PREVIEW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "생성 완료된 콘텐츠만 미리보기할 수 있습니다."),
    CONTENT_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "생성 완료된 콘텐츠만 배포 중단할 수 있습니다."),
    CONTENT_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "이미 배포 중단된 콘텐츠입니다."),
    CONTENT_ALREADY_PUBLISHED(HttpStatus.BAD_REQUEST, "이미 업로드된 콘텐츠는 배포 중단할 수 없습니다."),

    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    FORBIDDEN_COUPON_ACCESS(HttpStatus.FORBIDDEN, "해당 쿠폰에 접근할 수 없습니다."),


    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
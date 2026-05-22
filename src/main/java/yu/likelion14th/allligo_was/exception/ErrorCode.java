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

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
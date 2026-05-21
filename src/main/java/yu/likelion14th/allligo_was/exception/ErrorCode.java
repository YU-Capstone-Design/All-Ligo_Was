package yu.likelion14th.allligo_was.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식을 맞추어 작성해주세요."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 메일은 사용할 수 없어요."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일 인증 정보를 찾을 수 없습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),

    PASSWORD_INVALID_LENGTH(HttpStatus.BAD_REQUEST, "비밀번호는 6자 이상 12자 이하로 입력해주세요."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
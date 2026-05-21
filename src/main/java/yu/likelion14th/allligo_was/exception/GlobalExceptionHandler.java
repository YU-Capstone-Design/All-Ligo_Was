package yu.likelion14th.allligo_was.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse response = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        ErrorResponse response = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }
}
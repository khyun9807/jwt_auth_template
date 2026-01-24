package com.jwt_auth_template.exception;

import com.jwt_auth_template.auth.AuthController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(
        assignableTypes = {
                AuthController.class,
        }
)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthGlobalExceptionHandler {

    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleOAuthException(final OAuthException exception) {
        return buildErrorResponse(exception.getErrorCode());
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiResponse<Void>> handleMemberException(final MemberException exception) {
        return buildErrorResponse(exception.getErrorCode());
    }


    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }
}

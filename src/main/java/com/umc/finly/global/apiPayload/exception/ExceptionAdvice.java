package com.umc.finly.global.apiPayload.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.umc.finly.domain.member.exception.code.MemberErrorCode;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.FragmentPeriodKey;
import com.umc.finly.domain.record.exception.code.RecordErrorCode;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    // @RequestParam, @PathVariable 등 Bean Validation 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");

        ApiResponse<Object> body = ApiResponse.onFailure(
                ErrorCode.INVALID_REQUEST,
                message
        );
        return handleExceptionInternal(e, body, new HttpHeaders(),
                ErrorCode.INVALID_REQUEST.getHttpStatus(),
                request);
    }

    // JSON 파싱 실패 (요청 본문 읽기 실패)
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        log.error("HttpMessageNotReadableException 발생", e);
        ApiResponse<Object> body = ApiResponse.onFailure(
                ErrorCode.INVALID_REQUEST,
                "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요."
        );
        return handleExceptionInternal(e, body, headers,
                ErrorCode.INVALID_REQUEST.getHttpStatus(),
                request);
    }

    // @Valid @RequestBody DTO 검증 실패
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField();
            String msg = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
            errors.merge(field, msg, (a, b) -> a + ", " + b);
        });

        ApiResponse<Object> body = ApiResponse.onFailure(
                ErrorCode.INVALID_REQUEST,
                errors
        );
        return handleExceptionInternal(e, body, headers,
                ErrorCode.INVALID_REQUEST.getHttpStatus(),
                request);
    }

    // 모든 미처리 예외 → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception e, WebRequest request) {
        log.error("Unhandled exception", e); // printStackTrace() 지양
        // TODO: 배포 전 null로 변경 필요 (현재는 디버깅용)
        ApiResponse<Object> body = ApiResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        return handleExceptionInternal(e, body, new HttpHeaders(),
                ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(), request);
    }

    // 도메인 CustomException
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException e, HttpServletRequest request) {
        log.error("CustomException 발생", e);
        ApiResponse<Object> body = ApiResponse.onFailure(e.getErrorCode(), null);
        WebRequest webRequest = new ServletWebRequest(request);
        return handleExceptionInternal(e, body, new HttpHeaders(), e.getErrorCode().getHttpStatus(), webRequest);
    }

    @ExceptionHandler({InvalidFormatException.class})
    public ResponseEntity<ApiResponse<Object>> handleInvalidFormat(InvalidFormatException ex) {
        return ResponseEntity.badRequest().body(
                ApiResponse.onFailure(ErrorCode.INVALID_REQUEST, null)
        );
    }

    // FragmentController
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleTypeMismatch(MethodArgumentTypeMismatchException e) {

        // boxType enum 변환 실패
        if ("boxType".equals(e.getName()) && e.getRequiredType() == EmotionCode.class) {
            throw new CustomException(RecordErrorCode.INVALID_BOX_TYPE);
        }

        // periodKey enum 변환 실패 (에러코드 있으면)
        if ("periodKey".equals(e.getName()) && e.getRequiredType() == FragmentPeriodKey.class) {
            throw new CustomException(RecordErrorCode.INVALID_PERIOD_KEY);
        }

        // 그 외는 공통 400
        throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    // 이미지 용량 파일 초과
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request
    ) {
        if (ex instanceof MaxUploadSizeExceededException) {
            ApiResponse<Object> newBody = ApiResponse.onFailure(
                    MemberErrorCode.IMAGE_FILE_TOO_LARGE,
                    MemberErrorCode.IMAGE_FILE_TOO_LARGE.getMessage()
            );
            return super.handleExceptionInternal(
                    ex,
                    newBody,
                    headers,
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    request
            );
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }
}

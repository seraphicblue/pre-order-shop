package com.example.payment.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<String> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        logErrorDetails(ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<String> handleInvalidFinalQuantityException(InvalidFinalQuantityException ex) {
        logErrorDetails(ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 예외가 발생한 위치 정보를 로깅하는 메서드
    private void logErrorDetails(Exception ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            logger.error("에러가 발생했습니다. class: {}, method: {}, line: {}, with message: {}",
                    element.getClassName(), element.getMethodName(), element.getLineNumber(), ex.getMessage(), ex);
        } else {
            logger.error("에러가 알수 없는 위치에서 발생했습니다 : {}", ex.getMessage(), ex);
        }
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(Exception ex) {
        logErrorDetails(ex); // 로그 세부 정보를 기록
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

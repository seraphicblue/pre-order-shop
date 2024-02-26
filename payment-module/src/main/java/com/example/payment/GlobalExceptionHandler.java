package com.example.payment;

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

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(Exception ex) {

        // 예외가 발생한 위치 정보를 가져옴
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            String className = element.getClassName();
            String methodName = element.getMethodName();
            int lineNumber = element.getLineNumber();

            // 클래스 이름, 메서드 이름, 라인 넘버를 로그에 남김
            logger.error("클래스 명: {}, 메서드 명: {}, 라인넘 버: {}에서 에러가 발생했습니다.", className, methodName, lineNumber, ex);
        } else {

            logger.error("에러가 발생했습니다.: {}", ex.getMessage(), ex);
        }

        // 적절한 HTTP 응답 반환
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

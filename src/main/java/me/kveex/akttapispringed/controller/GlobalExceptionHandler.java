package me.kveex.akttapispringed.controller;

import me.kveex.akttapispringed.service.impl.exception.auth.TakenLoginException;
import me.kveex.akttapispringed.service.impl.exception.notification.NotValidWebhookException;
import me.kveex.akttapispringed.service.impl.exception.schedule.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler {
    public record ErrorResponse(String error, String errorDetails) {
        public static <T extends RuntimeException> ErrorResponse of(T exception) {
            return new ErrorResponse(exception.getMessage(), null);
        }
        public static ErrorResponse of(String errorMessage) {
            return new ErrorResponse(errorMessage, null);
        }
        public static ErrorResponse of(String errorMessage, String errorDetails) {
            return new ErrorResponse(errorMessage, errorDetails);
        }
        public static <T extends RuntimeException> ErrorResponse of (T exception, String errorDetails) {
            return new ErrorResponse(exception.getMessage(), errorDetails);
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ignored) {
        return new ResponseEntity<>(ErrorResponse.of("Неверный логин или пароль!"),  HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Неизвестная ошибка валидации!");

        return ResponseEntity.badRequest().body(ErrorResponse.of(errorMessage, exception.getMessage()));
    }

    @ExceptionHandler(BadScheduleRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadScheduleRequestException(BadScheduleRequestException exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleScheduleNotFoundException(ScheduleNotFoundException exception) {
        return new ResponseEntity<>(ErrorResponse.of(exception), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TakenLoginException.class)
    public ResponseEntity<ErrorResponse> handleTakenLoginException(TakenLoginException exception) {
        return new ResponseEntity<>(ErrorResponse.of(exception), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadPdfParseException.class)
    public ResponseEntity<ErrorResponse> handleBadPdfParseException(BadPdfParseException exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.of("Что-то случилось при парсинге PDF-файла!", exception.getMessage()));
    }

    @ExceptionHandler(EmptyParsedPdfException.class)
    public ResponseEntity<ErrorResponse> handleEmptyParsedPdfException(EmptyParsedPdfException exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(exception, "EMPTY"));
    }

    @ExceptionHandler(ScheduleAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleScheduleAlreadyExistsException(ScheduleAlreadyExistsException exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(exception, "ALREADY_EXISTS"));
    }

    @ExceptionHandler(NotValidWebhookException.class)
    public ResponseEntity<ErrorResponse> handleNotValidWebhookException(NotValidWebhookException exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(InvalidPdfScheduleDateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPdfScheduleDateException(InvalidPdfScheduleDateException exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(exception, "UNRECOGNISABLE_DATE"));
    }
}

package indocyber.exam.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NotFoundException exc){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(exc.getMessage());
        error.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NotAuthorizedException exc){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.FORBIDDEN.value());
        error.setMessage(exc.getMessage());
        error.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NotAllowedException exc){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        error.setMessage(exc.getMessage());
        error.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(RuntimeException exc){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(exc.getMessage());
        error.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        List<ObjectError> errorList = e.getBindingResult().getAllErrors();
        for (ObjectError obj : errorList){
            if(obj instanceof FieldError){
                String fieldName = ((FieldError) obj).getField();
                String errorMessage = obj.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            } else {
                String fieldName = obj.getObjectName();
                String errorMessage = obj.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            }
        }

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        errorResponse.setMessage(errors);
        errorResponse.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse,HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
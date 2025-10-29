package org.test.calendar.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.test.calendar.model.dto.ValidationErrorResponse;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    private static final String REQUEST_URI = "/api/events";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(REQUEST_URI);
    }

    @Test
    void handleMethodArgumentNotValid_withFieldErrors_shouldReturnBadRequestWithFieldErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("eventDto", "title", "Invalid title", false, null, null, "Title is required");
        FieldError fieldError2 = new FieldError("eventDto", "startDateTime", null, false, null, null, "Start date is required");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(bindingResult.getGlobalErrors()).thenReturn(List.of());

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        ValidationErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.error()).isEqualTo("Validation Failed");
        assertThat(body.message()).isEqualTo("Request validation failed");
        assertThat(body.path()).isEqualTo(REQUEST_URI);
        
        List<ValidationErrorResponse.FieldError> fieldErrors = body.fieldErrors();
        assertThat(fieldErrors).hasSize(2);
        
        ValidationErrorResponse.FieldError firstError = fieldErrors.getFirst();
        assertThat(firstError.field()).isEqualTo("title");
        assertThat(firstError.rejectedValue()).isEqualTo("Invalid title");
        assertThat(firstError.message()).isEqualTo("Title is required");
        
        ValidationErrorResponse.FieldError secondError = fieldErrors.get(1);
        assertThat(secondError.field()).isEqualTo("startDateTime");
        assertThat(secondError.rejectedValue()).isNull();
        assertThat(secondError.message()).isEqualTo("Start date is required");
    }

    @Test
    void handleMethodArgumentNotValid_withGlobalErrors_shouldReturnBadRequestWithGlobalErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        ObjectError globalError = new ObjectError("eventDto", "End date must be after start date");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        when(bindingResult.getGlobalErrors()).thenReturn(List.of(globalError));

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        ValidationErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        
        List<ValidationErrorResponse.FieldError> fieldErrors = body.fieldErrors();
        assertThat(fieldErrors).hasSize(1);
        
        ValidationErrorResponse.FieldError error = fieldErrors.getFirst();
        assertThat(error.field()).isEqualTo("Request Body");
        assertThat(error.rejectedValue()).isNull();
        assertThat(error.message()).isEqualTo("End date must be after start date");
    }

    @Test
    void handleMethodArgumentNotValid_withBothFieldAndGlobalErrors_shouldReturnAllErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("eventDto", "title", "Invalid", false, null, null, "Title is required");
        ObjectError globalError = new ObjectError("eventDto", "Global validation error");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(bindingResult.getGlobalErrors()).thenReturn(List.of(globalError));

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        ValidationErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.fieldErrors()).hasSize(2);
    }

    @Test
    void handleConstraintViolation_shouldReturnBadRequestWithConstraintViolations() {
        ConstraintViolation<?> violation1 = mockConstraintViolation("title", "Invalid title", "Title must not be blank");
        ConstraintViolation<?> violation2 = mockConstraintViolation("duration", 0, "Duration must be positive");

        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleConstraintViolation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        ValidationErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.error()).isEqualTo("Constraint Violation");
        assertThat(body.message()).isEqualTo("Request validation failed");
        assertThat(body.path()).isEqualTo(REQUEST_URI);
        assertThat(body.fieldErrors()).hasSize(2);
    }

    @Test
    void handleHttpMessageNotReadable_shouldReturnBadRequestWithInvalidJsonMessage() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleHttpMessageNotReadable(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        ValidationErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.error()).isEqualTo("Bad Request");
        assertThat(body.message()).isEqualTo("Invalid JSON format");
        assertThat(body.path()).isEqualTo(REQUEST_URI);
        assertThat(body.fieldErrors()).isEmpty();
    }

    @Test
    void handleIllegalArgument_shouldReturnBadRequestWithExceptionMessage() {
        String errorMessage = "Invalid argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleIllegalArgument(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        ValidationErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.error()).isEqualTo("Bad Request");
        assertThat(body.message()).isEqualTo(errorMessage);
        assertThat(body.path()).isEqualTo(REQUEST_URI);
        assertThat(body.fieldErrors()).isEmpty();
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        Exception exception = new RuntimeException("Unexpected error occurred");

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleGenericException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        
        ValidationErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.error()).isEqualTo("Unknown Error");
        assertThat(body.message()).isEqualTo("An unexpected error occurred");
        assertThat(body.path()).isEqualTo(REQUEST_URI);
        assertThat(body.fieldErrors()).isEmpty();
    }

    @Test
    void handleMethodArgumentNotValid_withEmptyErrors_shouldReturnBadRequestWithEmptyFieldErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        when(bindingResult.getGlobalErrors()).thenReturn(List.of());

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        ValidationErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.fieldErrors()).isEmpty();
    }

    private ConstraintViolation<?> mockConstraintViolation(String propertyPath, Object invalidValue, String message) {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(path.toString()).thenReturn(propertyPath);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getInvalidValue()).thenReturn(invalidValue);
        when(violation.getMessage()).thenReturn(message);

        return violation;
    }
}


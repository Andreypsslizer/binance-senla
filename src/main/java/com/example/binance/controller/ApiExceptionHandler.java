package com.example.binance.controller;
import com.example.binance.service.PriceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(PriceNotFoundException.class) @ResponseStatus(HttpStatus.NOT_FOUND) public Map<String,Object> notFound(RuntimeException ex) { return Map.of("timestamp", Instant.now().toString(), "status",404,"error","Not Found","message",ex.getMessage()); }
 @ExceptionHandler(IllegalArgumentException.class) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String,Object> badRequest(RuntimeException ex) { return Map.of("timestamp", Instant.now().toString(), "status",400,"error","Bad Request","message",ex.getMessage()); }
}

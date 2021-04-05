package rest;

import org.springframework.http.ResponseEntity;

@FunctionalInterface
public interface RestResultHandler {
    void handle(ResponseEntity responseEntity);
}

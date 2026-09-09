package org.example.urlshortener.domain.exception;

public class ShortUrlNotFoundException extends RuntimeException {

    public ShortUrlNotFoundException(String shortCode) {
        super("Short URL not found: " + shortCode);
    }
}
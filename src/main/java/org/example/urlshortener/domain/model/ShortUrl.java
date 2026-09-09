package org.example.urlshortener.domain.model;

public record ShortUrl(
        String shortCode,
        String originalUrl
) {
}
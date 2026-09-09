package org.example.urlshortener.adapter.in.web;

public record ShortUrlResponse(
        String shortCode,
        String originalUrl
) {
}
package org.example.urlshortener.application.port.in;

import org.example.urlshortener.domain.model.ShortUrl;

public interface ShortUrlUseCase {

    ShortUrl createShortUrl(String originalUrl);

    String resolveShortUrl(String shortCode);
}
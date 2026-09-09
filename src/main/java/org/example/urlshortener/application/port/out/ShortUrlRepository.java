package org.example.urlshortener.application.port.out;

import org.example.urlshortener.domain.model.ShortUrl;

import java.util.Optional;

public interface ShortUrlRepository {

    void save(ShortUrl shortUrl);

    Optional<ShortUrl> findByShortCode(String shortCode);
}
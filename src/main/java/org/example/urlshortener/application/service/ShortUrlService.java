package org.example.urlshortener.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import org.example.urlshortener.application.port.in.ShortUrlUseCase;
import org.example.urlshortener.application.port.out.ShortUrlRepository;
import org.example.urlshortener.domain.exception.ShortUrlNotFoundException;
import org.example.urlshortener.domain.model.ShortUrl;

@Service
public class ShortUrlService implements ShortUrlUseCase {

    private final ShortUrlRepository repository;

    public ShortUrlService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Override
    public ShortUrl createShortUrl(String originalUrl) {
        String shortCode = generateShortCode();

        ShortUrl shortUrl = new ShortUrl(
                shortCode,
                originalUrl
        );

        repository.save(shortUrl);

        return shortUrl;
    }

    @Override
    public String resolveShortUrl(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(ShortUrl::originalUrl)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException(shortCode));
    }

    private String generateShortCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }
}
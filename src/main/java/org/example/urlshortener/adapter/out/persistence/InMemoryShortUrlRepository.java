package org.example.urlshortener.adapter.out.persistence;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Repository;

import org.example.urlshortener.application.port.out.ShortUrlRepository;
import org.example.urlshortener.domain.model.ShortUrl;

@Repository
public class InMemoryShortUrlRepository implements ShortUrlRepository {

    private final ConcurrentMap<String, ShortUrl> urls =
            new ConcurrentHashMap<>();

    @Override
    public void save(ShortUrl shortUrl) {
        urls.put(shortUrl.shortCode(), shortUrl);
    }

    @Override
    public Optional<ShortUrl> findByShortCode(String shortCode) {
        return Optional.ofNullable(urls.get(shortCode));
    }
}
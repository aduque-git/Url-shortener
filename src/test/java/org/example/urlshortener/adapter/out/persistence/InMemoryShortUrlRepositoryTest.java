package org.example.urlshortener.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.example.urlshortener.domain.model.ShortUrl;
import org.junit.jupiter.api.Test;

class InMemoryShortUrlRepositoryTest {

    private final InMemoryShortUrlRepository repository =
            new InMemoryShortUrlRepository();

    @Test
    void shouldSaveAndFindShortUrl() {
        // Given
        ShortUrl shortUrl = new ShortUrl(
                "abc12345",
                "https://www.example.com"
        );

        // When
        repository.save(shortUrl);

        Optional<ShortUrl> result =
                repository.findByShortCode("abc12345");

        // Then
        assertTrue(result.isPresent());
        assertEquals(shortUrl, result.get());
    }

    @Test
    void shouldReturnEmptyWhenShortUrlDoesNotExist() {
        // When
        Optional<ShortUrl> result =
                repository.findByShortCode("notfound");

        // Then
        assertTrue(result.isEmpty());
    }
}

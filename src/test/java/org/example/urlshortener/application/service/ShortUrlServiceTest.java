package org.example.urlshortener.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.example.urlshortener.application.port.out.ShortUrlRepository;
import org.example.urlshortener.domain.exception.ShortUrlNotFoundException;
import org.example.urlshortener.domain.model.ShortUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepository repository;

    @InjectMocks
    private ShortUrlService service;

    @Test
    void shouldCreateShortUrl() {
        // Given
        String originalUrl = "https://www.example.com";

        // When
        ShortUrl result = service.createShortUrl(originalUrl);

        // Then
        assertNotNull(result);
        assertNotNull(result.shortCode());
        assertFalse(result.shortCode().isBlank());
        assertEquals(originalUrl, result.originalUrl());

        verify(repository).save(result);
    }

    @Test
    void shouldResolveExistingShortUrl() {
        // Given
        String shortCode = "abc12345";
        String originalUrl = "https://www.example.com";

        ShortUrl shortUrl = new ShortUrl(
                shortCode,
                originalUrl
        );

        when(repository.findByShortCode(shortCode))
                .thenReturn(Optional.of(shortUrl));

        // When
        String result = service.resolveShortUrl(shortCode);

        // Then
        assertEquals(originalUrl, result);

        verify(repository).findByShortCode(shortCode);
    }

    @Test
    void shouldThrowExceptionWhenShortUrlDoesNotExist() {
        // Given
        String shortCode = "notfound";

        when(repository.findByShortCode(shortCode))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                ShortUrlNotFoundException.class,
                () -> service.resolveShortUrl(shortCode)
        );

        verify(repository).findByShortCode(shortCode);
    }
}

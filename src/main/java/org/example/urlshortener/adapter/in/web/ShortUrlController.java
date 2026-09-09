package org.example.urlshortener.adapter.in.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.example.urlshortener.application.port.in.ShortUrlUseCase;
import org.example.urlshortener.domain.model.ShortUrl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/urls")
public class ShortUrlController {

    private final ShortUrlUseCase shortUrlUseCase;

    public ShortUrlController(ShortUrlUseCase shortUrlUseCase) {
        this.shortUrlUseCase = shortUrlUseCase;
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse> create(
            @Valid @RequestBody CreateShortUrlRequest request) {

        ShortUrl shortUrl =
                shortUrlUseCase.createShortUrl(request.url());

        ShortUrlResponse response = new ShortUrlResponse(
                shortUrl.shortCode(),
                shortUrl.originalUrl()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        String originalUrl =
                shortUrlUseCase.resolveShortUrl(shortCode);

        return ResponseEntity
                .status(302)
                .location(URI.create(originalUrl))
                .build();
    }
}
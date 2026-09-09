package org.example.urlshortener.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateShortUrlRequest(

        @NotBlank(message = "URL is required")
        @Pattern(
                regexp = "https?://.*",
                message = "URL must start with http:// or https://"
        )
        String url
) {
}
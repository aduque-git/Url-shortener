package org.example.urlshortener.adapter.in.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.urlshortener.application.port.in.ShortUrlUseCase;
import org.example.urlshortener.domain.model.ShortUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ShortUrlControllerTest {

    private MockMvc mockMvc;

    private ShortUrlUseCase shortUrlUseCase;

    @BeforeEach
    void setUp() {
        shortUrlUseCase = mock(ShortUrlUseCase.class);

        ShortUrlController controller =
                new ShortUrlController(shortUrlUseCase);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void shouldCreateShortUrl() throws Exception {
        ShortUrl shortUrl = new ShortUrl(
                "abc12345",
                "https://www.example.com"
        );

        when(shortUrlUseCase.createShortUrl("https://www.example.com"))
                .thenReturn(shortUrl);

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "url": "https://www.example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc12345"))
                .andExpect(jsonPath("$.originalUrl")
                        .value("https://www.example.com"));
    }

    @Test
    void shouldRedirectToOriginalUrl() throws Exception {
        String shortCode = "abc12345";
        String originalUrl = "https://www.example.com";

        when(shortUrlUseCase.resolveShortUrl(shortCode))
                .thenReturn(originalUrl);

        mockMvc.perform(get("/api/urls/{shortCode}", shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    void shouldRejectInvalidUrl() throws Exception {
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "url": "invalid-url"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}

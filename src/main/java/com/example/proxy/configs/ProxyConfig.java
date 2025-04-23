package com.example.proxy.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProxyConfig {
    @JsonProperty("http.port")
    private int httpPort;

    @JsonProperty("https.port")
    private int httpsPort;
}

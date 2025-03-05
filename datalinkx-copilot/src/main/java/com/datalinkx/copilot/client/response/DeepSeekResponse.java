package com.datalinkx.copilot.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DeepSeekResponse {
    private String id;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Choice {
        private int index;
        private String finish_reason;
        private Message message;
        private Message delta;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Message {
        private String role;
        private String content;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Usage {
        private int total_tokens;
    }
}

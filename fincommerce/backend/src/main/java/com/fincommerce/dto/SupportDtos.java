package com.fincommerce.dto;

import jakarta.validation.constraints.NotBlank;

public class SupportDtos {

    public static class CreateTicketRequest {
        @NotBlank(message = "Subject is required")
        private String subject;

        @NotBlank(message = "Category is required")
        private String category; // WALLET, PAYMENTS, ORDER, RETURN, GENERAL

        @NotBlank(message = "Message details are required")
        private String message;

        private String priority = "MEDIUM";

        public CreateTicketRequest() {}

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }

    public static class ChatQueryRequest {
        @NotBlank(message = "Query cannot be blank")
        private String query;

        public ChatQueryRequest() {}

        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
    }

    public static class ChatQueryResponse {
        private String reply;
        private String category;
        private String suggestedActionUrl;

        public ChatQueryResponse() {}

        public ChatQueryResponse(String reply, String category, String suggestedActionUrl) {
            this.reply = reply;
            this.category = category;
            this.suggestedActionUrl = suggestedActionUrl;
        }

        public String getReply() { return reply; }
        public void setReply(String reply) { this.reply = reply; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getSuggestedActionUrl() { return suggestedActionUrl; }
        public void setSuggestedActionUrl(String suggestedActionUrl) { this.suggestedActionUrl = suggestedActionUrl; }
    }
}

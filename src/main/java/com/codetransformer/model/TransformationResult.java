package com.codetransformer.model;

/**
 * Represents the result of a code transformation operation.
 * Immutable data class following Clean Code principles.
 */
public class TransformationResult {
    private final String content;
    private final String outputPath;
    private final boolean success;
    private final String errorMessage;

    private TransformationResult(Builder builder) {
        this.content = builder.content;
        this.outputPath = builder.outputPath;
        this.success = builder.success;
        this.errorMessage = builder.errorMessage;
    }

    public String getContent() {
        return content;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Builder pattern implementation for clean and flexible object creation.
     */
    public static class Builder {
        private String content = "";
        private String outputPath = "";
        private boolean success = false;
        private String errorMessage = "";

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withOutputPath(String outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder withSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public TransformationResult build() {
            return new TransformationResult(this);
        }
    }
}

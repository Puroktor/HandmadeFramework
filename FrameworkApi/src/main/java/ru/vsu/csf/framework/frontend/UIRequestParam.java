package ru.vsu.csf.framework.frontend;

public record UIRequestParam(String paramName, Class<?> param, SubmissionType submissionType) {
    public enum SubmissionType {
        QUERY_PARAM,
        REQUEST_BODY
    }
}

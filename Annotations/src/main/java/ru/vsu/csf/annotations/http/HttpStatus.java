package ru.vsu.csf.annotations.http;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    NOT_IMPLEMENTED(501, "Not Implemented");

    private final int code;
    private final String title;

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    HttpStatus(int code, String title) {
        this.code = code;
        this.title = title;
    }
}

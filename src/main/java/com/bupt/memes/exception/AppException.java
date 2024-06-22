package com.bupt.memes.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Builder
@Slf4j
public class AppException extends RuntimeException {
    private final ErrorType errorType;

    public enum ErrorType {
        INVALID_PARAM("The param %s is invalid.", 400), RESOURCE_NOT_FOUND("The resource %s was not found.", 404), UNAUTHORIZED("Unauthorized access. %s",
                401), METHOD_NOT_ALLOWED("Method not allowed.", 405), INTERNAL_ERROR("The server encountered an internal error. %s",
                        500), DATABASE_ERROR("The server encountered an database error. %s",
                                500), STORAGE_ERROR("The server encountered an storage error. %s", 500);

        private final String message;
        @Getter
        private final int code;

        ErrorType(String message, int code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage(Object... args) {
            return String.format(message, args);
        }

    }

    public AppException(ErrorType errorType, Object... args) {
        super(errorType.getMessage(args));
        this.errorType = errorType;
    }

    public static AppException invalidParam(String paramName) {
        return new AppException(ErrorType.INVALID_PARAM, paramName);
    }

    public static AppException unauthorized(String path) {
        return new AppException(ErrorType.UNAUTHORIZED, path);
    }

    public static AppException methodNotAllowed() {
        return new AppException(ErrorType.METHOD_NOT_ALLOWED);
    }

    public static AppException internalError(String... args) {
        return new AppException(ErrorType.INTERNAL_ERROR, (Object[]) args);
    }

    public static AppException databaseError(String param) {
        return new AppException(ErrorType.DATABASE_ERROR, param);
    }

    public static AppException storageError(String param) {
        return new AppException(ErrorType.STORAGE_ERROR, param);
    }

    public static AppException resourceNotFound(String param) {
        return new AppException(ErrorType.RESOURCE_NOT_FOUND, param);
    }

    public static void main(String[] args) {
        throw AppException.invalidParam("paramName");
    }

}

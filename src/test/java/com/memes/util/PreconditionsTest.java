package com.memes.util;

import com.memes.exception.AppException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PreconditionsTest {

    @Test
    void checkArgumentThrowsExceptionWhenConditionIsFalse() {
        assertThrows(AppException.class, () -> Preconditions.checkArgument(false, new AppException("Test")));
    }

    @Test
    void checkArgumentDoesNotThrowExceptionWhenConditionIsTrue() {
        Preconditions.checkArgument(true, new AppException("Test"));
    }

    @Test
    void checkArgumentWithSupplierThrowsExceptionWhenConditionIsFalse() {
        assertThrows(AppException.class, () -> Preconditions.checkArgument(false, () -> new AppException("Test")));
    }

    @Test
    void checkNotNullThrowsExceptionWhenObjectIsNull() {
        assertThrows(AppException.class, () -> Preconditions.checkNotNull(null, new AppException("Test")));
    }

    @Test
    void checkNotNullDoesNotThrowExceptionWhenObjectIsNotNull() {
        Preconditions.checkNotNull(new Object(), new AppException("Test"));
    }

    @Test
    void checkNotNullWithSupplierThrowsExceptionWhenObjectIsNull() {
        assertThrows(AppException.class, () -> Preconditions.checkNotNull(null, () -> new AppException("Test")));
    }

    @Test
    void checkStringNotEmptyThrowsExceptionWhenStringIsNull() {
        assertThrows(AppException.class, () -> Preconditions.checkStringNotEmpty(null, new AppException("Test")));
    }

    @Test
    void checkStringNotEmptyThrowsExceptionWhenStringIsEmpty() {
        assertThrows(AppException.class, () -> Preconditions.checkStringNotEmpty("", new AppException("Test")));
    }

    @Test
    void checkStringNotEmptyDoesNotThrowExceptionWhenStringIsNotEmpty() {
        Preconditions.checkStringNotEmpty("Test", new AppException("Test"));
    }

    @Test
    void checkStringNotEmptyWithSupplierThrowsExceptionWhenStringIsNull() {
        assertThrows(AppException.class, () -> Preconditions.checkStringNotEmpty(null, () -> new AppException("Test")));
    }

    @Test
    void checkStringNotEmptyWithSupplierThrowsExceptionWhenStringIsEmpty() {
        assertThrows(AppException.class, () -> Preconditions.checkStringNotEmpty("", () -> new AppException("Test")));
    }
}

package com.memes.util;

import com.memes.model.common.FileUploadResult;
import com.memes.service.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class FileUploaderTest {

    @Mock
    private MultipartFile file;

    @Mock
    private StorageService storageService;

    private FileUploader fileUploader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileUploader = new FileUploader(file, storageService);
    }

    @Test
    void callReturnsNullWhenFileIsNull() throws IOException {
        when(file.getBytes()).thenReturn(null);
        assertNull(fileUploader.call());
    }

    @Test
    void callReturnsNullWhenStoreThrowsException() throws IOException {
        when(file.getBytes()).thenThrow(IOException.class);
        assertNull(fileUploader.call());
    }

    @Test
    void callReturnsFileUploadResultWhenStoreIsSuccessful() throws IOException {
        FileUploadResult result = new FileUploadResult("", "", "");
        when(file.getBytes()).thenReturn(new byte[0]);
        when(storageService.store(any(), any())).thenReturn(result);
        assertSame(result, fileUploader.call());
    }

    @Test
    void uploadReturnsSameResultAsCall() throws IOException {
        FileUploadResult result = new FileUploadResult("", "", "");
        when(file.getBytes()).thenReturn(new byte[0]);
        when(storageService.store(any(), any())).thenReturn(result);
        assertSame(result, fileUploader.upload());
    }
}

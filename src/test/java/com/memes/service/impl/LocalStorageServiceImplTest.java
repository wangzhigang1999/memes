package com.memes.service.impl;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.memes.model.common.FileUploadResult;
import com.memes.service.StorageService;

class LocalStorageServiceImplTest {

    StorageService service = new LocalStorageServiceImpl();

    @Test
    void store() {
        FileUploadResult store = service.store(new byte[]{1, 2, 3}, "image/png");
        assert store.url().contains("png");
        service.delete(new String[]{store.fileName()});
    }

    @Test
    void storeAndDelete() {
        FileUploadResult store = service.store(new byte[]{1, 2, 3}, "image/png", "test.zhigang");
        assert store.url().contains("test.zhigang");
        Map<String, Boolean> delete = service.delete(new String[]{"test.zhigang"});
        assert delete.get("test.zhigang");
    }

    @Test
    void getMime() {
        assert service.getExtension("image/png").equals("png");
        assert service.getExtension("image/jpeg").equals("jpeg");
        assert service.getExtension("image/gif").equals("gif");
        assert service.getExtension("image/bmp").equals("bmp");
        assert service.getExtension("image/tiff").equals("tiff");
    }
}

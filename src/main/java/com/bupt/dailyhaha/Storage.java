package com.bupt.dailyhaha;

import java.io.InputStream;

public interface Storage {
    Image store(InputStream stream, boolean personal);
}

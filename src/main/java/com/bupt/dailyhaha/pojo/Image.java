package com.bupt.dailyhaha.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

@Data
@Accessors
@Document(collection = "Image")
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    String url;
    Date time;
    Integer hash;
    String name;
    Boolean deleted = false;

    public static String imageTypeCheck(InputStream stream) {
        try {
            ImageInputStream image = ImageIO.createImageInputStream(stream);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(image);
            return readers.next().getFormatName().toLowerCase();
        } catch (Exception e) {
            return null;
        }

    }
}

package com.bupt.dailyhaha.pojo.submission;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.InputStream;
import java.util.Iterator;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors
@Document(collection = "Image")
public class Image extends Submission {
    public Image(String url, String fileName, int hashCode) {

        this.timestamp = System.currentTimeMillis();
        this.submissionType = SubmissionType.IMAGE;

        this.url = url;
        this.name = fileName;
        this.hash = hashCode;
    }

    public Image() {
        this.timestamp = System.currentTimeMillis();
        this.submissionType = SubmissionType.IMAGE;
    }

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

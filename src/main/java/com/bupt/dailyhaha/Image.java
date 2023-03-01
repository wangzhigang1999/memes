package com.bupt.dailyhaha;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Accessors
@Document(collection = "Image")
public class Image {
    String url;
    Date time;
    Integer hash;
}

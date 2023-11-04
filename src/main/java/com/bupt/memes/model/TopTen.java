package com.bupt.memes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "top_ten")
public class TopTen {
    @Id
    String lastBuildDate;
    List<RSSItem> topTen;
}

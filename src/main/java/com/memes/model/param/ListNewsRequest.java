package com.memes.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class ListNewsRequest {
    String date;
    String title;
    String introduction;
    String content;
    String author;
    Set<String> tag;
    Integer pageSize;
    String lastID;
}

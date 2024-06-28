package com.memes.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ListRSSItemRequest {
    String author;
    String board;
    String keyword;
    int pageSize;
    String lastID;
}

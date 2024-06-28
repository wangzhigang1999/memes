package com.memes.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.MatchesPattern;

@Data
@Builder
@AllArgsConstructor
public class ListSubmissionsRequest {
    int pageSize;
    String lastID;
    @MatchesPattern("yyyy-MM-dd")
    String date;
}

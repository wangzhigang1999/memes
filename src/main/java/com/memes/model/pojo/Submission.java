package com.memes.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
    private Integer id;

    private List<Integer> mediaContentIdList;

    private Integer likesCount;

    private Integer dislikesCount;

    private Set<String> tags;
} 
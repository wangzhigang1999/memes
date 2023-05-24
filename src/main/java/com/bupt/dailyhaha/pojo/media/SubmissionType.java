package com.bupt.dailyhaha.pojo.media;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubmissionType {
    IMAGE("IMAGE"),
    VIDEO("VIDEO"),
    BILIBILI("BILIBILI");

    final String value;

}

package com.bupt.dailyhaha.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors
public class ImageReviewCallback {
    private String id;
    private String pipeline;
    private int code;
    private String desc;
    private String reqid;
    private String inputBucket;
    private String inputKey;
    private List<Item> items;


    @Data
    @Accessors
    public static class Item {
        private String cmd;
        private int code;
        private String desc;
        private Result result;
        private int returnOld;

        @Data
        @Accessors
        public static class Result {
            private boolean disable;
        }
    }
}
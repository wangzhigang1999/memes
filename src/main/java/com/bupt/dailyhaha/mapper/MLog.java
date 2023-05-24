package com.bupt.dailyhaha.mapper;

import com.bupt.dailyhaha.pojo.common.LogDocument;

import java.util.List;

public interface MLog {
    void insertLog(LogDocument document);

    List<LogDocument> find(long from, long to);
}

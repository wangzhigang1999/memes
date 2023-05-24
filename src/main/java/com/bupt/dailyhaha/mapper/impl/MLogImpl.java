package com.bupt.dailyhaha.mapper.impl;

import com.bupt.dailyhaha.mapper.MLog;
import com.bupt.dailyhaha.pojo.common.LogDocument;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MLogImpl implements MLog {
    @Override
    public void insertLog(LogDocument document) {

    }

    @Override
    public List<LogDocument> find(long from, long to) {
        return null;
    }
}

package com.bupt.dailyhaha.mapper;

import com.bupt.dailyhaha.pojo.common.LogDocument;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MLogImpl implements MLog {
    /**
     * 插入日志
     *
     * @param document 日志文档
     */
    @Override
    public void insertLog(LogDocument document) {

    }

    /**
     * 查询日志
     *
     * @param from 开始时间
     * @param to   结束时间
     * @return 日志列表
     */
    @Override
    public List<LogDocument> find(long from, long to) {
        return null;
    }
}

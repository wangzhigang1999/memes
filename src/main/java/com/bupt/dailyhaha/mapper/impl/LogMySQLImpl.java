package com.bupt.dailyhaha.mapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bupt.dailyhaha.mapper.MLog;
import com.bupt.dailyhaha.pojo.common.LogDocument;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogMySQLImpl implements MLog {

    final com.bupt.dailyhaha.mapper.mysql.MLog mLog;

    public LogMySQLImpl(com.bupt.dailyhaha.mapper.mysql.MLog mLog) {
        this.mLog = mLog;
    }

    @Override
    public void insertLog(LogDocument document) {
        mLog.insert(document);
    }

    @Override
    public List<LogDocument> find(long from, long to) {
        QueryWrapper<LogDocument> wrapper = new QueryWrapper<>();
        wrapper.ge("timestamp", from).le("timestamp", to);
        return mLog.selectList(wrapper);
    }
}

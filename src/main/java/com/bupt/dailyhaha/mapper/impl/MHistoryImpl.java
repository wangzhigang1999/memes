package com.bupt.dailyhaha.mapper.impl;

import com.bupt.dailyhaha.mapper.MHistory;
import com.bupt.dailyhaha.pojo.media.History;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MHistoryImpl implements MHistory {
    @Override
    public History findByDate(String date) {
        return null;
    }

    @Override
    public boolean updateHistory(String date, History history) {
        return false;
    }

    @Override
    public List<String> finAvailableDates(int limit) {
        return null;
    }
}

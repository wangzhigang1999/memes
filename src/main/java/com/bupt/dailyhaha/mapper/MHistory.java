package com.bupt.dailyhaha.mapper;

import com.bupt.dailyhaha.pojo.media.History;

import java.util.List;

public interface MHistory {
    History findByDate(String date);

    boolean updateHistory(String date, History history);

    List<String> finAvailableDates(int limit);
}

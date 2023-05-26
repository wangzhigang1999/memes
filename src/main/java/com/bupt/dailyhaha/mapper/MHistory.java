package com.bupt.dailyhaha.mapper;

import com.bupt.dailyhaha.pojo.media.History;

import java.util.List;

/**
 * History 的 Mapper
 */
public interface MHistory {
    /**
     * 查找某一天的记录
     * 当给出某天的日期时，返回该天的记录
     *
     * @param date 日期 YYYY——MM——DD
     * @return History
     */
    History findByDate(String date);

    /**
     * 更新某一天的记录
     * 在某些数据库中，比如MySQL，由于关系型数据库的特性，其实是不需要实现这个方法的
     * 只要能保证 findByDate 能够正常工作即可
     *
     * @param date    日期 YYYY——MM——DD
     * @param history History
     * @return boolean
     * @see com.bupt.dailyhaha.mapper.MHistory#findByDate(String)
     */
    boolean updateHistory(String date, History history);

    /**
     * 获取可用的日期
     *
     * @param limit 限制返回的日期数量
     * @return List<String>
     */
    List<String> findAvailableDates(int limit);
}

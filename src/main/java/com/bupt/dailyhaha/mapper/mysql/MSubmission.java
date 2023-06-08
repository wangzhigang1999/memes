package com.bupt.dailyhaha.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bupt.dailyhaha.pojo.media.Submission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MSubmission extends BaseMapper<Submission> {


    @Select(value = {"select * from submission where timestamp between #{from} and #{to} and deleted=#{deleted} and reviewed=#{reviewed}"})
    List<Submission> find(long from, long to, Integer deleted, Integer reviewed);

    @Select(value = {"select count(*) from submission where timestamp between #{from} and #{to} and deleted=#{deleted} and reviewed=#{reviewed}"})
    Long count(long from, long to, Integer deleted, Integer reviewed);

    @Select(value = {"select distinct date from submission order by date desc limit #{limit}"})
    List<String> findAvailableDates(int limit);
}

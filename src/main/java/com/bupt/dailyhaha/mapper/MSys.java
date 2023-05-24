package com.bupt.dailyhaha.mapper;

import com.bupt.dailyhaha.pojo.SysConfig;

public interface MSys {
    SysConfig load();

    SysConfig save(SysConfig sysConfig);
}

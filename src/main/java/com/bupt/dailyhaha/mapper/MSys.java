package com.bupt.dailyhaha.mapper;

import com.bupt.dailyhaha.pojo.SysConfig;

public interface MSys {
    /**
     * 加载配置
     *
     * @return 配置
     */
    SysConfig load();

    /**
     * 保存配置
     *
     * @param sysConfig 配置
     * @return 配置
     */
    SysConfig save(SysConfig sysConfig);
}

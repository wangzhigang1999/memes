package com.bupt.memes.controller.submission;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.service.SysConfigService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/submission")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class SubAdmin {

    final SysConfigService sysConfig;

    /**
     * 置顶
     *
     * @param id
     *            id
     * @return ResultData
     */
    @PostMapping("/top/{id}")
    @AuthRequired
    public Boolean top(@PathVariable("id") String id) {
        return sysConfig.addTop(id);
    }

    /**
     * 取消置顶
     *
     * @param id
     *            id
     * @return ResultData
     */
    @DeleteMapping("/top/{id}")
    @AuthRequired
    public Boolean unTop(@PathVariable("id") String id) {
        return sysConfig.removeTop(id);
    }

    /**
     * 设置每天的最少投稿数目，
     * 当天的投稿数目小于这个数目时，会自动的开启 bot；
     * 大于这个数目时，会关闭 bot
     *
     * @param min
     *            最少投稿数目
     * @return Boolean
     */
    @PostMapping("/minimum/{min}")
    @AuthRequired
    public Boolean setMinSubmissions(@PathVariable("min") int min) {
        return sysConfig.setMinSubmissions(min);
    }

}

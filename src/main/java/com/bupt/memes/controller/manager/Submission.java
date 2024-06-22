package com.bupt.memes.controller.manager;

import com.bupt.memes.anno.AuthRequired;
import com.bupt.memes.config.AppConfig;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/submission")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class Submission {

    final AppConfig appConfig;

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
        return appConfig.addTop(id);
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
        return appConfig.removeTop(id);
    }

}

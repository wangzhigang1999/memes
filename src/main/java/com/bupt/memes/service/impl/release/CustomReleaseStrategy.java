package com.bupt.memes.service.impl.release;

import com.bupt.memes.model.media.Submission;
import com.bupt.memes.service.Interface.ReleaseStrategy;
import com.bupt.memes.util.Utils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("Custom")
public class CustomReleaseStrategy implements ReleaseStrategy {

    /**
     * 一个自定义发布策略
     *
     * @param currentSubmissions 当前已发布的投稿
     * @param newSubmissions     新的投稿
     * @return 应该被发布的投稿
     */
    @Override
    public List<Submission> release(List<Submission> currentSubmissions, List<Submission> newSubmissions) {
        int size = newSubmissions.size();

        int hour = Utils.getCurrentHour();
        if (hour >= 8 && hour <= 20) {
            // 8:00 - 22:00 白天，每次发布5张
            if (size <= 5) {
                currentSubmissions.addAll(newSubmissions);
            } else {
                currentSubmissions.addAll(newSubmissions.subList(0, 5));
            }
        } else if (hour >= 20) {
            // 20:00 - 00:00 晚上了，全部发布
            currentSubmissions.addAll(newSubmissions);
        } else {
            // 00:00 - 8:00 睡觉的时候，每次只发布一张
            if (size <= 1) {
                currentSubmissions.addAll(newSubmissions);
            } else {
                currentSubmissions.addAll(newSubmissions.subList(0, 1));
            }
        }

        return currentSubmissions;
    }

}

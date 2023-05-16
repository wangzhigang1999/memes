package com.bupt.dailyhaha.service.impl.release;

import com.bupt.dailyhaha.Utils;
import com.bupt.dailyhaha.pojo.media.Submission;
import com.bupt.dailyhaha.service.Interface.ReleaseStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("Custom")
public class CustomReleaseStrategy implements ReleaseStrategy {
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
            // 22:00 - 00:00 晚上了，全部发布
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

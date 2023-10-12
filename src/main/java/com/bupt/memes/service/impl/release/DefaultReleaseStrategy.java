package com.bupt.memes.service.impl.release;

import com.bupt.memes.model.media.Submission;
import com.bupt.memes.service.Interface.ReleaseStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("default")
public class
DefaultReleaseStrategy implements ReleaseStrategy {

    /**
     * 默认的发布策略，全部发布
     *
     * @param currentSubmissions 当前已发布的投稿
     * @param newSubmissions     新的投稿
     * @return 应该被发布的投稿
     */
    @Override
    public List<Submission> release(List<Submission> currentSubmissions, List<Submission> newSubmissions) {
        if (currentSubmissions == null) {
            return newSubmissions;
        }
        currentSubmissions.addAll(newSubmissions);
        return currentSubmissions;
    }
}

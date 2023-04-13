package com.bupt.dailyhaha.service.impl.release;

import com.bupt.dailyhaha.pojo.Submission;
import com.bupt.dailyhaha.service.ReleaseStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("default")
public class DefaultReleaseStrategy implements ReleaseStrategy {
    @Override
    public List<Submission> release(List<Submission> currentSubmissions, List<Submission> newSubmissions) {
        if (currentSubmissions == null) {
            return newSubmissions;
        }
        currentSubmissions.addAll(newSubmissions);
        return currentSubmissions;
    }
}

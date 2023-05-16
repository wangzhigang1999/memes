package com.bupt.dailyhaha.service.Interface;

import com.bupt.dailyhaha.pojo.media.Submission;

import java.util.List;

public interface ReleaseStrategy {
    List<Submission> release(List<Submission> currentSubmissions, List<Submission> newSubmissions);
}

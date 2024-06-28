package com.memes.model.common;

import java.util.Set;

/**
 * 在数据库中有三张表，分别是等待审核投稿、审核过投稿、删除的投稿
 */
public class SubmissionCollection {

    public final static String WAITING_SUBMISSION = "waiting_submission";
    public final static String PASSED_SUBMISSION = "submission";
    public final static String DELETED_SUBMISSION = "deleted_submission";

    public final static Set<String> COLLECTIONS = Set.of(WAITING_SUBMISSION, PASSED_SUBMISSION, DELETED_SUBMISSION);

}
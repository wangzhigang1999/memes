package com.bupt.memes.model.common;

import java.util.List;

public class SubmissionCollection {

    public final static String WAITING_SUBMISSION = "waiting_submission";
    public final static String SUBMISSION = "submission";
    public final static String DELETED_SUBMISSION = "deleted_submission";

    public final static List<String> COLLECTIONS = List.of(WAITING_SUBMISSION, SUBMISSION, DELETED_SUBMISSION);


}
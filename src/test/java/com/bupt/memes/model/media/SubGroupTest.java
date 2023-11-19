package com.bupt.memes.model.media;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

class SubGroupTest {


    SubmissionGroup submissionGroup = null;

    @Test
    @Order(1)
    void fromSubmission() {
        assert submissionGroup == null;
        Optional<SubmissionGroup> group = SubmissionGroup.fromSubmission(null);
        assert group.isEmpty();

        Submission submission = new Submission().setSubmissionType(SubmissionType.IMAGE);
        group = SubmissionGroup.fromSubmission(List.of(submission));
        assert group.isPresent();

        submissionGroup = group.get();
        assert submissionGroup.children.size() == 1;

    }

    @Test
    @Order(2)
    void addSubmissions() {
        fromSubmission();
        Submission submission = new Submission().setSubmissionType(SubmissionType.IMAGE);
        submissionGroup.addSubmissions(List.of(submission));
        assert submissionGroup.children.size() == 2;


        Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();

        System.out.println(gson.toJson(submissionGroup));

    }

}
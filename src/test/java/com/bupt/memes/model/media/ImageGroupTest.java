package com.bupt.memes.model.media;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

class ImageGroupTest {


    ImageGroup imageGroup = null;

    @Test
    @Order(1)
    void fromSubmission() {
        assert imageGroup == null;
        Optional<ImageGroup> group = ImageGroup.fromSubmission(null);
        assert group.isEmpty();

        Submission submission = new Submission().setSubmissionType(SubmissionType.IMAGE);
        group = ImageGroup.fromSubmission(List.of(submission));
        assert group.isPresent();

        imageGroup = group.get();
        assert imageGroup.images.size() == 1;

    }

    @Test
    @Order(2)
    void addSubmissions() {
        fromSubmission();
        Submission submission = new Submission().setSubmissionType(SubmissionType.IMAGE);
        imageGroup.addSubmissions(List.of(submission));
        assert imageGroup.images.size() == 2;


        Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();

        System.out.println(gson.toJson(imageGroup));

    }

}
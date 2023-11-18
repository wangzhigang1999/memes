package com.bupt.memes.service.impl;

import com.bupt.memes.model.media.ImageGroup;
import com.bupt.memes.model.media.Submission;
import com.bupt.memes.service.Interface.IImageGroup;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ImageGroupImpl implements IImageGroup {

    final MongoTemplate template;

    final static Logger logger = org.slf4j.LoggerFactory.getLogger(ImageGroupImpl.class);

    @Override
    @Transactional
    public ImageGroup createImageGroup(List<String> submissionIds) {
        List<Submission> submissions = ensureSubmissionsExist(submissionIds);
        if (submissions.size() != submissionIds.size()) {
            logger.warn("create image group failed, submissions not exist");
            return null;
        }
        Optional<ImageGroup> group = ImageGroup.fromSubmission(submissions);
        assert group.isPresent();
        ImageGroup imageGroup = group.get();
        List<Submission> images = imageGroup.getImages();
        images.forEach(template::remove);
        template.insert(imageGroup);
        return imageGroup;
    }

    @Override
    @Transactional
    public ImageGroup addToImageGroup(String imageGroupId, List<String> submissionIds) {
        ImageGroup imageGroup = getById(imageGroupId);
        if (imageGroup == null) {
            return null;
        }
        List<Submission> submissions = ensureSubmissionsExist(submissionIds);
        if (submissions.size() != submissionIds.size()) {
            return null;
        }
        imageGroup.addSubmissions(submissions);
        template.save(imageGroup);
        submissions.forEach(template::remove);
        return imageGroup;
    }


    @Override
    public ImageGroup getById(String id) {
        return template.findById(id, ImageGroup.class);
    }


    private List<Submission> ensureSubmissionsExist(List<String> submissionsId) {
        return template.find(Query.query(Criteria.where("id").in(submissionsId)), Submission.class);
    }
}

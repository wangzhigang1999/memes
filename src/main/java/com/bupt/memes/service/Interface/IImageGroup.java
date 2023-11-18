package com.bupt.memes.service.Interface;

import com.bupt.memes.model.media.ImageGroup;

import java.util.List;

public interface IImageGroup {
    ImageGroup createImageGroup(List<String> submissionIds);

    ImageGroup addToImageGroup(String imageGroupId, List<String> submissionIds);


    ImageGroup getById(String id);

}

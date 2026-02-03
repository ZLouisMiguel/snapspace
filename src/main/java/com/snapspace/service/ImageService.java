package com.snapspace.service;

import com.snapspace.model.ImagePost;
import com.snapspace.dao.ImagePostDAO;

import java.util.List;

public class ImageService {

    private final ImagePostDAO dao = new ImagePostDAO();

    public void upload(ImagePost post) {
        dao.save(post);
    }

    public List<ImagePost> feed() {
        return dao.findAll();
    }
}
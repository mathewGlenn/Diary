package com.project.diary.model;

import java.util.List;

public class UniqueTags {
    List<String> unique_tags;

    public UniqueTags() {
    }

    public UniqueTags(List<String> pTags) {
        this.unique_tags = pTags;
    }

    public List<String> getUnique_tags() {
        return unique_tags;
    }

    public void setUnique_tags(List<String> unique_tags) {
        this.unique_tags = unique_tags;
    }
}

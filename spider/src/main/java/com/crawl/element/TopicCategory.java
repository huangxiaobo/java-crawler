package com.crawl.element;

import java.util.List;

/**
 * 知乎话题类别
 */
public class TopicCategory {

    private String topicCategoryName;       // 话题类别名称
    private Integer topicCategoryId;        // 话题类别id


    public TopicCategory(Integer topicCategoryId, String topicCategoryName) {
        this.topicCategoryId = topicCategoryId;
        this.topicCategoryName = topicCategoryName;
    }

    public Integer getTopicCategoryId() {
        return topicCategoryId;
    }

    public void setTopicCategoryId(Integer topicCategoryId) {
        this.topicCategoryId = topicCategoryId;
    }

    public String getTopicCategoryName() {
        return topicCategoryName;
    }

    public void setTopicCategoryName(String topicCategoryName) {
        this.topicCategoryName = topicCategoryName;
    }

    @Override
    public String toString() {
        return "[" + "TopicCategoryName: " + topicCategoryName + ", TopicCategoryId: "
            + topicCategoryId + "]";
    }
}

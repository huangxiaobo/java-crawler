package com.crawl.zhihu.element;

/**
 * 知乎话题
 */
public class Topic {

    public String topicName;            // 话题名称
    public Integer topicId;             // 话题id

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    @Override
    public String toString() {
        return "[" + "TopicName: " + topicName + ", TopicId: " + topicId + "]";
    }
}

package com.predictry.fisher.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.predictry.fisher.domain.util.JacksonTimeDeserializer;
import com.predictry.fisher.domain.util.JacksonTimeSerializer;
import org.springframework.util.Assert;

import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

public abstract class TimeBasedEntity {

    @Id
    @JsonSerialize(using=JacksonTimeSerializer.class)
    @JsonDeserialize(using=JacksonTimeDeserializer.class)
    private LocalDateTime time;

    @JsonIgnore
    private String indexBaseName;

    public TimeBasedEntity() {}

    public TimeBasedEntity(String indexBaseName) {
        this.indexBaseName = indexBaseName;
    }

    public TimeBasedEntity(String indexBaseName, LocalDateTime time) {
        this(indexBaseName);
        this.time = time;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    /**
     * Get Elasticsearch index name in form of "indexname_yyyy" based on the value of time.
     *
     * @return index name for Elasticsearch.
     */
    public String getIndexName() {
        Assert.notNull(time);
        return String.format("%s_%s", indexBaseName, time.getYear());
    }

    /**
     * Check if a time is in the range of this stat.
     *
     * @param anotherTime a <code>LocalDateTime</code> to check for.
     * @return <code>true</code> if the time is in this range.
     */
    public boolean isForTime(LocalDateTime anotherTime) {
        return !((time == null) || (anotherTime == null)) && time.toLocalDate().isEqual(anotherTime.toLocalDate()) && time.getHour() == anotherTime.getHour();
    }

}

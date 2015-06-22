package com.predictry.fisher.domain.pull;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.util.Assert;

@Document(indexName="pull", type="pullTime")
public class PullTime {

	public static final int MAX_REPEAT = 10;
	
	@Id
	private String id;
	private LocalDateTime forTime;
	private LocalDateTime lastExecutedTime;
	private Integer repeat = 0;
	
	/**
	 * This method should be called if pull operation was done successfully.
	 */
	public void success() {
		if (forTime == null) {
			forTime = LocalDateTime.now().plusHours(1);
		}
		lastExecutedTime = null;
		repeat = 0;
	}
	
	/**
	 * This method should be called if pull operation was failed.  After failed
	 * for several times, <code>PullTime</code> will assume nothing can be retrieved
	 * and advance to the next hour.
	 */
	public void fail() {
		Assert.notNull(forTime);
		// Assume nothing can be retrieved if still fails after repeating several times.
		if (repeat >= MAX_REPEAT) {
			success();
		}
		lastExecutedTime = LocalDateTime.now();
		repeat++;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDateTime getForTime() {
		return forTime;
	}

	public LocalDateTime getLastExecutedTime() {
		return lastExecutedTime;
	}

	public Integer getRepeat() {
		return repeat;
	}	
	
}

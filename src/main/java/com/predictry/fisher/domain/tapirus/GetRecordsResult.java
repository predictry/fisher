package com.predictry.fisher.domain.tapirus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GetRecordsResult {
	
	public enum STATUS { NOT_FOUND, PENDING, DOWNLOADED, BUILDING, PROCESSED };

	private LocalDate date;
	private Integer hour;
	private STATUS status;
	private String uri;
	private LocalDateTime lastUpdate;
	
	public GetRecordsResult() {}
	
	public GetRecordsResult(LocalDate date, Integer hour, STATUS status,
			String uri, LocalDateTime lastUpdate) {
		this.date = date;
		this.hour = hour;
		this.status = status;
		this.uri = uri;
		this.lastUpdate = lastUpdate;
	}

	public LocalDate getDate() {
		return date;
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public Integer getHour() {
		return hour;
	}
	
	public void setHour(Integer hour) {
		this.hour = hour;
	}
	
	public STATUS getStatus() {
		return status;
	}
	
	public void setStatus(STATUS status) {
		this.status = status;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "GetRecordsResult [date=" + date + ", hour=" + hour
				+ ", status=" + status + ", uri=" + uri + ", lastUpdate="
				+ lastUpdate + "]";
	}

}

package com.predictry.fisher.domain.tapirus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetRecordsResult {
	
	public enum STATUS { NOT_FOUND, PENDING, DOWNLOADED, BUILDING, PROCESSED };

	private LocalDate date;
	private Integer hour;
	private STATUS status;
	@JsonProperty("record_files")
	private RecordFile[] recordFiles;
	private LocalDateTime lastUpdate;
	
	public GetRecordsResult() {}
	
	public GetRecordsResult(LocalDate date, Integer hour, STATUS status,
			String uri, LocalDateTime lastUpdate) {
		this.date = date;
		this.hour = hour;
		this.status = status;
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
		
	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public RecordFile[] getRecordFiles() {
		return recordFiles;
	}

	public void setRecordFiles(RecordFile[] recordFiles) {
		this.recordFiles = recordFiles;
	}

	@Override
	public String toString() {
		return "GetRecordsResult [date=" + date + ", hour=" + hour
				+ ", status=" + status + ", recordFiles="
				+ Arrays.toString(recordFiles) + ", lastUpdate=" + lastUpdate
				+ "]";
	}
	
}

package com.predictry.fisher.domain.tapirus;

public class RecordFile {

	private String tenantId;
	private String uri;
	
	public String getTenantId() {
		return tenantId;
	}
	
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		return "RecordFile [tenantId=" + tenantId + ", uri=" + uri + "]";
	}
	
}

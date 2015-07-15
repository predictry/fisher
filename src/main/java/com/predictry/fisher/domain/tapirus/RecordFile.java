package com.predictry.fisher.domain.tapirus;


public class RecordFile {

	private String tenant;
	private String uri;
	
	public String getTenantId() {
		return tenant;
	}
	
	public void setTenantId(String tenantId) {
		this.tenant = tenantId;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		return "RecordFile [tenantId=" + tenant + ", uri=" + uri + "]";
	}
	
}

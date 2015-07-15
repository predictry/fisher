package com.predictry.fisher.domain.tapirus;


public class RecordFile {

	private String tenant;
	private String uri;
	
	public String getTenant() {
		return tenant;
	}
	
	public void setTenant(String tenant) {
		this.tenant = tenant;
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

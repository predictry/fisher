package com.predictry.fisher.service;

import static com.amazonaws.util.StringUtils.UTF8;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.fisher.domain.item.ItemRecommendation;

@Service
public class RecommendationS3Service {

	private static final Logger log = LoggerFactory.getLogger(RecommendationS3Service.class);

	@Autowired
	private ObjectMapper objectMapper;
	
	/**
	 * Put JSON content that contains recommendation information for an item as a file in S3 bucket.
	 * 
	 * @param id is the id of the item.
	 * @param tenantId is the tenant id that has this item.
	 * @param content is the content of the JSON file that will be created.
	 * @throws JsonProcessingException 
	 */
	public void putFile(String id, String tenantId, ItemRecommendation itemRecommendation) throws JsonProcessingException {
		log.debug("Pushing recommendation for item [" + id + "] for tenant [" + tenantId + "] to S3.");
		AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));
		String key = "data/tenants/" + tenantId + "/recommendations/similiar/" + id + ".json";
		byte[] contentBytes = objectMapper.writeValueAsString(itemRecommendation).getBytes(UTF8);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(contentBytes.length);
		PutObjectRequest request = new PutObjectRequest("predictry", key, new ByteArrayInputStream(contentBytes), metadata);
		s3Client.putObject(request);
	}
	
	/**
	 * Read JSON file that contains item recommendation from S3 bucket.
	 * 
	 * @param id is the id of the item.
	 * @param tenantId is the tenant id that has this item.
	 * @return content of this JSON file.
	 * @throws IOException 
	 */
	public String readFile(String id, String tenantId) throws IOException {
		AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));
		String key = "data/tenants/" + tenantId + "/recommendations/similiar/" + id + ".json";
		GetObjectRequest request = new GetObjectRequest("predictry", key);
		S3Object object = s3Client.getObject(request);
		return IOUtils.toString(object.getObjectContent());
	}

	/**
	 * Delete JSON recommendation file from S3 bucket.
	 * 
	 * @param id the id of the item
	 * @param tenantId is the tenant id that has this item.
	 */
	public void deleteFile(String id, String tenantId) {
		AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));
		String key = "data/tenants/" + tenantId + "/recommendations/similiar/" + id + ".json";
		DeleteObjectRequest request = new DeleteObjectRequest("predictry", key);
		s3Client.deleteObject(request);
	}
	
}

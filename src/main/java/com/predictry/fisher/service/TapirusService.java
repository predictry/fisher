package com.predictry.fisher.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.predictry.fisher.domain.tapirus.GetRecordsResult;

@Service
public class TapirusService {

	private static final Logger log = LoggerFactory.getLogger(PullService.class);
	
	private static final String TAPIRUS_URL = "http://119.81.208.244:7870";
	private static final String S3_BUCKET_NAME = "trackings";
	
	public GetRecordsResult getRecords(LocalDateTime time) {
		RestTemplate restTemplate = new RestTemplate();
		Map<String,String> vars = new HashMap<>();
		vars.put("date", time.format(DateTimeFormatter.ISO_LOCAL_DATE));
		vars.put("hour", String.valueOf(time.getHour()));
		log.info("Invoking Tapirus getRecords() with the following vars: " + vars);
		GetRecordsResult result = restTemplate.getForObject(TAPIRUS_URL + "/records?date={date}&hour={hour}", 
			GetRecordsResult.class, vars);
		log.info("Result = " + result);
		return result;
	}
	
	public List<String> readFile(GetRecordsResult response) throws IOException {
		log.info("Fetching " + response.getUri() + " from S3");
		List<String> results = new ArrayList<>();
		AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));
		S3ObjectInputStream s3InputStream = null;
		try (S3Object s3Object = s3Client.getObject(S3_BUCKET_NAME, response.getUri().replaceFirst(S3_BUCKET_NAME + "/", ""))) {			
			s3InputStream = s3Object.getObjectContent();
			try (GZIPInputStream gzipInputStream = new GZIPInputStream(s3InputStream)) {
				try (InputStreamReader isr = new InputStreamReader(gzipInputStream)) {
					try (BufferedReader reader = new BufferedReader(isr)) {
						String line;
						while ((line = reader.readLine()) != null) {
							results.add(line);
						}	
					}
				}
			}
		} finally {
			if (s3InputStream != null) {
				s3InputStream.close();
			}
		}
		log.info("Done fetching " + response.getUri());
		return results;
	}
	
}

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.predictry.fisher.domain.tapirus.GetRecordsResult;
import com.predictry.fisher.domain.tapirus.RecordFile;

@Service
public class TapirusService {

	private static final Logger log = LoggerFactory.getLogger(PullService.class);
	
	private static final String TAPIRUS_URL = "http://119.81.208.244:7870";
	private static final String S3_BUCKET_NAME = "trackings";
	
	/**
	 * Read result from Tapirus. If it is not found, this method will return <code>null</code>.
	 * 
	 * @param time the time to search for.
	 * @return an instance of <code>GetRecordsResult</code> or <code>null</code> if it is not found.
	 */
	public GetRecordsResult getRecords(LocalDateTime time) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			Map<String,String> vars = new HashMap<>();
			vars.put("date", time.format(DateTimeFormatter.ISO_LOCAL_DATE));
			vars.put("hour", String.valueOf(time.getHour()));
			log.info("Invoking Tapirus getRecords() with the following vars: " + vars);
			GetRecordsResult result = restTemplate.getForObject(TAPIRUS_URL + "/records?date={date}&hour={hour}", 
				GetRecordsResult.class, vars);
			log.info("Result = " + result);
			return result;
		} catch (HttpClientErrorException httpError) {
			if (httpError.getStatusCode().value() == 404) {
				return null;
			} else {
				throw httpError;
			}
		}
	}
	
	/**
	 * Read any extra data in the S3 bucket if it is available.
	 * 
	 * @param time the time to search for.
	 * @return the content of the file in form of list of <code>String</code>.
	 */
	public List<String> readFromS3(String uri) throws IOException {
		List<String> results = new ArrayList<>();
		log.info("Fetching " + uri + " from S3");
		AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));
		S3ObjectInputStream s3InputStream = null;
		try (S3Object s3Object = s3Client.getObject(S3_BUCKET_NAME, uri)) {			
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
		log.info("Done fetching " + uri);
		return results;
	}
		
	/**
	 * Read from S3 bucket based on Tapirus results.
	 * 
	 * @param recordFile is the <code>RecordFile</code> that contains uri to read.
	 * @return the content of the file in form of list of <code>String</code>.
	 */
	public List<String> readFile(RecordFile recordFile) throws IOException {
		String uri = recordFile.getUri().replaceFirst(S3_BUCKET_NAME + "/", "");
		return readFromS3(uri);
	}
	
	/**
	 * @deprecated It is not recommended to use this method since this will execute many
	 * requests to S3 and causes garbage collection problem.
	 */
	public List<String> readFile(GetRecordsResult response) throws IOException {
		List<String> results = new ArrayList<>();
		for (RecordFile recordFile: response.getRecordFiles()) {
			String uri = recordFile.getUri().replaceFirst(S3_BUCKET_NAME + "/", "");
			log.info("Fetching " + uri + " from S3");
			AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));
			S3ObjectInputStream s3InputStream = null;
			try (S3Object s3Object = s3Client.getObject(S3_BUCKET_NAME, uri)) {			
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
			log.info("Done fetching " + uri);
		}
		return results;
	}
	
}

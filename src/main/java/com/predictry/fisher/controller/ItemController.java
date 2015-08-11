package com.predictry.fisher.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.predictry.fisher.domain.util.Helper;
import com.predictry.fisher.service.ItemAsMapService;

@RestController
public class ItemController {

	private static final Logger log = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private ItemAsMapService itemAsMapService;
	
	/**
	 * Retrieve information about an item based on tenant id and item id.
	 * 
	 * @return JSON value (<code>Item</code>).
	 */
	@RequestMapping("/items/{tenantId}/{itemId}")
	public Map<String, Object> findItem(@PathVariable String tenantId, @PathVariable String itemId) {
		tenantId = Helper.tenantIdRemapping(tenantId);
		log.info("Find item [" + itemId + "] for tenant id [" + tenantId + "]" );
		return itemAsMapService.find(tenantId, itemId);
	}
	
	/**
	 * Retrieve number of items stored for a tenant id.
	 * 
	 * @return number of items.
	 */
	@RequestMapping("/items/{tenantId}/count")
	public Long count(@PathVariable String tenantId) {
		tenantId = Helper.tenantIdRemapping(tenantId);
		log.info("Calculating number of items for tenant id [" + tenantId + "]");
		return itemAsMapService.count(tenantId);
	}
	
	/**
	 * Upload file that contains list of items.
	 * 
	 * @param tenantId is the tenant id.
	 * @param file is the file that is is upload.
	 * @return information about the upload status.
	 */
	@RequestMapping(value="/items/{tenantId}/upload", method=RequestMethod.POST)
	public Map<String, Object> upload(@PathVariable String tenantId, @RequestParam("file") MultipartFile file,
			@RequestParam(value="convertUrlToRelative", required=false, defaultValue="false") Boolean convertUrlToRelative) {
		int lineCount = 0;
		int errCount = 0;
		String errMessages = null;
		log.info("Importing item from CSV file [" + file.getOriginalFilename() + "], convertUrlToRelative = [" + convertUrlToRelative + "]");
		if (!file.isEmpty()) {
			Map<String, Map<String, Object>> items = new HashMap<>();
			try (InputStream is = file.getInputStream()) {
				try (InputStreamReader isr = new InputStreamReader(is)) {
					try (BufferedReader reader = new BufferedReader(isr)) {
						String line;
						boolean firstLine = true;
						String[] fieldNames = null;
						while ((line=reader.readLine()) != null) {
							lineCount++;
							if (firstLine) {
								fieldNames = line.split(",");
								firstLine = false;
							} else {
								String[] fields = line.split(",");
								if (fields.length < fieldNames.length) {
									errCount++;
									log.error("Error at line {" + lineCount + "}, fields length {" + fields.length + "} is less than the required size {" + fieldNames.length + "}");
								} else {
									String id = fields[0];
									Map<String, Object> item = new HashMap<>();
									for (int i=0; i<fieldNames.length; i++) {
										if (convertUrlToRelative) {
											item.put(fieldNames[i], Helper.convertToRelativeUrl(fields[i]));
										} else {
											item.put(fieldNames[i], fields[i]);
										}
									}
									items.put(id, item);
								}
							}
						}
					}
				}
				itemAsMapService.saveBatch(tenantId, items);
			} catch (IOException ex) {
				log.error("Error while processing uploaded file", ex);
			} catch (RuntimeException ex) {
				errMessages = ex.getMessage();
			}
		}
		
		Map<String, Object> result = new HashMap<>();
		result.put("lineCount", lineCount);
		result.put("errCount", errCount);
		if (errMessages != null) {
			result.put("errMessages", errMessages);
		}
		log.info("Importing item from CSV file [" + file.getOriginalFilename() + "] was done.");		
		return result;
	}
}

package org.projectreactor.qs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extract JSON data from a local file into memory.
 *
 * @author Jon Brisbin
 */
@Service
public class DataService {

	private static final List<Map<String, Object>> RECORDS = new ArrayList<>();
	private static final List<String> RECORDS_JSON;
	private static final int          RECORD_COUNT;

	static {
		try {
			RECORDS_JSON = Files.readAllLines(Paths.get("src/main/resources/land_grant_data.json"));
			ObjectMapper mapper = new ObjectMapper();
			for(String s : RECORDS_JSON) {
				//noinspection unchecked
				RECORDS.add(mapper.readValue(s, Map.class));
			}
		} catch(IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		RECORD_COUNT = RECORDS.size();
	}

	private final AtomicInteger counter = new AtomicInteger();

	/**
	 * Randomly choose a record to return from the available raw data.
	 *
	 * @return a randomly-selected record
	 */
	public Map<String, Object> getRandomRecord() {
		int idx = ThreadLocalRandom.current().nextInt(RECORD_COUNT);
		return RECORDS.get(idx);
	}

	/**
	 * Sequentially select a record of data from the available raw data.
	 *
	 * @return a record selected sequentially based on an internal counter
	 */
	public Map<String, Object> getSequentialRecord() {
		return RECORDS.get(counter.incrementAndGet() % RECORD_COUNT);
	}

	/**
	 * Sequentially select a record of data from the available raw data.
	 *
	 * @return a record selected sequentially based on an internal counter
	 */
	public String getSequentialRecordJson() {
		return RECORDS_JSON.get(counter.incrementAndGet() % RECORD_COUNT);
	}

}

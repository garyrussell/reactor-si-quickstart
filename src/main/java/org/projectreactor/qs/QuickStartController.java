package org.projectreactor.qs;

import org.projectreactor.qs.service.DataService;
import org.projectreactor.qs.service.MessageCountService;
import org.projectreactor.qs.service.ThroughputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Simple web controller to provide a REST interface for interacting with the application.
 *
 * @author Jon Brisbin
 */
@RestController
public class QuickStartController {

	@Value("${reactor.port:3000}")
	private int reactorPort;

	private final MessageCountService msgCnt;
	private final DataService         data;
	private final ThroughputService   throughput;

	@Autowired
	public QuickStartController(MessageCountService msgCnt,
	                            DataService data,
	                            ThroughputService throughput) {
		this.msgCnt = msgCnt;
		this.data = data;
		this.throughput = throughput;
	}

	@RequestMapping(value = "/count", produces = "text/plain")
	public String count() {
		int cnt = msgCnt.getCount();
		msgCnt.reset();
		return String.format("msg count: %s%n", cnt);
	}

	@RequestMapping(value = "/throughput", produces = "text/plain")
	public String throughput() {
		int th = throughput.getCurrentThroughput();
		return String.format("throughput: %s%n", th);
	}

	@RequestMapping(value = "/data/random", produces = "application/json")
	public Map<String, Object> randomRecord() {
		return data.getRandomRecord();
	}

	@RequestMapping(value = "/data/sequential", produces = "application/json")
	public Map<String, Object> sequentialRecord() {
		return data.getSequentialRecord();
	}

}


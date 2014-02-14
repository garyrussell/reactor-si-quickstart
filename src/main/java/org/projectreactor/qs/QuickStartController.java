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

	/**
	 * Display the current message count from the {@link org.projectreactor.qs.service.MessageCountService}.
	 *
	 * @return the current message count
	 */
	@RequestMapping(value = "/count", produces = "text/plain")
	public String count() {
		int cnt = msgCnt.getCount();
		msgCnt.reset();
		return String.format("msg count: %s%n", cnt);
	}

	/**
	 * Display the current throughput from the {@link org.projectreactor.qs.service.ThroughputService}.
	 *
	 * @return the current throughput
	 */
	@RequestMapping(value = "/throughput", produces = "text/plain")
	public String throughput() {
		int th = throughput.getCurrentThroughput();
		return String.format("throughput: %s%n", th);
	}

	/**
	 * Display a randomly-selected record of data from the {@link org.projectreactor.qs.service.DataService}.
	 *
	 * @return a data record
	 */
	@RequestMapping(value = "/data/random", produces = "application/json")
	public Map<String, Object> randomRecord() {
		return data.getRandomRecord();
	}

	/**
	 * Display a sequentially-selected record of data from the {@link org.projectreactor.qs.service.DataService}.
	 *
	 * @return a data record
	 */
	@RequestMapping(value = "/data/sequential", produces = "application/json")
	public Map<String, Object> sequentialRecord() {
		return data.getSequentialRecord();
	}

}


package org.projectreactor.qs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Keep tabs on the message count and calculate throughput between calls to {@link #getCurrentThroughput()}.
 *
 * @author Jon Brisbin
 */
@Service
public class ThroughputService {

	private final    MessageCountService msgCnt;
	private volatile long                lastCount;
	private volatile int                 lastThroughput;
	private volatile long lastStart = System.currentTimeMillis();

	@Autowired
	public ThroughputService(MessageCountService msgCnt) {
		this.msgCnt = msgCnt;
	}

	/**
	 * Calculate the throughput of the messages that have been published in the system since the last call to this
	 * method.
	 *
	 * @return the current throughput
	 */
	public int getCurrentThroughput() {
		if(msgCnt.getCount() == lastCount) {
			return lastThroughput;
		}

		long endCount = msgCnt.getCount();
		long total = endCount - lastCount;
		lastCount = endCount;

		long end = System.currentTimeMillis();
		double elapsed = end - lastStart;
		lastStart = end;

		lastThroughput = (int)(total / (elapsed / 1000.0));
		return lastThroughput;
	}

}

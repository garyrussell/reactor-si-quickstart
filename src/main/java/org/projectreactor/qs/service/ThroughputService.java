package org.projectreactor.qs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
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

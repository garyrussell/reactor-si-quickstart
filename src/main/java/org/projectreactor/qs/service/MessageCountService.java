package org.projectreactor.qs.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service to abstract access to an shared, internal counter.
 *
 * @author Jon Brisbin
 */
@Service
public class MessageCountService {

	private final AtomicInteger count = new AtomicInteger();

	/**
	 * Reset the count to zero.
	 */
	public void reset() {
		count.set(0);
	}

	/**
	 * Get the current count.
	 *
	 * @return the current count
	 */
	public int getCount() {
		return count.get();
	}

	/**
	 * Increment the counter.
	 *
	 * @return the post-incremented value
	 */
	public int increment() {
		return count.incrementAndGet();
	}

	/**
	 * Decrement the counter.
	 *
	 * @return the post-decremented value
	 */
	public int decrement() {
		return count.decrementAndGet();
	}

}

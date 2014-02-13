package org.projectreactor.qs.integration;

import org.springframework.context.ApplicationEvent;

/**
 * @author Jon Brisbin
 */
public class TcpConnectionExceptionEvent extends ApplicationEvent {
	public TcpConnectionExceptionEvent(Throwable ex) {
		super(ex);
	}

	@Override
	public Throwable getSource() {
		return (Throwable)super.getSource();
	}
}

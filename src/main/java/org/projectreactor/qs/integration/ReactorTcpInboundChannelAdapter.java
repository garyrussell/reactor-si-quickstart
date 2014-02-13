package org.projectreactor.qs.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import reactor.core.Environment;
import reactor.function.Consumer;
import reactor.io.Buffer;
import reactor.io.encoding.Codec;
import reactor.tcp.TcpConnection;
import reactor.tcp.TcpServer;
import reactor.tcp.config.ServerSocketOptions;
import reactor.tcp.config.SslOptions;
import reactor.tcp.netty.NettyTcpServer;
import reactor.tcp.spec.TcpServerSpec;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A Spring Integration {@literal InboundChannelAdapter} that ingest incoming TCP data using Reactor's Netty-based TCP
 * support.
 *
 * @author Jon Brisbin
 */
public class ReactorTcpInboundChannelAdapter<IN, OUT>
		extends MessageProducerSupport
		implements ApplicationEventPublisherAware {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final    TcpServerSpec<IN, OUT>    spec;
	private volatile TcpServer<IN, OUT>        server;
	private volatile ApplicationEventPublisher eventPublisher;

	public ReactorTcpInboundChannelAdapter(Environment env,
	                                       int listenPort,
	                                       String dispatcher) {
		Assert.notNull(env, "Environment cannot be null.");

		this.spec = new TcpServerSpec<IN, OUT>(NettyTcpServer.class)
				.env(env)
				.dispatcher(dispatcher)
				.listen(listenPort)
				.consume(new Consumer<TcpConnection<IN, OUT>>() {
					@Override
					public void accept(TcpConnection<IN, OUT> conn) {
						final AtomicLong msgCnt = new AtomicLong();
						final StopWatch stopWatch = new StopWatch();
						stopWatch.start();
						conn
								.when(Throwable.class, new Consumer<Throwable>() {
									@Override
									public void accept(Throwable t) {
										if(null != eventPublisher) {
											eventPublisher.publishEvent(new TcpConnectionExceptionEvent(t));
										}
									}
								})
								.consume(new Consumer<IN>() {
									@Override
									public void accept(IN in) {
										sendMessage(new GenericMessage<>(in));
										msgCnt.incrementAndGet();
									}
								})
								.on().close(new Runnable() {
							@Override
							public void run() {
								long cnt = msgCnt.get();
								stopWatch.stop();

								log.info("throughput this session: {}/sec in {}ms",
								         (int)(cnt / stopWatch.getTotalTimeSeconds()),
								         stopWatch.getTotalTimeMillis());
							}
						});
					}
				});
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Set the {@link reactor.tcp.config.ServerSocketOptions} to use.
	 *
	 * @param options
	 * 		the options to use
	 *
	 * @return {@literal this}
	 */
	public ReactorTcpInboundChannelAdapter<IN, OUT> setServerSocketOptions(ServerSocketOptions options) {
		spec.options(options);
		return this;
	}

	/**
	 * Set the {@link reactor.tcp.config.SslOptions} to use.
	 *
	 * @param sslOptions
	 * 		the options to use
	 *
	 * @return {@literal this}
	 */
	public ReactorTcpInboundChannelAdapter<IN, OUT> setSslOptions(SslOptions sslOptions) {
		spec.ssl(sslOptions);
		return this;
	}

	/**
	 * Set the {@link reactor.io.encoding.Codec} to use.
	 *
	 * @param codec
	 * 		the codec to use
	 *
	 * @return {@literal this}
	 */
	public ReactorTcpInboundChannelAdapter<IN, OUT> setCodec(Codec<Buffer, IN, OUT> codec) {
		spec.codec(codec);
		return this;
	}

	@Override
	protected void doStart() {
		server = spec.get().start();
	}

	@Override
	protected void doStop() {
		try {
			server.shutdown().await();
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}

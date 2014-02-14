package org.projectreactor.qs.integration;

import org.projectreactor.qs.service.MessageCountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.config.ConsumerEndpointFactoryBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import reactor.core.Environment;
import reactor.io.Buffer;
import reactor.io.encoding.LengthFieldCodec;
import reactor.io.encoding.PassThroughCodec;
import reactor.tcp.config.ServerSocketOptions;

/**
 * JavaConfig that merges external, XML-based Spring Integration components with Reactor SI compoments.
 *
 * @author Jon Brisbin
 */
@Configuration
@ImportResource("org/projectreactor/qs/integration/common.xml")
public class SpringIntegrationConfig {

	@Value("${reactor.port:3000}")
	private int    reactorPort;
	@Value("${reactor.dispatcher:ringBuffer}")
	private String dispatcher;

	/**
	 * Count up messages as they come through the channel.
	 *
	 * @param msgCnt
	 * 		the counter service
	 * @param output
	 * 		the output channel
	 *
	 * @return new {@link org.springframework.integration.config.ConsumerEndpointFactoryBean}
	 */
	@Bean
	public ConsumerEndpointFactoryBean counterEndpoint(final MessageCountService msgCnt,
	                                                   MessageChannel output) {
		ConsumerEndpointFactoryBean factoryBean = new ConsumerEndpointFactoryBean();
		factoryBean.setInputChannel(output);
		factoryBean.setHandler(new MessageHandler() {
			@Override
			public void handleMessage(Message<?> msg) throws MessagingException {
				msgCnt.increment();
			}
		});
		return factoryBean;
	}

	/**
	 * Reactor-based TCP InboundChannelAdapter. Since we're testing with random data, we can't really decode anything, so
	 * the {@link reactor.io.encoding.PassThroughCodec} just skips over any bytes to pretend it's dealt with them.
	 *
	 * @param env
	 * 		the Reactor {@code Environment} in use
	 * @param output
	 * 		the output channel
	 *
	 * @return the new {@code ReactorTcpInboundChannelAdapter}
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public ReactorTcpInboundChannelAdapter tcpChannelAdapter(Environment env, MessageChannel output) {
		ReactorTcpInboundChannelAdapter tcp = new ReactorTcpInboundChannelAdapter(env,
		                                                                          reactorPort,
		                                                                          dispatcher);
		tcp.setOutputChannel(output);
		return tcp.setServerSocketOptions(new ServerSocketOptions()
				                                  .tcpNoDelay(true)
				                                  .backlog(1000)
				                                  .rcvbuf(1048576)
				                                  .sndbuf(1048576))
		          .setCodec(new LengthFieldCodec(new PassThroughCodec<Buffer>() {
			          @Override
			          protected Buffer beforeAccept(Buffer b) {
				          // pretend like we did something with the data
				          return b.skip(b.remaining());
			          }
		          }));
	}

}

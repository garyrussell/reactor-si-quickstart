package org.projectreactor.qs.integration.test;

import org.junit.Test;
import org.projectreactor.qs.encoding.GsonCodec;
import org.projectreactor.qs.service.DataService;
import org.springframework.util.StopWatch;
import reactor.function.Function;
import reactor.io.Buffer;
import reactor.io.encoding.Codec;
import reactor.io.encoding.json.JsonCodec;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test the relative throughput performance of various codecs.
 *
 * @author Jon Brisbin
 */
public class CodecThroughputTests {

	static DataService DATA    = new DataService();
	static long        TIMEOUT = 10000;

	@Test
	public void testGsonCodecThroughput() {
		doTest(new GsonCodec<Map, Map>(Map.class));
	}

	@Test
	public void testJacksonJsonCodecThroughput() {
		doTest(new JsonCodec<Map, Map>(Map.class));
	}

	private void doTest(Codec<Buffer, Map, Map> codec) {
		AtomicLong counter = new AtomicLong();
		Function<Map, Buffer> encoder = codec.encoder();
		Function<Buffer, Map> decoder = codec.decoder(null);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		long start = System.currentTimeMillis();
		while((System.currentTimeMillis() - start) < TIMEOUT) {
			String s = DATA.getSequentialRecordJson();
			decoder.apply(Buffer.wrap(s));
			//encoder.apply(DATA.getSequentialRecord());
			counter.incrementAndGet();
		}
		stopWatch.stop();

		long throughput = (long)(counter.get() / stopWatch.getTotalTimeSeconds());
		System.out.format("%s throughput: %s/sec%n", codec.getClass().getSimpleName(), throughput);
	}
}

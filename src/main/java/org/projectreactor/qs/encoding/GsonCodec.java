package org.projectreactor.qs.encoding;

import com.google.gson.Gson;
import reactor.function.Consumer;
import reactor.function.Function;
import reactor.io.Buffer;
import reactor.io.encoding.Codec;

/**
 * Simple Reactor {@code Codec} that uses Google GSON to transcode data.
 *
 * @author Jon Brisbin
 */
public class GsonCodec<IN, OUT> implements Codec<Buffer, IN, OUT> {

	private final Gson       gson;
	private final Class<IN>  inType;
	private final Class<OUT> outType;
	private final Encoder<OUT> encoder = new Encoder<>();

	@SuppressWarnings("unchecked")
	public GsonCodec(Class type) {
		this(type, type);
	}

	public GsonCodec(Class<IN> inType, Class<OUT> outType) {
		this.inType = inType;
		this.outType = outType;
		this.gson = new Gson();
	}

	@Override
	public Function<Buffer, IN> decoder(Consumer<IN> next) {
		return new Decoder<>(next);
	}

	@Override
	public Function<OUT, Buffer> encoder() {
		return encoder;
	}

	private class Decoder<IN> implements Function<Buffer, IN> {
		private final Consumer<IN> next;

		private Decoder(Consumer<IN> next) {
			this.next = next;
		}

		@SuppressWarnings("unchecked")
		@Override
		public IN apply(Buffer buff) {
			IN in;
			if(null != (in = (IN)gson.fromJson(buff.asString(), inType)) && null != next) {
				next.accept(in);
			}
			return in;
		}
	}

	private class Encoder<OUT> implements Function<OUT, Buffer> {
		@Override
		public Buffer apply(OUT out) {
			return Buffer.wrap(gson.toJson(out, outType));
		}
	}

}

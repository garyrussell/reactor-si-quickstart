package org.projectreactor.qs.integration.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Jon Brisbin
 */
public class WriteLengthFieldDataFileApp {

	public static void main(String... args) throws IOException {
		long numItems = (args.length == 1 ? Integer.parseInt(args[0]) : 100000);
		Random random = ThreadLocalRandom.current();

		File dataFile = new File("src/main/resources/data.bin");
		OutputStream out = new FileOutputStream(dataFile);
		for(long l = 0; l < numItems; l++) {
			int len = (int)(2000 * random.nextDouble()) + 500;
			byte[] data = new byte[len];
			random.nextBytes(data);

			ByteBuffer buff = ByteBuffer.allocate(len + 4);
			buff.putInt(len);
			buff.put(data);
			buff.flip();

			out.write(buff.array());
		}
		out.flush();
		out.close();
	}

}

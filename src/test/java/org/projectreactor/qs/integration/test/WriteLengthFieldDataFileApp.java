package org.projectreactor.qs.integration.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple app to generate length-field-prefixed random data and write it to the file "src/main/resources/data.bin"
 * for
 * use later by the {@link org.projectreactor.qs.integration.test.LoadTestClient}.
 *
 * @author Jon Brisbin
 */
public class WriteLengthFieldDataFileApp {

	/**
	 * Generate random data and write it to the data file.
	 *
	 * @param args
	 * 		arg0: the number of records to write
	 *
	 * @throws IOException
	 */
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

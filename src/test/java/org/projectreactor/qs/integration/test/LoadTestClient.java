package org.projectreactor.qs.integration.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Jon Brisbin
 */
public class LoadTestClient {

	public static void main(String... args) throws InterruptedException,
	                                               IOException,
	                                               ExecutionException,
	                                               TimeoutException {
		int port = Integer.parseInt(args[0]);
		int threads = Integer.parseInt(args[1]);

		List<Client> clients = new ArrayList<>();
		for(int i = 0; i < threads; i++) {
			clients.add(new Client(port));
		}

		ExecutorService threadPool = Executors.newFixedThreadPool(threads);

		for(Future<Void> f : threadPool.invokeAll(clients)) {
			f.get();
		}
		threadPool.shutdown();
	}

	private static class Client implements Callable<Void> {
		private final int port;

		private Client(int port) {
			this.port = port;
		}

		@Override
		public Void call() {
			try {
				SocketChannel client = SocketChannel.open();
				client.configureBlocking(true);
				client.connect(new InetSocketAddress(port));

				FileChannel data = FileChannel.open(Paths.get("src/main/resources/data.bin"));
				long size = data.size();
				data.transferTo(0, size, client);

				client.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}

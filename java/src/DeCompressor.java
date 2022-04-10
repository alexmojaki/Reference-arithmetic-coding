import inputstreams.InputStreamFactory;

import java.io.IOException;
import java.io.OutputStream;

public abstract class DeCompressor extends ByteTransformer {

	@Override
	public void transformStream(InputStreamFactory inputStreamFactory, OutputStream out) throws IOException {
		try (BitInputStream in = new BitInputStream(inputStreamFactory.getStream());
		     out) {
			decompress(in, out);
		}
	}

	protected abstract void decompress(BitInputStream in, OutputStream out) throws IOException;
}

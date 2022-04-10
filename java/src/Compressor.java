import inputstreams.InputStreamFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Compressor extends ByteTransformer {

	@Override
	public void transformStream(InputStreamFactory inputStreamFactory, OutputStream outputStream) throws IOException {
		try (InputStream in = inputStreamFactory.getStream();
				BitOutputStream out = new BitOutputStream(outputStream)) {
			compress(in, out);
		}
	}

	protected abstract void compress(InputStream in, BitOutputStream out) throws IOException;
}

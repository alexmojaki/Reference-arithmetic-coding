/* 
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */

/**
 * Tests {@link PpmCompress} coupled with {@link PpmDecompress}.
 */
public class PpmCompressTest extends ArithmeticCodingTest {
	
	@Override
	protected Compressor getCompressor() {
		return new PpmCompress();
	}
	
	@Override
	protected DeCompressor getDecompressor() {
		return new PpmDecompress();
	}
	
}

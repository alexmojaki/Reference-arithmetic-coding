/* 
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Decompression application using prediction by partial matching (PPM) with arithmetic coding.
 * <p>Usage: java PpmDecompress InputFile OutputFile</p>
 * <p>This decompresses files generated by the "PpmCompress" application.</p>
 */
public final class PpmDecompress {
	
	// Must be at least -1 and match PpmCompress. Warning: Exponential memory usage at O(257^n).
	private static final int MODEL_ORDER = 3;
	
	
	public static void main(String[] args) throws IOException {
		// Handle command line arguments
		if (args.length != 2) {
			System.err.println("Usage: java PpmDecompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		File inputFile  = new File(args[0]);
		File outputFile = new File(args[1]);
		
		// Perform file decompression
		try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
			decompress(in, out);
		}
	}
	
	
	// To allow unit testing, this method is package-private instead of private.
	static void decompress(BitInputStream in, OutputStream out) throws IOException {
		// Set up decoder and model. In this PPM model, symbol 256 represents EOF;
		// its frequency is 1 in the order -1 context but its frequency
		// is 0 in all other contexts (which have non-negative order).
		ArithmeticDecoder dec = new ArithmeticDecoder(32, in);
		PpmModel model = new PpmModel(MODEL_ORDER, 256);
		int[] history = new int[0];
		
		while (true) {
			// Decode and write one byte
			int symbol = decodeSymbol(dec, model, history);
			if (symbol == model.escapeSymbol)  // EOF symbol
				break;
			out.write(symbol);
			model.incrementContexts(history, symbol);
			history = model.addToHistory(history, symbol);
		}
	}
	
	
	private static int decodeSymbol(ArithmeticDecoder dec, PpmModel model, int[] history) throws IOException {
		// Try to use highest order context that exists based on the history suffix. When symbol 256
		// is consumed at a context at any non-negative order, it means "escape to the next lower order
		// with non-empty context". When symbol 256 is consumed at the order -1 context, it means "EOF".
		outer:
		for (int order = history.length; order >= 0; order--) {
			PpmModel.Context ctx = model.rootContext;
			for (int i = 0; i < order; i++) {
				ctx = ctx.getSubcontexts()[history[i]];
				if (ctx == null)
					continue outer;
			}
			int symbol = dec.read(ctx.frequencies);
			if (symbol < model.escapeSymbol)
				return symbol;
			// Else we read the context escape symbol, so continue decrementing the order
		}
		// Logic for order = -1
		return dec.read(model.orderMinus1Freqs);
	}
	
}

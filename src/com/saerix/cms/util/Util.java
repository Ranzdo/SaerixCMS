package com.saerix.cms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class Util {
	public static String readFile(File file) throws IOException {
		  FileInputStream stream = new FileInputStream(file);
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    /* Instead of using default, pass in a decoder. */
		    return Charset.availableCharsets().get("UTF-8").decode(bb).toString();
		  }
		  finally {
		    stream.close();
		  }
	}
}

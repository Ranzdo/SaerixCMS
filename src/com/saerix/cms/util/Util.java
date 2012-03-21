package com.saerix.cms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;

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
	
	public static byte[] createChecksum(File file) {
		try {
			InputStream fis =  new FileInputStream(file);
			
			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;
			
			do {
				numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
			} while (numRead != -1);
			
			fis.close();
			return complete.digest();
		}
		catch(Exception e) {
			e.printStackTrace();
			return new byte[1];
		}
	}
	
    public static String getMD5Checksum(File file) {
		byte[] b = createChecksum(file);
		String result = "";
		   
		for (int i=0; i < b.length; i++) {
			result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
    }
}

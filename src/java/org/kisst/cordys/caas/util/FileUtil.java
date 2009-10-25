package org.kisst.cordys.caas.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
	public static void saveString(File filename, String content) {
		FileWriter out=null;
		try {
			out=new FileWriter(filename);
			out.write(content);
		}
		catch (IOException e) { throw new RuntimeException(e); }
		finally {
			if (out!=null) {
				try {
					out.close();
				}
				catch (IOException e) { throw new RuntimeException(e); }
			}
		}
	}



	public static String loadString(String filename) {
		BufferedReader inp = null;
		try {
			inp =new BufferedReader(new FileReader(filename));
			StringBuilder result=new StringBuilder();
			String line;
			while ((line=inp.readLine()) != null) {
				result.append(line);
				result.append("\n");
			}
			return result.toString();
		} 
		catch (java.io.IOException e) { throw new RuntimeException(e);  }
		finally {
			try {
				if (inp!=null) 
					inp.close();
			}
			catch (java.io.IOException e) { throw new RuntimeException(e);  }
		}
	}

}

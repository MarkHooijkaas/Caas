package org.kisst.cordys.caas.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
	    This class demonstrates how to read a line of text from the keyboard
 */
public class LogCommand extends CommandBase {
	public LogCommand() {
		super("[options] [--grep <string>] [<file> ...]", "treat log4j xml files as linebased logfiles");
	}

	@Override public void run(String[] args) {
		try {
			main(args);
		} catch (IOException e) { throw new RuntimeException(e); }
	}
	
	@Override public String getHelp() {
		return "\nOPTIONS"
  			+"\n\t   --grep <string>         search for lines that contain the string"
			+"\n\t-h,--no-filename           do not show the filename"
			+"\n\t-l,--files-with-matches    list only matching files"
			+"\n\t-c,--count                 count the number of lines with the pattern occurences"
			+"\n\t-t,--only-time             only show the timestamps (for statistics etc)"
			+"\n\t-v,--invert-match          only show lines that do not match";
	}
	
	// TODO: Old code from an all static class, need to clean it up
	static boolean onlyTime=false;
	static String grep=null;
	static boolean listFiles=false;
	static boolean countFiles=false;
	static boolean inverse=false;
	static boolean showFilename=true;

	public static void main(String[] args) throws IOException{


		int i;
		for (i=0; i<args.length; i++){
			if (args[i].equals("-t") || args[i].equals("--only-time"))
				onlyTime=true;
			else if (args[i].equals("--grep"))
				grep=args[i+1];
			else if (args[i].equals("-h") || args[i].equals("--no-filename"))
				showFilename=false;
			else if (args[i].equals("-l") || args[i].equals("--files-with-matches"))
				listFiles=true;
			else if (args[i].equals("-c") || args[i].equals("--count"))
				countFiles=true;
			else if (args[i].equals("-v") || args[i].equals("--invert-match"))
				inverse=true;
			else if (args[i].equals("-"))
				break;
			else {
				grep=args[i++];
				break;
			}
		}

		if (i<args.length) {
			for (;i<args.length; i++) 
				process(args[i], new FileInputStream(args[i]));
		}
		else {
			showFilename=false;
			process(null, System.in);
		}
	}

	static void process(String filename, InputStream is) throws IOException {
		InputStreamReader converter = new InputStreamReader(is);
		BufferedReader in = new BufferedReader(converter);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		String line= "";
		int count=0;
		while (line!=null){
			line = in.readLine();
			if (line!=null && line.startsWith("<log4j:event")) {
				String logger=getAttribute(line, "logger");
				long timestamp=Long.parseLong(getAttribute(line,"timestamp"));
				String level=getAttribute(line, "level");
				String thread=getAttribute(line, "thread");
				line = in.readLine();
				String message=getElementData(line,"log4j:message");
				String logline=format.format(new Date(timestamp))+"\t"+level+"\t"+logger+"\t"+thread+"\t"+message;
				boolean match= grep==null || logline.indexOf(grep)>=0;
				if (onlyTime)
					logline=format.format(new Date(timestamp));
				if (inverse)
					match=!match;
				if (match && message!=null) {
					if (countFiles || listFiles)
						count++;
					else {
						if (showFilename)
							System.out.print(filename+":");
						System.out.println(logline);
					}
				}
			}
		}
		if (countFiles && listFiles && count>0)
			System.out.println(filename+":"+count);
		else if (listFiles && count>0)
			System.out.println(filename);
		else if (countFiles && ! listFiles)
			System.out.println(filename+":"+count);
	}

	private static String getAttribute(String line, String name) {
		int pos=line.indexOf(name+"=\"")+name.length()+2;
		int pos2=line.indexOf("\"",pos);
		return line.substring(pos, pos2);
	}
	private static String getElementData(String line, String name) {
		int pos=line.indexOf("<"+name+">")+name.length()+2;
		if (pos<0)
			return null;
		int pos2=line.indexOf("</"+name+">",pos);
		if (pos2>0)
			return line.substring(pos, pos2);
		else
			return line.substring(pos);
	}
}

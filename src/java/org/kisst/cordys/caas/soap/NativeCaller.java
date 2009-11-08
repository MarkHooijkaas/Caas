/**
Copyright 2008, 2009 Mark Hooijkaas

This file is part of the Caas tool.

The Caas tool is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The Caas tool is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with the Caas tool.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.kisst.cordys.caas.soap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import org.kisst.cordys.caas.main.Environment;

public class NativeCaller extends BaseCaller {
	private static class MyAuthenticator extends Authenticator {
		private String username=null;
		private String password=null;
		private MyAuthenticator () { Authenticator.setDefault(this); }
		public void setCredentials(String username, String password) {
			this.username=username;
			this.password=password;
		}
		@Override public PasswordAuthentication getPasswordAuthentication() {
			Environment.get().debug("getPasswordAuthentication"
					+"\n\t"+this.getRequestingHost()
					+"\n\t"+this.getRequestingPort()
					+"\n\t"+this.getRequestingPrompt()
					+"\n\t"+this.getRequestingProtocol()
					+"\n\t"+this.getRequestingScheme()
					+"\n\t"+this.getRequestingSite()
					+"\n\t"+this.getRequestingURL()
					+"\n\t"+this.getRequestorType()
			);
			if (username!=null)
				return new PasswordAuthentication(username, password.toCharArray());
			else
				return super.getPasswordAuthentication();
		}
	}
	protected static final MyAuthenticator myAuthenticator = new MyAuthenticator();
	

	public NativeCaller(String name) { super(name); }

	@Override public String httpCall(String urlstr, String input) {
		try {
			URL url=new URL(urlstr);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

			byte[] b = input.getBytes();

			httpConn.setRequestProperty("Content-Length", ""+b.length);
			httpConn.setRequestProperty("Content-Type","text/xml; charset=utf-8");
			//httpConn.setRequestProperty("SOAPAction",SOAPAction);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);

			// Dangerous in multithreaded environments
			myAuthenticator.setCredentials(username, password);
			
			OutputStream out = httpConn.getOutputStream();
			out.write( b );    
			out.close();

			InputStreamReader isreader = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isreader);

			StringBuilder result=new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				result.append(inputLine);
			in.close();
			return result.toString();
		}
		catch (IOException e) { throw new RuntimeException(e); }
	}
}

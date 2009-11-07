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

package org.kisst.cordys.caas.main;

public class CaasMain {
	public static void main(String[] args) {
		if (args.length==1 && args[0].equals("setup"))
			// skip all option parsing, because cli jar may not be available.
			new SetupCommand().run(new String[]{});
		else {
			try {
				new CaasMainCommand().run(args);
			}
			catch (NoClassDefFoundError e) {
				System.out.println("not all libraries available");
				e.printStackTrace();
				missingJar();
			}
		}
	}
	
	private static void missingJar() {
		System.out.println("Some kind of error occured, This might be due to not having downloaded the necessary jar files");
		System.out.println("In order to download the necessary jar files you should execute the following command");
		System.out.println("\tjava -jar "+System.getProperty("java.class.path")+" setup");
	}

}

package org.kisst.cordys.caas.exception;

import org.kisst.cordys.caas.main.Environment;

/**
 *This class used to throw the runtime exceptions that may occur while executing the Caas framework code
 *
 *@author galoori 
 */
public class CaasRuntimeException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	Environment env = Environment.get();
	
	public CaasRuntimeException(String message) 
	{
		super(message);
		env.error(message);
	}
	public CaasRuntimeException(Exception e) 
	{
		super(e);
		env.error(e.getMessage());
	}
	public CaasRuntimeException(Throwable exception) 
	{
		super(exception);
	}
}

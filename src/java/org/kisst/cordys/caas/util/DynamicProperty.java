package org.kisst.cordys.caas.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.kisst.cordys.caas.CordysSystem;

public class DynamicProperty<T> {
	private final CordysSystem system;
	private final Constructor cons;
	private final String prefix;
	private final String dn;

	public DynamicProperty(CordysSystem system, Class resultClass, String prefix, String dn) {
		this.system=system;
		this.dn=dn;
		this.prefix=prefix;
		cons=ReflectionUtil.getConstructor(resultClass, new Class[] {CordysSystem.class, String.class});
	}
	@SuppressWarnings("unchecked")
	public T  propertyMissing(String name) {
		try {
			return (T) cons.newInstance(new Object[]{ system, prefix+name+","+dn});
		}
		catch (IllegalArgumentException e) { throw new RuntimeException(e); }
		catch (InstantiationException e) { throw new RuntimeException(e); }
		catch (IllegalAccessException e) { throw new RuntimeException(e); }
		catch (InvocationTargetException e) { throw new RuntimeException(e); }

	}
}

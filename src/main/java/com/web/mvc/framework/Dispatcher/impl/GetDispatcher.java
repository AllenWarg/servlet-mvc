package com.web.mvc.framework.Dispatcher.impl;

import com.web.mvc.framework.Dispatcher.Dispatcher;
import com.web.mvc.framework.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetDispatcher implements Dispatcher {
	private Object instance;
	private Method method;
	private String[] parameterNames;
	private Class<?>[] parameterTypes;

	public GetDispatcher(Object instance, Method method, String[] parameterNames, Class<?>[] parameterTypes) {
		this.instance = instance;
		this.method = method;
		this.parameterNames = parameterNames;
		this.parameterTypes = parameterTypes;
	}

	public Object getInstance() {
		return instance;
	}

	public Method getMethod() {
		return method;
	}

	public String[] getParameterNames() {
		return parameterNames;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	@Override
	public ModelAndView invoke(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int length = parameterNames.length;
		Object[] arguments = new Object[length];//根据参数类型创建
		for (int i=0;i<length;i++){
			String parameterName = parameterNames[i];
			Class<?> parameterClass = parameterTypes[i];
			if (parameterClass == HttpServletRequest.class) {
				arguments[i] = request;//如果请参数里面有HttpServletRequest类型就添加
			} else if (parameterClass == HttpServletResponse.class) {
				arguments[i] = response;
			} else if (parameterClass == HttpSession.class) {
				arguments[i] = request.getSession();
			} else if (parameterClass == int.class) {
				arguments[i] = Integer.valueOf(getOrDefault(request, parameterName, "0"));
			} else if (parameterClass == long.class) {
				arguments[i] = Long.valueOf(getOrDefault(request, parameterName, "0"));
			} else if (parameterClass == boolean.class) {
				arguments[i] = Boolean.valueOf(getOrDefault(request, parameterName, "false"));
			} else if (parameterClass == double.class) {
				arguments[i] = Double.valueOf(getOrDefault(request, parameterName, "false"));
			} else if (parameterClass == String.class) {
				arguments[i] = getOrDefault(request, parameterName, "");
			} else {
				throw new RuntimeException("Missing handler for type: " + parameterClass);
			}
		}
		return (ModelAndView) this.method.invoke(this.instance, arguments);//调用controller方法
	}

	private String getOrDefault(HttpServletRequest request, String name, String defaultValue) {

		String s = request.getParameter(name);
		return s == null ? defaultValue : s;
	}
}

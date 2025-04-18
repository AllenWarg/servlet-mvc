package com.web.mvc.framework.Dispatcher.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.mvc.framework.Dispatcher.Dispatcher;
import com.web.mvc.framework.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.lang.reflect.Method;


public class PostDispatcher implements Dispatcher {
	private Object instance;
	private Method method;
	final ObjectMapper objectMapper;
	private Class<?>[] parameterTypes;

	public PostDispatcher(Object instance, Method method, ObjectMapper objectMapper, Class<?>[] parameterTypes) {
		this.instance = instance;
		this.method = method;
		this.objectMapper = objectMapper;
		this.parameterTypes = parameterTypes;
	}

	public Object getInstance() {
		return instance;
	}

	public Method getMethod() {
		return method;
	}

	public ObjectMapper getParameterNames() {
		return objectMapper;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	@Override
	public ModelAndView invoke(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Object[] arguments = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterClass = parameterTypes[i];
			if (parameterClass == HttpServletRequest.class) {
				arguments[i] = request;
			} else if (parameterClass == HttpServletResponse.class) {
				arguments[i] = response;
			} else if (parameterClass == HttpSession.class) {
				arguments[i] = request.getSession();
			} else {
				BufferedReader reader = request.getReader();
				arguments[i] = this.objectMapper.readValue(reader, parameterClass);//映射为类对象
			}
		}
		return (ModelAndView) this.method.invoke(instance, arguments);
	}
}

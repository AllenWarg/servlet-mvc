package com.web.mvc.framework.Dispatcher;

import com.web.mvc.framework.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;

public interface Dispatcher {
	ModelAndView invoke(HttpServletRequest request, HttpServletResponse response) throws Exception;
}

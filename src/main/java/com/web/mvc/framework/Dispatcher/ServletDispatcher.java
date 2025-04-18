package com.web.mvc.framework.Dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.mvc.framework.Dispatcher.Dispatcher;
import com.web.mvc.framework.Dispatcher.impl.GetDispatcher;
import com.web.mvc.framework.Dispatcher.impl.PostDispatcher;
import com.web.mvc.framework.ModelAndView;
import com.web.mvc.framework.ViewEngine;
import com.web.mvc.framework.annotation.Controller;
import com.web.mvc.framework.annotation.GetMapping;
import com.web.mvc.framework.annotation.PostMapping;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

@WebServlet("/")
public class ServletDispatcher extends HttpServlet {
	//存储请求路径String和请求处理类Dispatcher的映射，请求处理类Dispatcher类中主要存储的是反射信息，
	private final Map<String, Dispatcher> postRequestMappings=new HashMap<>();
	private final Map<String, Dispatcher> getRequestMappings=new HashMap<>();
	//所有的Controller类字节码
	private final Set<Class<?>> allControllers=new HashSet<>();
	//模板引擎，处理渲染HTML模板
	private ViewEngine viewEngine;

	@Override
	public void init() {
		System.out.println("初始化");
		//初始化
		//1)获取所有的Controller字节码(包扫描)
		try {
			scannerPackage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//2)将请求路径url和Dispatcher请求处理器实例绑定并存入requestMappings
		try {
			handleRequestMappings();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//3）初始化视图引擎
		this.viewEngine=new ViewEngine(getServletContext());
	}
	public void scannerPackage() throws Exception{
		String packagePath="com.web.mvc.controller";
		scannerPackage(packagePath);
	}
	public void scannerPackage(String packagePath) throws Exception {
		String filePath=packagePath.replace(".", File.separatorChar+"");
		URL resourcePath = Thread.currentThread().getContextClassLoader().getResource(filePath);
		if (resourcePath==null){
			throw new IOException("包路径未找到："+packagePath);
		}
		File file = new File(resourcePath.getFile()+"");
		File[] files = file.listFiles();
		assert files != null;
		listFiles(files,packagePath);
	}
	private void listFiles(File[] files,String packagePath) throws Exception {
		for (File item : files){
			if (item.isFile()&&item.getName().endsWith(".class")){
				String tempPath=packagePath+"."+
						item.getName().replace(".class","");
				this.allControllers.add(Class.forName(tempPath));
			}else if (!item.isFile()){
				listFiles(Objects.requireNonNull(item.listFiles()),packagePath+"."+item.getName());
			}
		}
	}

	private void handleRequestMappings() throws Exception {
		for (Class<?> item:this.allControllers){
			if (!item.isAnnotationPresent(Controller.class)){
				continue;
			}
			Object controllerItem = item.getConstructor().newInstance();
			Method[] methods = item.getMethods();
			for (Method method : methods) {
				 if (method.isAnnotationPresent(GetMapping.class)){
					 GetMapping annotation = method.getAnnotation(GetMapping.class);
					 String value = annotation.value();
					 String[] parameterNames
							 = Arrays.stream(method.getParameters()).map(temp -> temp.getName()).toArray(String[]::new);
					 GetDispatcher getDispatcher
							 = new GetDispatcher(controllerItem, method, parameterNames, method.getParameterTypes());
					 getRequestMappings.put(value,getDispatcher);
				 }else if (method.isAnnotationPresent(PostMapping.class)){
					 PostMapping annotation = method.getAnnotation(PostMapping.class);
					 String value = annotation.value();
					 ObjectMapper objectMapper = new ObjectMapper();
					 PostDispatcher postDispatcher
							 = new PostDispatcher(controllerItem, method, objectMapper, method.getParameterTypes());
					 postRequestMappings.put(value,postDispatcher);
				 }
			}
		}
	}

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			process(req,resp,this.getRequestMappings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			process(req,resp,this.postRequestMappings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void process(HttpServletRequest req,
	                     HttpServletResponse resp,
	                     Map<String, ? extends Dispatcher> requestMappings) throws Exception {
		String requestURI = req.getRequestURI();
		Dispatcher dispatcher = requestMappings.get(requestURI);
		if (dispatcher == null) {
			resp.sendError(404);
			return;
		}
		ModelAndView mv = null;
		ModelAndView invoke = dispatcher.invoke(req, resp);
		if (invoke==null){
			return;
		}
		mv=invoke;
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter pw = resp.getWriter();//获取字节码
		this.viewEngine.render(mv, pw);//调用模板引擎渲染html页面，并作为http的body响应体
		pw.flush();
	}
}

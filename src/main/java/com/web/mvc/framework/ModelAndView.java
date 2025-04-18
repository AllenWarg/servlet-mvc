package com.web.mvc.framework;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
	private Map<String,Object> model;
	private String view;
	public ModelAndView(String view) {
		this.view = view;
	}

	public ModelAndView(String view,String key,Object object) {
		HashMap<String, Object> stringObjectHashMap = new HashMap<>();
		stringObjectHashMap.put(key,object);
		this.model=stringObjectHashMap;
		this.view=view;
	}

	public ModelAndView(String view,Map<String, Object> model) {
		this.model = model;
		this.view = view;
	}

	public Map<String, Object> getModel() {
		return model;
	}

	public String getView() {
		return view;
	}
}

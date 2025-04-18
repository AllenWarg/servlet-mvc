package com.web.mvc.controller.myIndex;

import com.web.mvc.framework.ModelAndView;
import com.web.mvc.framework.annotation.Controller;
import com.web.mvc.framework.annotation.GetMapping;
@Controller
public class MyIndex {
	@GetMapping("/index")
	public ModelAndView getabcd(){
		return new ModelAndView("/index.html");
	}
}

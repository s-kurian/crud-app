package com.aquent.crudapp;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex, Model model) {
    	ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", ex.getMessage());
        return mav;
    }
    
    @ExceptionHandler(Exception.class) 
    public ModelAndView handleGenericException(Exception ex, Model model) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "An unexpected error occurred. Please try again later.");
        return mav;
    }
}

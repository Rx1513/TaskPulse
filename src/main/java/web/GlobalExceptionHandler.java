package web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ex.printStackTrace(); // печатает stack trace в консоль
        ModelAndView mv = new ModelAndView("error");
        mv.getModel().put("message", ex.getMessage());
        return mv;
    }
}

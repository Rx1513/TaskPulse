package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ContentController {
    @GetMapping("/auth/register")
    public ModelAndView register() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("dummy");
        mv.getModel().put("data", "Welcome to registration page!");

        return mv;
    }

    @GetMapping("/auth/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("dummy");
        mv.getModel().put("data", "Welcome to login page!");

        return mv;
    }

    // Main page
    @GetMapping(path = {"/tasks", "/index", "/"})
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("dummy");
        mv.getModel().put("data", "Welcome to main page!");

        return mv;
    }

    @GetMapping("/task/show/{id}")
    public ModelAndView task() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("dummy");
        mv.getModel().put("data", "Welcome to task view page!");

        return mv;
    }

    @GetMapping("/task/new")
    public ModelAndView showTaskCreationPage() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("dummy");
        mv.getModel().put("data", "Welcome to task creation page!");

        return mv;
    }
}


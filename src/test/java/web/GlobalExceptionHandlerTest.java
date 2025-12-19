package web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

class GlobalExceptionHandlerTest {

    @Test
    void wrapsExceptionIntoErrorView() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Exception ex = new IllegalArgumentException("boom");

        ModelAndView mv = handler.handleException(ex);

        assertThat(mv.getViewName()).isEqualTo("error");
        assertThat(mv.getModel().get("message")).isEqualTo("boom");
    }
}

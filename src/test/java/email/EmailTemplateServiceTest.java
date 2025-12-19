package email;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;

class EmailTemplateServiceTest {

    @Test
    void rendersTemplateWithVariables() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());
        EmailTemplateService service = new EmailTemplateService(templateEngine);
        Map<String, Object> variables = Map.of("name", "Alice", "project", "TaskPulse");

        String result = service.render("Hello [[${name}]] from [[${project}]]", variables);

        assertThat(result).isEqualTo("Hello Alice from TaskPulse");
    }
}

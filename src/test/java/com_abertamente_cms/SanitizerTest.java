package com_abertamente_cms;

import org.junit.jupiter.api.Test;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class SanitizerTest {
    @Test
    public void testSanitizerNewlines() {
        PolicyFactory customPolicy = new HtmlPolicyBuilder()
                .allowElements("pre", "code")
                .allowAttributes("class").onElements("pre", "code")
                .toFactory();
                
        PolicyFactory policy = Sanitizers.FORMATTING
                .and(Sanitizers.LINKS)
                .and(Sanitizers.BLOCKS)
                .and(Sanitizers.IMAGES)
                .and(Sanitizers.STYLES)
                .and(Sanitizers.TABLES)
                .and(customPolicy);
                
        String html = "<p>Text</p><pre><code class=\"language-java\">line1\nline2\nline3</code></pre>";
        String sanitized = policy.sanitize(html);
        System.out.println("---- SANITIZED OUTPUT ----");
        System.out.println(sanitized);
        System.out.println("---- END OUTPUT ----");
    }
}

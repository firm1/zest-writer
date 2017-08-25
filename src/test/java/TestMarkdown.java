import com.zestedesavoir.zestwriter.model.markdown.ZMarkdown;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMarkdown {
    @Test
    public void test() {
        String strBefore = "Bonjour `Set<Class<? extends Object>>`";
        String strAfter = "<p>Bonjour <code>Set&lt;Class&lt;? extends Object&gt;&gt;</code></p>";

        assertEquals(ZMarkdown.markdownToHtml(strBefore), strAfter);
    }

}

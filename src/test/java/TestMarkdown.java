import org.junit.Before;
import org.junit.Test;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class TestMarkdown {

    PythonInterpreter pyconsole;

    @Before
    public void Setup() {
        pyconsole = new PythonInterpreter();
    }

    @Test
    public void test() {
        String strBefore = "Bonjour `Set<Class<? extends Object>>`";
        String strAfter = "<p>Bonjour <code>Set&lt;Class&lt;? extends Object&gt;&gt;</code></p>";

        pyconsole.exec("from markdown import Markdown");
        pyconsole.exec("from markdown.extensions.zds import ZdsExtension");
        pyconsole.exec("from smileys_definition import smileys");

        pyconsole.set("text", strBefore);
        pyconsole.exec("mk_instance = Markdown(extensions=(ZdsExtension(inline=False, emoticons=smileys, js_support=False, ping_url=None),),safe_mode = 'escape', enable_attributes = False, tab_length = 4, output_format = 'html5', smart_emphasis = True, lazy_ol = True)");
        pyconsole.exec("render = mk_instance.convert(text)");

        PyString render = pyconsole.get("render", PyString.class);
        assertEquals(render.toString(), strAfter);

    }

}

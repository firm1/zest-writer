import com.zds.zw.utils.Lang;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class TestLang {
    @Test
    public void testKeyLang() {
        List<Lang> langs = Lang.getLangAvailable();
        List<Set<String>> keys = new ArrayList<>();
        for(Lang lang:langs) {
            ResourceBundle bundle = ResourceBundle.getBundle("com/zds/zw/locales/ui", lang.getLocale());
            keys.add(bundle.keySet());
        }
        Set<String> firstLang = keys.get(0);
        for(Set<String> key:keys) {
            assertEquals(key.size(), firstLang.size());
            assertArrayEquals(key.toArray(), firstLang.toArray());
        }
    }
}

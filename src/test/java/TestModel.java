

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Map;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.model.Container;
import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.Extract;
import com.zestedesavoir.zestwriter.model.MetaAttribute;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.readability.Readability;

public class TestModel {

    Content content;
    Container part1;
    Container part2;
    Container part3;
    Container chapter11;
    Container chapter12;
    Container chapter13;
    Container chapter14;
    Container chapter15;
    Container chapter16;
    Container chapter17;
    Container chapter21;
    Container chapter31;
    Extract extract111;
    Extract extract112;
    Extract extract113;
    Extract extract211;
    Extract extract212;

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File manifest = new File(getClass().getResource("fixtures").getFile()+File.separator+"le-guide-du-contributeur"+File.separator+"manifest.json");
        content = mapper.readValue(manifest, Content.class);
        content.setRootContent(content, manifest.getParentFile().getAbsolutePath());
    }

    private void loadParts() {
        part1 = (Container) content.getChildren().get(0);
        part2 = (Container) content.getChildren().get(1);
        part3 = (Container) content.getChildren().get(2);
    }

    private void loadChapters() {
        chapter11 = (Container) part1.getChildren().get(0);
        chapter12 = (Container) part1.getChildren().get(1);
        chapter13 = (Container) part1.getChildren().get(2);
        chapter14 = (Container) part1.getChildren().get(3);
        chapter15 = (Container) part1.getChildren().get(4);
        chapter16 = (Container) part1.getChildren().get(5);
        chapter17 = (Container) part1.getChildren().get(6);
        chapter21 = (Container) part2.getChildren().get(0);
        chapter31 = (Container) part3.getChildren().get(0);
        loadExtracts();
    }

    private void loadExtracts() {
        extract111 = (Extract) chapter11.getChildren().get(0);
        extract112 = (Extract) chapter11.getChildren().get(1);
        extract113 = (Extract) chapter11.getChildren().get(2);
        extract211 = (Extract) chapter21.getChildren().get(0);
        extract212 = (Extract) chapter21.getChildren().get(1);
    }

    @Test
    public void testCountContainerAndExtract() {
        assertEquals(content.getChildren().size(), 3);

        loadParts();
        assertEquals(part1.getChildren().size(), 7);
        assertEquals(part2.getChildren().size(), 5);
        assertEquals(part3.getChildren().size(), 3);

        loadChapters();
        assertEquals(chapter11.getChildren().size(), 6);
        assertEquals(chapter12.getChildren().size(), 3);
        assertEquals(chapter13.getChildren().size(), 6);
        assertEquals(chapter14.getChildren().size(), 7);
        assertEquals(chapter15.getChildren().size(), 4);
        assertEquals(chapter16.getChildren().size(), 0);
        assertEquals(chapter17.getChildren().size(), 3);

    }

    @Test
    public void testMoves() {
        loadParts();
        loadChapters();
        assertEquals("Un chapitre ne peut pas être déplacé dans lui même", chapter11.isMoveableIn(chapter11, content), false);
        assertEquals("Un chapitre ne peut pas être déplacé dans un conteneur qui a déjà des extraits", chapter11.isMoveableIn(chapter12, content), false);
        assertEquals("Un chapitre (niveau 3) ne peut pas aller dans un autre chapitre", chapter11.isMoveableIn(chapter12, content), false);
        assertEquals("Un chapitre (niveau 3) ne peut pas aller dans un autre chapitre", chapter11.isMoveableIn(chapter16, content), false);
        assertEquals("Le chapitre 1.1 (niveau 3) est déplaceable dans une partie (niveau2)", chapter11.isMoveableIn(part2, content), true);
        assertEquals("Le chapitre 1.1 est déplaceable dans sa propre partie", chapter11.isMoveableIn(part1, content), true);
        assertEquals("Le chapitre 1.2 est déplaceable après l'introduction de la partie 1", chapter12.isMoveableIn((MetaAttribute) part1.getIntroduction(), content), true);
        assertEquals("Le chapitre 1.2 n'est pas déplaceable après la conclusion de la partie 1", chapter12.isMoveableIn((MetaAttribute) part1.getConclusion(), content), false);
        assertEquals("Le chapitre 1.2 n'est pas déplaceable après l'introduction du chapitre 1.1", chapter12.isMoveableIn((MetaAttribute) chapter11.getIntroduction(), content), false);
        assertEquals("Le chapitre 1.2 n'est pas déplaceable après la conclusion du chapitre 1.1", chapter12.isMoveableIn((MetaAttribute) chapter11.getConclusion(), content), false);
        assertEquals("Un chapitre ne peut pas prendre un autre conteneur", chapter11.canTakeContainer(content), false);
        assertEquals("Un chapitre ne peut pas prendre un autre conteneur", chapter16.canTakeContainer(content), false);
        assertEquals("Une partie peut recevoir un conteneur", part1.canTakeContainer(content), true);
        assertEquals("Un tutoriel peut recevoir un conteneur", content.canTakeContainer(content), true);
        assertEquals("Un extrait n'est pas déplaceable à la racine dans un big tuto", extract111.isMoveableIn(content, content), false);
        assertEquals("Un extrait est déplaceable dans un autre chapitre", extract111.isMoveableIn(chapter12, content), true);
        assertEquals("Un extrait est déplaceable dans le même chapitre", extract111.isMoveableIn(chapter11, content), true);
        assertEquals("Un extrait est déplaceable après un extrait du même chapitre", extract111.isMoveableIn(extract112, content), true);
        assertEquals("Un extrait est déplaceable après un extrait d'un autre chapitre", extract111.isMoveableIn(extract211, content), true);
        assertEquals("Un extrait est déplaceable après une introduction", extract111.isMoveableIn((MetaAttribute)chapter11.getIntroduction(), content), true);
        assertEquals("Un extrait n'est pas déplaceable après une conclusion", extract111.isMoveableIn((MetaAttribute)chapter11.getConclusion(), content), false);
    }

    @Test
    public void testEditable() {
        loadParts();
        loadChapters();
        assertEquals("Un chapitre est éditable", chapter11.isEditable(), true);
        assertEquals("Un extrait est éditable", part2.isEditable(), true);
        assertEquals("Un tutoriel n'est pas editable", content.isEditable(), false);
    }

    @Test
    public void testDeletable() {
        loadParts();
        loadChapters();
        assertEquals("Un chapitre est supprimable", chapter11.canDelete(), true);
        assertEquals("Un extrait est supprimable", part2.canDelete(), true);
        assertEquals("Un tutoriel est supprimable", content.canDelete(), true);
    }

    @Test
    public void testRecept() {
        loadParts();
        loadChapters();
        assertEquals("Un chapitre peut recevoir un extrait", chapter11.canTakeExtract(), true);
        assertEquals("Une partie ne peut pas recevoir d'extrait", part1.canTakeExtract(), false);
    }

    @Test
    public void testExport() {
        loadParts();
        loadChapters();
        String res = content.exportContentToMarkdown(0, 2);
        assertEquals(res == null, false);
    }

    @Test
    public void testGenericTextual() {
        loadParts();
        loadChapters();
        Function<Textual, Integer> countWords = (Textual ch) -> {
            Readability rd = new Readability(ch.readMarkdown());
            return rd.getWords();
        };

        Map<Textual, Integer> result = content.doOnTextual(countWords);

    }
}

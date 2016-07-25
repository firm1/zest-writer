

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

import com.zestedesavoir.zestwriter.model.*;
import com.zestedesavoir.zestwriter.utils.Configuration;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.utils.readability.Readability;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import org.python.bouncycastle.asn1.cms.MetaData;

public class TestModel {

    private final static String TEST_DIR = System.getProperty("java.io.tmpdir");
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
        Configuration config = new Configuration (TEST_DIR);
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

        assertEquals(content.getDepth(), 3);

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
        assertEquals("Une conclusion ne peut pas être déplacée", ((MetaAttribute) chapter11.getConclusion()).isMoveableIn(chapter12, content), false);
        assertEquals("Une introduction ne peut pas être déplacée", ((MetaAttribute) chapter11.getIntroduction()).isMoveableIn(chapter12, content), false);
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

    @Test
    public void testCreateBigTuto() {
        File workspace = new File(new File(TEST_DIR), "zworkspace");
        if(!workspace.exists()) {
            workspace.mkdirs();
        }
        System.out.println("workspace = "+workspace.getAbsolutePath());
        String title = "Tutoriel de test";
        String description = "Description d'un tutoriel de test";
        String part_1_title = "Premiere partie";
        String part_2_title = "Deuxieme partie";
        String chapter_11_title = "Premier chapitre";
        String chapter_12_title = "Deuxieme chapitre";
        String extract_111_title = "Premier Extrait";
        String extract_21_title = "Autre Extrait";

        Content bigtuto = new Content("container", ZdsHttp.toSlug(title), title, "introduction.md", "conclusion.md", new ArrayList<>(), 2, "CC-BY", description, "TUTORIAL");
        assertEquals(bigtuto.getSlug(), "tutoriel-de-test");
        Container part_1 = new Container("container", ZdsHttp.toSlug(part_1_title), part_1_title, ZdsHttp.toSlug(part_1_title)+"/introduction.md", ZdsHttp.toSlug(part_1_title)+"/conclusion.md", new ArrayList<>());
        bigtuto.getChildren().add(part_1);
        assertEquals(part_1.getSlug(), "premiere-partie");
        Container part_2 = new Container("container", ZdsHttp.toSlug(part_2_title), part_2_title, ZdsHttp.toSlug(part_2_title)+"/introduction.md", ZdsHttp.toSlug(part_2_title)+"/conclusion.md", new ArrayList<>());
        bigtuto.getChildren().add(part_2);
        assertEquals(part_2.getSlug(), "deuxieme-partie");

        Container chapter_11 = new Container("container", ZdsHttp.toSlug(chapter_11_title), chapter_11_title, ZdsHttp.toSlug(part_1_title)+"/"+ZdsHttp.toSlug(chapter_11_title)+"/introduction.md", ZdsHttp.toSlug(part_1_title)+"/"+ZdsHttp.toSlug(chapter_11_title)+"/conclusion.md", new ArrayList<>());
        part_1.getChildren().add(chapter_11);
        assertEquals(chapter_11.getSlug(), "premier-chapitre");
        Container chapter_12 = new Container("container", ZdsHttp.toSlug(chapter_12_title), chapter_12_title, ZdsHttp.toSlug(part_1_title)+"/"+ZdsHttp.toSlug(chapter_12_title)+"/introduction.md", ZdsHttp.toSlug(part_1_title)+"/"+ZdsHttp.toSlug(chapter_12_title)+"/conclusion.md", new ArrayList<>());
        part_1.getChildren().add(chapter_12);
        assertEquals(chapter_12.getSlug(), "deuxieme-chapitre");

        Extract extract111 = new Extract("extract", ZdsHttp.toSlug(extract_111_title), extract_111_title, ZdsHttp.toSlug(part_1_title)+"/"+ZdsHttp.toSlug(chapter_11_title)+"/"+ZdsHttp.toSlug(extract_111_title)+".md");
        chapter_11.getChildren().add(extract111);
        assertEquals(extract111.getSlug(), "premier-extrait");
        Extract extract_21 = new Extract("extract", ZdsHttp.toSlug(extract_21_title), extract_21_title, ZdsHttp.toSlug(part_1_title)+"/"+ZdsHttp.toSlug(extract_21_title)+".md");
        part_2.getChildren().add(extract_21);
        assertEquals(extract_21.getSlug(), "autre-extrait");

        bigtuto.setRootContent(bigtuto, new File(workspace, bigtuto.getSlug()).getAbsolutePath());
        // create file
        try {
            (new File(bigtuto.getFilePath())).mkdir();
            (new File(bigtuto.getIntroduction().getFilePath())).createNewFile();
            (new File(bigtuto.getConclusion().getFilePath())).createNewFile();
            (new File(bigtuto.getFilePath(), "manifest.json")).createNewFile();
            (new File(part_1.getFilePath())).mkdir();
            (new File(part_1.getIntroduction().getFilePath())).createNewFile();
            (new File(part_1.getConclusion().getFilePath())).createNewFile();
            (new File(part_2.getFilePath())).mkdir();
            (new File(part_2.getIntroduction().getFilePath())).createNewFile();
            (new File(part_2.getConclusion().getFilePath())).createNewFile();
            (new File(chapter_11.getFilePath())).mkdir();
            (new File(chapter_11.getIntroduction().getFilePath())).createNewFile();
            (new File(chapter_11.getConclusion().getFilePath())).createNewFile();
            (new File(chapter_12.getFilePath())).mkdir();
            (new File(chapter_12.getIntroduction().getFilePath())).createNewFile();
            (new File(chapter_12.getConclusion().getFilePath())).createNewFile();
            (new File(extract111.getFilePath())).createNewFile();
            (new File(extract_21.getFilePath())).createNewFile();

            bigtuto.getIntroduction().setMarkdown("Introduction du tutoriel");
            bigtuto.getIntroduction().save();
            bigtuto.getIntroduction().loadMarkdown();
            assertEquals(bigtuto.getIntroduction().getMarkdown().trim(), "Introduction du tutoriel");
            extract_21.setMarkdown("My new content");
            extract_21.save();
            extract_21.loadMarkdown();
            assertEquals(extract_21.getMarkdown().trim(), "My new content");

            // rename content
            bigtuto.renameTitle("Nouveau Contenu");
            assertEquals(bigtuto.getSlug(), "nouveau-contenu");
            assertEquals(bigtuto.getTitle(), "Nouveau Contenu");
            assertEquals(bigtuto.getFilePath(), workspace.getAbsolutePath()+File.separator+"nouveau-contenu");
            assertTrue((new File(bigtuto.getFilePath())).exists());

            part_2.delete();
            assertEquals((new File(part_2.getFilePath())).exists(), false);
            extract111.delete();
            assertEquals((new File(extract111.getFilePath())).exists(), false);
            chapter_12.delete();
            assertEquals((new File(chapter_12.getFilePath())).exists(), false);
            bigtuto.delete();
            assertEquals((new File(bigtuto.getFilePath())).exists(), false);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            workspace.delete();
        }
    }

    @Test
    public void testMetaDataContent() {
        MetadataContent c1 = new MetadataContent("1", "contenu-1", "TUTORIAL");
        MetadataContent c2 = new MetadataContent("2", "contenu-1", "TUTORIAL");
        MetadataContent c3 = new MetadataContent("1", "contenu-2", "TUTORIAL");
        MetadataContent c4 = new MetadataContent("1", "contenu-1", "ARTICLE");
        MetadataContent c5 = new MetadataContent(null, "", null);
        MetadataContent c5b = new MetadataContent(null, "contenu-1", null);
        MetadataContent c6 = new MetadataContent("1", "Contenu 1", null);
        MetadataContent c6b = new MetadataContent("1", "contenu-1", null);
        MetadataContent c7 = new MetadataContent(null, "contenu-1", "TUTORIAL");
        MetadataContent c7b = new MetadataContent(null, "contenu-1", "ARTICLE");
        MetadataContent c8 = new MetadataContent("1", "contenu-1", null);
        MetadataContent c9 = new MetadataContent("1", "contenu-1", "TUTORIAL");

        assertFalse(c1.equals(null));
        assertFalse(c2.equals(c1));
        assertFalse(c1.equals(c2));
        assertFalse(c3.equals(c1));
        assertFalse(c1.equals(c3));
        assertFalse(c4.equals(c1));
        assertFalse(c1.equals(c4));
        assertFalse(c5.equals(c1));
        assertFalse(c1.equals(c5));
        assertFalse(c5b.equals(c1));
        assertFalse(c1.equals(c5b));
        assertFalse(c6.equals(c1));
        assertFalse(c1.equals(c6));
        assertFalse(c6b.equals(c1));
        assertFalse(c1.equals(c6b));
        assertFalse(c7.equals(c1));
        assertFalse(c1.equals(c7));
        assertFalse(c7b.equals(c1));
        assertFalse(c1.equals(c7b));
        assertFalse(c8.equals(c1));
        assertFalse(c1.equals(c8));
        assertTrue(c9.equals(c1));
        assertTrue(c1.equals(c9));
        assertFalse(new String("FAILED").equals(c1));
        assertTrue(c1.equals(c1));
        assertTrue(c2.equals(c2));
        assertTrue(c3.equals(c3));
        assertTrue(c4.equals(c4));
        assertTrue(c5.equals(c5));
        assertTrue(c5b.equals(c5b));
        assertTrue(c6.equals(c6));
        assertTrue(c6b.equals(c6b));
        assertTrue(c7.equals(c7));
        assertTrue(c7b.equals(c7b));
        assertTrue(c8.equals(c8));
        assertTrue(c9.equals(c9));

    }
}

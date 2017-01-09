import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.model.*;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.GithubHttp;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.utils.readability.Readability;
import org.junit.Before;
import org.junit.Test;
import org.apache.http.client.HttpResponseException;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import static org.junit.Assert.*;

public class TestModel {

    private final static String TEST_DIR = System.getProperty("java.io.tmpdir");
    private Content content;
    private Container part1;
    private Container part2;
    private Container part3;
    private Container chapter11;
    private Container chapter12;
    private Container chapter13;
    private Container chapter14;
    private Container chapter15;
    private Container chapter16;
    private Container chapter17;
    private Container chapter21;
    private Container chapter31;
    private Extract extract111;
    private Extract extract112;
    private Extract extract113;
    private Extract extract211;
    private Extract extract212;

    @Before
    public void setUp() throws Exception {
        MainApp.setLogger(LoggerFactory.getLogger(MainApp.class));
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

    private void checkManifestAntislash(Content c) {
        File file = new File(c.getFilePath(), "manifest.json");
        StringBuilder bfString = new StringBuilder();
        try(Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.name())) {
            while (scanner.hasNextLine()) {
                bfString.append(scanner.nextLine());
                bfString.append("\n");
            }
            assertFalse(bfString.toString().contains("\\"));
        } catch (IOException e) {
            MainApp.getLogger().error(e.getMessage(), e);
        }
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
        checkManifestAntislash(content);
        assertEquals(part1.getChildren().size(), 7);
        assertEquals(part2.getChildren().size(), 5);
        assertEquals(part3.getChildren().size(), 3);

        loadChapters();
        checkManifestAntislash(content);
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
        assertEquals("Un chapitre ne peut pas être déplacé dans lui même", chapter11.isMovableIn(chapter11, content), false);
        assertEquals("Un chapitre ne peut pas être déplacé dans un conteneur qui a déjà des extraits", chapter11.isMovableIn(chapter12, content), false);
        assertEquals("Un chapitre (niveau 3) ne peut pas aller dans un autre chapitre", chapter11.isMovableIn(chapter12, content), false);
        assertEquals("Un chapitre (niveau 3) ne peut pas aller dans un autre chapitre", chapter11.isMovableIn(chapter16, content), false);
        assertEquals("Le chapitre 1.1 (niveau 3) est déplaceable dans une partie (niveau2)", chapter11.isMovableIn(part2, content), true);
        assertEquals("Le chapitre 1.1 est déplaceable dans sa propre partie", chapter11.isMovableIn(part1, content), true);
        assertEquals("Le chapitre 1.2 est déplaceable après l'introduction de la partie 1", chapter12.isMovableIn((MetaAttribute) part1.getIntroduction(), content), true);
        assertEquals("Le chapitre 1.2 n'est pas déplaceable après la conclusion de la partie 1", chapter12.isMovableIn((MetaAttribute) part1.getConclusion(), content), false);
        assertEquals("Le chapitre 1.2 n'est pas déplaceable après l'introduction du chapitre 1.1", chapter12.isMovableIn((MetaAttribute) chapter11.getIntroduction(), content), false);
        assertEquals("Le chapitre 1.2 n'est pas déplaceable après la conclusion du chapitre 1.1", chapter12.isMovableIn((MetaAttribute) chapter11.getConclusion(), content), false);
        assertEquals("Un chapitre ne peut pas prendre un autre conteneur", chapter11.canTakeContainer(content), false);
        assertEquals("Un chapitre ne peut pas prendre un autre conteneur", chapter16.canTakeContainer(content), false);
        assertEquals("Une partie peut recevoir un conteneur", part1.canTakeContainer(content), true);
        assertEquals("Un tutoriel peut recevoir un conteneur", content.canTakeContainer(content), true);
        assertEquals("Un extrait n'est pas déplaceable à la racine dans un big tuto", extract111.isMovableIn(content, content), false);
        assertEquals("Un extrait est déplaceable dans un autre chapitre", extract111.isMovableIn(chapter12, content), true);
        assertEquals("Un extrait est déplaceable dans le même chapitre", extract111.isMovableIn(chapter11, content), true);
        assertEquals("Un extrait est déplaceable après un extrait du même chapitre", extract111.isMovableIn(extract112, content), true);
        assertEquals("Un extrait est déplaceable après un extrait d'un autre chapitre", extract111.isMovableIn(extract211, content), true);
        assertEquals("Un extrait est déplaceable après une introduction", extract111.isMovableIn((MetaAttribute)chapter11.getIntroduction(), content), true);
        assertEquals("Un extrait n'est pas déplaceable après une conclusion", extract111.isMovableIn((MetaAttribute)chapter11.getConclusion(), content), false);
        assertEquals("Une conclusion ne peut pas être déplacée", ((MetaAttribute) chapter11.getConclusion()).isMovableIn(chapter12, content), false);
        assertEquals("Une introduction ne peut pas être déplacée", ((MetaAttribute) chapter11.getIntroduction()).isMovableIn(chapter12, content), false);
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
        checkManifestAntislash(content);

    }

    @Test
    public void testCreateBigTuto() {
        File workspace = new File(new File(TEST_DIR), "zworkspace");
        if(!workspace.exists()) {
            workspace.mkdirs();
        }
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
        assertFalse(extract111.canTakeExtract());
        Extract extract_21 = new Extract("extract", ZdsHttp.toSlug(extract_21_title), extract_21_title, ZdsHttp.toSlug(part_1_title)+"/"+ZdsHttp.toSlug(extract_21_title)+".md");

        part_2.getChildren().add(extract_21);
        assertEquals(extract_21.getSlug(), "autre-extrait");

        bigtuto.setRootContent(bigtuto, new File(workspace, bigtuto.getSlug()).getAbsolutePath());
        assertFalse(bigtuto.canTakeExtract());

        assertTrue(new File(part_1.getIntroduction().getFilePath()).exists());
        assertFalse(((ContentNode) part_1.getIntroduction()).canDelete());
        ((ContentNode) part_1.getIntroduction()).delete();
        assertTrue(new File(part_1.getIntroduction().getFilePath()).exists());

        // create file
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
        checkManifestAntislash(bigtuto);
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
        assertFalse("FAILED".equals(c1));
    }

    @Test
    public void testLicense() {
        License l1 = new License("CC-BY", "CC-BY");
        License l2 = new License("CC-BY-SA", "CC-BY-SA");
        License l3 = new License("CC-BY", "CC-BY-ND");
        assertTrue(l1.equals(l3));
        assertTrue(l3.equals(l1));
        assertFalse(l1.equals(l2));
        assertFalse(l2.equals(l1));
        l3.setCode("CC-BY-ND");
        assertFalse(l3.equals(l1));
        assertFalse(l1.equals(l3));
        l3.setLabel("CC-BY");
        assertFalse(l3.equals(l1));
        assertFalse(l1.equals(l3));
        assertEquals(l3.toString(), "CC-BY");
        assertFalse(l1.equals("CC-BY"));
    }

    @Test
    public void testTypeContent() {
        TypeContent tc1 = new TypeContent("TUTORIAL", "Tutoriel");
        TypeContent tc2 = new TypeContent("ARTICLE", "Article");
        TypeContent tc3 = new TypeContent("TUTORIAL", "Tribune");
        assertTrue(tc1.equals(tc3));
        assertTrue(tc3.equals(tc1));
        assertFalse(tc1.equals(tc2));
        assertFalse(tc2.equals(tc1));
        tc3.setCode("TL");
        assertFalse(tc3.equals(tc1));
        assertFalse(tc1.equals(tc3));
        tc3.setLabel("Tutoriel");
        assertFalse(tc3.equals(tc1));
        assertFalse(tc1.equals(tc3));
        assertEquals(tc3.toString(), "Tutoriel");
        assertFalse(tc1.equals("TUTORIAL"));
    }

    @Test
    public void testImport() throws IOException {
        File workspace = new File(new File(TEST_DIR), "zworkspace");
        if(!workspace.exists()) {
            workspace.mkdirs();
        }
        String filePath = GithubHttp.getGithubZipball ("steeve", "france.code-civil", workspace.getAbsolutePath());
        File folder = GithubHttp.unzipOnlineContent (filePath, workspace.getAbsolutePath());
        File off = new File(workspace, "france.code-civil");
        assertTrue(off.exists());
        try {
            Content loadContent = GithubHttp.loadManifest(folder.getAbsolutePath(), "steeve", "france.code-civil");
            checkManifestAntislash(loadContent);
            assertNotNull(loadContent);
            assertNotNull(loadContent.getTitle());
            assertNotNull(loadContent.getFilePath());
            assertNotNull(loadContent.getType());
            assertNotNull(loadContent.getLicence());
            assertTrue(loadContent.getChildren().size() > 0);
        } catch(HttpResponseException hhtpe) {
            
        }
    }
}

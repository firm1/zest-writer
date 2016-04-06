package com.zestedesavoir.zestwriter.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestedesavoir.zestwriter.model.Container;
import com.zestedesavoir.zestwriter.model.Content;

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

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File manifest = new File("fixtures/le-guide-du-contributeur/manifest.json");
        content = mapper.readValue(manifest, Content.class);
        content.setBasePath(manifest.getParentFile().getAbsolutePath());
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
        assertEquals("Un chapitre (niveau 3) ne peut pas aller dans un autre chapitre", chapter11.isMoveableIn(chapter12, content), false);
        assertEquals("Un chapitre (niveau 3) ne peut pas aller dans un autre chapitre", chapter11.isMoveableIn(chapter16, content), false);
        assertEquals("Le chapitre 1.1 (niveau 3) est déplaceable dans une partie (niveau2)", chapter11.isMoveableIn(part2, content), true);
        assertEquals("Le chapitre 1.1 est déplaceable dans sa propre partie", chapter11.isMoveableIn(part1, content), true);
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

}

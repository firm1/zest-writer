Certaines fois, le contenu a été rédigé ou modifié ailleurs que sur ZdS. Il faut alors importer la version récente.

[[attention]]
| Si vous souhaitez importer un contenu dont vous n’êtes pas l’auteur originel, nous vous conseillons de lire cette [section](https://zestedesavoir.com/contenus/705/le-guide-du-contributeur/828_contribuer-au-contenu/5467_generalites/#6-les-cas-particuliers) si ce n’est pas déjà fait.

L’import est le processus qui permet d’actualiser un contenu du site en cours de rédaction avec une archive externe. Cette archive aura le même format que celle disponible par la fonction d’export.

Il s’agit d’une archive compressée au format `zip`, contenant un fichier `manifest.json` et des fichiers représentant l’ensemble des chapitres du contenu.

# Construire une archive

Le fichier `manifest.json` contient les métadonnées du contenu et de l’ensemble de ses chapitres et sections.

Il se présente comme suit.

```json
{
    "title": "TITRE DE MON CONTENU",
    "description": "MON SUPER CONTENU",
    "slug": "titre-de-mon-contenu",
    "type": "ARTICLE/TUTORIAL",
    "license": "CC BY-SA",
    "object": "container",
    "version": 2,
    "children": [
        ...
    ]
}
```

Le nœud racine du document présente le titre du contenu, sa description, son *slug* (titre encodé de façon unique et pouvant être utilisé dans une URL), sa licence (`license`) et son type (`"ARTICLE"` ou `"TUTORIAL"` suivant qu’il s’agit d’un article ou d’un tutoriel).

Ce nœud est aussi un objet conteneur (`container`), c’est à dire qu’il contient des nœuds enfants (`children`).
Un nœud se présente toujours sous la forme d’un *hash* contenant au moins les clefs `"object"` (type de nœud), `"title"` (titre du document/partie/chapitre/section) et `"slug"` (titre encodé).

Il existe deux types d’objets, les conteneurs et les extraits (`extract`). Un extrait sera agrémenté d’une clef `"text"` référençant le fichier dans l’archive qui contient le texte de l’extrait.
Les enfants d’un nœud conteneur peuvent alors être eux-mêmes des conteneurs ou des extraits. Il ne peut y avoir plus de trois niveaux de nœuds conteneurs (racine + parties + chapitres, les sections étant forcément des extraits) pour un tutoriel, et un seul niveau pour les articles.

Les nœuds conteneurs possèdent aussi des clefs `"introduction"` et `"conclusion"` facultatives, qui, à la manière de `"text"` pour les extraits, référencent les fichiers contenant le texte de l’introduction et de la conclusion du contenu, de la partie ou du chapitre.

La clef `"version"` du nœud racine permet enfin d’inscrire la version du format de l’archive. Il s’agit actuellement de la version 2, mais le format pourrait évoluer, et une nouvelle version serait alors disponible. L’inscription du numéro de version dans le fichier `manifest.json` permet de rester compatible avec les anciennes structures.

Voici à quoi pourrait ressembler le fichier `manifest.json` d’un article :

```json
{
    "title": "Toto à la plage",
    "description": "L’histoire délirante de Toto à la plage",
    "slug": "toto-a-la-plage",
    "type": "ARTICLE",
    "license": "CC BY-SA",
    "object": "container",
    "version": 2,
    "introduction": "introduction.md",
    "conclusion": "conclusion.md",
    "children": [
        {
            "title": "Le voyage",
            "slug": "le-voyage",
            "text": "voyage.md",
            "object": "extract"
        },
        {
            "title": "La plage",
            "slug": "la-plage",
            "text": "plage.md",
            "object": "extract"
        }
    ]
}
```

Ou celui d’un tutoriel comprenant parties et sections (sans chapitres) :

```json
{
    "title": "Comment aller à la plage",
    "description": "Un tutoriel pour apprendre à faire comme Toto",
    "slug": "comment-aller-a-la-plage",
    "type": "TUTORIAL",
    "license": "CC BY-SA",
    "object": "container",
    "version": 2,
    "introduction": "introduction.md",
    "conclusion": "conclusion.md",
    "children": [
        {
            "title": "Les préparatifs",
            "slug": "les-preparatifs",
            "object": "container",
            "introduction": "preparatifs/introduction.md",
            "conclusion": "preparatifs/conclusion.md",
            "children": [
                {
                    "title": "Choix de la plage",
                    "slug": "choix-de-la-plage",
                    "text": "preparatifs/choix-plage.md",
                    "object": "extract"
                },
                {
                    "title": "Maillot de bain",
                    "slug": "maillot-de-bain",
                    "text": "preparatifs/maillot.md",
                    "object": "extract"
                }
            ]
        },
        {
            "title": "Le voyage",
            "slug": "le-voyage",
            "object": "container",
            "introduction": "voyage/introduction.md",
            "conclusion": "voyage/conclusion.md",
            "children": [
                {
                    "title": "Autoroute et péage",
                    "slug": "autoroute-et-peage",
                    "text": "voyage/autoroute.md",
                    "object": "extract"
                }
            ]
        },
        {
            "title": "La mer",
            "slug": "la-mer",
            "object": "container",
            "introduction": "mer/introduction.md",
            "conclusion": "mer/conclusion.md",
            "children": [
                {
                    "title": "Baignade surveillée",
                    "slug": "baignade-surveillee",
                    "text": "mer/baignade-surveillee.md",
                    "object": "extract"
                },
                {
                    "title": "Marées",
                    "slug": "marees",
                    "text": "mer/marees.md",
                    "object": "extract"
                }
            ]
        }
    ]
}
```

Fichier contenu dans une archive de la sorte :

```
.
|-- manifest.json
|-- introduction.md
|-- conclusion.md
|-- preparatifs
|   |-- introduction.md
|   |-- conclusion.md
|   |-- choix-plage.md
|   `-- maillot.md
|-- voyage
|   |-- introduction.md
|   |-- conclusion.md
|   `-- autoroute.md
`-- mer
    |-- introduction.md
    |-- conclusion.md
    |-- baignade-surveillee.md
    `-- marees.md
```

Tous les fichiers référencés par les clefs `"introduction"`, `"conclusion"` et `"text"` devant bien sûr être présents dans l’archive aux emplacements indiqués.
Comme sur le site, ces fichiers devront être rédigés au format *zMarkdown*.

# Importer une nouvelle version avec une archive

Une fois votre archive prête, il ne vous reste plus qu’à l’importer pour actualiser votre contenu.

Il vous suffit pour cela de vous diriger vers la page de rédaction, et de cliquer sur le lien « Importer une nouvelle version » dans la colonne de gauche.

![Lien vers la page d’import d’une nouvelle version](/media/galleries/1121/e671a26b-e51d-4d6e-be6e-7f44d268be60.png)
Figure: Lien vers la page d’import d’une nouvelle version.

Ce lien vous amène à la page d’import proprement dite. Cette page comporte un formulaire assez simple, permettant de spécifier dans un premier champs l’archive à importer, et l’archive d’images dans un second champ (nous nous occuperons de cette archive par la suite).
Un dernier champ permet enfin d’inscrire un message correspondant à la révision du document.

![Formulaire d’import de nouvelle version](/media/galleries/1121/ec201a03-199a-4354-9f37-6795dd3504b1.png)
Figure: Formulaire d’import de nouvelle version.

Il ne reste alors plus qu’à cliquer sur le bouton « Importer l’archive », pour voir l’archive importée et extraite.

[[attention]]
| L’article/tutoriel sera remplacé dans sa totalité par le contenu de l’archive.
| Les fichiers non référencés dans le nouveau `manifest.json` seront perdus.

Une fois le contenu importé, il peut être judicieux d’effectuer une revue afin de s’assurer qu’il n’y a pas eu de problème lors de l’importation et de soigner la mise en page.

# Archives d’images

Il est aussi possible d’importer directement un contenu avec ses images.
Pour cela, une autre archive au format *zip* doit être construite, qui contiendra l’ensemble des images utilisées par cette révision du contenu.

L’archive du contenu importée simultanément à l’archive d’images pourra alors utiliser les images qui y sont contenus.

En effet, les balises *zMarkdown* d’images (`![légende](chemin)`) permettent de référencer une image contenue dans une archive, *via* le chemin `archive:chemin/de/limage/dans/larchive.png`.

Une fois les deux archives importées, les images sont fusionnées à la galerie actuelle, et les chemins du contenu vers l’archive sont remplacés par les chemins réels des images. L’archive d’images et sa structure n’ont donc d’existence que lors de la phase d’import, et disparaissent ensuite.

![Importer une archive d’images](/media/galleries/1121/886b2d52-d26c-4e8d-a214-d975fa4e9e13.png)
Figure: Importer une archive d’images.


[[attention]]
| Evitez d’importer une archive d’images si vous travaillez en local (comprendre : je rédige, j’importe sur ZdS, je mets en bêta, je modifie la version sur mon ordinateur, je réimporte et les images sont réimportées et dupliquées *même si non modifiées*). Cela peut très vite alourdir votre disque dur.

# Créer un contenu par importation

De manière similaire, il n’est pas nécessaire de posséder déjà un contenu sur le site pour importer une archive.
Le contenu peut directement être créé par importation.

Il faudra pour cela utiliser le lien présent sur la page d’accueil de vos contenus, tout en précisant la catégorie et les tags du contenu.

![Lien vers page d’import d’un nouveau contenu](/media/galleries/1121/ecb463cb-b481-4ee3-a47a-ed5fe1d0600e.png)
Figure: Lien vers la page d’import d’un nouveau contenu.

![Formulaire d’import de nouveau contenu](/media/galleries/1121/198358e1-75be-41b6-8009-0c16003ede14.png)
Figure: Formulaire d’import d’un nouveau contenu.
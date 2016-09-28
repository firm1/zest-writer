# Zest Writer

Un éditeur **hors-ligne** de contenus au format zMarkdown.

![](src/logo/logo-128.png)

Linux | Windows
---|---
[![Build Status](https://travis-ci.org/firm1/zest-writer.svg?branch=master)](https://travis-ci.org/firm1/zest-writer) | [![Build status](https://ci.appveyor.com/api/projects/status/n3aa5h519uxvjufq/branch/master?svg=true)](https://ci.appveyor.com/project/firm1/zest-writer/branch/master)

[![Coverage Status](https://coveralls.io/repos/github/firm1/zest-writer/badge.svg?branch=master)](https://coveralls.io/github/firm1/zest-writer?branch=master)

[![Dependency Status](https://www.versioneye.com/user/projects/5719ed6bfcd19a0039f17b07/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5719ed6bfcd19a0039f17b07)

[![Statis](https://www.openhub.net/p/zest-writer/widgets/project_thin_badge.gif)](https://www.openhub.net/p/zest-writer?ref=sample)

ZestWriter est un éditeur d'articles et de tutoriels en mode hors-ligne (sans avoir besoin d'une connexion internet). Il supporte la syntaxe zMarkdown (le Markdown avec les petits ajouts utilisés sur le site [Zeste de Savoir](https://zestedesavoir.com/)).

Pour la procédure d'installation ou des informations concernant le développement de ZestWriter, merci de consulter [la documentation officielle sur ReadTheDocs](http://zest-writer.readthedocs.io).

## Fonctionnalités

### Interface

**Général**

- différents thèmes proposés (clair, sombre, etc.) ;

![Capture d'écran du menu de ZestWrite avec thème sombre](doc/images/zw_dark_menu.png)

![Capture d'écran de la fenêtre de rédaction de ZestWrite avec thème clair](doc/images/zw_light_redaction.png)

**Zone d'édition**

- boutons d'aide à la rédaction Markdown (gras, italique, blocs spéciaux, tableaux, listes, etc.).

![Barre de boutons](doc/images/buttons_bar.png)

- possibilité de modifier la taille du texte et sa police ;


**Zone de rendu**

- prévisualisation instantanée lors de la rédaction ;
- décrochage de la zone de rendu dans une fenêtre externe afin de pouvoir la placer sur un écran séparé ;

![Zone de rendu dans une fenêtre externe](doc/images/render_window.png)

**Arbre de navigation des contenus (tutoriels ou articles)**

- navigation à travers les différents conteneurs ;
- déplacement des conteneurs et des extraits par *drag'n drop* ;
- édition des titres des extraits et conteneurs.

![Arbre de navigation](doc/images/tree_view.png)

### Révision

- proposition de corrections orthographiques, grammaticales et typographiques du contenu ;

![Prosition de correction](doc/images/grammar_hint.png)

- indices de lisibilité des extraits pour améliorer leur lisibilité (*Flesch* et *Gunning*) ;

![Indice de Flesch](doc/images/flesch_indice.png)

- compteur de mots et de caractères affichés en temps réel ;
- graphiques de répartition du contenu dans les différents conteneurs.

![Graphique de répartition du contenu](doc/images/chart.png)

### Synchronisation avec le site de Zeste de Savoir

![Logo de Zeste de Savoir](doc/images/logo_zds.png)

- possibilité de récupérer ses contenus en rédaction sur le site ;
- possibilité d'envoyer sur le site les modifications effectuées sur Zeste Writer.

### Multiplateforme

- L'application fonctionne sur Windows, Linux et OS X.

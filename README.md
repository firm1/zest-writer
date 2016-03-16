# Zest Writer

Editeur **Hors-ligne** de contenus au format zMarkdown.

![](src/logo/logo-128.png)

Linux | Windows
---|---
[![Build Status](https://travis-ci.org/firm1/zest-writer.svg?branch=master)](https://travis-ci.org/firm1/zest-writer) | [![Build status](https://ci.appveyor.com/api/projects/status/n3aa5h519uxvjufq/branch/master?svg=true)](https://ci.appveyor.com/project/firm1/zest-writer/branch/master)

## Installation (version stable)

- [Tous les systèmes (jar executable)](doc/install-jar.md)
- [Debian/Ubuntu](doc/install-deb.md)
- [Fedora/CentOs/etc.](doc/install-rpm.md)

## Screenshoot

![](http://zestedesavoir.com/media/galleries/2958/0796bf63-8ff3-41a1-9550-2c9ff31089b2.png)

## Fonctionnalités supportées

### La rédaction

- Rédaction d'articles et de tutoriels en mode hors ligne, c'est à dire sans avoir besoin d'une connexion internet
- Support de la syntaxe zMarkdown (le Markdown avec les petits ajouts utilisés sur le site Zeste de Savoir)
- La prévisualisation instantanée lors de la rédaction
- Bouton d'aide à la rédaction Markdown (gras, italique, blocs customs, tableaux, listes, etc.)

### La navigation

- La navigation dans le sommaire d'un contenu sous forme d'arbre
- Le déplacement des conteneurs et des extraits grâce au *drag'n drop*
- L'édition des titres des extraits et conteneurs depuis l'arbre de navigation

### Conseil de rédaction

- Proposition de correction orthotypographique du contenu
- Indice de lisibilité des extraits (afin d'améliorer la lisibilité de son texte)

### Synchronisation avec le site de ZdS

- Possibilité de se connecter au site depuis l'éditeur
- Possibilité de télécharger en local ses contenus en rédaction sur ZdS
- Possiilité d'envoyer sur le site les modifications effectuées sur le contenus en local

### Multiplateforme

- L'application fonctionne sur Windows (32 et 64 bits), Linux et OS X quelque soit la version de la JVM sur votre machine, car l'application embarque sa propre JVM.

## Le developpement

### Outils

- L'application est developpée en Java **8** (vous devez donc avoir cette version pour pouvoir developper)
- L'interface utilise JavaFX.
- Le parseur mardkown utilisé est le même que celui de ZdS (l'application émule un interpreteur python grace à Jython)

### *build* de l'application

Pour *builder* l'application, vous devez avoir installé gradle et lancer la commande suivante (depuis le dossier du projet):

```sh
gradle build
```

A la fin, selon votre système d'exploitation, vous retrouver un `.exe`, un `.deb`, un `.rpm` ou un `.dmg` dans le dossier `build/distributions`.

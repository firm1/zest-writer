# Zest Writer

Éditeur **Hors-ligne** de contenus au format zMarkdown.

![](src/logo/logo-128.png)

Linux | Windows
---|---
[![Build Status](https://travis-ci.org/firm1/zest-writer.svg?branch=master)](https://travis-ci.org/firm1/zest-writer) | [![Build status](https://ci.appveyor.com/api/projects/status/n3aa5h519uxvjufq/branch/master?svg=true)](https://ci.appveyor.com/project/firm1/zest-writer/branch/master)

[![Coverage Status](https://coveralls.io/repos/github/firm1/zest-writer/badge.svg?branch=master)](https://coveralls.io/github/firm1/zest-writer?branch=master)

[![Dependency Status](https://www.versioneye.com/user/projects/5719ed6bfcd19a0039f17b07/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5719ed6bfcd19a0039f17b07)

## Installation (version stable)

### Tous les systèmes d'exploitation (Jar executable)

Pour cela vous avez besoin de **Java 8** installé sur votre système.

1. Téléchargez la dernière version de Zest Writer via [ce lien](https://bintray.com/firm1/maven/zest-writer/_latestVersion#files).

2. Ouvrez un terminal et lancez le jar via (x.y.z étant le numéro de version) : `java -jar zest-writer-all-x.y.z.jar`.

### Windows

Les instructions se trouvent sur [ce post](https://zestedesavoir.com/forums/sujet/5354/zest-writer-un-editeur-hors-ligne-pour-vos-contenus-zds/#p98286).

### Linux

#### Ubuntu, Debian, etc.

1. Ouvrez un terminal et lancez la commande : `echo "deb https://dl.bintray.com/firm1/deb wheezy main" | sudo tee -a /etc/apt/sources.list.d/zestwriter.list`.
2. mettez à jour vos dépôts via la commande : `sudo apt-get update`.
3. installez Zest Writer via la commande : `sudo apt-get install zestwriter`.

#### Fedora, ArchLinux, CentOs, etc.

1. Créez le fichier `/etc/yum.repos.d/zestwriter.repo` et copiez le contenu suivant à l'intérieur :

  ```
  [zestwriter]
  name=zestwriter 
  baseurl=http://dl.bintray.com/firm1/rpm
  gpgcheck=0
  enabled=1 
  ```

2. Installez Zest Writer via la commande : `yum install zestwriter`.

### Os X

1. Téléchargez la dernière version de Zest Writer via [ce lien](https://bintray.com/firm1/dmg/zest-writer/_latestVersion#files).
2. Installez le dmg sur votre système d'exploitation.

## Screenshot

![](http://zestedesavoir.com/media/galleries/2958/0796bf63-8ff3-41a1-9550-2c9ff31089b2.png)

![](http://zestedesavoir.com/media/galleries/2958/90d51a40-f5ba-431f-8a55-c4c556048535.png)

## Fonctionnalités supportées

### La rédaction

- Rédaction d'articles et de tutoriels en mode hors ligne, c'est à dire sans avoir besoin d'une connexion internet ;
- support de la syntaxe zMarkdown (le Markdown avec les petits ajouts utilisés sur le site Zeste de Savoir) ;
- la prévisualisation instantanée lors de la rédaction ;
- bouton d'aide à la rédaction Markdown (gras, italique, blocs customs, tableaux, listes, etc.).

### La navigation

- La navigation dans le sommaire d'un contenu sous forme d'arbre ;
- le déplacement des conteneurs et des extraits grâce au *drag'n drop* ;
- l'édition des titres des extraits et conteneurs depuis l'arbre de navigation.

### Conseils de rédaction

- Proposition de correction orthotypographique du contenu ;
- indice de lisibilité des extraits (afin d'améliorer la lisibilité de son texte).

### Synchronisation avec le site de ZdS

- Possibilité de se connecter au site depuis l'éditeur ;
- possibilité de télécharger en local ses contenus en rédaction sur ZdS ;
- possiilité d'envoyer sur le site les modifications effectuées sur le contenu en local.

### Multiplateforme

- L'application fonctionne sur Windows (32 et 64 bits), Linux et OS X quel que soit la version de la JVM sur votre machine, car l'application embarque sa propre JVM.

## Le developpement

### Outils

- L'application est developpée en Java **8** (vous devez donc avoir cette version pour pouvoir developper) ;
- l'interface utilise JavaFX ;
- le parseur markdown utilisé est le même que celui de ZdS (l'application émule un interpreteur Python grace à Jython).

### *build* de l'application

Pour *builder* l'application, vous devez avoir installé Gradle et lancé la commande suivante (depuis le dossier du projet) :

```sh
gradle build
```

À la fin, selon votre système d'exploitation, vous retrouvez un `.exe`, un `.deb`, un `.rpm` ou un `.dmg` dans le dossier `build/distributions`.

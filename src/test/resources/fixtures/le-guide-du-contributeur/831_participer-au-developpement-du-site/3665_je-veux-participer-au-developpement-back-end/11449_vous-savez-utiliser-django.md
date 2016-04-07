Zeste de Savoir repose sur le framework web Django en version 1.6.

Tout développeur connaissant Python et ayant fait un minimum de Django dans sa vie peut aisément participer au développement de Zeste de Savoir. Les bugs à résoudre et améliorations à apporter son nombreuses et de différents niveau. Si jamais vous êtes développeur Python mais que vous ne connaissez pas Django, vous pouvez vous former grâce à l'excellent [tutoriel](https://zestedesavoir.com/tutoriels/232/developpez-votre-site-web-avec-le-framework-django/) de [Ssx`z](https://zestedesavoir.com/membres/voir/Ssx`z/) et [MathX](https://zestedesavoir.com/membres/voir/MathX/).

Passons à une rapide présentation qui vous permettra de rapidement de plonger au cœur du projet.

# Organisation générale du projet

Le projet côté back-end est découpé en différentes parties :

* Les applications dans le dossier `zds` que nous verrons plus en détail un peu plus loin ;
* Les *templates* (ou gabarits) qui sont séparés de leur application et regroupés dans le dossier `templates` ;
* Les *fixtures* dans le dossier `fixtures` qui permettent le chargement de données de tests ;
* Le fichier contenant l'internationalisation du site qui se trouve dans `conf/locale/en/LC_MESSAGES`.

## Les différentes applications de ZdS

Le projet est organisé 8 applications qui représentent les 8 « parties » du site :

* **article** qui comme son nom l'indique, cette partie concerne les articles de ZdS ;
* **forum** pour les forums du site ;
* **gallery** pour la gestion des galeries d'images, incluant celles associées aux tutoriels et aux articles ;
* **member** qui permet la gestion des membres et de leurs profiles (en train d'être entièrement refactorisé suivant le modèle CBV décrit plus bas) ;
* **mp** pour les messages privés (qui fait l'objet d'une ZEP actuellement *Note : ça arrive bientôt*) ;
* **pages** qui regroupe les différentes pages statiques du site ;
* **search** pour le module de recherche ;
* **tutorial** qui est la partie assez complexe des tutoriels ;
* **utils** pour finir qui n'est pas vraiment une partie à part entière mais qui regroupe un ensemble d'outils et de code qui sont réutilisés dans plusieurs autres applications.

## Les templates

Les templates (ou gabarits en bon français) se trouvent dans leur propre dossier : `template`. L'organisation suit celle des applications avec comme template de base `base.html`.

## Les fixtures

## L'internationalisation et les conventions de langue

Tout le code du site ainsi que les commentaires dans le code sont en anglais. Les  chaines de caractères dans le code sont en français mais prêtes à être traduites. Un fichier contenant toutes les chaines à traduire se trouve dans `conf/locale/en/LC_MESSAGES`.

# Les dépendances principales du projets

## south pour les migrations

Le projet fonctionne actuellement avec Django 1.6 et utilise donc [South](https://south.readthedocs.org/en/latest/) pour la migration de modèles. Cette dépendance sera supprimée lors du passage à Django 1.7.

## GitPython (forké pour ZdS)

## Python-ZMarkdown (forlé pour ZdS)

## Django-Rest-Framework pour l'API

*à venir*

# Les dépendances pour les développeurs

* DDT
* flake8 (PEP-8)
* PyYAML (fixtures)

# Conventions de code

## Le modèle CBV

Il existe deux modèles pour servir les vues en Django : utiliser des fonctions ou alors utiliser des classes, ce qu'on appelle le modèle [Class-based views](https://docs.djangoproject.com/en/dev/topics/class-based-views/) (CBV). Aujourd'hui le code utilise entièrement le modèle basé sur les fonctions mais cela va changer. Le premier module qui utilisera le modèle CBV en même temps que l'intégration sera l'application `member`. Il est donc bien de connaitre les deux modèles bien que celui sur les fonction restera encore utilisés un long moment.
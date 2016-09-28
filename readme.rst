***********
Zest Writer
***********

Un éditeur **hors-ligne** de contenus au format zMarkdown.

.. image:: /doc/images/logo.png
   :alt: Logo de ZestWriter

--------------------------------------------------------------------------------

.. image:: https://travis-ci.org/firm1/zest-writer.svg?branch=master
   :target: https://travis-ci.org/firm1/zest-writer
   :alt: Travis-CI Build Status

.. image:: https://ci.appveyor.com/api/projects/status/n3aa5h519uxvjufq/branch/master?svg=true
   :target: https://ci.appveyor.com/project/firm1/zest-writer/branch/master
   :alt: Appveyor build Status

.. image:: https://coveralls.io/repos/github/firm1/zest-writer/badge.svg?branch=master
   :target: https://coveralls.io/github/firm1/zest-writer?branch=master
   :alt: Coverage Status

.. image:: https://www.versioneye.com/user/projects/5719ed6bfcd19a0039f17b07/badge.svg?style=flat
   :target: https://www.versioneye.com/user/projects/5719ed6bfcd19a0039f17b07
   :alt: Dependency Status

.. image:: https://readthedocs.org/projects/zest-writer/badge
   :target: http://zest-writer.readthedocs.io/
   :alt: Documentation Status

.. image:: https://www.openhub.net/p/zest-writer/widgets/project_thin_badge.gif
   :target: https://www.openhub.net/p/zest-writer?ref=sample
   :alt: Statis

ZestWriter est un éditeur d'articles et de tutoriels en mode hors-ligne (sans avoir besoin d'une connexion internet). Il supporte la syntaxe zMarkdown (le Markdown avec les petits ajouts utilisés sur le site `Zeste de Savoir <https://zestedesavoir.com/>`_).

.. no_rtd

Pour la procédure d'installation ou des informations concernant le développement de ZestWriter, merci de consulter `la documentation officielle sur ReadTheDocs <http://zest-writer.readthedocs.io>`_.

.. rtd

Interface
#########

Général
*******

- différents thèmes proposés (clair, sombre, etc.) ;

.. figure:: /doc/images/zw_dark_menu.png
   :align: center

   Capture d'écran de la page d'accueil (thème sombre)

.. figure:: /doc/images/zw_light_redaction.png
   :align: center

   Capture d'écran de la zone de rédaction (thème clair)

Zone d'édition
**************

- boutons d'aide à la rédaction Markdown (gras, italique, blocs spéciaux, tableaux, listes, etc.).

.. image:: /doc/images/buttons_bar.png
   :alt: Barre de boutons

- possibilité de modifier la taille du texte et sa police ;

Zone de rendu
*************

- prévisualisation instantanée lors de la rédaction ;
- décrochage de la zone de rendu dans une fenêtre externe afin de pouvoir la placer sur un écran séparé ;

.. image:: /doc/images/render_window.png
   :alt: Zone de rendu dans une fenêtre externe

Arbre de navigation des contenus (tutoriels ou articles)
********************************************************

- navigation à travers les différents conteneurs ;
- déplacement des conteneurs et des extraits par *drag'n drop* ;
- édition des titres des extraits et conteneurs.

.. image:: /doc/images/tree_view.png
   :alt: Arbre de navigation

Révision
########

- proposition de corrections orthographiques, grammaticales et typographiques du contenu ;

.. image:: /doc/images/grammar_hint.png
   :alt: Prosition de correction

- indices de lisibilité des extraits pour améliorer leur lisibilité (*Flesch* et *Gunning*) ;

.. image:: /doc/images/flesch_indice.png
   :alt: Indice de Flesch

- compteur de mots et de caractères affichés en temps réel ;
- graphiques de répartition du contenu dans les différents conteneurs.

.. image:: /doc/images/chart.png
   :alt: Graphique de répartition du contenu

Synchronisation
###############

- possibilité de récupérer ses contenus depuis le site `Zeste de Savoir <https://zestedesavoir.com/>`_ ;
- possibilité d'envoyer sur le site les modifications effectuées sur Zeste Writer.

.. image:: /doc/images/logo_zds.png
   :alt: Logo de Zeste de Savoir

- possibilité de récupérer des contenus depuis un dépôt GitHub ;

Multiplateforme
###############

- L'application fonctionne sur Windows, Linux et OS X.

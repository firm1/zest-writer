.. _dev_guide:

********************
Guide du développeur
********************

Outils utilisés
###############

code Java
*********

===================  ===========================
Version de Java      Java 8
Interface graphique  `JavaFX <http://www.oracle.com/technetwork/java/javase/overview/javafx-overview-2158620.html>`_
Parseur Markdown     `Python-ZMarkdown <https://github.com/zestedesavoir/Python-ZMarkdown>`_ [1]_
Tests unitaires      Junit 4.12
===================  ===========================

.. [1] Le même parseur qu'utilisé sur Zeste de Savoir (l'application émule un interpreteur Python grace à Jython).

Environnement
*************

===========================  ===========================
Intégration continue         `Travis CI <https://travis-ci.org/firm1/zest-writer>`_ pour Linux et Mac ; `AppVeyor <https://ci.appveyor.com/project/firm1/zest-writer/branch/master>`_ pour Windows
Couverture des tests         `Coveralls <https://coveralls.io/github/firm1/zest-writer?branch=master>`_
Vérification de dépendances  `VersionEye <https://www.versioneye.com/user/projects/5719ed6bfcd19a0039f17b07>`_
Analyse du code              `OpenHub <https://www.openhub.net/p/zest-writer?ref=sample>`_ et Sonar (à venir)
===========================  ===========================

Build du projet
###############

Voir la partie :ref:`install_from_sources`.

Workflow
########

Principe
********

Vous devez travailler sur une branche séparée de ``master``, ensuite pousser vos modifications sur GitHub et faire votre *Pull Request* via l'interface web.

Petit guide à l'usage des débutants sur Git et GitHub
*****************************************************

- *Forkez* `le projet sur GitHub <https://github.com/firm1/zest-writer>`_ en cliquant sur l'icône *Fork* en haut à droite ;
- sur la page de votre dépôt, cliquez sur le bouton *Clone or download* et copiez l'URL ;
- *clonez* votre propre dépôt avec ``git clone url_de_votre_dépôt``.

.. WARNING::
   Si votre projet local provient d'un clone du dépôt officiel et non du votre, pensez à modifier la destitation de ``remote origin`` :

   .. code-block:: sh

      git remote set-url origin url_de_votre_dépôt

   Vous pouvez ensuite vérifier avec ``git remote -v``.

- placez-vous sur une nouvelle branche (``git checkout -b "new_feature"``)

.. NOTE::
   Si vous avez déjà apporté vos modifications (non commitées) sur master, vous pouvez les remiser : ``git stash``, ``git checkout -b "new_feature"``, ``git stash pop``.

- apportez vos modifications en autant de commits que nécessaire, puis poussez vos modification sur une branche distante dédiée (``git push origin new_feature``).

- ensuite, revenez sur la page du `dépôt officiel <https://github.com/firm1/zest-writer>`_ et cliquez sur le bouton *New pull request*, puis décrivez celle-ci ;

- Pensez à récupérer régulièrement les derniers changements effectués (``git fetch https://github.com/firm1/zest-writer`` depuis ``master``).

.. TIP::
   Vous pouvez créer un *git remote* pour la version officielle :

   .. code-block:: sh

      git remote add firm1 https://github.com/firm1/zest-writer

   ... ainsi vous n'aurez pas à taper l'URL du dépôt pour révupérer les derniers changements par exemple (ie, l'étape ci-dessus devient ``git fetch firm1``).

Tests unitaires
###############

Documentation à venir.

.. _dev_guide:

********************
Guide du développeur
********************

Outils utilisés
###############

Code Java
*********

===================  ===========================
Version de Java      Java 11
Interface graphique  `JavaFX`_
Parseur Markdown     `ZMarkdown`_ [1]_
Tests unitaires      Junit 4.12
===================  ===========================

.. _JavaFX: http://www.oracle.com/technetwork/java/javase/overview/javafx-overview-2158620.html
.. _ZMarkdown: https://github.com/zestedesavoir/zmarkdown

.. [1] Le même parseur qu'utilisé sur Zeste de Savoir (l'application émule un moteur zmarkdown à travers GraalVM).

Livraison continue
******************

+------------------------------+---------------+----------------+---------------+
| Intégration continue         | Linux et Os X | `Travis CI`_   | |travisCI|    |
+                              +---------------+----------------+---------------+
|                              | Windows       | `AppVeyor`_    | |AppVeyor|    |
+------------------------------+---------------+----------------+---------------+
| Couverture des tests                         | `Coveralls`_   | |coverAlls|   |
+------------------------------+---------------+----------------+---------------+
| Analyse du code                              | `OpenHub`_     | |OpenHub|     |
+------------------------------+---------------+----------------+---------------+
| Publication de documentation                 | `ReadTheDocs`_ | |ReadTheDocs| |
+------------------------------+---------------+----------------+---------------+

.. _Travis CI: https://travis-ci.org/firm1/zest-writer
.. _AppVeyor: https://ci.appveyor.com/project/firm1/zest-writer/branch/master
.. _Coveralls: https://coveralls.io/github/firm1/zest-writer?branch=master
.. _OpenHub: https://www.openhub.net/p/zest-writer?ref=sample
.. _ReadTheDocs: http://zest-writer.readthedocs.io

.. |travisCI| image:: https://travis-ci.org/firm1/zest-writer.svg?branch=master
.. |appVeyor| image:: https://ci.appveyor.com/api/projects/status/n3aa5h519uxvjufq/branch/master?svg=true
.. |coverAlls| image:: https://coveralls.io/repos/github/firm1/zest-writer/badge.svg?branch=master
.. |OpenHub| image:: https://www.openhub.net/p/zest-writer/widgets/project_thin_badge.gif
.. |ReadTheDocs| image:: https://readthedocs.org/projects/zest-writer/badge

Workflow
########

Principe
********

Vous devez travailler sur une branche séparée de ``master``, ensuite pousser vos modifications sur GitHub et faire votre *Pull Request* via l'interface web.

Guide pas à pas
***************

1. *Forkez* `le projet sur GitHub <https://github.com/firm1/zest-writer>`_ en cliquant sur l'icône *Fork* en haut à droite ;
2. sur la page de votre dépôt, cliquez sur le bouton *Clone or download* et copiez l'URL ;
3. *clonez* votre propre dépôt : ``git clone url_de_votre_dépôt`` ;

.. WARNING::
   Si votre projet local provient d'un clone du dépôt principal et non du votre, pensez à modifier la destitation de ``remote origin`` : ::

      git remote set-url origin url_de_votre_dépôt

   Vous pouvez ensuite vérifier avec ``git remote -v``.

4. buildez le projet, en suivant la partie :ref:`install_from_sources` (sauf l'étape 3.) ;
5. placez-vous sur une nouvelle branche : ``git checkout -b "new_feature"`` (``new_feature`` étant généralement le nom de votre PR) ;

.. NOTE::
   Si vous avez déjà apporté vos modifications (non commitées) sur ``master``, vous pouvez les remiser : ``git stash``, ``git checkout -b "new_feature"``, puis ``git stash pop``.

6. apportez vos modifications en autant de commits que nécessaire (sans oublier de commenter votre code avec la :ref:`javadoc`) ;
7. vérifier que ces modifications passent les tests unitaires : ::

   gradle check

8. poussez vos modification sur une branche distante dédiée : ``git push origin new_feature`` ;
9. ensuite, revenez sur la page du `dépôt principal <https://github.com/firm1/zest-writer>`_ et cliquez sur le bouton *New pull request*, puis décrivez celle-ci ;
10. vérifiez, au bout de quelques minutes, que l'integration continue s'est bien passée (*All checks have passed* en commentaire de la PR).

    Le cas échéant, cliquez sur *details* au niveau de la vérification qui pose problème, analysez les logs, puis apportez vos modification et poussez-les sur votre branche (elle seront directement intégrée sur la PR).

11. pensez à récupérer régulièrement les derniers changements effectués sur le dépôt principal : ``git fetch https://github.com/firm1/zest-writer`` depuis ``master`` ;

.. TIP::
   Vous pouvez créer un *git remote* qui pointe vers le dépôt principal : ::

      git remote add firm1 https://github.com/firm1/zest-writer

   ... ainsi vous n'aurez pas à taper l'URL du dépôt pour récupérer les derniers changements par exemple (ie, l'étape ci-dessus devient ``git fetch firm1``).

Tests unitaires
###############

.. todo::
   Rédiger une documentation pour les tests unitaires (@firm1).

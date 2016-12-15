.. _doc:

*************
Documentation
*************

.. _javadoc:

Javadoc
#######

Afin de générer la documentation technique, vous devez documenter dans votre code chaque conteneur (classe, interface, méthode, etc.) au moyen de commentaires Javadoc :

.. code-block:: java

   /**
   Greet someone.
   @param name The name of a person to greet.
   @return The greeting.
   */
   String greetings(String name) {
     return "Hello " + name;
   }

Voici une liste des tags les plus courament utilisés :

=================================  ==========================================================
``@see reference``                 Fournit un lien vers un autre élément de la documentation.
``@param name description``        Décrit le paramètre ``name`` d'une méthode.
``@return description``            Décrit la valeur de retour.
``@throws exception description``  Décrit une ``exception`` qui peut être levée depuis cette méthode.
=================================  ==========================================================

Documentation fonctionnelle et utilisateur
##########################################

Les pages contenant de la documentation fonctionnelle ou utilisateur telle que celle-ci sont à rédiger en `reStructuredText <https://fr.wikipedia.org/wiki/ReStructuredText>`_ (reST).

Pour connaitre la syntaxe, vous pouvez vous référer :

- aux fichiers sources de cette documentation en cliquant sur *Edit on GitHub* (*View page source* en local) en haut à droite de chaque page.
- à `cet éditeur reStructuredText en ligne <http://rst.ninjs.org/>`_, qui vous évitera d'attendre le build complet si vous voulez tester une syntaxe dont vous n'êtes pas sur ;
- à la `documentation de Sphinx <http://www.sphinx-doc.org/en/stable/rest.html>`_, notamment `les balises Sphinx <http://www.sphinx-doc.org/en/stable/markup/index.html>`_ ;
- `la référence complète <http://docutils.sourceforge.net/docs/ref/rst/restructuredtext.html>`_ de reST, notamment `la liste des directives <http://docutils.sourceforge.net/docs/ref/rst/directives.html>`_.

Voici toutefois une cheatsheet minimaliste :

============================================  ===================
Rendu                                         Syntaxe
============================================  ===================
*italique*                                    .. code-block:: txt

                                                 *italique*
**gras**                                      .. code-block:: txt

                                                 **gras**
``code``                                      .. code-block:: txt

                                                 ``code``
`lien externe <https://zestedesavoir.com/>`_  .. code-block:: txt

                                                 `lien externe <https://zestedesavoir.com/>`_
référence vers :ref:`Javadoc`                 .. code-block:: txt

                                                 référence vers :ref:`Javadoc`
============================================  ===================


Configuration
*************

Le fichier `conf.py </doc/conf.py>`_ contient les paramètres pour générer la documentation. Voici ceux qui ont une importance pour la syntaxe :

- language par défaut pour les blocs de code : ``shell`` ;

Extensions Sphinx
*****************

Sphinx permet d'ajouter `des extensions <http://www.sphinx-doc.org/en/stable/extensions.html>`_` au processus de build : chacune d'elles peuvent modifier à peu près n'importe quel aspect du traitement des documents. Les extensions Sphinx utilisées pour générer cette documentations sont :

- `sphinx.ext.todo <http://www.sphinx-doc.org/en/stable/ext/todo.html>`_ : permet d'ajouter des balises ``todo`` dans la documentation et d'en faire une liste (voir :ref:`todo` ) ;
- `sphinx.ext.autosectionlabel <http://www.sphinx-doc.org/en/stable/ext/autosectionlabel.html>`_ : permet de faire des référence vers des titres
- `javasphinx <https://bronto.github.io/javasphinx/>`_ : permet de générer la Javadoc à partir des commentaires dans le code.

Ajouts spécifiques à Zest Writer
********************************

Le fichier ``conf.py`` a été modifié afin d'apporter quelques ajouts supplémentaires. Ceux listés ci-dessous ont un impact sur la syntaxe.

**Fichier readme**

Dans le fichier ``readme.rst`` à la racine du dépôt (fichier copié dans la documentation, partie :ref:`presentation`), les balises ``.. no_rtd`` et ``.. rtd`` permetent respectivement de marquer le début et la fin d'une zone qui ne sera pas publiée dans la documentation.

**Réécriture d'URL**

Les URLs précédées de ``//`` ont le comportement suivant :
- lorsque la documentation est générée en local, elles pointent vers la racine du projet ;
- lorsque la documentation est sur ReadTheDocs, elles pointent vers le fichier correspondant sur le GitHub (en raw).

Ainsi, ```licence <\//LICENSE>`_`` donne `licence <//LICENSE>`_.

Générer la documentation
########################

Installez les dépendances requises pour générer la documentation : ::

   pip install sphinx javasphinx sphinx_rtd_theme

.. WARNING::
   Assurez-vous d'avoir suivi la partie :ref:`install_from_sources` avant de tenter de générer la documentation.

À la racine du projet, tapez simplement :

   gradle doc

.. NOTE::
   En vous placant sur le dossier ``doc``, vous pouvez également taper ``make`` et utiliser les nombreuses options disponibles (le ``gradle doc`` ci-dessus lance un ``make html``).

La documentation apparaitra dans ``doc/build/html``. Ouvrez le fichier ``index.html`` avec votre navigateur web pour la consulter.

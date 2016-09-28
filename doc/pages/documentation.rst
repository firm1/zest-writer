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

Documentation fonctionnelle ou utilisateur
##########################################

Les pages contenant de la documentation fonctionnelle ou utilisateur telle que celle-ci sont à rédiger en `reStructuredText <https://fr.wikipedia.org/wiki/ReStructuredText>`_ (rst).

Pour connaitre la syntaxe, vous pouvez vous référer aux resources officielles sur sourceforge :

- `la référence rapide <http://docutils.sourceforge.net/docs/user/rst/quickref.html#tables>`_ ;
- `la référence complète <http://docutils.sourceforge.net/docs/ref/rst/restructuredtext.html>`_,
- `la liste des directives <http://docutils.sourceforge.net/docs/ref/rst/directives.html>`_.

Vous pouvez également regarder comment sont rédigées ces pages en lisant les fichiers ``.rst`` du dossier ``doc/pages`` (par exemple `celle-ci <../../../pages/documentation.rst>`_).

Générer la documentation
########################

Installez les dépendances requises pour générer la documentation :

.. code-block:: sh

   pip install -r doc/local_requirements.txt

.. WARNING::
   Assurez-vous d'avoir suivi la partie :ref:`install_from_sources` avant de tenter de générer la documentation.

À la racine du projet, tapez simplement :

.. code-block:: sh

   gradle doc

.. NOTE::
   En vous placant sur le dossier ``doc``, vous pouvez également taper ``make`` et utiliser les nombreuses options disponibles (le ``gradle doc`` ci-dessus lance un ``make html``).

La documentation apparaitra dans ``doc/build/html``. Ouvrez le fichier ``index.html`` avec votre navigateur web pour la consulter.

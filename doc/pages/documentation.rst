*************
Documentation
*************

Préparation des outils de documentation
***************************************

Installez les dépendances requises pour générer la documentation :

.. code-block:: sh

   pip install -r doc/local_requirements.txt

Documenter le code
******************

Vous devez documenter dans votre code chaque classe, interface, méthode, etc. ainsi que leurs arguments et la valeur retournée au moyen de commentaires Javadoc :

.. code-block:: java

   /**
   Greet someone.
   @param name The name of a person to greet.
   @return The greeting.
   */
   String greetings(String name) {
     return "Hello " + name;
   }

Vous trouverez `ici <https://en.wikipedia.org/wiki/Javadoc#Structure_of_a_Javadoc_comment>`_ d'avantage d'information sur la Javadoc et les tags pouvant être utilisés.

Les pages contenant de la documentation fonctionnelle ou utilisateur telle que celle-ci sont à rédiger en `reStructuredText <https://fr.wikipedia.org/wiki/ReStructuredText>`_ (rst). La syntaxe est détaillée sur le site de `sphinx doc <http://www.sphinx-doc.org/en/stable/rest.html>`_.

Générer la documentation
************************

À la racine du projet, tapez simplement :

.. code-block:: sh

   gradle doc

En vous placant sur le dossier ``doc``, vous pouvez également taper ``make`` et utiliser les nombreuses options disponibles (le ``gradle doc`` ci-dessus fait un ``make html``).

La documentation apparaitra dans `doc/build/html`. Ouvrez index.html avec votre navigateur web pour la consulter.

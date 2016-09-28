.. _install:

************
Installation
************

Windows
#######

Téléchargez le setup (recommandé) ou l'executable correspondant à votre architecture (32 bits ou 64 bits) et lancez l'installateur. Un fichier ``setup`` lancera un installateur qui installera le programme dans le dossier approprié, tandis qu'un fichier ``executable`` contient l'ensemble du programme et peut être executé n'importe où.

====================================================================================================================================  ====================================================================================================================================
Windows 32 bits                                                                                                                       Windows 64 bits
`exécutable <https://ci.appveyor.com/api/buildjobs/1b8oa0uv8b6skthh/artifacts/build%2Fdistributions%2Fzest-writer-for-windows.zip>`_  `exécutable <https://ci.appveyor.com/api/buildjobs/cmwax4m523t6gol4/artifacts/build%2Fdistributions%2Fzest-writer-for-windows.zip>`_
`setup <https://ci.appveyor.com/api/buildjobs/1b8oa0uv8b6skthh/artifacts/build%2Fdistributions%2FZestWriter-1.3.0.exe>`_              `setup <https://ci.appveyor.com/api/buildjobs/cmwax4m523t6gol4/artifacts/build%2Fdistributions%2FZestWriter-1.3.0.exe>`_
====================================================================================================================================  ====================================================================================================================================

.. NOTE::
   Pour vérifier si votre ordinateur tourne sur une version 32 bits ou 64 bits de Windows, cliquez le bouton ``Windows``, ``Ordinateur``, puis ``Propriétés`` et regardez en dessous de ``Système``.

Os X
####

Téléchargez la dernière version de Zest Writer via `ce lien <https://bintray.com/firm1/dmg/zest-writer/_latestVersion#files>`_, puis installez le fichier dmg.

Linux
#####

Debian, Ubuntu, etc.
********************

*Pour une mise à jour passez directement à l'étape 3*.

1. importez la clé GPG de bintray :

.. code-block:: sh

   sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 379CE192D401AB61

2. ajouter une nouvelle source :

.. code-block:: sh

   echo "deb https://dl.bintray.com/firm1/deb wheezy main" | sudo tee -a /etc/apt/sources.list.d/zestwriter.list

3. mettez à jour vos dépôts :

.. code-block:: sh

   sudo apt-get update

4. installez Zest Writer :

.. code-block:: sh

   sudo apt-get install zestwriter

Fedora, ArchLinux, CentOs, etc.
*******************************

1. Créez le fichier ``/etc/yum.repos.d/zestwriter.repo`` et copiez le contenu suivant à l'intérieur :

::

   [zestwriter]
   name=zestwriter
   baseurl=http://dl.bintray.com/firm1/rpm
   gpgcheck=0
   enabled=1

2. Installez Zest Writer en tapant ``yum install zestwriter``.

Jar executable (tous les systèmes)
##################################

Les fichiers ``jar`` sont plus légers car ils n'embarquent pas la machine virtuelle Java, en revanche il est nécessaire que celle-ci soit au préalable installée sur votre système.

1. Vérifier votre version de Java : ouvrez un terminal (``Windows + R`` puis ``cmd`` sous Windows) et tapez ``java -version`` ;
    - si vous avez Java 1.7 ou antérieure, téléchargez la dernière version de Java depuis `le site officiel <http://www.java.com/fr/download/>`_ ;
3. téléchargez la dernière version de Zest Writer via `ce lien <https://bintray.com/firm1/maven/zest-writer/_latestVersion#files>`_ ;
4. ouvrez un terminal et lancez le jar en tapant ``java -jar zest-writer-all-x.y.z.jar`` (x.y.z étant le numéro de version).

.. _install_from_sources:

Installation depuis les sources
###############################

1. Vérifiez que vous utilisez une version du JDK supérieure ou égale à 1.8 : ouvrez un terminal et tapez ``echo $JAVA_HOME`` (sous Linux et Mac), ou ``echo %JAVA_HOME%`` (sous Windows)
    - si votre JDK est inférieur à 1.8, téléchargez la dernière version depuis `le site officiel <http://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ ;
2. Installez *Gradle*
    - via ``sudo apt-get install gradle`` (ou équivalent) ;
    - ou bien depuis `le site officiel <https://docs.gradle.org/current/userguide/installation.html>`_.
3. téléchargez les sources :
    - via l'archive : téléchargez `l'archive contenant la dernière version <https://github.com/firm1/zest-writer/archive/master.zip>`_ puis décompressez-la ;
    - ou via la commande Git : ``git clone https://github.com/firm1/zest-writer.git`` ;
4. placez-vous dans le répertoire du projet (``cd zest-writer``) ;
5. enfin, tapez dans un terminal :

.. code-block:: sh

   gradle build

Selon votre système d'exploitation, vous devriez retrouvez un ``.exe``, un ``.deb``, un ``.rpm`` ou un ``.dmg`` dans le dossier ``build/distributions``.

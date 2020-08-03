.. _install:

************
Installation
************

Windows
#######

Téléchargez le setup et lancez l'installateur. Le fichier ``setup`` lancera un installateur qui installera le programme dans le dossier approprié.

======================
Windows 64 bits
======================
`setup 64 bits`_
======================

.. _setup 64 bits: https://bintray.com/zest-writer/windows-x64/setup/_latestVersion#files

Os X
####

Téléchargez la dernière version de Zest Writer via `ce lien <https://bintray.com/zest-writer/dmg/zest-writer/_latestVersion#files>`_, puis installez le fichier dmg.

Linux
#####

Debian, Ubuntu, etc.
********************

.. NOTE::
   Pour une mise à jour, passez directement à l'étape 3.

1. importez la clé GPG de bintray :

   ``sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 379CE192D401AB61``

2. ajoutez une nouvelle source :

   ``echo "deb https://dl.bintray.com/zest-writer/deb wheezy main" | sudo tee -a /etc/apt/sources.list.d/zestwriter.list``

3. mettez à jour vos dépôts :

   ``sudo apt-get update``

4. installez Zest Writer :

   ``sudo apt-get install zestwriter``


Fedora, ArchLinux, CentOs, etc.
*******************************

1. Créez le fichier ``/etc/yum.repos.d/zestwriter.repo`` et copiez le contenu suivant à l'intérieur :

::

   [zestwriter]
   name=zestwriter
   baseurl=http://dl.bintray.com/zest-writer/rpm
   gpgcheck=0
   enabled=1

2. Installez Zest Writer en saisissant dans votre terminal ``yum install zestwriter``.

.. _install_from_sources:

Installation depuis les sources
###############################

1. Vérifiez que vous utilisez une version du JDK supérieure ou égale au Jdk 11 :
    - ouvrez un terminal et tapez ``echo $JAVA_HOME`` (sous Linux et Mac) ou ``echo %JAVA_HOME%`` (sous Windows)
    - si votre JDK est inférieur au Jdk 11, téléchargez la dernière version depuis `le site officiel <http://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ ;
2. installez *Gradle* :
    - via ``sudo apt-get install gradle`` (ou équivalent) ;
    - ou bien depuis `le site officiel <https://docs.gradle.org/current/userguide/installation.html>`_.
3. téléchargez les sources :
    - via l'archive : téléchargez `l'archive contenant la dernière version <https://github.com/firm1/zest-writer/archive/master.zip>`_ puis décompressez-la ;
    - ou via la commande Git : ``git clone https://github.com/firm1/zest-writer.git`` ;
4. placez-vous dans le répertoire du projet (``cd zest-writer``) ;
5. enfin, tapez dans un terminal :

   ``gradle jpackage``

Selon votre système d'exploitation, vous devriez retrouvez un ``.msi``, un ``.deb``, un ``.rpm`` ou un ``.dmg`` dans le dossier ``build/installer``.

************
Installation
************

Windows
#######

Les instructions se trouvent sur `ce post <https://zestedesavoir.com/forums/sujet/5354/zest-writer-un-editeur-hors-ligne-pour-vos-contenus-zds/#p98286>`.

Os X
####

1. Téléchargez la dernière version de Zest Writer via `ce lien <https://bintray.com/firm1/dmg/zest-writer/_latestVersion#files>`_.
2. Installez le dmg sur votre système d'exploitation.

Linux
#####

Debian, Ubuntu, etc.
********************

*Pour une mise à jour passez directement à l'étape 3*.

1. Importez la clé GPG bintray de  via la commande : ``sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 379CE192D401AB61``
2. Ajouter une nouvelle source via la commande : ``echo "deb https://dl.bintray.com/firm1/deb wheezy main" | sudo tee -a /etc/apt/sources.list.d/zestwriter.list``.
3. mettez à jour vos dépôts via la commande : ``sudo apt-get update``.
4. installez Zest Writer via la commande : ``sudo apt-get install zestwriter``.

Fedora, ArchLinux, CentOs, etc.
*******************************

1. Créez le fichier ``/etc/yum.repos.d/zestwriter.repo`` et copiez le contenu suivant à l'intérieur :

```
[zestwriter]
name=zestwriter
baseurl=http://dl.bintray.com/firm1/rpm
gpgcheck=0
enabled=1
```

2. Installez Zest Writer via la commande : ``yum install zestwriter``.

Jar executable (tous les OS)
############################

Pour cela vous avez besoin de **Java 8** installé sur votre système.

1. Téléchargez la dernière version de Zest Writer via `ce lien <https://bintray.com/firm1/maven/zest-writer/_latestVersion#files>`.
2. Ouvrez un terminal et lancez le jar via (x.y.z étant le numéro de version) : ``java -jar zest-writer-all-x.y.z.jar``.

Depuis les sources
##################

Voir le *Guide du développeur*.

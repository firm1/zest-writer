********************
Guide du développeur
********************

Outils
######

- L'application est developpée en Java **8** (vous devez donc avoir cette version pour pouvoir developper) ;
- l'interface utilise JavaFX ;
- le parseur markdown utilisé est le même que celui de ZdS (l'application émule un interpreteur Python grace à Jython).

Build
#####

Pour *builder* l'application, vous devez avoir installé Gradle et lancé la commande suivante (depuis le dossier du projet) ::

   gradle build

À la fin, selon votre système d'exploitation, vous retrouvez un ``.exe``, un ``.deb``, un ``.rpm`` ou un ``.dmg`` dans le dossier ``build/distributions``.

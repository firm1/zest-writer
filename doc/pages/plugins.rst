**************************************
Plugins pour l'application Zest-Writer
**************************************

Il est possible de créer ses propres plugins pour l'application, grâce à une API que vous pouvez télécharger `ici <http://>`_.

*TODO*

Mise en place l'API
###################

Sur IntelliJ
************

*TODO*

Sur Eclipse
***********

*TODO*

Création d'un plugin pour Zest Writer
#####################################

Nous vous conseillons de télécharger les fichiers modèles, disponible pour `IntelliJ <http://>`_ ainsi que pour `Eclipse <http://>`_.

Détail du fichier modèle
************************

.. code-block:: java

   package com.winxaito.main;

   import com.zestedesavoir.zestwriter.MainApp;
   import com.zestedesavoir.zestwriter.contents.plugins.ZwPlugin;
   import com.zestedesavoir.zestwriter.contents.plugins.ZwPluginVersion;

   import java.util.ArrayList;

   public class Main implements ZwPlugin{
       private MainApp mainApp;

       @Override
       public void onEnable(MainApp mainApp){
           System.out.println("[FROM PLUGIN] Enable plugin");
           this.mainApp = mainApp;
       }

       @Override
       public ArrayList<Class> getListener(){
           ArrayList<Class> listener = new ArrayList<>();
           listener.add(Main.class);

           return listener;
      }

       @Override
       public void onDisable(){
           System.out.println("[FROM PLUGIN] Disable plugin");
       }

       @Override
       public ZwPluginVersion getVersion(){
           return new ZwPluginVersion(0, 0, 0);
       }
   }

La méthode ``onEnable``
***********************

La méthode ``onEnable`` est appelée lors du lancement de l'application.
Cette méthode prend en paramètre la classe ``MainApp`` (classe principale de l'application).
C'est grâce à cette classe que nous pouvons modifier notre application.

La méthode ``getListener``
**************************

La méthode ``getListener`` est également obligatoire, elle va retourner la liste des classes qui doivent être écoutées pour les différents événements de l'application.
Pour ajouter une classe à écouter, il suffit d'ajouter la classe dans l'``ArrayList``, comme ceci :

.. code-block:: java

   listener.add(com.winxaito.main.events.WindowEvents.class);

La méthode ``onDisable``
************************

Cette méthode est appelée lors de l'arrêt de l'application.

La méthode ``getVersion``
*************************

Cette méthode retourne la version du plugin.

Le système de version fonctionne ainsi :

- Le premier chiffre correspond à une version majeure ;
- le second à une modification mineure ;
- le dernier servant de ``hotfix``, pour corriger des petits bugs.

.. NOTE::
   Ce système de version n'est pas obligatoire, mais il est vivement conseillé de le suivre.

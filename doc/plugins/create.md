#Création d'un plugin pour Zest-Writer

Nous vous conseillons de télécharger les fichiers modèles, disponible pour [IntelliJ]() ainsi que pour [Eclipse]().

##Détail du fichier modèle

```java
package com.winxaito.main;

import com.zestedesavoir.zestwriter.MainApp;
import com.zestedesavoir.zestwriter.plugins.ZwPlugin;
import com.zestedesavoir.zestwriter.plugins.ZwPluginVersion;

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
```

##La méthode `onEnable`
Ensuite, la méthode `onEnable` est appelé lors du lancement de l'application.  
Cette méthode prend en paramètre la classe `MainApp`, il s'agit de la classe principale de l'application.
C'est grâce à cette classe que nous pouvons modifié notre application.

##La méthode `getListener`
La méthode `getListener` est également obligatoire, elle va retourner la liste des classes qui doivent être écoutés pour les différents événements de l'application.
Pour ajouter une classe à écouter, il suffit d'ajouter la classe dans l'ArrayList, comme ceci
 
```java
listener.add(com.winxaito.main.events.WindowEvents.class);
```

##La méthode `onDisable`
Cette méthode est appelé lors de l'arrêt de l'application.

##La méthode `getVersion`
Cette méthode retourne la version du plugin.

Le système de version fonctionne comme suis:

* Le premier chiffre correspond à une version majeure.
* Le second à une modification mineur.
* Et le dernier servant de `hotfix`, pour corriger des petits bug.

*Ce système de version n'est pas obligatoire, mais il est vivement conseillé de le suivre.*
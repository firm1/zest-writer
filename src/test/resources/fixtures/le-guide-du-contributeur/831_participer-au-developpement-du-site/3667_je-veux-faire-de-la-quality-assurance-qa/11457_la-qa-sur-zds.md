Afin de parler de la qualité logicielle sur Zeste de Savoir, je vais reprendre ici le même plan que l'extrait précédent en détaillant concrètement comment ces détails sont appliqués ici.

# Propreté du code et PEP

Nous l'avons vu et vous le savez sûrement si vous codez depuis un moment, la propreté du code est primordiale pour le maintien de celui-ci. C'est pourquoi ZdS applique un soin particulier à la vérification de ce dernier et encourage les contributeurs à écrire clairement.

Pour simplifier tout cela, un mécanisme automatique nommé `flake` se charge de vérifier pour vous que le code respecte les conventions PEP. Si l'indentation est mauvaise ou le nombre d'espaces ne convient pas, il vous le dira !

Seul une règle est autorisée à être transgressée : la longueur des lignes est fixée à 120 caractères au lieu de 80.

Pour lancer les tests de syntaxe localement, vous pouvez exécuter la ligne suivante sur votre ordinateur, dans le dossier racine :

```bash
flake8 --exclude=migrations,urls.py,settings.py --max-line-length=120 zds
```

Si vous voyez des erreurs apparaître (peu probable), n’hésitez pas à les corriger et à proposer votre correction !

# Tests Unitaires (TU)

Les Tests Unitaires sont omniprésents sur tout le *back-end* de ZdS. En effet, à l'heure d’écriture de ces lignes c'est un peu plus de 80% qui est testé automatiquement. Cela signifie que lorsqu'un testeur fait une modification, il ne lui reste que 20% de chances de casser quelque chose. :D

## Organisation des tests

Les tests sont écrits pour chaque module. On retrouve donc des séries de tests pour `mp`, `forum`, `articles` etc. Ensuite, dans chaque module on trouve deux situations :

+ le module n'a pas encore subi la *refactorisation* des tests, dans ce cas seul un fichier `tests.py` est présent ;
+ le module a subi la *refactorisation* des tests. Dans ce cas un dossier `tests/` contient trois fichiers :
    + `tests_forms.py` qui teste les formulaires contenus dans `forms.py` ;
    + `tests_models.py` qui teste les méthodes des modèles contenus dans `models.py` ;
    + `tests_views.py` qui teste les méthodes des vues contenues dans `views.py`.

Ensuite, le contenu des fichiers est semblable. On retrouve des classes qui vont tester un ensemble de cas similaires. Les classes contiennent alors des méthodes qui sont les cas de tests. On trouve aussi éventuellement des méthodes `setup` et `teardown` chargées respectivement d'instancier des objets/variables globales pour cette classe de test et de détruire des objets à la fin de la série de tests de la classe.

L’écriture des tests est un excellent exercice et est vraiment utile. Chaque nouvelle fonction ou correction de bug doit s'accompagner de son test prouvant la résolution du bug (si possible évidemment, certains bugs n’étant pas testables automatiquement).

## Lancement des tests

Pour lancer les tests et vérifier que tout marche, c'est assez simple. Il suffit de lancer la commande suivante depuis la racine de votre dossier de travail :

```python
python manage.py test zds
```

Si vous ne voulez vérifier qu'un module et ainsi gagner du temps, lancer juste ce module ! Par exemple pour le forum :

```python
python manage.py test zds.forum
```

## Tests du front-end

À l'heure actuelle, aucune procédure de tests automatiques du front n'est mise en place.

# Tests manuels

Il n'existe pas de règles absolues concernant les tests manuels. Généralement, c'est le développeur de la PR qui propose les cas à tester et si possible les cas dont il faudra se méfier. Le reste étant en général laissé à l'imagination du testeur.

Concrètement, le développeur fera dons des recommandations en rajoutant un paragraphe `QA` dans son message de PR. Lire le ticket du bug corrigé ou de la fonction concernée est évidemment un plus pour savoir de quoi il retourne.

Ensuite, n'importe quel utilisateur peut se proposer pour "faire la QA" de la PR. Pour cela il faut avant tout avoir une installation fonctionnelle de ZdS en local sur son ordinateur. Ensuite, il faudra récupérer le nouveau code puis jouer avec. Après cela, il est de bon ton de faire un petit message à l'auteur de la modification pour lui signaler si tout va bien, ou ce qui doit être modifié.

Pour vous aider à préparer votre branche de travail, un petit guide a été rédigé par Eskimon sur le forum : [Guide technique de la QA](https://zestedesavoir.com/forums/sujet/1351/la-qa-pour-les-nuls/).

# Documentation

La documentation est un des points d’entrée pour tout nouveau contributeur. Si un nouveau développeur souhaite rejoindre le projet pour travailler sur une [nouvelle] fonctionnalité, il y a fort à parier que ce dernier aura besoin de lire la documentation sur cette dernière.

Afin de garantir une pérennité du code, il est donc **indispensable** que la documentation soit écrite ou mise à jour pour toute fonctionnalité. Ainsi chaque PR apportant un changement fonctionnel doit aussi proposer la documentation qui va avec. Si ce n'est pas le cas, la PR doit être mise en attente le temps d'obtenir cette dernière.

[[i]]
| À l'attention des développeurs : oui, écrire de la doc ce n'est pas drôle, mais non, ça ne prend pas des années. En général, il faut 5 à 10 minutes pour produire le morceau de documentation technico-fonctionnelle nécessaire.

La documentation de Zeste de Savoir est en deux parties.

+ Pour le front : 
+ Pour le back : http://zds-site.readthedocs.org/fr/latest/
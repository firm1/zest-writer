Le but de la qualité logicielle se déduit du nom : produire un logiciel de qualité.

Pour être plus précis, l'objectif est double : s'assurer de la qualité d'une évolution et pallier aux éventuelles régressions. Autrement dit, la QA permet d'attester qu'une modification du code remplit correctement son rôle (bug corrigé, fonctionnalité fonctionnelle, etc.) et n'introduit pas de pépins autre part.

Assurer la qualité d'un logiciel passe par plusieurs étapes.

# Tests manuels

Les tests manuels sont des tests effectués par des humains, comme vous et moi en principe. Ils sont réalisés par des utilisateurs n'ayant pas forcément de connaissances techniques. Le but ici est de tester que la correction d'un bug remplit bien son rôle sans nuire à d'autres fonctions (on appelle cela des *effets de bords*, ou *régressions* ). Dans le cas du développement d'une fonction, il s'agit de la même façon de s'assurer que la nouvelle fonction ne casse pas un autre module ou comportement.

Pour faire ces tests, il suffit d'avoir sur son ordinateur un environnement de travail à jour (nous y reviendrons) et un peu d'imagination pour anticiper les cas atypiques qui pourraient avoir des effets de bord pouvant nuire à d'autres parties du logiciel.

# Tests Unitaires (TU)

Les tests unitaires sont les premiers tests à être exécutés sur le code. Ces derniers sont complètement automatiques (bien souvent juste une commande à lancer) et permettent de vérifier des fonctions du code. Leur but est de tester un maximum de cas particuliers du code.

Ces tests fonctionnent en "boite noire". Ils vont donner un ensemble de paramètres à une fonction, exécuter la fonction et vérifier que ces dernières renvoient bien les valeurs de retour attendues et avec les bonnes valeurs.

Ces tests sont très importants. En effet, de part leur caractère automatique ils sont rapides ç exécuter et permettent ainsi de tester simplement beaucoup de choses. Chaque nouvelle fonction devrait proposer un ensemble de tests unitaires permettant de tester les différentes situations dans lesquelles le code peut se retrouver.

# Propreté du code et PEP

Une des premières étapes de la qualité logicielle passe par la qualité de l’écriture du code lui-même. Un code mal indenté, avec des variables aux noms obscures et sans commentaires sera jugé peu lisible et va nuire à la qualité du code. Afin de remédier à cela, plusieurs pratiques sont recommandées.

## Bonnes pratiques d’écritures

Comme nous venons de le dire, bien écrire son code est primordial. Pour cela, des règles sont souvent suivies :

+ nommer clairement ses fonctions et variables ;
+ commenter les longs morceaux de codes ;
+ éviter les fonctions trop longues ;
+ bien indenter son code ;
+ avoir du bon sens et penser aux relecteurs !

## Python Enhancement Proposal (PEP)

Pour garantir une lisibilité du code, Python propose un ensemble de règles à suivre. Elles ne sont pas obligatoires mais recommandées. Ces règles sont recueillies au sein des PEP, *Python Enhancement Proposal*.

Zeste de Savoir, comme de nombreux projets python, essaie au maximum de suivre la PEP-8. Ainsi, pour garantir que le code reste propre, un outil d'**analyse statique** parcourt le code avant chaque soumission dans le tronc commun pour s'assurer que les règles sont respectées. Nous verrons comment cela se traduit concrètement un peu plus tard.

# Documentation

Enfin, une dernière étape trop souvent négligée est celle de la documentation. En effet, un logiciel non documenté est souvent voué à une mort lente et douloureuse. Quel programmeur a envie de passer son temps à faire de la rétro-ingénierie pour comprendre une fonction ou un bout de code ?

Écrire de la documentation permet donc de garantir que son code est clair, que la fonctionnalité écrite est bien exprimée pour soi-même et pour les contributeurs externes et ainsi le code devient plus facile à reprendre plus tard.

Il existe plusieurs types de documentations, chacune ayant un rôle précis.

## Les commentaires de code

Les commentaires dans le code source sont la première source de documentation. Si un morceau parait obscur, il doit posséder une ligne ou deux de commentaires. Bien entendu, les évidences ne sont pas à documenter ! Enfin, avoir quelques lignes de description du rôle de la fonction directement dans le code est vraiment un plus. Ainsi, un contributeur qui reprend le code peut comprendre rapidement le but de la fonction. Ce but devrait d'ailleurs toujours être compréhensible rien qu'avec le nom de la fonction ! ;)

## La documentation Technico-Fonctionnelle

Ce type de documentation est nettement plus "littéraire" que la précédente. En effet, il s'agit là de décrire le rôle de chaque fonction vis-à-vis de l'utilisateur. Par exemple, pour un module gérant un garage de véhicules, la documentation raconterait que le but de ce module est de gérer une collection de voitures, que la limite de places est définie par une variable nommée `x` et différentes fonctions sont utilisables pour gérer le garage.
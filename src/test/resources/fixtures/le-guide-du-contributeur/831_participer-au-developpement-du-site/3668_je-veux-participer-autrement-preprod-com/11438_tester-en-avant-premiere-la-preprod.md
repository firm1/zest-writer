Vous ne savez pas (encore) programmer dans les langages de programmation qu'utilise ZdS ? Qu'à cela ne tienne, vous pouvez quand même participer à la grande aventure de développement du site en faisant partie des premiers testeurs qui iront vérifier en avant-première que tout marche bien sur la pré-prod'.

# La pré-prod, qu'est-ce que c'est ?

Pré-prod' est un raccourci signifiant "pré-production". À l'heure actuelle, vous consultez probablement ce tutoriel ou le site sur la version dite de "production". C'est la version la plus stable et éprouvée du site, celle où seules les mises à jour fiables voient le jour pour garantir une expérience utilisateur la meilleure possible.

Seulement, avant d'arriver en production, les mises à jour passent toutes par le serveur de pré-production. Ce dernier n'est ni plus ni moins qu'un serveur **identique** à celui utilisé au quotidien sur Zeste de Savoir (on dit qu'il est *iso-prod*). La seule différence se trouve dans la version du logiciel qui y fonctionne. En effet, ce dernier possède alors un lot de nouveautés que les développeurs ont proposées et codées.

La pré-prod est donc une étape très importante de la vie du site puisqu'elle garantit que toutes les mises à jour mises ensemble ne cassent en rien le fonctionnement du site et fonctionnent bien comme prévu. C'est le dernier rempart pour détecter des nouvelles anomalies avant la mise en production finale pour le public.

# Que faut-il y faire ?

Et c'est là que tout le monde peut jouer. La pré-prod est littéralement un bac à sable où le but du jeu est de prendre à défaut le système ! :D N'importe qui peut venir la tester, pas besoin d’être techniquement connaisseur, le but étant simplement d'agir en utilisateur pour vérifier que tout fonctionne bien et qu'il n'y a pas de nouveaux bugs introduits par les nouveautés. Cela permet notamment aux plus curieux de tester les nouveautés en avant-première, puisque durant les phases de tests la pré-prod' possède toutes les corrections et nouvelles fonctionnalités, que ce soit pour le design ou pour des améliorations plus obscures.

Sachez tout de même que pour le bon fonctionnement du site et pour rester *iso-prod*, la base de données est remise à jour à chaque nouvelle période de test.

[[i]]
| Les bases de données de production et pré-production sont indépendantes. Si vous modifiez votre compte sur l'une ou faites des actions particulières, cela ne changera en rien votre compte sur l'autre.

# Comment cela s'organise ?

Le fonctionnement des tests sur la pré-prod' est assez simple :

1. les développeurs du site travaillent dans leur coin pour corriger des bugs ou proposer de nouvelles fonctionnalités ;
2. lorsque suffisamment de mises à jour sont prêtes (souvent par vingtaines), elles sont regroupées dans le gestionnaire de versions pour faire une *release* ;
3. la *release* est alors mise en route sur le serveur de pré-prod pendant une quinzaine de jour ;
4. durant cette période, tout le monde peut venir jouer et essayer de repérer des nouveaux bugs ou suggérer des améliorations sur les nouvelles fonctions mise en œuvre ;
5. si tout semble OK au bout de la quinzaine de jour, la *release* est alors envoyée sur le serveur de production pour que tout le monde en profite.
6. si au contraire de nouveaux bugs sont introduits et non corrigés durant cette période, la *release* est abandonnée et devra être recommencée plus tard.

# Je participe !

Pour cela, rien de plus simple !

Lors de chaque nouvelle *release*, un message dans la [Dev Zone](https://zestedesavoir.com/forums/communaute/dev-zone/) est créé. Une fois ce dernier publié, vous avez alors 2 semaines pour traquer les nouveaux bugs. L'adresse de la pré-prod' est http://preprod.zestedesavoir.com/ (je vous avais dit que c’était simple !). Si un mot de passe vous est demandé pour rentrer sur le site, tapez `clementine/souris` comme couple login/mot de passe. Et voilà, vous êtes maintenant un débogueur en avant première de ZdS !

[[i]]
| Si vous êtes nouveau sur le site, il se peut que vous n'ayez pas de compte sur la pré-prod'. Dans ce cas vous pouvez simplement en créer un avec votre adresse e-mail habituelle. Sinon, connectez-vous avec votre compte habituel.

[[a]]
| La base de données étant remise sur pied à chaque *release*, ne faites pas de travaux de longue haleine sur la pré-prod', ou alors assurez-vous de garder des copies autre part !
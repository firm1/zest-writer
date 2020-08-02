Dans un premier temps, il est nécessaire de rappeler que tous les contenus ne sont pas autorisés sur Zeste de Savoir. Nous aborderons ensuite les critères généraux dont tiennent compte les validateurs quand ils s’occupent de votre contenu.

# Les contenus refusés

## Les contenus plagiés

Les contenus entièrement ou partiellement plagiés sont systématiquement refusés. Nous rappelons que le plagiat est un délit. En cas de plagiat, l’auteur du plagiat recevra un avertissement. En cas de récidive, une sanction plus lourde pourra être appliquée.

## Les contenus contraires aux conditions générales d’utilisation 

Certains contenus sont refusés car leur sujet est clairement à visée illégale et [contraires aux conditions générales d’utilisation de Zeste de Savoir](https://zestedesavoir.com/pages/cgu/).

Parmi ces sujets, on peut citer :

- l’intrusion malveillante dans un système informatique ;
- le téléchargement d’applications piratées.


[[attention]]
| Attention, certains contenus peuvent osciller entre légalité et illégalité. Les validateurs y sont particulièrement attentifs. Citons, à titre d’exemple, un article sur le *jailbreak* ou un tutoriel Aircrack qui serait particulièrement maladroit dans sa forme (où il y a, par ailleurs, eu un cas de condamnation en justice). Le caractère tendancieux de ce genre de contenus amène **à la plus grande prudence** et un contenu pourrait se voir refusé si le validateur estime que l’auteur incite à une utilisation malveillante, si l’objet du contenu devient illégal par une évolution récente de la loi ou si l’un ou l’autre élément a fait l’objet d’une décision judiciaire.

## Installation (et utilisation) de logiciels simples et trucs et astuces (TEA)

Les contenus présentant simplement l’installation de logiciels clients ou serveurs sont refusés. Par exemple, un tutoriel sur l’installation d’un éditeur de texte. 

Il est de même fréquent de recevoir des contenus qui expliquent comment utiliser un logiciel et/ou une extension qui, de base, se révèle plutôt simple à prendre en main comme *AdBlock Plus* ou *Paint*, ou fournissent des astuces quant à l’utilisation d’un logiciel donné. Ce genre de tutoriel est refusé.

Nous pouvons désormais entrer dans le vif du sujet ! :)

# Les critères de validation

Voici désormais les différents points sur lesquels les validateurs insistent. Certains éléments, tel que la pédagogie, sont plus généraux en raison de la grande variété des contenus et de style des auteurs. Il s’agit néanmoins d’insister sur les éléments considérés comme essentiels.

## Le fond

### Introduction générale (et des différents chapitres, s’il y a lieu) 

L’introduction est l’un des aspects les plus importants de votre contenu. Une bonne introduction **doit donner envie de lire le contenu** (posez-vous la question : pourquoi le lecteur devrait lire ce contenu ? Qu’est-ce qui le motiverait ?) et donne, par ailleurs, plusieurs informations comme les prérequis nécessaires préalablement à la lecture du contenu, une explication du sujet et de l’angle abordé, le public cible, etc.

#### Exemples de bonne introduction

Voici un exemple d’une bonne introduction générale : 

> Vous aimeriez créer une application pour bureau, mais vos compétences se limitent aux technologies du Web comme HTML5, CSS3 et JavaScript ? Ça tombe bien, Electron est là !
>
> Electron vous permettra de créer des applications parfaitement intégrées à Windows, Linux ou Mac OS X simplement avec du HTML, du CSS et un peu de JavaScript.
>
> De nombreuses applications comme Atom, Visual Studio Code, ou encore le navigateur Brave sont basées sur Electron et permettent de créer rapidement de belles interfaces parfaitement intégrées au système d’exploitation, quel qu’il soit.
>
> [[information]]
| **Prérequis nécessaires**  
| Connaître un minimum HTML5, CSS3 et JavaScript.  
| Ne pas avoir peur d’utiliser la ligne de commande et savoir l’utiliser basiquement (ouverture, `cd` …).  
| Disposer de Node.js, ou au moins de npm.
|
| **Prérequis optionnels**  
| Connaître les APIs de Node.js.  
| Connaître JavaScript dans sa version ES6.
|
| **Objectifs**  
| Découvrir Electron, ainsi que certaines APIs qui lui sont spécifiques.  
| Savoir utiliser `electron-packager` pour distribuer ces applications.
Source:[Vos applications avec Electron - un tutoriel de Bat'](https://zestedesavoir.com/tutoriels/996/vos-applications-avec-electron/)

Comme vous le voyez, tous les éléments d’une bonne introduction générale sont présents :

- Elle doit donner envie de lire, motiver le lecteur ;
- Elle doit être relativement courte : une introduction de 10 paragraphes redondants raterait sa cible, cela ne donne pas envie de lire ;
- Elle introduit clairement le sujet abordé par le contenu ;
- Les prérequis et objectifs éventuels sont indiqués.

L’introduction des chapitres compte aussi beaucoup, ne la négligez pas. Ici, il suffit d’introduire les notions abordées de manière succincte et de soulever l’importance des notions qui suivront. Soyez simples, mais efficaces. Ne développez pas les notions dans l’introduction du chapitre, c’est dans le corps de celui-ci qu’il sera nécessaire de le faire.

Exemple :

> Il n’est pas dans ce chapitre question de régler la succession de votre grand-tante par alliance, mais de nous intéresser à l’extension de classes.
> 
> Imaginons que nous voulions définir une classe `Admin`, pour gérer des administrateurs, qui réutiliserait le même code que la classe `User`.
> Tout ce que nous savons faire actuellement c’est copier/coller le code de la classe `User` en changeant son nom pour `Admin`.
> 
> Nous allons maintenant voir comment faire ça de manière plus élégante, grâce à l’héritage. Nous étudierons de plus les relations entre classes ansi créées.
> 
> Nous utiliserons donc la classe `User` suivante pour la suite de ce chapitre.
> 
> ```python
> class User:
>     def __init__(self, id, name, password):
>         self.id = id
>         self.name = name
>         self._salt = crypt.mksalt()
>         self._password = self._crypt_pwd(password)
> 
>     def _crypt_pwd(self, password):
>         return crypt.crypt(password, self._salt)
> 
>     def check_pwd(self, password):
>         return self._password == self._crypt_pwd(password)
> ```
> 
Source:[Extension et héritage - La programmation orientée objet en Python - un tutoriel d’entwanne](https://zestedesavoir.com/tutoriels/1253/la-programmation-orientee-objet-en-python/3-inheritance/)

### Conclusion générale (et des différents chapitres, s’il y a lieu)

Tout comme l’introduction, la conclusion importe beaucoup. Une bonne conclusion synthétise les grandes lignes du contenu et fourni, *si possible*, des pistes de réflexion pour approfondir la matière apprise (par exemple, des idées pour améliorer la charte graphique de votre site web, d’optimiser les performances de votre base de données, etc.). La conclusion doit comprendre des références pour aller plus loin lorsqu’elles existent (ce qui est pratiquement toujours le cas :) ). 

Exemple : [Énergie solaire : du panneau photovoltaïque au réseau électrique](https://zestedesavoir.com/tutoriels/279/energie-solaire-du-panneau-photovoltaique-au-reseau-electrique/).

Pour les conclusions de chapitres, il n’est pas inhabituel (et c’est même recommandé ;) ) d’annoncer succinctement l’objet abordé dans le chapitre suivant.

[[information]]
| Il arrive très souvent que l’auteur remercie les membres qui l’ont aidé durant le processus de bêta, le validateur et les lecteurs d’une manière générale. Ce n’est pas obligatoire (mais ça fait plaisir) et cela se fait habituellement dans l’introduction générale ou en conclusion de votre contenu. :)

### Pédagogie

Il est très difficile de donner une ligne de conduite qui fonctionne tout le temps en ce qui concerne la pédagogie. Cependant, nous l’avons déjà vu mais il est primordial de le redire, selon le sujet abordé dans votre contenu, vous devez prêter attention à divers éléments : la pertinence des exemples, la fluidité de votre texte et l’évolution logique de votre contenu (plus une lecture est aisée et l’approfondissement du sujet logique, plus le lecteur sera à l’aise pour comprendre le sujet abordé ou l’enseignement du tutoriel), des illustrations lisibles et légendées, la présence de TP, etc.

Ce sont les éléments auxquels les validateurs font inéluctablement attention lors de la validation de votre contenu. Nous vous renvoyons à [ce qui a été dit précédemment sur la pédagogie](https://zestedesavoir.com/contenus/705/le-guide-du-contributeur/828_contribuer-au-contenu/la-redaction/#3-importance-de-la-pedagogie) pour de plus amples conseils.

## La forme

### La rédaction

Précisons d’emblée que votre contenu doit évidemment être rédigé en français. *Eh* oui, et même rédigé dans un français relativement acceptable, c’est-à-dire sans fautes d’orthographe. Evitez donc le langage SMS (et ses « dérivés »). Vous devez donc avoir une bonne orthographe (si ce n’est pas le cas, faites-vous relire par une autre personne et profitez de la bêta pour améliorer le plus possible votre texte **avant** de l’envoyer en validation) !

[[information]]
| Il existe également nombre de logiciels qui favorisent la correction orthographique et typographique (pour un petit rappel typographique, vous pouvez lire [cet article](https://zestedesavoir.com/articles/2071/introduction-a-lorthotypographie-1/) rédigé par [qwerty](https://zestedesavoir.com/membres/voir/qwerty/)) , voire qui comportent des dictionnaires. Les navigateurs disposent aussi de correcteurs. Citons, [Antidote](https://www.antidote.info/) (payant) ou encore [Grammalecte](http://www.dicollecte.org/grammalecte/) (gratuit). Il existe également des correcteurs en ligne, à la manière de bonpatron.com.

[Comme vu au début de ce guide](https://zestedesavoir.com/contenus/705/le-guide-du-contributeur/828_contribuer-au-contenu/5467_generalites/#1-un-mot-sur-le-zmarkdown-1), Zeste de Savoir utilise un langage de formatage de texte très proche du Markdown, auquel ont été ajouté quelques extensions courantes. Ce langage est utilisé sur ZdS pour écrire les tutoriels, les articles, les messages de forums, les messages privés, etc.

Savoir l’utiliser est un impératif pour la rédaction et la publication de vos contenus.

### Remarque concernant les images et leurs légendes

N’hésitez pas à utiliser des images dans vos tutoriels : c’est le meilleur moyen pour rompre un peu le rythme et illustrer vos propos. 

Le formatage markdown d’une image est le suivant : 

```text
![Texte alternatif de l’image](url_vers_la_source)
```

Très souvent, l’auteur a tendance à confondre le **texte alternatif** de l’image (équivalent de l’attribut `alt` en HTML) avec la **légende**.

[[attention]]
| Dans tous les cas, le texte alternatif **doit** être renseigné. Il sert à apporter la même information que l’image si celle-ci ne peut être chargée ou bien ne peut être vue (notamment pour les synthétiseurs vocaux pour les non-voyants).

Exemple : 

![hiéroglyphes dessinés sur un papyrus](http://upload.wikimedia.org/wikipedia/commons/9/9d/Papyrus_Ani_curs_hiero.jpg)

Mais ce n’est toutefois pas une légende et de ce fait, il n’est pas possible d’y ajouter un lien vers une autre source. 

Il est possible d’utiliser une légende, en utilisant le mot-clé `Figure:`, de la même façon que pour les légendes de tableaux (`Table:`) ou blocs de code (`Code:`) :

```text hl_lines="2"
![hiéroglyphes dessinés sur un papyrus](http://upload.wikimedia.org/wikipedia/commons/9/9d/Papyrus_Ani_curs_hiero.jpg)
Figure: Cette illustration représente des hiéroglyphes dessinés sur un papyrus. Et vous pouvez dès lors utiliser des [liens](exemple.com) !
```
Résultat :

![hiéroglyphes dessinés sur un papyrus](http://upload.wikimedia.org/wikipedia/commons/9/9d/Papyrus_Ani_curs_hiero.jpg)
Figure: Cette illustration représente des hiéroglyphes dessinés sur un papyrus. Et vous pouvez dès lors utiliser des [liens](#4-criteres-de-validation) !

[[attention]]
| Vous devez héberger vos images sur Zeste de Savoir, c’est impératif. Comme ça, elles resteront toujours présentes au côté de votre contenu (et pour rappel, une galerie est automatiquement créée pour chaque contenu afin d’y héberger vos images).

### Qualité du code

Les extraits de code et script **doivent** être testés, bien commentés, structurés et indentés. Attention toutefois, trop de commentaires tue les commentaires ! Si vous mettez des commentaires, mettez le strict nécessaire, sinon la lisibilité du code ne sera pas optimale. Nous y sommes très attentifs. 

#### Exemple de code 

```python
def attribut_optimal(self, ID3=True):
    """
        retourne un str avec le nom de l’attribut à tester
    """
    max, ret = float("-inf"), ""
    #pour chaque attribut
    for attribut in self.liste_attributs:
        if ID3:
            gain = self.gain_entropie(attribut)
        else:
            gain = self.ratio_gain(attribut)
        #si le gain d’entropie est le plus grande
        if gain >= max:
            #on le garde en mémoire
            max, ret = gain, attribut
    #et on le retourne
    return ret
```
Code: Exemple tiré de [ce tutoriel](https://zestedesavoir.com/tutoriels/962/les-arbres-de-decisions/) de Bermudes.

[[information]]
| Pour rappel, vous pouvez (et il est souhaitable de le faire) mettre en évidence des lignes de code pour appuyer votre explication. [N’hésitez pas à consulter l’aide markdown sur ce point](https://zestedesavoir.com/tutoriels/249/rediger-sur-zds/#6-4780_code).
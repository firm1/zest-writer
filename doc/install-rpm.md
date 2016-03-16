## Installation sous les systèmes Fedora/CentOS/Redhat/etc.

Créez le fichier `/etc/yum.repos.d/zestwriter.repo` et copier le contenu suivant à l'intérieur:

```bash
[zestwriter]
name=zestwriter 
baseurl=http://dl.bintray.com/firm1/rpm
gpgcheck=0
enabled=1
```

Lancer la commande suivante :

```bash
yum install zestwriter
```

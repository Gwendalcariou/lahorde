## Stack :

- Java 21+
- JavaFX
- Gradle
- Sauvegardes JSON (Jackson / Gson)

## Gameplay

### Temps en jeu

- Temps (minutes/heures) qui s’écoule à chaque action
- Plus on a d'énergie, moins les actions coûtent cher en temps (Sauf certains actions). La fatigue ne baisse pas pour les tâches basiques et on peut trade de la fatigue quand on a plus d'énergie mais un gros taux de fatigue peut entrainer des courbatures, des blessures ou même un malaise.
- Plus le temps passe, plus la "difficulté" du jeu augmente

### Ressources

- Eau
- Nourriture
- Matériaux
- Médicaments
- Armes Blanches
- Armes à feu
- Munitions
- Vêtements
- Objets (Ex : Machine à coudre, TV, Radio, matériel pêche...)

**NB :** Les Objets ne sont pas inutiles, ils permettent de créer quelque chose, exemple : la machine à coudre permet d'apprendre la couture, la TV si électricité de perdre beaucoup plus facile le stress...

### Statistiques du joueur

- Points de vie
- Energie
- Fatigue
- Relations avec les PNJ
- Santé mentale (Si elle est haute, ça peut mener à une depression)

### La ville fortifiée (Base)

- Zones d'habitations (pour le joueur et les PNJ)
- Puit (au départ plein puis vide à partir d'un moment)
- Zone de stockage (Capacité max)
- Atelier (Crafts / Building / Upgrade)
- Infirmerie
- Zone de commerce (Troc avec les PNJ)

### PNJ

Ils agissent indépendamment du joueur et dépendent du leader du groupe. Il y a une élection toutes les 30 jours et le joueur peut y participer, s'il le devient il peut donner des directives aux autres PNJ (Davantage d'explorations, axer les améliorations sur la défense...).
Les PNJ peuvent mourir de différentes façons (ne reviennent pas d'exploration, maladie, depression...) mais peuvent également augmenter (naissances ou sauvetages de réfugiés).
Chaque PNJ dispose comme le joueur d'un inventaire et d'un logement donc s'il meurt, son logement est mis en vente et le joueur peut participer aux enchères si ça l'intéresse, et si on récupère le cadavre, on peut alors récupérer son inventaire qui sera remis dans les stocks des zones de stockages.

### Relations entre Joueur et PNJ

Les relations Joueur-PNJ détermine comment les PNJ réagissent à travers les actions du Joueur (ex : marchandage...)

### Hordes

Une jauge de menace de 0 à 100 et qui monte avec le temps ou certaines actions (exploration, crafts qui sont voyants/font du bruits...) mais également qui peut baisser avec des actions (faire du bruit loin de la base, élimination des mini-hordes proches de la base...). Quand elle dépasse 100, elle retourne à 0 mais une horde s'abat sur la ville dépendant du temps de jeu.

### Métiers
Chaque personnage a un métier, joueur comme PNJ qui donne quelques avantages ou désavantages : 
- Policier : Avantage Arme à feu + Un peu plus d'énergie
- Diéteticien : Contrôle mieux ses apports caloriques et a moins besoin de manger
- ...

## Actions du joueur

### Dans la base 
- Se reposer (Baisse fatigue, Récupération énergie/santé mentale en fonction des activités choisies)
- Boire/Manger (Ration d'eau via puit, deux repas par jour via l'entrepôt au départ mais ça peut changer plus tard)
- Discuter avec les PNJ
- Marchander avec les PNJ
- Fabriquer des petites recettes
- Fabriquer/Améliorer la ville grâce à l'atelier
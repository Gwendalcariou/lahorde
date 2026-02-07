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

- Se reposer/Dormir (Baisse fatigue, Récupération énergie/santé mentale en fonction des activités choisies)
- Boire/Manger (Ration d'eau via puit, deux repas par jour via l'entrepôt au départ mais ça peut changer plus tard)
- Discuter avec les PNJ
- Marchander avec les PNJ
- Fabriquer des petites recettes
- Fabriquer/Améliorer la ville grâce à l'atelier

### Dehors

- Exploration (Coût en énergie/fatigue/temps) et permet de loot certaines zones ou de tomber sur des évenements
- Manger/Boire en fonction de que le joueur a apporté dans son sac ou loot pendant ses phases d'exploration
- Dormir/Patienter (Attention à trouver un endroit "safe", sinon on peut se retrouver avec une surprise dans la nuit)

## Carte

### Exploration

#### Events

- Rencontre avec des survivants
- Croise une horde
- Bruit alerte des zombies
- Zone de loot remplie (+ de loot que ce qui aurait du)
- Chasse

etc...

#### Zones explorations

- Zone Commerciale (Vetements basiques, objets divers, nourriture, eau, un peu de matériaux)
- Zone Entrepôt (Objets divers, beaucoup de matériaux, Arme blanche, quelques médicaments)
- Zone d'habitation (Objets divers, nourriture, eau, armes blanches, vetemens basiques, quelques médicaments, arme à feu (rare), quelques munitions si arme à feu et quelques matériaux)
- Zone médicale (Un peu de nourriture et d'eau, beaucoup de médicaments)
- Zone Policière (Vêtement assez solide (ex : tenue anti-émeute), armes à feu (bcp armes de poing et quelques armes lourdes), des munitions, armes blanches, un peu d'eau/nourriture/médicaments)
- Zone militaire (Vêtements lourds, armes à feu de tout type, beaucoup de munitions, médicaments, nourriture et eau, matériaux)
- Zone libre (Rien à part la campagne et donc la chasse y est abondante et on peut y trouver quelques maisons de campagne qui peuvent avoir le même loot que les zones d'habitations mais c'est rare)

### Base

#### Events

- Eau du puit contaminée (si le puit pas encore vide)
- Disparition d'objets du stockage
- Des survivants frappent à la porte de la ville
- Des courriers dans la boîte aux lettres (chaque logement à une boîte au lettre et chaque pnj ou joueur peut en envoyer)
- Panne d'électricité

etc...

#### Zones

- Zones d'habitations (pour le joueur et les PNJ)
- Puit (au départ plein puis vide à partir d'un moment)
- Zone de stockage (Capacité max)
- Atelier (Crafts / Building / Upgrade)
- Infirmerie
- Zone de commerce (Troc avec les PNJ)
- Garage pour les véhicules

## Règles

### Actions

Chaque action a un coût en énergie (ou un gain d'énergie), une durée et un risque. La plupart ont un risque 0.
L'échelle de risque va de 0 à 5 :

- 0 : Il ne se passe rien
- 1 : Il peut se passer quelque chose pendant l'action mais très généralement positif
- 2 : Il peut se passer quelque chose, souvent positif mais parfois mitigé
- 3 : il peut se passer quelque chose, souvent mitigé et parfois négatif
- 4 : Il va sans doute se passer quelque chose, quelque fois mitigé mais surtout négatif
- 5 : Il va sûrement se passer quelque chose et ce sera sans aucun doute quelque chose de négatif

L'évènement lié au risque peut être soit un évènement qui va influencer à l'instant T (Ex : Une action fait du bruit -> une horde à proximité a entendu et arrive), ou influencer sur le temps (Ex : Un travail technique baclé -> entraîne des problèmes avec l'électricité plus tard)

### Events aléatoires

Comment les events peuvent apparaître ? Sur certaines actions, (hors risque) certaines peuvent mener à des events aléatoires. Selon moi comment ça devrait être calculé ? Si on fait l'action qui peut mener à un event alors on fait un premier tirage de proba et si alors on est bon, on pioche aléatoirement dans le bdd des events liés à cette action. Ex :
50% de chance d'avoir un event lié à une action, si on fait 49 on a un event aléatoire(négatif/positif), si on fait 51 alors on a pas d'event.

### Combats

On a pas vraiment de combats, plutôt des choix face à des situations dangereuses qui ont une certaine probabalité de réussir ainsi que des malus/bonus associés

### Conditions de défaite / victoire

#### Défaite

- Si PV <= 0 alors le personne s'effondre sur le sol et a une chance de mourir si personne ne s'occupe de lui dans les heures qui suivent l'incident.
- Si le personnage se fait mordre et que la partie du corps n'est pas amputé rapidement (si mordu dans une zone non amputable, se transforme dans les jours qui suivent en passant par une phase de transformation peu agréable)
- Le Personnage n'a pas bu depuis 5 jours ou n'a pas mangé depuis 20 jours et donc termine par mourir
- Les défenses de la base sont détruites par une horde ou un groupe de pillards entrainent d'énormes malus sur les personnages concernés et donc s'échapper vivant ça devient très compliqué mais il est tout de même possible de s'enfuir et de trouver un nouvel endroit en zone Libre pour créer une nouvelle base mais cette fois-ci on part de rien contrairement au départ.
- Laisser les portes de la ville ouvertes la nuit, ça implique que les zombies ou les pillards peuvent rentrer et donc on se retrouve dans la même situation que ci-dessus.

#### Victoire

- La base actuelle atteint le maximum de toutes les améliorations possibles, on peut en conclure que la base sera assez prospère pour survivre dans le temps
- Trouver une zone sécurisée gérée par les militaires (très rare)
- Recevoir un appel radio et se faire sauver par l'armée

## Objets

### Eau

- Ration d'eau (1L) [1KG] qu'on obtient via le puit ou un récupérateur d'eau de pluie et qu'on stocke dans une gourde
- Pack de bouteilles d'eau (6 bouteilles de 1,5L) [9KG]
- Eau trouble qu'on peut trouver dans la nature (flaque, étang...) et qu'on peut purifier en la chauffant. On peut la stocker dans les bouteilles ou la gourde

### Nourriture

- Boîtes de conserve
- Légumes et fruits
- Viande

### Médicaments

- Anti-inflammatoire
- Antibiotiques
- Médicaments (généraliste ça gère toux, maux de tête...)
- Bandage
- Alcool
- Antidépresseur

### Armes blanches

#### Contandantes

- Outils (Ferme, ouvrier...)
- Batte de baseball
- Matraque

#### Coupantes

- Couteau de cuisine
- Couteau de survie
- Machette

### Armes à feu

#### Armes de poings

- Glock 19 (9mm)
- Beretta 92 (9mm)
- FN Five-SeveN (5.7mm)
- Desert Eagle (.44 Magnum)
- Smith & Wesson M&P (.38 S&W)

#### Fusil de chasse

- Winchester Model 1887 (Calibre 10)
- Verney-Carron Sagittaire (Calibre 12)

#### Fusil à pompe

- Remington 870 (Calibre 12)
- SPAS 12 (Calibre 12)

#### Pistolet-mitrailleur

- MP5A3 (9mm)
- P90 (5.7mm)
- MP7A1 (4.6mm)

#### Fusil d'assaut

- Famas (5.56mm)
- M16 (5.56mm)
- AKM (7.62mm)

### Munitions

- 9mm
- 5.7mm
- .44 Magnum
- .38 S&W
- Calibre 10
- Calibre 12
- 4.6mm
- 5.56mm
- 7.62mm

### Vêtements

#### Basiques

- Chapeaux
- T-shirt
- Pull / Sweat
- Manteau
- Pantalon
- Chaussettes
- Chaussures
- Sacs à main ou à dos basiques

#### Moyens

Equipements policiers, tenues d'ouvriers, gros sacs...

#### Lourds

Equipements militaires, équipements pompiers, sacs de survies...

#### Objets

- TV
- Radio
- Machines à coudre
- Camping
- Cigarettes
- Instruments de musique
- Ustensiles cuisine
- A COMPLETER

#### Matériaux

- Bois
- Métal
- "Electronique"
- A COMPLETER

### Véhicules

#### Basiques

- Voiture 2 places
- Camionnette 2 places
- Voiture 5 places
- Voiture Sportive 2 places

#### Lourd

- Muscle car 5 places
- Jeep blindée 5 places
- Camion transport armée 20 places

## Crafts

A compléter

## V0 — “Text UI”

- Un écran, des boutons d’action, un log d’événements.

- Quelques ressources, 1 jauge menace, 1 défense base

- Exploration = un tirage loot + risque blessure

- Sauvegarde/chargement

## V1 — UI mieux + progression

- 2–3 améliorations (atelier, stockage, défense)

- Quelques objets craftables

- Zones d’exploration

## V2 — profondeur légère

- Équipements (arme, sac → augmente loot, etc.)

- Événements spéciaux (marchand, survivant, tempête)

- Petits choix (prendre un risque, abandonner du loot pour survivre)

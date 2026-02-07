# Jeu de survie solo – Document de design

> Jeu solo de survie et de gestion inspiré de *La Horde* / *Hordes*, sans découpage strict en jours.
> Le joueur peut enchaîner des actions tant que son **énergie**, son **temps** et la **menace** le permettent.

---

## Stack technique

- Java 21+
- JavaFX
- Gradle
- Sauvegardes JSON (Jackson ou Gson)

---

# BOUCLE DE JEU CENTRALE (COMMUNE À TOUTES LES VERSIONS)

- Chaque action consomme ou génère : **temps**, **énergie**, **fatigue**
- Le temps qui passe augmente progressivement la **difficulté globale**
- Une **jauge de menace (0–100)** monte avec le temps et certaines actions
- À 100 → attaque de horde → la jauge retombe à 0
- Les actions ont un **risque (0 à 5)** pouvant déclencher des événements immédiats ou différés

---

# RÈGLES FONDAMENTALES

## Actions

Chaque action possède :
- un coût ou gain d’énergie
- une durée
- un niveau de risque (0–5)

### Échelle de risque

- 0 : Aucun événement
- 1 : Rare, généralement positif
- 2 : Souvent positif, parfois mitigé
- 3 : Mitigé à négatif
- 4 : Majoritairement négatif
- 5 : Quasi-certainement négatif

Les conséquences peuvent être :
- **immédiates** (blessure, bruit, horde proche)
- **différées** (panne future, moral dégradé, menace accrue)

---

## Système d’événements aléatoires

- Certaines actions sont marquées comme *éligibles aux événements*
- Un premier tirage détermine si un événement survient
- Si oui, un événement est tiré dans la base liée à l’action

---

## Combats

Il n’existe pas de combats classiques.

À la place :
- Situations dangereuses
- Choix (fuir / forcer / négocier / se cacher…)
- Probabilités de succès
- Conséquences immédiates et différées

---

# VERSION V0 — PROTOTYPE JOUABLE

## Objectif

Valider la boucle de jeu, la tension et le plaisir sans complexité excessive.

---

## Ressources (V0)

- Eau
- Nourriture
- Matériaux
- Médicaments

---

## Statistiques du joueur (V0)

- Points de vie
- Énergie
- Fatigue
- Santé mentale

---

## Base (V0)

- Zone d’habitation (joueur)
- Puits (plein au départ)
- Stockage (capacité limitée)
- Atelier basique

---

## Actions du joueur (V0)

### Dans la base

- Dormir / se reposer
- Boire / manger
- Fabriquer des recettes simples
- Améliorer légèrement la base

### Dehors

- Explorer une zone
- Manger / boire depuis le sac
- Patienter / dormir (risque élevé)

---

## Carte et exploration (V0)

### Zones disponibles

- Zone d’habitation
- Zone commerciale
- Zone entrepôt
- Zone libre (chasse)

### Événements possibles

- Bruit attirant des zombies
- Zone de loot abondante
- Blessure mineure
- Rencontre neutre

---

## Menace et hordes (V0)

- La menace augmente avec le temps et certaines actions
- À 100 : attaque de horde sur la base
- Les défenses réduisent les pertes

---

## Conditions de défaite (V0)

- PV ≤ 0 sans aide rapide
- Absence d’eau prolongée
- Absence de nourriture prolongée
- Base détruite par une horde

---

## Conditions de victoire (V0)

- Survivre un nombre de jours donné

---

# VERSION V1 — PROGRESSION ET PNJ

## Ajouts principaux

- PNJ autonomes
- Relations sociales
- Métiers
- Équipements simples

---

## Ressources supplémentaires (V1)

- Armes blanches
- Armes à feu (catégories)
- Munitions
- Vêtements

---

## PNJ (V1)

- Inventaire personnel
- Logement individuel
- Mort permanente possible
- Peuvent explorer, tomber malades, mourir

### Relations

- Les relations influencent :
  - le troc
  - l’aide reçue
  - les réactions aux décisions du joueur

---

## Métiers (V1)

Chaque personnage possède un métier avec bonus **et malus** :

- Policier : + armes à feu / − relations civiles
- Médecin : + soins / − combat
- Ouvrier : + construction / fatigue accrue
- Artisan : crafts avancés / besoin d’outils

---

## Base (V1)

- Infirmerie
- Zone de commerce
- Améliorations avancées

---

## Événements enrichis (V1)

- Survivants demandant refuge
- Vols dans le stockage
- Pannes électriques
- Courriers et messages

---

## Victoires supplémentaires (V1)

- Base totalement améliorée

---

# VERSION V2 — PROFONDEUR ET SIMULATION

## Ajouts majeurs

- Leadership et élections
- Véhicules
- Objets complexes
- Narratif émergent

---

## Leadership et PNJ (V2)

- Élections tous les 30 jours
- Le leader peut donner des directives globales
- Tensions politiques internes

---

## Objets complexes (V2)

- Radio
- TV
- Générateur
- Panneaux solaires
- Instruments de musique
- Jeux de société

Effets :
- réduction du stress
- amélioration morale
- accès à des fins alternatives

---

## Matériaux avancés (V2)

- Tissu
- Plastique
- Électronique
- Carburant

---

## Véhicules (V2)

Chaque véhicule possède :
- Capacité
- Bruit
- Consommation
- Risque de panne
- Difficulté de réparation

---

## Conditions de victoire avancées (V2)

- Zone militaire sécurisée
- Sauvetage par l’armée
- Fondation d’une nouvelle base prospère

---

# NOTE DE DESIGN IMPORTANTE

Toute mécanique doit interagir avec au moins **un** de ces piliers :

- Temps
- Énergie
- Risque
- Menace

Sinon, elle ne doit pas exister dans la version actuelle.


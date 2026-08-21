# Freelance Linker

## Présentation

Freelance  est une application web qui permet de mettre en relation des clients avec des freelancers dans le domaine IT.

La plateforme permet de gérer des missions, de consulter des profils freelancers, de créer des évaluations client, de recommander des freelancers et de classer les freelancers selon un score final.

Ce projet a été réalisé dans le cadre de la session 2 ING2.

---

## Étudiant

- Hamza EL AMRANI — Session 2 ING2

---

## Fonctionnalités réalisées

Dans ma partie, j’ai travaillé sur :

- l’algorithme d’évaluation des freelancers ;
- le calcul du score final ;
- la gestion de la tendance du freelancer ;
- la gestion de la disponibilité ;
- l’algorithme de recommandation ;
- l’affichage du ranking côté administrateur ;
- l’affichage du détail d’un freelancer ;
- l’affichage de l’historique des évaluations ;
- la connexion entre le frontend React et le backend Spring Boot.

---

## Technologies utilisées

### Backend

- Java
- Spring Boot
- Spring Data JPA
- PostgreSQL

### Frontend

- React
- TypeScript
- Tailwind CSS

---

## Algorithme d’évaluation

Le client évalue un freelancer selon plusieurs critères.

| Critère | Pondération |
|---|---:|
| Qualité technique | 30% |
| Communication | 20% |
| Autonomie | 15% |
| Qualité des tests | 15% |
| Livraison | 20% |

Le statut de livraison est transformé en score :

| Statut | Score |
|---|---:|
| ON_TIME | 5 |
| LATE_DELIVERY | 2.5 |
| CANCELLED | 0 |

Formule utilisée :

```txt
Score évaluation =
0.30 × qualité technique
+ 0.20 × communication
+ 0.15 × autonomie
+ 0.15 × qualité des tests
+ 0.20 × livraison
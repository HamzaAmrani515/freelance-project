#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import random
import psycopg2
from faker import Faker
from math import ceil

fake = Faker(["fr_FR"])

# =============================================================================
# 1) PARAMÈTRES DE CONNEXION
# =============================================================================
DB_HOST = "172.31.252.97"
DB_PORT = 5432
DB_NAME = "dev_freelance"
DB_USER = "dev_freelance_user"
DB_PASSWORD = "freelance"

# =============================================================================
# VOLUMÉTRIE
# =============================================================================
NB_CLIENTS = 5
NB_FREELANCERS = 36000  # Insère 36 000 freelances
NB_GERANTS = 2
NB_MISSIONS = 12
NB_CANDIDATURES = 15
NB_EVALUATIONS = 10

# Batch size pour l'insertion groupée (freelancers / compétences)
BATCH_SIZE = 1000

# =============================================================================
# 2) LISTE DE COMPÉTENCES À INSÉRER
# =============================================================================
ALL_COMPETENCES = [
    ("Frontend", "Maîtrise des technologies HTML, CSS, JavaScript, frameworks modernes (React, Angular, Vue.js)"),
    ("HTML/CSS", "Intégration et mise en page web, responsive design"),
    ("React", "Bibliothèque JavaScript pour interfaces utilisateur"),
    ("Angular", "Framework TypeScript pour applications web complexes"),
    ("Vue.js", "Framework JavaScript progressif pour interfaces utilisateur"),
    ("Backend", "Conception d’API RESTful, logique métier côté serveur, gestion des bases de données"),
    ("Node.js", "Plateforme JavaScript côté serveur pour applications évolutives"),
    ("PHP", "Langage de script côté serveur, frameworks MVC (Laravel, Symfony)"),
    ("Python", "Langage polyvalent utilisé en développement web (Django, Flask) et en data science"),
    ("Java", "Langage orienté objet pour applications backend robustes"),
    ("C#", "Langage développé par Microsoft pour applications backend et desktop"),
    ("FullStack", "Compétences à la fois en frontend et backend, capable de gérer l'ensemble du développement web"),
    ("Android", "Développement d’applications mobiles natives (Java/Kotlin)"),
    ("iOS", "Développement mobile sur iPhone/iPad (Swift, Objective-C)"),
    ("Flutter", "Framework cross-platform mobile (Dart) pour applications iOS et Android"),
    ("React Native", "Framework JavaScript pour développement mobile multiplateforme"),
    ("DevOps", "CI/CD, Docker, Kubernetes, automatisation des déploiements, gestion de l'infrastructure"),
    ("Cloud Computing", "Maîtrise des services cloud (AWS, Azure, GCP), déploiement et gestion des ressources"),
    ("Machine Learning", "Développement de modèles prédictifs, Python/Scikit-learn, TensorFlow, PyTorch"),
    ("Data Analysis", "Analyse de données, visualisation, SQL, outils de BI"),
    ("Big Data", "Traitement de grandes quantités de données, Hadoop, Spark"),
    ("Cybersecurity", "Sécurité des systèmes d'information, protection des données, conformité"),
    ("Pentest", "Tests d’intrusion, évaluation des vulnérabilités, sécurité réseau"),
    ("Blockchain", "Technologie des registres distribués, développement de smart contracts"),
    ("Internet of Things (IoT)", "Développement et gestion de dispositifs connectés, communication M2M"),
    ("Artificial Intelligence", "Conception de systèmes intelligents, NLP, vision par ordinateur"),
    ("Augmented Reality/Virtual Reality", "Développement d'applications immersives, Unity, Unreal Engine"),
    ("Project Management", "Gestion de projets IT, méthodologies Agile/Scrum"),
    ("UI/UX Design", "Conception d'interfaces utilisateur, expérience utilisateur, prototypage"),
    ("Quality Assurance", "Tests logiciels, assurance qualité, automatisation des tests"),
    ("SRE (Site Reliability Engineering)", "Maintien de la fiabilité des systèmes, surveillance, incidents"),
    ("Database Administration", "Gestion des bases de données relationnelles et NoSQL, optimisation"),
    ("Network Administration", "Gestion des réseaux, configuration routeurs/switches, sécurité réseau"),
    ("System Administration", "Gestion des systèmes (Linux, Windows), scripts d'automatisation"),
    ("IT Support", "Assistance technique aux utilisateurs, résolution de problèmes matériels/logiciels"),
    ("Technical Writing", "Rédaction de documentation technique, guides utilisateurs"),
    ("SEO/SEA", "Optimisation moteurs de recherche, campagnes publicitaires en ligne"),
    ("Digital Marketing", "Stratégies de marketing en ligne, réseaux sociaux, analyse de performance"),
    ("E-commerce", "Gestion de plateformes e-commerce, intégration de solutions de paiement"),
    ("Game Development", "Développement de jeux vidéo, moteurs de jeu, conception de gameplay"),
    ("Robotics", "Conception et programmation de robots, automatisation industrielle"),
    ("Embedded Systems", "Développement de logiciels pour systèmes embarqués, microcontrôleurs"),
    ("IT Consulting", "Conseil en stratégie informatique, transformation digitale"),
    ("IT Training", "Formation des utilisateurs et des équipes techniques, développement des compétences"),
    ("IT Sales", "Vente de solutions informatiques, gestion relation client"),
    ("IT Recruitment", "Recrutement de talents IT, gestion RH techniques"),
    ("IT Legal Compliance", "Conformité aux réglementations informatiques, RGPD"),
    ("IT Financial Management", "Gestion budgétaire des projets IT, analyse des coûts"),
    ("IT Innovation Management", "Gestion de l'innovation technologique, veille stratégique"),
    ("IT Ethics", "Éthique dans l'utilisation des technologies, responsabilité sociétale"),
    ("IT Sustainability", "Pratiques informatiques durables, réduction de l'empreinte carbone"),
    ("IT Disaster Recovery", "Plans de reprise après sinistre, continuité des activités"),
    ("IT Change Management", "Gestion du changement lors de l'implémentation de nouvelles technologies"),
    ("IT Vendor Management", "Gestion des relations avec les fournisseurs de technologies"),
    ("IT Asset Management", "Gestion des actifs informatiques, suivi licences et équipements"),
    ("IT Service Management", "Gestion des services IT, ITIL, amélioration continue"),
    ("IT Governance", "Gouvernance des systèmes d'information, alignement stratégique"),
    ("IT Risk Management", "Identification et gestion des risques informatiques"),
    ("IT Audit", "Audit des SI, évaluation des processus"),
    ("IT Procurement", "Achat de solutions et services informatiques, négociation de contrats"),
    ("IT Infrastructure Management", "Gestion des infrastructures IT, data centers, virtualisation"),
    ("IT Research and Development", "R&D en technologies de l'information"),
    ("IT Entrepreneurship", "Création et gestion de startups technologiques, innovation"),
    ("IT Policy Development", "Élaboration de politiques informatiques, directives organisationnelles"),
    ("IT User Experience", "Amélioration de l'expérience utilisateur des systèmes et applications"),
    ("IT Accessibility", "Conception de solutions accessibles, conformité aux normes d'accessibilité"),
    ("IT Localization", "Adaptation logicielle pour marchés linguistiques/culturels"),
    ("IT Data Privacy", "Protection de la vie privée, conformité aux réglementations sur les données"),
    ("IT Data Governance", "Gestion de la qualité et de l'intégrité des données"),
    ("IT Data Architecture", "Conception de l'architecture des données, modélisation"),
    ("IT Data Integration", "Intégration de sources de données multiples, ETL"),
    ("IT Data Warehousing", "Conception et gestion d'entrepôts de données"),
    ("IT Data Mining", "Exploration de données pour découvrir des patterns et insights"),
    ("IT Data Visualization", "Création de visualisations pour représenter les données"),
    ("IT Data Quality", "Assurance de la qualité des données, nettoyage et validation"),
]

# =============================================================================
# 3) PROFILS FREELANCERS
# =============================================================================
PROFILS_FREELANCERS = [
    "Développement Web",
    "Développement Mobile",
    "DevOps & Cloud",
    "Data Science & IA",
    "Sécurité Informatique",
    "Design UI/UX",
    "Gestion de Projet IT",
    "Analyse de Données",
    "Administration Systèmes & Réseaux",
    "Consultant en Cybersécurité",
    "Spécialiste en Blockchain",
    "Ingénieur en Intelligence Artificielle",
    "Développeur en Réalité Augmentée/Virtuale",
    "Expert en Internet des Objets (IoT)",
    "Formateur IT",
    "Rédacteur Technique",
    "Spécialiste en Marketing Digital",
    "Analyste en Big Data",
    "Architecte Logiciel",
    "Ingénieur en Systèmes Embarqués"
]

# =============================================================================
# 4) GESTION DU NOMBRE DE COMPÉTENCES PAR EXPÉRIENCE
# =============================================================================
def compute_competence_range(experience: float) -> (int, int):
    """
    Logique pour le nombre de compétences en fonction de l'expérience.
    """
    if experience < 2:
        return (2, 3)
    elif experience < 5:
        return (3, 5)
    elif experience < 8:
        return (5, 8)
    elif experience < 10:
        return (8, 10)
    else:
        return (10, 15)

# =============================================================================
# 5) GÉNÉRATION D'ADRESSES E-MAIL UNIQUES
# =============================================================================
EMAIL_CACHE = set()

def get_unique_email():
    """
    Génère un e-mail via faker, et garantit qu'il est unique
    grâce à un set Python.
    """
    while True:
        candidate = fake.email()
        if candidate not in EMAIL_CACHE:
            EMAIL_CACHE.add(candidate)
            return candidate

# =============================================================================
# 6) FONCTIONS D'INSERTION
# =============================================================================
def create_clients(cursor, nb_clients=5):
    """
    Insère `nb_clients` clients de manière simple (un par un).
    Retourne la liste des IDs créés.
    """
    client_ids = []
    for _ in range(nb_clients):
        nom = fake.last_name().replace("'", "''")
        prenom = fake.first_name().replace("'", "''")
        email = get_unique_email().replace("'", "''")
        budget = round(random.uniform(2000, 15000), 2)

        cursor.execute(f"""
            INSERT INTO clients (nom, prenom, email, budget)
            VALUES ('{nom}', '{prenom}', '{email}', {budget})
            RETURNING id;
        """)
        client_ids.append(cursor.fetchone()[0])
    return client_ids

def create_gerants(cursor, nb_gerants=2):
    """
    Insère `nb_gerants` gérants de manière simple (un par un).
    Retourne la liste des IDs créés.
    """
    gerant_ids = []
    for _ in range(nb_gerants):
        nom = fake.last_name().replace("'", "''")
        prenom = fake.first_name().replace("'", "''")
        email = get_unique_email().replace("'", "''")

        cursor.execute(f"""
            INSERT INTO gerants (nom, prenom, email)
            VALUES ('{nom}', '{prenom}', '{email}')
            RETURNING id;
        """)
        gerant_ids.append(cursor.fetchone()[0])
    return gerant_ids

def bulk_insert_competences(cursor):
    """
    Insère toutes les compétences de ALL_COMPETENCES dans la table `competences` (sans doublon).
    Retourne un dict {nom_competence: id_competence}.
    """
    # Récupérer éventuellement ce qui existe déjà en base
    cursor.execute("SELECT lower(nom), id FROM competences;")
    existing = dict(cursor.fetchall())  # { "frontend": <id>, ... }

    compet_name_to_id = {}

    new_compet_insert_rows = []
    for (comp_name, comp_desc) in ALL_COMPETENCES:
        lower_name = comp_name.lower()
        if lower_name in existing:
            # La compétence existe déjà
            compet_name_to_id[comp_name] = existing[lower_name]
        else:
            # On prépare l'insertion
            comp_name_sql = comp_name.replace("'", "''")
            comp_desc_sql = comp_desc.replace("'", "''")
            new_compet_insert_rows.append((comp_name_sql, comp_desc_sql))

    # Insertion en une seule requête des compétences manquantes
    if new_compet_insert_rows:
        values_str = ",".join(
            f"('{row[0]}','{row[1]}')" for row in new_compet_insert_rows
        )
        insert_sql = f"""
            INSERT INTO competences (nom, description)
            VALUES {values_str}
            RETURNING id, nom;
        """
        cursor.execute(insert_sql)
        # On récupère (id, nom)
        for (cid, cname) in cursor.fetchall():
            compet_name_to_id[cname] = cid

    # Compléter compet_name_to_id si certaines compétences étaient déjà existantes
    for (comp_name, comp_desc) in ALL_COMPETENCES:
        if comp_name not in compet_name_to_id:
            compet_name_to_id[comp_name] = existing[comp_name.lower()]

    return compet_name_to_id

def bulk_insert_freelancers(cursor, nb_freelancers=1000, batch_size=1000):
    """
    Insertion groupée de `nb_freelancers` freelances par paquets de `batch_size`.
    Retourne la liste de tous les IDs insérés (dans l'ordre).
    """
    all_freelancer_ids = []
    nb_batches = ceil(nb_freelancers / batch_size)

    for batch_index in range(nb_batches):
        current_batch_size = min(batch_size, nb_freelancers - batch_index * batch_size)
        rows_values = []

        for _ in range(current_batch_size):
            nom = fake.last_name().replace("'", "''")
            prenom = fake.first_name().replace("'", "''")
            email = get_unique_email().replace("'", "''")  # email unique
            experience = round(random.uniform(0.5, 12.0), 1)
            age = random.randint(20, 60)
            gender = random.choice(["HOMME", "FEMME"])
            profil = random.choice(PROFILS_FREELANCERS)
            rows_values.append((nom, prenom, email, experience, age, gender, profil))

        # Construction d'un seul gros INSERT
        values_str = ",".join(
            f"('{r[0]}','{r[1]}','{r[2]}',{r[3]},{r[4]},'{r[5]}','{r[6]}')"
            for r in rows_values
        )

        insert_sql = f"""
            INSERT INTO freelancers (nom, prenom, email, experience, age, gender, profil)
            VALUES {values_str}
            RETURNING id;
        """
        cursor.execute(insert_sql)
        batch_ids = [row[0] for row in cursor.fetchall()]
        all_freelancer_ids.extend(batch_ids)

    return all_freelancer_ids

def bulk_insert_freelancer_competences(cursor, freelancer_ids, compet_name_to_id):
    """
    Associe à chaque freelance un nombre de compétences (en fonction de l'expérience).
    Insertions groupées dans `freelancer_competences`.
    """
    data_to_insert = []

    all_compet_names = list(compet_name_to_id.keys())

    for fid in freelancer_ids:
        # On simule une expérience pour définir le nombre de compétences
        experience = random.uniform(0.5, 12.0)
        min_comp, max_comp = compute_competence_range(experience)
        nb_to_create = random.randint(min_comp, max_comp)

        chosen_competences = random.sample(all_compet_names, min(nb_to_create, len(all_compet_names)))
        for comp_name in chosen_competences:
            comp_id = compet_name_to_id[comp_name]
            data_to_insert.append((fid, comp_id))

    # Insertion par batch
    nb_rows = len(data_to_insert)
    nb_batches = ceil(nb_rows / BATCH_SIZE)

    for batch_index in range(nb_batches):
        start_i = batch_index * BATCH_SIZE
        end_i = min(start_i + BATCH_SIZE, nb_rows)
        sub_data = data_to_insert[start_i:end_i]
        if not sub_data:
            break

        values_str = ",".join(
            f"({row[0]},{row[1]})" for row in sub_data
        )
        sql = f"""
            INSERT INTO freelancer_competences (freelancer_id, competence_id)
            VALUES {values_str};
        """
        cursor.execute(sql)

def create_mission(cursor, client_id):
    """
    Insère une mission et retourne son ID.
    """
    titre = "Mission " + fake.word().capitalize().replace("'", "''")
    description = fake.paragraph(nb_sentences=3).replace("'", "''")
    budget = round(random.uniform(800, 5000), 2)
    duree = f"{random.randint(1, 6)} mois"
    statut = random.choice(["EN_ATTENTE", "ACCEPTEE", "REFUSEE"])

    cursor.execute(f"""
        INSERT INTO missions (titre, description, budget, duree, statut, client_id)
        VALUES ('{titre}', '{description}', {budget}, '{duree}', '{statut}', {client_id})
        RETURNING id;
    """)
    return cursor.fetchone()[0]

def link_mission_competences(cursor, mission_id, competences_ids):
    """
    Associe un ensemble de compétences à une mission.
    """
    for cid in competences_ids:
        cursor.execute(f"""
            INSERT INTO mission_competences (mission_id, competence_id)
            VALUES ({mission_id}, {cid});
        """)

def create_evaluation(cursor, client_id, freelancer_id, mission_id):
    """
    Insère une évaluation.
    """
    note = round(random.uniform(1, 5), 1)
    commentaire = fake.sentence().replace("'", "''")
    date_eval = fake.date_time_this_year().strftime('%Y-%m-%d %H:%M:%S')

    cursor.execute(f"""
        INSERT INTO evaluations (note, commentaire, date_evaluation, client_id, freelancer_id, mission_id)
        VALUES ({note}, '{commentaire}', '{date_eval}', {client_id}, {freelancer_id}, {mission_id});
    """)

def create_candidature(cursor, freelancer_id, mission_id):
    """
    Insère une candidature.
    """
    date_candidature = fake.date_time_this_year().strftime('%Y-%m-%d %H:%M:%S')
    statut = random.choice(["EN_ATTENTE", "ACCEPTEE", "REFUSEE"])
    commentaire = fake.sentence().replace("'", "''")

    cursor.execute(f"""
        INSERT INTO candidatures (date_candidature, statut, commentaire, freelancer_id, mission_id)
        VALUES ('{date_candidature}', '{statut}', '{commentaire}', {freelancer_id}, {mission_id});
    """)

# =============================================================================
# 7) SCRIPT PRINCIPAL
# =============================================================================
def main():
    try:
        conn = psycopg2.connect(
            host=DB_HOST,
            port=DB_PORT,
            dbname=DB_NAME,
            user=DB_USER,
            password=DB_PASSWORD
        )
    except Exception as e:
        print("Erreur de connexion à la base :", e)
        return

    conn.autocommit = False
    cursor = conn.cursor()

    try:
        # 1) Insertion des Clients
        client_ids = create_clients(cursor, NB_CLIENTS)

        # 2) Insertion des Gérants
        gerant_ids = create_gerants(cursor, NB_GERANTS)

        # 3) Insertion (unique) de toutes les compétences
        compet_name_to_id = bulk_insert_competences(cursor)

        # 4) Insertion groupée de 36 000 freelances (avec emails uniques)
        freelancer_ids = bulk_insert_freelancers(cursor, NB_FREELANCERS, BATCH_SIZE)

        # 5) Association des compétences aux freelances
        bulk_insert_freelancer_competences(cursor, freelancer_ids, compet_name_to_id)

        # 6) Création de Missions
        mission_ids = []
        for _ in range(NB_MISSIONS):
            cl = random.choice(client_ids)
            mid = create_mission(cursor, cl)
            mission_ids.append(mid)

        # 7) Associer quelques compétences (au hasard) à chaque mission
        all_compet_ids = list(compet_name_to_id.values())
        for mid in mission_ids:
            how_many = random.randint(3, 7)
            if all_compet_ids:
                chosen = random.sample(all_compet_ids, min(how_many, len(all_compet_ids)))
                link_mission_competences(cursor, mid, chosen)

        # 8) Création d'Evaluations
        for _ in range(NB_EVALUATIONS):
            c = random.choice(client_ids)
            f = random.choice(freelancer_ids)
            m = random.choice(mission_ids)
            create_evaluation(cursor, c, f, m)

        # 9) Création de Candidatures
        for _ in range(NB_CANDIDATURES):
            f = random.choice(freelancer_ids)
            m = random.choice(mission_ids)
            create_candidature(cursor, f, m)

        # Commit final
        conn.commit()
        print("Insertion terminée avec succès !")

    except Exception as e:
        conn.rollback()
        print("Erreur pendant l’insertion, rollback :", e)
    finally:
        cursor.close()
        conn.close()

if __name__ == "__main__":
    main()

# 🤖 DashboardIoT – Système de Simulation IoT en Java

## 📌 Description
DashboardIoT est une application Java simulant un réseau d’objets connectés (IoT) dans une maison intelligente.  
Elle permet de gérer des capteurs et actionneurs, de suivre leurs états en temps réel et d’exporter les données pour analyse.

Le projet est développé en Programmation Orientée Objet (POO) et suit une architecture MVC (Model – View – Controller).

## 🎯 Objectifs du projet
- Gérer un réseau d’appareils IoT (ajout, suppression, connexion)
- Simuler des capteurs (température, humidité, qualité d’air…)
- Contrôler des actionneurs (lumière, caméra, ventilation…)
- Stocker l’historique des mesures et actions
- Exporter les données (TXT & Excel)
- Assurer la persistance des données (sérialisation Java)

## 🏗️ Architecture du projet (MVC)

Model       → logique métier (appareils, capteurs, actionneurs)  
View        → interface graphique Java Swing  
Controller  → communication entre View et Model  

## ⚙️ Fonctionnalités principales

### 📡 Gestion des appareils
- Ajouter / supprimer un appareil
- Connecter / déconnecter un appareil
- Vérifier l’état en temps réel

### 🌡️ Capteurs IoT
- Température 🌡️
- Humidité 💧
- Qualité de l’air 🌫️
- Débit d’eau 🚰

### 💡 Actionneurs
- Lumière (ON/OFF)
- Ventilation (niveaux)
- Volets (ouvrir/fermer)
- Caméra IP (activation)

## 🖥️ Interface utilisateur (Swing)

L’application propose une interface graphique permettant de :
- Visualiser les appareils
- Lancer des actions
- Consulter les historiques
- Voir les logs système


## 📤 Export des données

### 📄 Export TXT
- Format lisible
- Contient état des appareils et historique

### 📊 Export Excel
- Données structurées en tableau
- Facile à analyser

## 💾 Persistance des données

- Sauvegarde automatique via sérialisation Java
- Chargement automatique au démarrage
- Fichier binaire (.dat)

## 🧠 Concepts utilisés
- Programmation Orientée Objet (POO)
- Héritage
- Polymorphisme
- Encapsulation
- Gestion des exceptions
- Architecture MVC
- Java Swing
- Sérialisation
- JSON
- Export TXT / Excel

## 📦 Structure du projet

DashboardIoT  
│  
├── model  
│   ├── AppareilConnecte  
│   ├── Capteurs (Temperature, Humidite, Air, DebitEau)  
│   ├── Actionneurs (Lumiere, Ventilation, Volet, CameraIP)  
│   ├── GestionIoT  
│   └── Exceptions  
│  
├── controller  
│   ├── IoTController  
│   ├── ConfigLoader  
│   ├── ExcelExporter  
│   ├── TextExporter  
│   ├── LectureJSON  
│   └── GestionPersistance  
│  
├── view  
│   └── Interface Swing (Dashboard)  
│  
└── resources  
    ├── config.properties  
    └── data.json  

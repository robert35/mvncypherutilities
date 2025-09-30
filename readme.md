# MVNCypherUtilities

Extension Maven Core pour le déchiffrement automatique de propriétés Maven sécurisées.

## Résumé

**MVNCypherUtilities** permet de stocker vos mots de passe et secrets Maven de manière chiffrée dans `settings.xml`, puis les déchiffre automatiquement lors du build.

### 🔐 Zéro mot de passe en clair dans vos projets

Plus aucun mot de passe en clair dans vos `pom.xml` ou fichiers de configuration versionnés ! Tous les secrets restent chiffrés et sécurisés dans `~/.m2/settings.xml`.

**Workflow sécurisé :**
```
Projet Git (pom.xml)          →  Aucun mot de passe visible ✅
        ↓
~/.m2/settings.xml            →  Mots de passe chiffrés #{...} ✅
        ↓
~/.m2/settings-security.xml   →  Clé maître chiffrée ✅
        ↓
Clé USB (optionnel)           →  Clé maître accessible uniquement aux autorisés ✅
```

### 💡 Cas d'usage : Déploiements contrôlés avec clé USB

**Problème classique :** Comment autoriser uniquement certaines personnes à déployer en production ?

**Solution élégante :**
1. **Développeurs** : travaillent avec des mots de passe chiffrés, peuvent build/test localement
2. **Équipe DevOps** : reçoit une clé USB contenant le mot de passe maître
3. **Déploiement** : impossible sans brancher la clé USB
4. **Gestion** : révoquer l'accès = récupérer la clé USB

### 🎨 Interface graphique incluse (mvndecryptui)

L'outil **mvndecryptui** (interface Swing) permet de :
- Chiffrer/déchiffrer vos mots de passe en masse
- Gérer vos `settings.xml` et `settings-security.xml` visuellement
- Mettre à jour tous vos secrets en quelques clics
- Accessible uniquement avec la clé USB (si configurée)

**En résumé :**
- Chiffrez vos mots de passe une fois avec `mvn --encrypt-password`
- Stockez-les sous forme `#{...}` dans `settings.xml`
- L'extension les déchiffre automatiquement au runtime
- Compatible avec tous les plugins Maven (Liquibase, Flyway, etc.)
- **Sécurité maximale** : stockez la clé maître sur USB pour un contrôle d'accès physique

**Exemple concret :**
```xml
<!-- pom.xml - Versionné dans Git - AUCUN SECRET -->
<properties>
    <db.url>${db.url}</db.url>
    <db.password>${db.password}</db.password>
</properties>

<!-- settings.xml - Local uniquement - Secrets chiffrés -->
<properties>
    <db.url>jdbc:postgresql://prod:5432/myapp</db.url>
    <db.password>#{COQLCE3DU6GtcS5P=}</db.password>
</properties>

<!-- Au runtime, vos plugins reçoivent automatiquement -->
<db.password>MySecureP@ss123</db.password>
```

---

## Guide rapide de chiffrement Maven

### En 3 étapes simples

#### Étape 1 : Créer le mot de passe maître

```bash
mvn --encrypt-master-password
```

Maven vous demande votre mot de passe maître (choisissez un mot de passe fort) :
```
Master password: ********
```

Maven affiche le mot de passe chiffré :
```
{jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}
```

**Créez** `~/.m2/settings-security.xml` :
```xml
<settingsSecurity>
    <master>{jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}</master>
</settingsSecurity>
```

---

#### Étape 2 : Chiffrer vos mots de passe

```bash
mvn --encrypt-password
```

Maven vous demande le mot de passe à chiffrer :
```
Password: ********
```

Maven affiche le mot de passe chiffré :
```
{COQLCE3DU6GtcS5P=}
```

---

#### Étape 3 : Utiliser dans settings.xml

**Ajoutez** dans `~/.m2/settings.xml` :

```xml
<settings>
    <profiles>
        <profile>
            <id>production</id>
            <properties>
                <!-- Format : #{...} pour que l'extension le déchiffre -->
                <db.password>#{COQLCE3DU6GtcS5P=}</db.password>
                <db.url>jdbc:postgresql://prod:5432/mydb</db.url>
                <db.username>admin</db.username>
            </properties>
        </profile>
    </profiles>
</settings>
```

**Important :** Utilisez le format `#{...}` (avec le dièse #) et non `{...}` pour que l'extension détecte et déchiffre le mot de passe.

---

### Workflow complet

```
┌─────────────────────────────────────┐
│ 1. Générer master password         │
│    mvn --encrypt-master-password    │
└─────────────┬───────────────────────┘
              ↓
┌─────────────────────────────────────┐
│ 2. Stocker dans                     │
│    ~/.m2/settings-security.xml      │
└─────────────┬───────────────────────┘
              ↓
┌─────────────────────────────────────┐
│ 3. Chiffrer chaque password         │
│    mvn --encrypt-password           │
└─────────────┬───────────────────────┘
              ↓
┌─────────────────────────────────────┐
│ 4. Stocker dans settings.xml        │
│    avec format #{...}               │
└─────────────┬───────────────────────┘
              ↓
┌─────────────────────────────────────┐
│ 5. L'extension déchiffre au runtime │
│    Transparent pour vos plugins     │
└─────────────────────────────────────┘
```

---

### Exemple complet

```bash
# 1. Créer le master password
$ mvn --encrypt-master-password
Master password: MySecureM@sterP@ss123
{jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}

# 2. Créer settings-security.xml
$ cat > ~/.m2/settings-security.xml << 'EOF'
<settingsSecurity>
    <master>{jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}</master>
</settingsSecurity>
EOF

# 3. Chiffrer le mot de passe de la base de données
$ mvn --encrypt-password
Password: MyDbP@ssword456
{COQLCE3DU6GtcS5P=}

# 4. Utiliser dans settings.xml
$ cat >> ~/.m2/settings.xml << 'EOF'
<profile>
    <id>production</id>
    <properties>
        <db.password>#{COQLCE3DU6GtcS5P=}</db.password>
    </properties>
</profile>
EOF

# 5. Build avec l'extension - le mot de passe est déchiffré automatiquement
$ mvn clean install -Pproduction
```

---

### Tester le déchiffrement

```bash
# Vérifier qu'un mot de passe chiffré peut être déchiffré
mvn --decrypt-password {COQLCE3DU6GtcS5P=}
```

Si la configuration est correcte, Maven affichera le mot de passe en clair.

---

### Formats de tokens

L'extension supporte deux modes de déchiffrement :

#### Mode 1 : Propriété entièrement chiffrée

```xml
<properties>
    <!-- Toute la valeur est un token chiffré -->
    <db.password>#{COQLCE3DU6GtcS5P=}</db.password>
</properties>
```

**Résultat au runtime :** `MySecureP@ss123`

#### Mode 2 : Token chiffré dans une chaîne

```xml
<properties>
    <!-- Token chiffré intégré dans une URL -->
    <liquibase.url>jdbc:postgresql://prod-db:5432/myapp?user=admin&amp;password=#{COQLCE3DU6GtcS5P=}&amp;ssl=true</liquibase.url>
</properties>
```

**Résultat au runtime :**
```
jdbc:postgresql://prod-db:5432/myapp?user=admin&password=MySecureP@ss123&ssl=true
```

#### Mode 3 : Plusieurs tokens dans une même chaîne

```xml
<properties>
    <!-- Plusieurs secrets différents chiffrés dans la même propriété -->
    <api.config>https://api.service.com?apiKey=#{8kFmP2nQrT5vW=}&amp;secret=#{zXcV9bNm4LkJ=}&amp;region=eu-west-1</api.config>
</properties>
```

**Résultat au runtime :**
```
https://api.service.com?apiKey=live_abc123xyz&secret=sk_secretABC456DEF&region=eu-west-1
```

Chaque token `#{...}` est remplacé indépendamment par sa valeur déchiffrée.

---

## Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Guide rapide de chiffrement Maven](#guide-rapide-de-chiffrement-maven)
3. [Architecture du projet](#architecture-du-projet)
4. [Installation et construction](#installation-et-construction)
5. [Configuration et utilisation](#configuration-et-utilisation)
6. [Sécurité avancée](#sécurité-avancée)
7. [Dépannage](#dépannage)
8. [Références techniques](#références-techniques)

---

## Vue d'ensemble

### Description

MVNCypherUtilities est une **Maven Core Extension** qui intercepte le cycle de vie Maven pour déchiffrer automatiquement les propriétés contenant des tokens chiffrés au format `#{...}`. Elle utilise le mécanisme natif de Maven (`SecDispatcher`) pour décrypter les valeurs stockées dans `~/.m2/settings-security.xml`.

### Cas d'usage

- Sécuriser les mots de passe de base de données dans les profils Maven
- Centraliser la gestion des secrets dans `settings.xml` et `settings-security.xml`
- Déchiffrer automatiquement les propriétés avant l'exécution des plugins (Liquibase, Flyway, etc.)
- Gérer différents environnements (dev, prod) avec des credentials chiffrés

### Prérequis

- **Maven** : 3.8.5 ou supérieur
- **JDK** : 16+ (pour mvndecrypt), 9+ (pour le projet parent), 21+ (pour les autres modules)

---

## Architecture du projet

### Structure des modules

```
MVNCypherUtilities/
├── pom.xml                          # POM parent (multi-module)
├── build.sh                         # Script de build automatique
├── mvndecrypt/                      # Maven Core Extension
│   ├── pom.xml
│   └── src/main/
│       ├── java/
│       │   └── com/epsilon777/maven/extensions/
│       │       └── CryptoExtension.java
│       └── resources/
│           └── META-INF/plexus/
│               └── components.xml   # Configuration Plexus (CRITIQUE)
├── mvndecryptui/                    # Interface utilisateur Swing
└── SampleApp/                       # Application d'exemple
    ├── pom.xml
    └── .mvn/
        └── extensions.xml           # Déclaration de l'extension
```

### Composants clés

| Module | Description | Packaging |
|--------|-------------|-----------|
| **mvndecrypt** | Extension Maven Core qui déchiffre les propriétés | `jar` |
| **mvndecryptui** | Interface graphique pour gérer le chiffrement | `jar` |
| **SampleApp** | Application de démonstration | `jar` |

---

## Installation et construction

### Méthode 1 : Script bash (le plus simple)

Un script bash est fourni pour automatiser tout le processus de build.

```bash
cd /chemin/vers/MVNCypherUtilities
chmod +x build.sh
./build.sh
```

**Actions du script :**
1. Nettoyage du repository local Maven
2. Installation du POM parent
3. Installation de l'extension mvndecrypt
4. Build complet de tous les modules

**Contenu du script :**

```bash
#!/bin/bash
set -e

echo "==> Nettoyage du repository local"
rm -rf ~/.m2/repository/com/epsilon777/mvncypherutilities/

echo "==> Installation du POM parent"
mvn clean install -N -DskipTests

echo "==> Installation de l'extension mvndecrypt"
cd mvndecrypt
mvn clean install -DskipTests
cd ..

echo "==> Build complet de tous les modules"
mvn clean install -DskipTests

echo "✓ Build terminé avec succès"
```

---

### Méthode 2 : Plugin Maven AntRun

Intégration directe dans le cycle de vie Maven via le plugin `maven-antrun-plugin`.

#### Configuration

Ajoutez dans `MVNCypherUtilities/pom.xml`, section `<build><plugins>` :

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-antrun-plugin</artifactId>
    <version>3.1.0</version>
    <inherited>false</inherited>  <!-- IMPORTANT : ne s'exécute QUE dans le parent -->
    <executions>
        <execution>
            <id>build-complete</id>
            <phase>validate</phase>
            <goals>
                <goal>run</goal>
            </goals>
            <configuration>
                <target>
                    <echo message="==> Nettoyage du repository local"/>
                    <delete dir="${user.home}/.m2/repository/com/epsilon777/mvncypherutilities/" 
                            quiet="true" 
                            failonerror="false"/>
                    
                    <echo message="==> Installation du POM parent"/>
                    <exec executable="mvn" failonerror="true">
                        <arg value="clean"/>
                        <arg value="install"/>
                        <arg value="-N"/>
                        <arg value="-DskipTests"/>
                    </exec>
                    
                    <echo message="==> Installation de l'extension mvndecrypt"/>
                    <exec executable="mvn" dir="${project.basedir}/mvndecrypt" failonerror="true">
                        <arg value="clean"/>
                        <arg value="install"/>
                        <arg value="-DskipTests"/>
                    </exec>
                    
                    <echo message="==> Build complet de tous les modules"/>
                    <exec executable="mvn" failonerror="true">
                        <arg value="clean"/>
                        <arg value="install"/>
                        <arg value="-DskipTests"/>
                    </exec>
                    
                    <echo message=""/>
                    <echo message="✓ Build terminé avec succès"/>
                </target>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### Utilisation avec profil Maven (recommandé)

Pour éviter que le plugin ne s'exécute à chaque build, créez un profil dédié :

```xml
<profiles>
    <profile>
        <id>full-build</id>
        <build>
            <plugins>
                <!-- Configuration maven-antrun-plugin ici -->
            </plugins>
        </build>
    </profile>
</profiles>
```

Activation du profil :

```bash
cd /chemin/vers/MVNCypherUtilities
mvn validate -Pfull-build
```

#### Points d'attention

- **`<inherited>false</inherited>`** : Empêche le plugin de s'exécuter dans les sous-modules (critique)
- **Phase `validate`** : Exécution au début du cycle Maven
- **Propriétés Maven** : Utilise `${user.home}` et `${project.basedir}` pour la portabilité

**Avantages :**
- Intégré au cycle Maven
- Multi-plateforme (Windows, Linux, macOS)
- Utilise les propriétés Maven natives

**Inconvénients :**
- Nécessite modification du POM
- Risque de récursion si mal configuré

---

### Méthode 3 : Construction manuelle

Pour un contrôle total de chaque étape.

#### Étape 1 : Nettoyer le repository local (recommandé)

```bash
rm -rf ~/.m2/repository/com/epsilon777/mvncypherutilities/
```

**Pourquoi ?** Évite les conflits avec d'anciennes versions ou fichiers `.lastUpdated`.

#### Étape 2 : Installer le POM parent

```bash
cd /chemin/vers/MVNCypherUtilities
mvn clean install -N -DskipTests
```

**Option `-N` (--non-recursive)** : Installe uniquement le POM parent sans construire les modules enfants.

**Pourquoi cette étape est critique ?**
- Le POM de `mvndecrypt` référence ce parent avec `<relativePath>../pom.xml</relativePath>`
- Quand Maven charge l'extension depuis `~/.m2/repository`, le chemin relatif n'existe pas
- Maven doit trouver le parent dans le repository local

#### Étape 3 : Installer l'extension mvndecrypt

```bash
cd mvndecrypt
mvn clean install -DskipTests
cd ..
```

**Installation dans :**
```
~/.m2/repository/com/epsilon777/mvncypherutilities/mvndecrypt/1.0/
├── mvndecrypt-1.0.jar
└── mvndecrypt-1.0.pom
```

**Vérification :**

```bash
jar tf ~/.m2/repository/com/epsilon777/mvncypherutilities/mvndecrypt/1.0/mvndecrypt-1.0.jar | grep components.xml
```

**Résultat attendu :** `META-INF/plexus/components.xml`

#### Étape 4 : Build complet

```bash
cd /chemin/vers/MVNCypherUtilities
mvn clean install -DskipTests
```

---

### Méthode 4 : Construction partielle

Pour reconstruire seulement certains modules après modification.

**Uniquement le parent :**
```bash
mvn clean install -N -DskipTests
```

**Uniquement l'extension :**
```bash
# Nécessite que le parent soit déjà installé
cd mvndecrypt
mvn clean install -DskipTests
```

**Uniquement SampleApp :**
```bash
# Nécessite que le parent et l'extension soient déjà installés
cd SampleApp
mvn clean install -DskipTests
```

**Module spécifique depuis la racine :**
```bash
mvn clean install -pl mvndecryptui -DskipTests
```

---

## Configuration et utilisation

### Configuration de l'extension

Créez le fichier `.mvn/extensions.xml` à la racine de votre projet :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
    <extension>
        <groupId>com.epsilon777.mvncypherutilities</groupId>
        <artifactId>mvndecrypt</artifactId>
        <version>1.0</version>
    </extension>
</extensions>
```

### Configuration des propriétés chiffrées

#### Dans settings.xml

```xml
<settings>
    <profiles>
        <profile>
            <id>production</id>
            <properties>
                <db.password>#{COQLCE3...encrypted...}</db.password>
                <db.url>jdbc:postgresql://prod-server:5432/mydb</db.url>
            </properties>
        </profile>
    </profiles>
</settings>
```

#### Dans pom.xml

```xml
<profiles>
    <profile>
        <id>production</id>
        <properties>
            <liquibase.password>${db.password}</liquibase.password>
            <liquibase.url>${db.url}</liquibase.url>
        </properties>
    </profile>
</profiles>
```

L'extension déchiffrera automatiquement `#{...}` lors de la phase `afterProjectsRead` du cycle Maven.

### Format des tokens

- **Token chiffré** : `#{COQLCE3...}` (sera déchiffré)
- **Token clair** : `password123` (inchangé)
- **Variable Maven** : `${maven.property}` (résolu normalement par Maven)

## 🔍 Points techniques importants

### Packaging : JAR, pas maven-plugin

```xml
<packaging>jar</packaging>  <!-- CORRECT pour une Core Extension -->
```

**Pourquoi ?**
- Une **Maven Core Extension** utilise `AbstractMavenLifecycleParticipant`
- Elle est chargée avant l'initialisation des plugins Maven
- Le packaging `maven-plugin` est réservé aux plugins classiques (Mojos)

### Déclaration : extensions.xml, pas pom.xml

**✅ CORRECT** : `.mvn/extensions.xml`
```xml
<extensions>
    <extension>...</extension>
</extensions>
```

**❌ INCORRECT** : Dans `<build><extensions>` du pom.xml
```xml
<build>
    <extensions>
        <extension>...</extension>  <!-- Ne fonctionne pas pour une Core Extension -->
    </extensions>
</build>
```

### Fichier components.xml obligatoire

Le fichier `src/main/resources/META-INF/plexus/components.xml` est **obligatoire** pour déclarer les composants Plexus (Dependency Injection de Maven).

Structure minimale :
```xml
<component-set>
  <components>
    <component>
      <role>org.apache.maven.AbstractMavenLifecycleParticipant</role>
      <implementation>com.epsilon777.maven.extensions.CryptoExtension</implementation>
      <hint>crypto-extension</hint>
    </component>
  </components>
</component-set>
```

## 🐛 Dépannage

### Erreur : "Failed to read artifact descriptor"

```
Extension com.epsilon777.mvncypherutilities:mvndecrypt:1.0 or one of its dependencies could not be resolved
```

**Causes possibles** :
1. Le POM parent n'est pas installé dans le repository local
2. Des fichiers `.lastUpdated` polluent le repository local
3. Le POM de l'extension est corrompu

**Solution** :
```bash
# 1. Nettoyer le repository local
rm -rf ~/.m2/repository/com/epsilon777/mvncypherutilities/mvndecrypt/1.0/

# 2. Réinstaller le parent
cd /chemin/vers/MVNCypherUtilities
mvn clean install -N

# 3. Réinstaller l'extension
cd mvndecrypt
mvn clean install -DskipTests
```

### Erreur : "Unresolveable build extension"

Depuis Maven 3.8.5, les extensions non résolues empêchent Maven de démarrer.

**Solutions** :
1. S'assurer que l'extension est correctement installée (voir ci-dessus)
2. Utiliser Maven 3.8.4 ou antérieur en attendant de corriger le problème
3. Supprimer temporairement `.mvn/extensions.xml` pour diagnostiquer

### Fichiers .lastUpdated

Si Maven a tenté de télécharger l'extension depuis un repository distant :

```bash
# Supprimer les marqueurs d'échec
rm ~/.m2/repository/com/epsilon777/mvncypherutilities/mvndecrypt/1.0/*.lastUpdated

# Réinstaller
cd mvndecrypt
mvn clean install -DskipTests
```

### L'extension ne se charge pas

Vérifiez que `components.xml` est bien dans le JAR :

```bash
jar tf ~/.m2/repository/com/epsilon777/mvncypherutilities/mvndecrypt/1.0/mvndecrypt-1.0.jar | grep components.xml
```

Si absent, vérifiez la configuration des resources dans le POM :

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>META-INF/plexus/components.xml</include>
            </includes>
        </resource>
    </resources>
</build>
```

### Mode debug

Pour diagnostiquer les problèmes de chargement de l'extension :

```bash
mvn validate -X 2>&1 | grep -A 30 "CryptoExtension"
```

## 📝 Notes de développement

### Modifier l'extension

Après modification du code :

```bash
cd mvndecrypt
mvn clean install -DskipTests
# Maven rechargera automatiquement la nouvelle version au prochain build
```

### Tester dans SampleApp

```bash
cd SampleApp
mvn clean validate
# Regardez les logs pour voir l'extension se charger
```

Les logs afficheront :
```
>> ✅ CryptoExtension loaded : ...
🔓 Decryption for key 'liquibase.password'
   Original: #{COQLCE3...}
   Decrypted: monmotdepasse
```

### Cycle de vie de l'extension

1. **Maven démarre** → Lit `.mvn/extensions.xml`
2. **Charge l'extension** → Instancie `CryptoExtension`
3. **Phase `afterProjectsRead`** → Déchiffre les propriétés
4. **Plugins exécutés** → Utilisent les propriétés déchiffrées

## 🔐 Sécurité

### Stockage des mots de passe

- Les mots de passe maîtres sont stockés dans `~/.m2/settings-security.xml`
- Les mots de passe chiffrés sont dans `~/.m2/settings.xml`
- **Ne jamais** commiter `settings-security.xml` dans Git
- Les tokens `#{...}` sont déchiffrés en mémoire uniquement

### 💾 Stockage du mot de passe maître sur clé USB (recommandé)

Pour une sécurité maximale, vous pouvez stocker le mot de passe maître sur un support amovible (clé USB). Cette approche garantit que le déchiffrement ne fonctionne **que lorsque la clé USB est branchée**, limitant ainsi les accès aux personnes autorisées (ex: équipe de déploiement).

#### Configuration avec relocation

**Étape 1** : Créer le mot de passe maître et le stocker sur la clé USB

```bash
# Générer le mot de passe maître chiffré
mvn --encrypt-master-password
# Entrez votre mot de passe maître quand Maven vous le demande
# Maven affichera quelque chose comme : {jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}
```

**Étape 2** : Créer `settings-security.xml` sur la clé USB

Montez votre clé USB (ex: `/Volumes/mySecureUsb` sur macOS, `/media/usb` sur Linux, `E:\` sur Windows)

Créez le fichier sur la clé USB :

```bash
# Exemple macOS
mkdir -p /Volumes/mySecureUsb/maven-security
nano /Volumes/mySecureUsb/maven-security/settings-security.xml
```

Contenu du fichier :
```xml
<settingsSecurity>
    <master>{jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}</master>
</settingsSecurity>
```

**Étape 3** : Créer un fichier de redirection dans `~/.m2/`

Créez `~/.m2/settings-security.xml` avec une **relocation** pointant vers la clé USB :

```xml
<settingsSecurity>
    <relocation>/Volumes/mySecureUsb/maven-security/settings-security.xml</relocation>
</settingsSecurity>
```

Exemples de chemins selon les systèmes :
- **macOS** : `/Volumes/mySecureUsb/maven-security/settings-security.xml`
- **Linux** : `/media/usb/maven-security/settings-security.xml` ou `/mnt/usb/...`
- **Windows** : `E:/maven-security/settings-security.xml`

**Étape 4** : Tester la configuration

```bash
# Sans la clé USB branchée - ÉCHEC attendu
mvn --encrypt-password test123
# Erreur : FileNotFoundException sur le chemin de la clé USB

# Avec la clé USB branchée - SUCCÈS attendu
mvn --encrypt-password test123
# Résultat : {COQLCE3DU6GtcS5P=}
```

#### Avantages de cette approche

✅ **Sécurité physique** : Le mot de passe maître n'est jamais sur le disque dur
✅ **Contrôle d'accès** : Seuls ceux qui ont la clé USB peuvent déployer
✅ **Audit trail** : La clé USB peut être stockée en lieu sûr et tracée
✅ **Révocation facile** : Retirer la clé = désactiver les déploiements

#### Points d'attention

⚠️ **Chemin absolu obligatoire** : La relocation doit utiliser un chemin absolu
⚠️ **Point de montage stable** : Assurez-vous que la clé USB monte toujours au même endroit
⚠️ **Backup** : Conservez une copie sécurisée du `settings-security.xml` de la clé USB
⚠️ **Chiffrement de la clé USB** : Pour une sécurité maximale, utilisez une clé USB chiffrée (BitLocker, LUKS, FileVault)

## 📚 Références

- [Maven Extension Guide](https://maven.apache.org/guides/mini/guide-using-extensions.html)
- [Maven Password Encryption](https://maven.apache.org/guides/mini/guide-encryption.html)
- [Plexus Container](https://codehaus-plexus.github.io/plexus-containers/)

## 🤝 Contributeur

- Jean-Paul VALLÉE (jeanpaul.vallee1@gmail.com)

## 📄 Licence

Version 1.0 - 2025

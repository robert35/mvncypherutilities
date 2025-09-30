# MVNCypherUtilities

Extension Maven permettant de déchiffrer automatiquement des propriétés Maven chiffrées avec `settings-security.xml` lors du cycle de vie du build.

## 📋 Vue d'ensemble

Cette **Maven Core Extension** intercepte le cycle de vie Maven pour déchiffrer les propriétés qui contiennent des tokens chiffrés au format `#{...}`. Elle utilise le mécanisme natif de Maven (`SecDispatcher`) pour décrypter les valeurs stockées dans `~/.m2/settings-security.xml`.

### Cas d'usage

- Stocker des mots de passe de base de données chiffrés dans les profils Maven
- Centraliser la gestion des secrets dans `settings.xml` et `settings-security.xml`
- Déchiffrer automatiquement les propriétés avant l'exécution des plugins (Liquibase, etc.)

## 🏗️ Structure du projet

```
MVNCypherUtilities/
├── pom.xml                          # POM parent (multi-module)
├── mvndecrypt/                      # Maven Core Extension
│   ├── pom.xml
│   └── src/main/
│       ├── java/
│       │   └── com/epsilon777/maven/extensions/
│       │       └── CryptoExtension.java
│       └── resources/
│           └── META-INF/plexus/
│               └── components.xml   # Configuration Plexus (CRITIQUE)
├── mvndecryptui/                    # Interface utilisateur (optionnel)
└── SampleApp/                       # Application d'exemple
    ├── pom.xml
    └── .mvn/
        └── extensions.xml           # Déclaration de l'extension
```

## 🔧 Installation et construction

### Prérequis

- Maven 3.8.5 ou supérieur
- JDK 16+ (pour mvndecrypt)
- JDK 9+ (pour le projet parent)

### Ordre de construction (IMPORTANT ⚠️)

L'ordre est **critique** car Maven doit pouvoir résoudre le POM parent depuis le repository local lors du chargement de l'extension.

#### 1. Installer le POM parent

```bash
cd /chemin/vers/MVNCypherUtilities
mvn clean install -N
```

**Explication** : L'option `-N` (`--non-recursive`) installe uniquement le POM parent sans construire les modules enfants. Cette étape est indispensable car :
- Le POM de `mvndecrypt` référence ce parent avec `<relativePath>../pom.xml</relativePath>`
- Quand Maven charge l'extension depuis `~/.m2/repository`, le chemin relatif n'existe pas
- Maven doit donc trouver le parent dans le repository local

#### 2. Construire et installer l'extension

```bash
cd mvndecrypt
mvn clean install -DskipTests
```

Cela installe l'extension dans :
```
~/.m2/repository/com/epsilon777/mvncypherutilities/mvndecrypt/1.0/
├── mvndecrypt-1.0.jar
└── mvndecrypt-1.0.pom
```

**Vérification** :
```bash
# Vérifier que components.xml est bien empaqueté
jar tf ~/.m2/repository/com/epsilon777/mvncypherutilities/mvndecrypt/1.0/mvndecrypt-1.0.jar | grep components.xml
```

Résultat attendu : `META-INF/plexus/components.xml`

#### 3. Construire les autres modules (optionnel)

```bash
cd /chemin/vers/MVNCypherUtilities
mvn clean install -DskipTests
```

## 🚀 Utilisation dans un projet

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

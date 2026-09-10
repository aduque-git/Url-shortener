# 🔗 URL Shortener — CI/CD Lab

> Pequeño servicio REST para experimentar, de forma progresiva, con **CI/CD, calidad de código, contenedores, registro de imágenes, Kubernetes, Helm y GitOps**.

El objetivo principal de este proyecto **no es construir un URL Shortener complejo**, sino disponer de una aplicación suficientemente pequeña para poder centrarnos en entender cómo funciona un flujo moderno de entrega de software.

---

## 🎯 Objetivo del proyecto

Construir una API REST de acortamiento de URLs y utilizarla como laboratorio para aprender y probar:

* ☕ Java 21
* 🌱 Spring Boot
* 🧩 Arquitectura Hexagonal
* 🧪 JUnit 5
* 🎭 Mockito
* 📏 Checkstyle
* 📊 JaCoCo
* 🔎 SonarQube
* 🧠 Qodana
* ⚙️ GitHub Actions
* 🐳 Docker
* 📦 GitHub Container Registry (GHCR)
* ☸️ Kubernetes
* ⛵ Helm
* 🔄 Argo CD
* 🧬 GitOps

La idea es avanzar progresivamente desde:

```text
Código fuente
     ↓
Calidad
     ↓
CI
     ↓
Containerización
     ↓
Registry
     ↓
Kubernetes
     ↓
GitOps / CD
```

---

# 🏗️ Arquitectura de la aplicación

La aplicación sigue una **Arquitectura Hexagonal (Ports & Adapters)**.

```text
                    ┌──────────────────────┐
                    │      REST API        │
                    │   Spring MVC         │
                    └──────────┬───────────┘
                               │
                         Adapter In
                               │
                               ▼
                    ┌──────────────────────┐
                    │   Application        │
                    │                      │
                    │   Use Case           │
                    │   Service            │
                    └──────────┬───────────┘
                               │
                            Port Out
                               │
                               ▼
                    ┌──────────────────────┐
                    │     Repository       │
                    │                      │
                    │   In-Memory          │
                    └──────────────────────┘
```

La ventaja para este laboratorio es que podemos cambiar posteriormente la infraestructura sin modificar la lógica de negocio.

Por ejemplo:

```text
                 Application
                      │
              ShortUrlRepository
                      │
          ┌───────────┴───────────┐
          ▼                       ▼
   InMemoryRepository       PostgreSQL
```

La aplicación permanece desacoplada de la implementación concreta.

---

# 📁 Estructura

```text
src/
├── main/
│   └── java/
│       └── org/example/urlshortener/
│           ├── domain/
│           │   ├── model/
│           │   │   └── ShortUrl.java
│           │   └── exception/
│           │       └── ShortUrlNotFoundException.java
│           │
│           ├── application/
│           │   ├── port/
│           │   │   ├── in/
│           │   │   │   └── ShortUrlUseCase.java
│           │   │   └── out/
│           │   │       └── ShortUrlRepository.java
│           │   │
│           │   └── service/
│           │       └── ShortUrlService.java
│           │
│           └── adapter/
│               ├── in/
│               │   └── web/
│               │       ├── ShortUrlController.java
│               │       ├── CreateShortUrlRequest.java
│               │       └── ShortUrlResponse.java
│               │
│               └── out/
│                   └── persistence/
│                       └── InMemoryShortUrlRepository.java
│
└── test/
    └── java/
        └── org/example/urlshortener/
```

---

# 🚀 API

## Crear una URL corta

```http
POST /api/urls
Content-Type: application/json
```

Request:

```json
{
  "url": "https://www.example.com"
}
```

Response:

```json
{
  "shortCode": "a1b2c3d4",
  "originalUrl": "https://www.example.com"
}
```

---

## Resolver una URL

```http
GET /api/urls/{shortCode}
```

La API responde con:

```http
302 Found
Location: https://www.example.com
```

---

# 🧪 Testing

Utilizamos **JUnit 5** y **Mockito**.

Los tests comprueban diferentes capas de la aplicación:

```text
JUnit
 │
 ├── Domain / Application
 │     └── ShortUrlService
 │
 ├── Persistence Adapter
 │     └── InMemoryShortUrlRepository
 │
 └── Web Adapter
       └── ShortUrlController
```

El objetivo es comprobar que la aplicación funciona correctamente antes de incorporarla al proceso de CI.

---

# 📊 Code Coverage — JaCoCo

**JaCoCo** se utiliza para medir la cobertura de código producida por los tests.

La pipeline comprueba actualmente un mínimo del:

```text
80% LINE COVERAGE
```

El flujo es:

```text
JUnit
  │
  ▼
JaCoCo
  │
  ├── Genera reporte
  │
  └── Comprueba mínimo 80%
```

Si la cobertura cae por debajo del límite configurado, la validación falla.

---

# 📏 Checkstyle

**Checkstyle** analiza el estilo y determinadas reglas estructurales del código Java.

Actualmente se utiliza para detectar, entre otras cosas:

* imports innecesarios
* imports duplicados
* wildcard imports

El objetivo es que los problemas básicos de estilo se detecten automáticamente.

```text
Código
  │
  ▼
Checkstyle
  │
  ├── OK  → continúa
  │
  └── FAIL → pipeline falla
```

---

# 🔎 SonarQube — análisis local

**SonarQube** se ha implementado y probado **localmente** mediante Docker Compose.

No forma parte actualmente de la pipeline de GitHub Actions.

La instalación local permite experimentar con un servidor de análisis persistente:

```text
Docker Compose
      │
      ▼
  SonarQube
      │
      ▲
      │
Maven Sonar Scanner
```

El análisis local se ejecuta sobre el proyecto y permite visualizar:

* 🐛 Bugs
* 🔐 Vulnerabilidades
* 🧹 Code smells
* 📐 Complejidad
* ♻️ Duplicación
* 📊 Coverage
* 🛠️ Maintainability

SonarQube y JaCoCo cumplen funciones diferentes:

```text
JaCoCo
   └── ¿Cuánto código está cubierto por tests?

SonarQube
   └── ¿Qué problemas potenciales existen en el código?
```

SonarQube consume además información de cobertura generada por JaCoCo.

### Ejecución local

```bash
mvnw.cmd clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=url-shortener \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_TOKEN
```

SonarQube se ejecuta mediante:

```bash
docker compose up -d
```

> ℹ️ SonarQube se mantiene como herramienta de experimentación y análisis local. No necesitamos exponer nuestro servidor local a Internet para ejecutar la CI.

---

# 🧠 Qodana — análisis en CI

Para el análisis estático dentro de **GitHub Actions** utilizamos **Qodana**.

Qodana funciona de forma independiente de Checkstyle y JaCoCo.

```text
GitHub Actions
      │
      ├── JUnit
      ├── Checkstyle
      ├── JaCoCo
      │
      └── Qodana
            │
            ▼
       Static Analysis
```

Qodana analiza el proyecto y busca problemas potenciales en el código.

El workflow se encuentra en:

```text
.github/workflows/code_quality.yml
```

Esto permite mantener separado:

```text
ci.yml
└── Build + Tests + Quality checks

code_quality.yml
└── Qodana analysis
```

El token utilizado por Qodana se almacena como **GitHub Secret** y nunca se incorpora al código fuente.

---

# ⚙️ Continuous Integration — GitHub Actions

La CI se ejecuta mediante **GitHub Actions**.

Actualmente se ejecuta para:

* `push` a `main`
* Pull Requests hacia `main`

El flujo principal es:

```text
Push / Pull Request
        │
        ▼
   GitHub Actions
        │
        ▼
     Checkout
        │
        ▼
      Java 21
        │
        ▼
      Compile
        │
        ▼
       Tests
        │
        ▼
    Checkstyle
        │
        ▼
    JaCoCo report
        │
        ▼
  JaCoCo coverage
        │
        ▼
      Artifacts
```

El workflow principal se encuentra en:

```text
.github/workflows/ci.yml
```

La CI tiene una responsabilidad concreta:

> **Comprobar que el código puede compilarse, probarse y superar las validaciones de calidad.**

La CI **no despliega directamente en Kubernetes**.

---

# 🐳 Docker

El siguiente objetivo del laboratorio es containerizar la aplicación.

Queremos pasar de:

```text
Java application
```

a:

```text
Docker image
```

El flujo será:

```text
Source Code
     │
     ▼
 Maven Build
     │
     ▼
 Docker Build
     │
     ▼
 Docker Image
```

Primero construiremos y probaremos la imagen localmente.

Después incorporaremos la construcción de la imagen a GitHub Actions.

---

# 📦 GitHub Container Registry — GHCR

Una vez que la imagen funcione localmente, utilizaremos **GitHub Container Registry (GHCR)** para almacenarla.

El flujo será:

```text
GitHub Actions
      │
      ▼
Docker Build
      │
      ▼
Docker Image
      │
      ▼
GHCR
```

Esto nos permitirá tener una imagen versionada y disponible para Kubernetes.

---

# ☸️ Kubernetes

Después utilizaremos **Kubernetes** para ejecutar la aplicación.

La arquitectura pasará a ser:

```text
                    Kubernetes
                        │
                        ▼
                 ┌─────────────┐
                 │ URL         │
                 │ Shortener   │
                 │ Pod         │
                 └─────────────┘
```

Se estudiarán progresivamente conceptos como:

* Deployment
* Pod
* Service
* ConfigMap
* Secrets
* Health checks
* Replicas

La aplicación continuará siendo la misma.

Lo que cambia es **cómo se ejecuta y administra**.

---

# ⛵ Helm

Una vez que la aplicación funcione directamente en Kubernetes, utilizaremos **Helm** para empaquetar sus manifests.

En lugar de mantener múltiples YAML independientes:

```text
deployment.yaml
service.yaml
configmap.yaml
...
```

tendremos un Chart:

```text
helm/
└── url-shortener/
    ├── Chart.yaml
    ├── values.yaml
    └── templates/
```

Helm nos permitirá parametrizar aspectos como:

```yaml
image:
  repository: ...
  tag: ...
```

y reutilizar la misma configuración para diferentes entornos.

---

# 🔄 Argo CD — Continuous Delivery

La última parte del laboratorio será **Argo CD**.

Aquí introducimos GitOps.

La idea fundamental es:

> Git define el estado deseado de Kubernetes.

El flujo será:

```text
Developer
    │
    ▼
  GitHub
    │
    ▼
GitHub Actions
    │
    ├── Build
    ├── Test
    ├── Quality
    ├── Docker Build
    │
    └── Push image → GHCR
                         │
                         ▼
                    Kubernetes
                         ▲
                         │
                      Argo CD
                         ▲
                         │
                    Git repository
```

Un punto importante:

### ❌ GitHub Actions NO desplegará en Kubernetes

No utilizaremos:

```text
kubectl
kubeconfig
Kubernetes credentials
```

dentro de GitHub Actions.

GitHub Actions será responsable de la **CI**.

Argo CD será responsable de la **CD**.

```text
CI
│
└── GitHub Actions
       └── Build / Test / Quality / Image

CD
│
└── Argo CD
       └── Kubernetes / GitOps
```

Esto permite separar claramente ambas responsabilidades.

---

# 🧬 Flujo completo

El objetivo final del laboratorio es conseguir este flujo:

```text
                   ┌───────────────┐
                   │   Developer   │
                   └───────┬───────┘
                           │
                           ▼
                    ┌─────────────┐
                    │   GitHub    │
                    └──────┬──────┘
                           │
                           ▼
                 ┌──────────────────┐
                 │  GitHub Actions   │
                 │                  │
                 │ Compile          │
                 │ Test             │
                 │ Checkstyle       │
                 │ JaCoCo           │
                 │ Qodana           │
                 │ Docker Build     │
                 └────────┬─────────┘
                          │
                          ▼
                    ┌───────────┐
                    │    GHCR   │
                    │  Image    │
                    └─────┬─────┘
                          │
                          ▼
                    ┌───────────┐
                    │ Kubernetes│
                    └─────▲─────┘
                          │
                      ┌───┴───┐
                      │Argo CD│
                      └───▲───┘
                          │
                          │ GitOps
                          │
                       GitHub
```

---

# 🗺️ Roadmap

El proyecto se desarrollará en este orden:

* [x] ☕ Crear aplicación Spring Boot
* [x] 🧩 Implementar Arquitectura Hexagonal
* [x] 🧪 Añadir tests con JUnit y Mockito
* [x] 📏 Integrar Checkstyle
* [x] 📊 Integrar JaCoCo
* [x] 🔎 Probar SonarQube localmente con Docker Compose
* [x] ⚙️ Crear CI con GitHub Actions
* [x] 🧠 Integrar Qodana en CI
* [ ] 🐳 Crear Dockerfile
* [ ] 🐳 Construir y probar la imagen localmente
* [ ] 📦 Publicar imagen en GHCR
* [ ] ☸️ Ejecutar aplicación en Kubernetes
* [ ] ⛵ Crear Helm Chart
* [ ] 🔄 Integrar Argo CD
* [ ] 🧬 Completar flujo GitOps

---

# 🧰 Stack

| Área                  | Tecnología                |
| --------------------- | ------------------------- |
| Lenguaje              | Java 21                   |
| Framework             | Spring Boot               |
| Build                 | Maven                     |
| Testing               | JUnit 5 + Mockito         |
| Code style            | Checkstyle                |
| Coverage              | JaCoCo                    |
| Local static analysis | SonarQube                 |
| CI static analysis    | Qodana                    |
| CI                    | GitHub Actions            |
| Container             | Docker                    |
| Registry              | GitHub Container Registry |
| Orchestration         | Kubernetes                |
| Packaging             | Helm                      |
| CD / GitOps           | Argo CD                   |

---

# 📌 Principios del laboratorio

Este proyecto busca mantener una separación clara de responsabilidades:

### Código

La aplicación debe ser sencilla.

### Calidad

Las herramientas deben detectar problemas automáticamente.

### CI

GitHub Actions valida cada cambio.

### Containerización

Docker empaqueta la aplicación.

### Registry

GHCR almacena las imágenes.

### Orquestación

Kubernetes ejecuta la aplicación.

### CD

Argo CD mantiene Kubernetes sincronizado con el estado definido en Git.

---

## 🏁 Resultado esperado

Al finalizar, el objetivo será poder realizar:

```bash
git push
```

y tener un proceso automatizado similar a:

```text
git push
   │
   ▼
GitHub
   │
   ▼
CI
├── Build
├── Test
├── Checkstyle
├── JaCoCo
└── Qodana
   │
   ▼
Docker Image
   │
   ▼
GHCR
   │
   ▼
Argo CD
   │
   ▼
Kubernetes
   │
   ▼
🚀 URL Shortener running
```

El objetivo final no es el URL Shortener.

**El objetivo es entender y construir un pipeline CI/CD completo, desde el commit hasta una aplicación desplegada mediante GitOps.**

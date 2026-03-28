 # productsapi — Instruções de execução

Este README descreve como executar o projeto `productsapi` em desenvolvimento local e usando Docker Compose, como configurar variáveis de ambiente.

Checklist (o que este README cobre)
- [x] Requisitos e pré-requisitos
- [x] Execução local com Gradle 
- [x] Execução com Docker Compose (usando `.env`)
- [x] Rodar testes e gerar JAR
- [x] Variáveis de ambiente importantes e precedência
- [x] Notas de segurança / produção

## Requisitos

- Java 21 
- Gradle
- Docker e Docker Compose (para rodar com containers)

## Arquivos importantes

- `src/main/resources/application.yaml` — configuração principal que usa variáveis de ambiente.
- `.env` — template para desenvolvimento local.
- `docker-compose.yml` — orquestra containers `db` (Postgres) e `app`.

> Observação: você deve criar um `.env` na raiz do projeto. Ele está listado em `.gitignore` para não ser comitado.

## Execução local

1. Defina variáveis de ambiente na sessão

```powershell
$env:DB_HOST = 'localhost'
$env:DB_PORT = '5432'
$env:DB_NAME = 'productsdb'
$env:DB_USER = 'postgres'
$env:DB_PASSWORD = 'password'
$env:SPRING_PROFILES_ACTIVE = 'local'
gradlew bootRun
```

1. A aplicação estará disponível em http://localhost:8080 (padrão)

## Rodando com Docker Compose

1. Coloque/edite o arquivo `.env` na raiz do projeto

```dotenv
# .env (exemplo)
DB_HOST=localhost
DB_PORT=5432
DB_NAME=productsdb
DB_USER=postgres
DB_PASSWORD=password
SPRING_PROFILES_ACTIVE=local
```

1. Suba os serviços

```powershell
docker-compose up -d --build
# Para parar e remover:
docker-compose down
```

1. Observação para Docker Compose: o serviço `app` no `docker-compose.yml` define `DB_HOST: db` internamente, 
então quando executar via Compose a aplicação usará o serviço `db` como host do PostgreSQL.

Se quiser forçar um `.env` específico:

```powershell
docker-compose --env-file .env up -d --build
```

## Testes e build

- Rodar testes:

```powershell
gradlew test
```

- Gerar JAR e executar:

```powershell
gradlew build
java -jar build\libs\productsapi-0.0.1-SNAPSHOT.jar
```

## Variáveis de ambiente (principais)

- `DB_HOST` — host do banco (ex.: `localhost` ou `db` no Compose)
- `DB_PORT` — porta do Postgres (padrão `5432`)
- `DB_NAME` — nome da base (ex.: `productsdb`)
- `DB_USER` — usuário do banco
- `DB_PASSWORD` — senha do banco
- `DB_JDBC_URL` — (opcional) se definido, sobrescreve a URL JDBC completa usada por `jakarta.persistence.jdbc.url`

No `application.yaml` usamos placeholders com defaults do tipo `${DB_HOST:localhost}`.
Para sobrescrever, defina variáveis de ambiente ou passe argumentos na linha de comando (maior precedência).

## Notas sobre produção e segurança

- NÃO comitar `.env` contendo segredos. O `.env` de exemplo está em `.gitignore`.
- Em produção, use soluções seguras para gerenciar segredos: Kubernetes Secrets, HashiCorp Vault, AWS Secrets Manager, Azure Key Vault, etc.
- Dê permissões mínimas ao usuário do banco e rotacione senhas regularmente.

## Alterações aplicadas no repositório (para referência)

- `.gitignore` — adicionado `.env`
- `docker-compose.yml` — adicionado serviço `app` que usa `env_file` e define `DB_HOST: db` para execução em Compose.
- `build.gradle` — adicionado `testRuntimeOnly 'com.h2database:h2'` para testes.
- `src/test/resources/application.yaml` — configuração H2 para testes.


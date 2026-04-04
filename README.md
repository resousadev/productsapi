# productsapi — Instruções de execução

Este README descreve como executar o projeto `productsapi` em desenvolvimento local e usando Docker Compose, como configurar variáveis de ambiente.

## Checklist (o que este README cobre)
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

1. Defina as variáveis de ambiente necessárias (adapte conforme seu sistema operacional):

**Linux/macOS:**
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=productsdb
export DB_USER=postgres
export DB_PASSWORD=password
export SPRING_PROFILES_ACTIVE=local
./gradlew bootRun
```

**Windows:**
```cmd
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=productsdb
set DB_USER=postgres
set DB_PASSWORD=password
set SPRING_PROFILES_ACTIVE=local
gradlew.bat bootRun
```

2. A aplicação estará disponível em http://localhost:8080 (padrão)

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

```bash
docker-compose up -d --build
# Para parar e remover:
docker-compose down
```

1. Observação para Docker Compose: o serviço `app` no `docker-compose.yml` define `DB_HOST: db` internamente, 
então quando executar via Compose a aplicação usará o serviço `db` como host do PostgreSQL.

Se quiser forçar um `.env` específico:

```bash
docker-compose --env-file .env up -d --build
```

## Migrations (Flyway)

O projeto usa **Flyway** para gerenciar migrations do banco de dados de forma segura e versionada.

### Como funciona

- As migrations são arquivos SQL localizados em `src/main/resources/db/migration/`
- Seguem a convenção de nomenclatura: `V<YYYYMMDD>__<descrição>.sql` (ex.: `V20260404__Create_products_table.sql`)
- Ao iniciar a aplicação, o Flyway automaticamente:
  1. Detecta as migrations
  2. Verifica quais já foram executadas
  3. Executa as novas migrations em ordem

### Criar nova migration

1. Crie um novo arquivo em `src/main/resources/db/migration/`
2. Use a convenção de nome: `V<YYYYMMDD>__<descrição>.sql`
   - Exemplo: `V20260405__Add_category_column.sql`

```sql
ALTER TABLE products ADD COLUMN category VARCHAR(100);
CREATE INDEX idx_products_category ON products(category);
```

3. Execute a aplicação - a migration será executada automaticamente:

```bash
./gradlew bootRun
```

### Verificar migrations executadas

O Flyway mantém histórico em uma tabela especial `flyway_schema_history` no banco de dados. Você pode consultar:

```sql
SELECT version, description, success FROM flyway_schema_history ORDER BY version;
```

### Configurações do Flyway

No arquivo `src/main/resources/application.yaml`:

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration        # Diretório das migrations
    baseline-on-migrate: false                # Não cria baseline automaticamente
    out-of-order: false                       # Valida ordem sequencial das migrations
    sql-migration-prefix: V                   # Prefixo dos arquivos de migration
    sql-migration-suffixes: .sql              # Sufixo dos arquivos de migration
  jpa:
    hibernate:
      ddl-auto: none                          # Deixa o Flyway gerenciar o schema

logging:
  level:
    org.flywaydb: DEBUG                       # Ativa logs de debug do Flyway
```

**Importante:** O `ddl-auto` está configurado como `none` para que o **Flyway seja o responsável** por criar e gerenciar o schema do banco. Isso evita conflitos entre o Hibernate e o Flyway.

## Testes e build

- Rodar testes:

```bash
./gradlew test
```

- Gerar JAR e executar:

```bash
./gradlew build
java -jar build/libs/productsapi-0.0.1-SNAPSHOT.jar
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
- `build.gradle` — adicionadas dependências Flyway (`flyway-core` e `flyway-database-postgresql`).
- `src/test/resources/application.yaml` — configuração H2 para testes.
- `src/main/resources/application.yaml` — alterado `ddl-auto: update` para `ddl-auto: none`; adicionadas configurações do Flyway com logging DEBUG.
- `src/main/java/io/resousadev/productsapi/model/Product.java` — alterado `@GeneratedValue(strategy = GenerationType.IDENTITY)` para `@GeneratedValue(strategy = GenerationType.UUID)` com `columnDefinition = "UUID"`.
- `src/main/java/io/resousadev/productsapi/config/FlywayConfig.java` — classe de configuração criada para garantir que as migrations Flyway sejam executadas antes do Hibernate validar o schema.
- `src/main/resources/db/migration/V20260404__Create_products_table.sql` — migration SQL criada com definição correta da tabela `products` com coluna `id` do tipo `UUID PRIMARY KEY`.

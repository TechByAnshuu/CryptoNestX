#!/bin/bash
# Creates separate PostgreSQL databases for each microservice and SonarQube.
# Mounted into postgres container at /docker-entrypoint-initdb.d/
set -e

MASTER_DB="$POSTGRES_DB"
MASTER_USER="$POSTGRES_USER"

create_db() {
  local DB=$1
  echo "Creating database: $DB"
  psql -v ON_ERROR_STOP=1 --username "$MASTER_USER" --dbname "$MASTER_DB" <<-EOSQL
    CREATE DATABASE ${DB};
    GRANT ALL PRIVILEGES ON DATABASE ${DB} TO ${MASTER_USER};
EOSQL
}

create_db auth_db
create_db portfolio_db
create_db exchange_db
create_db notification_db
create_db sonar_db

echo "All CryptoNestX databases initialized."

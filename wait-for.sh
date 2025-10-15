#!/bin/sh
set -e

db_host="${1:-localhost}"
redis_host="${2:-localhost}"
shift 2

# Default values
db_port="${DB_PORT:-5432}"
db_user="${DB_USER:-postgres}"

# Wait for Postgres
until pg_isready -h "$db_host" -p "$db_port" -U "$db_user"; do
  >&2 echo "Postgres ($db_host:$db_port) is unavailable - sleeping"
  sleep 2
done

# Wait for Redis
until nc -z "$redis_host" 6379; do
  >&2 echo "Redis ($redis_host:6379) is unavailable - sleeping"
  sleep 2
done

>&2 echo "Postgres and Redis are up - executing command"
exec "$@"

#!/bin/bash

set -euo pipefail

if [[ $# -lt 4 ]]; then
  echo "Usage: $0 <main-class> <service> <port> <config-file> [extra-args...]"
  exit 1
fi

cd "$(dirname "$0")/.."

MAIN_CLASS="$1"
SERVICE="$2"
PORT="$3"
CONFIG_FILE="$4"
shift 4

mvn -q -DskipTests compile exec:java \
  -Dexec.mainClass="$MAIN_CLASS" \
  -Dexec.args="udp $SERVICE $PORT --config=$CONFIG_FILE $*"
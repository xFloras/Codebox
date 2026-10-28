#!/bin/bash

# $1 - host's workdir (e.g. /tmp/submission_123)
# $2 - RAM limit
# $3 - CPU limit

SUBMISSION_DIR="$1"
MEMORY_LIMIT="${2:-128m}"
CPU_LIMIT="${3:-1.0}"

if [ -z "$SUBMISSION_DIR" ]; then
    echo "ERROR: Missing submission directory path."
    exit 1
fi


docker run --rm \
  --network none \
  --memory="$MEMORY_LIMIT" \
  --memory-swap="$MEMORY_LIMIT" \
  --cpus="$CPU_LIMIT" \
  --read-only \
  --tmpfs /tmp:exec,size=64m \
  -v "$SUBMISSION_DIR":/app \
  -w /app \
  eclipse-temurin:21-jdk-alpine \
  sh -c "javac Solution.java Main.java 2>&1 && java -Xmx96m Main"
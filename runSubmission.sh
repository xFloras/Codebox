#!/bin/bash

# $1 - host's workdir (e.g. /tmp/submission_123)
# $2 - RAM limit
# $3 - CPU limit
# $4 - Time limit per test

SUBMISSION_DIR="$1"
MEMORY_LIMIT="${2:-128m}"
CPU_LIMIT="${3:-1.0}"
TIME_LIMIT_S="${4:-2}"

if [ -z "$SUBMISSION_DIR" ]; then
    echo "ERROR: Missing submission directory path."
    exit 1
fi

NAME="judge_$(basename "$SUBMISSION_DIR")"


docker run --rm --name "$NAME" \
  --user "$(id -u):$(id -g)" \
  --network none \
  --memory="$MEMORY_LIMIT" --memory-swap="$MEMORY_LIMIT" \
  --cpus="$CPU_LIMIT" \
  --pids-limit 64 \
  --cap-drop ALL --security-opt no-new-privileges \
  --read-only --tmpfs /tmp:exec,size=64m \
  -v "$SUBMISSION_DIR":/app:ro \
  -v "$SUBMISSION_DIR/out":/app/out \
  -e TIME_LIMIT_S="$TIME_LIMIT_S" \
  -w /app \
  eclipse-temurin:21-jdk-alpine \
  sh -c '
    javac -d /tmp Main.java > out/compile.txt 2>&1 || exit 2
    for f in inputs/*.in; do
      n=$(basename "$f" .in)
      start=$(date +%s%N)
      timeout "$TIME_LIMIT_S" java -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -Xmx96m -cp /tmp Main \
        < "$f" > "out/$n.out" 2> "out/$n.err"
      echo $? > "out/$n.code"
      echo $(( ($(date +%s%N) - start) / 1000000 )) > "out/$n.time"
    done
  '

#!/bin/bash
cd "$(dirname "$0")/.."

mvn -q -DskipTests compile

osascript <<'APPLESCRIPT'
tell application "Terminal"
  activate
  do script "cd \"$(pwd)\"; mvn -q exec:java -Dexec.mainClass=br.dev.danielrl.GatewayMain -Dexec.args='udp gateway 9000 --config=settings.json'"
  do script "cd \"$(pwd)\"; mvn -q exec:java -Dexec.mainClass=br.dev.danielrl.LogWritterMain -Dexec.args='udp logwriter 9001 --config=settings.json'"
  do script "cd \"$(pwd)\"; mvn -q exec:java -Dexec.mainClass=br.dev.danielrl.LogWritterMain -Dexec.args='udp logwriter 9004 --config=settings.json'"
  do script "cd \"$(pwd)\"; mvn -q exec:java -Dexec.mainClass=br.dev.danielrl.LogReaderMain -Dexec.args='udp logreader 9002 --config=settings.json'"
  do script "cd \"$(pwd)\"; mvn -q exec:java -Dexec.mainClass=br.dev.danielrl.LogReaderMain -Dexec.args='udp logreader 9005 --config=settings.json'"
  do script "cd \"$(pwd)\"; mvn -q exec:java -Dexec.mainClass=br.dev.danielrl.TestingClientMain -Dexec.args='udp testing-client 9003 --config=settings.json'"
end tell
APPLESCRIPT
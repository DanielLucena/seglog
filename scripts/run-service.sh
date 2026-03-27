#!/bin/bash

echo "Select a protocol:"
echo "1) UDP"
echo "2) TCP"
echo "3) HTTP"
read -p "Enter your choice (1-3): " protocol_choice

case $protocol_choice in
  1) PROTOCOL="udp" ;;
  2) PROTOCOL="tcp" ;;
  3) PROTOCOL="http" ;;
  *) echo "Invalid choice"; exit 1 ;;
esac

echo "Select a service name:"
echo "1) Gateway"
echo "2) Writer"
echo "3) Reader"
read -p "Enter your choice (1-3): " service_choice

case $service_choice in
  1) SERVICE="gateway" ;;
  2) SERVICE="writter" ;;
  3) SERVICE="reader" ;;
  *) echo "Invalid choice"; exit 1 ;;
esac

java -cp target/seglog-1.0-SNAPSHOT.jar br.dev.danielrl.App $PROTOCOL $SERVICE

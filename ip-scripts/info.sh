#!/bin/bash


bash query_logs.sh

read -p "Enter an IP address to fetch its location: " IP

bash fetch_location.sh "$IP"


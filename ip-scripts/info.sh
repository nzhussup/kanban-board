#!/bin/bash

# Run query_logs.sh to simulate log querying
bash query_logs.sh

# Prompt the user to input an IP
read -p "Enter an IP address to fetch its location: " IP

# Run fetch_location.sh with the user-provided IP
bash fetch_location.sh "$IP"


#!/bin/bash

# Check if an IP address is passed as an argument
if [ -z "$1" ]; then
    echo "Usage: $0 <IPv4 address>"
    exit 1
fi

IP=$1

# Validate the IPv4 address format
if [[ ! $IP =~ ^([0-9]{1,3}\.){3}[0-9]{1,3}$ ]]; then
    echo "Invalid IPv4 address format. Please enter a valid address."
    exit 1
fi

# Use the ipinfo.io API to get the location information
RESPONSE=$(curl -s "https://ipinfo.io/$IP/json")

# Check if the response contains an error
if echo "$RESPONSE" | grep -q "error"; then
    echo "Error: Unable to fetch location information for IP $IP."
    exit 1
fi

# Extract and display location details
CITY=$(echo "$RESPONSE" | grep '"city":' | awk -F: '{print $2}' | tr -d ' ",')
REGION=$(echo "$RESPONSE" | grep '"region":' | awk -F: '{print $2}' | tr -d ' ",')
COUNTRY=$(echo "$RESPONSE" | grep '"country":' | awk -F: '{print $2}' | tr -d ' ",')
LOC=$(echo "$RESPONSE" | grep '"loc":' | awk -F: '{print $2}' | tr -d ' ",')
ORG=$(echo "$RESPONSE" | grep '"org":' | awk -F: '{print $2}' | tr -d ' ",')
IP_DETAILS=$(echo "$RESPONSE" | grep '"ip":' | awk -F: '{print $2}' | tr -d ' ",')

echo "IP Details:"
echo "IP Address: $IP_DETAILS"
echo "City: $CITY"
echo "Region: $REGION"
echo "Country: $COUNTRY"
echo "Coordinates: $LOC"
echo "Organization: $ORG"


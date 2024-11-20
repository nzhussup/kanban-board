#!/bin/bash

LOGS_DIR="k8s-logs"
mkdir -p $LOGS_DIR

NAMESPACE="default"
APP_LABEL="kanban-service"

PODS=$(kubectl get pods -n $NAMESPACE -l app=$APP_LABEL -o jsonpath='{.items[*].metadata.name}')

MERGED_LOG_FILE="$LOGS_DIR/merged_ip_logs.log"
> $MERGED_LOG_FILE 


for POD in $PODS; do
    echo "Fetching logs from pod: $POD"
    kubectl exec $POD -n $NAMESPACE -- cat /app/logs/ip.log | grep "Request from IP" >> $MERGED_LOG_FILE
    echo -e "\n\n# End of logs from $POD\n\n" >> $MERGED_LOG_FILE
done

echo "Filtered logs have been merged and saved to $MERGED_LOG_FILE"
cat k8s-logs/merged_ip_logs.log

echo -e "\nDistinct IP addresses:"
grep "Request from IP" $MERGED_LOG_FILE | awk -F "Request from IP: " '{print $2}' | awk '{print $1}' | sort | uniq
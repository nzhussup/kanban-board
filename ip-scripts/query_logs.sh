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

if [ ! -f "$MERGED_LOG_FILE" ]; then
    echo "Log file '$MERGED_LOG_FILE' does not exist. Please run the log merge script first."
    exit 1
fi

echo "Enter the date (DD.MM.YYYY):"
read DATE_INPUT

if [[ ! "$DATE_INPUT" =~ ^[0-9]{2}\.[0-9]{2}\.[0-9]{4}$ ]]; then
    echo "Invalid date format. Please ensure the format is DD.MM.YYYY."
    exit 1
fi

DAY=$(echo $DATE_INPUT | cut -d '.' -f 1)
MONTH=$(echo $DATE_INPUT | cut -d '.' -f 2)
YEAR=$(echo $DATE_INPUT | cut -d '.' -f 3)

if ! echo "$DAY" | grep -E '^[0-9]+$' > /dev/null || ! echo "$MONTH" | grep -E '^[0-9]+$' > /dev/null || ! echo "$YEAR" | grep -E '^[0-9]+$' > /dev/null; then
    echo "Invalid date format. Only numbers are allowed."
    exit 1
fi

DATE_STRING="$YEAR-$MONTH-$DAY"

echo "Displaying logs for $DATE_STRING..."

grep "$DATE_STRING" "$MERGED_LOG_FILE"

if [ $? -ne 0 ]; then
    echo "No logs found for $DATE_STRING."
fi

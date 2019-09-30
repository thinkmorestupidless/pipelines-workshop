#!/bin/bash

DEFAULT_DATASET="../data-generator/target/generated-payments.json"
if [ "$1" == "" ]
then
  RESOURCE=$DEFAULT_DATASET
else
  RESOURCE="$1"
fi

DEFAULT_ROUTE_HOST="http://localhost:3002"
if [ "$2" == "" ]
then
  ROUTE_HOST=$DEFAULT_ROUTE_HOST
else
  ROUTE_HOST="$2"
fi

echo "Sending $RESOURCE to $ROUTE_HOST"

#ROUTE_HOST=http://sherlock-pipeline.apps.purplehat.lightbend.com/ingress
#ROUTE_HOST=http://sherlock-pipeline.apps.gsa2.lightbend.com/ingress

for str in $( cat $RESOURCE ); do
  echo Sending $str
  curl -i \
  -X POST $ROUTE_HOST \
  -u assassin:4554551n \
  -H "Content-Type: application/json" \
  --data "$str"
done

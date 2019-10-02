#!/bin/bash

PORT=$1
DATA=$2

curl -i -X POST http://localhost:"$PORT" -H "Content-Type: application/json" --data @data/valid-card-payment.json

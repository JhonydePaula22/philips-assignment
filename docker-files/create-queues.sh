#!/usr/bin/env bash

echo "Creating queues"
docker exec local-stack-aws /bin/sh -c "aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name reprocess-event-queue.fifo --attributes FifoQueue=true; aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name propagate-event-queue.fifo --attributes FifoQueue=true"


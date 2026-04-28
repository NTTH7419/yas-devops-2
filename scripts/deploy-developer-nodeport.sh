#!/bin/bash
# Script to patch Kubernetes Services to NodePort and retrieve access URL

NAMESPACE="yas"

echo "Checking services in namespace $NAMESPACE..."

# Get all deployment services
SERVICES=$(kubectl get svc -n $NAMESPACE -o jsonpath='{.items[*].metadata.name}')

for SVC in $SERVICES; do
  if [[ "$SVC" != *"postgres"* && "$SVC" != *"redis"* && "$SVC" != *"kafka"* ]]; then
    echo "Patching service $SVC to NodePort..."
    kubectl patch svc $SVC -n $NAMESPACE -p '{"spec": {"type": "NodePort"}}'
  fi
done

echo "=================================================="
echo "Access Information for Developers:"
NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[0].address}')

kubectl get svc -n $NAMESPACE -o wide | grep NodePort | awk -v ip="$NODE_IP" '{print "Service: " $1 " -> URL: http://" ip ":" substr($5, index($5, ":")+1, 5)}'
echo "=================================================="
echo "Note: Please update your local /etc/hosts file to map the domain names to $NODE_IP if required."

echo "starting the minicube"
minikube start

cd k8s/

echo "starting the pods"
kubectl apply -f configmap.yml
kubectl apply -f secrets.yml
kubectl apply -f namespace.yml
kubectl apply -f redis-deployment.yml
kubectl apply -f db-deployment.yml
kubectl apply -f kanban-service-deployment.yml
kubectl apply -f prometheus-deployment.yml
kubectl apply -f grafana-deployment.yml
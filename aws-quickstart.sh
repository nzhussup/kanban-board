echo "Updating and upgrading apt"

sudo apt update && sudo apt upgrade -y

echo "Installing K3s"

curl -sfL https://get.k3s.io | sh -
alias kubectl="k3s kubectl"
sudo chmod 644 /etc/rancher/k3s/k3s.yaml

echo "Starting pods"

cd k8s/

kubectl apply -f secrets.yml
kubectl apply -f configmap.yml
kubectl apply -f namespace.yml
kubectl apply -f redis-deployment.yml
kubectl apply -f db-deployment.yml
kubectl apply -f kanban-service-deployment.yml
kubectl apply -f prometheus-deployment.yml
kubectl apply -f grafana-deployment.yml
docker build -t aurora-iam-app .

or

docker build --platform linux/amd64 -t aurora-iam-app .


docker run -d --name aurora-iam-app-container -e AWS_ACCESS_KEY_ID=*** -e AWS_SECRET_ACCESS_KEY=**** aurora-iam-app

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 393604994235.dkr.ecr.us-east-1.amazonaws.com

aws ecr create-repository --repository-name aurora-iam-app --region us-east-1

docker tag aurora-iam-app:latest 393604994235.dkr.ecr.us-east-1.amazonaws.com/aurora-iam-app:latest

docker push 393604994235.dkr.ecr.us-east-1.amazonaws.com/aurora-iam-app:latest


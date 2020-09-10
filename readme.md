# Secure multi-party computation
This project is for the multi-party computation. In total project, we used privacy-preserving computation framework to integrate the Geoinformation data between two parties without leaking any information

## Environment

For a true deployment, you need in Linux server. Here, we used CentOS7.2. Also, you should have java environment and so on. 
In this project, we assumed that only two the participators with their private geoinformation data. They want to get the result without expose their information to anyone.

## Roles and functions
we have at least two servers to play the part of two participators. One is named Starter, another is Accepter.
Starter have to run "Kstart" "LocalmoranStart" and "moranstart", which represent three algorithoms respectively. In addition, Accepter need run others.
run the servers, then you can see the API list on the pages.

### Deployment multi-party computation framework
we used multi-party computation framework. You should start the servers and the data will cryptographically transfer and compute. 
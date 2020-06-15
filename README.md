# microservices-core

This live session describes three simple services used in most distributed microservice architecture systems. We use Go for creating a reverse proxy and Java for service discovery and registering profiles.

The services use heartbeats to stay registered. We use the MySQL database in service registry and profile. The applications are written using Spring Boot, with their jar files running in AWS.

The Gateway service is written in Go. Every service registers it's IP and ports to the service registry. The EC2 instance running the gateway and profile services is different from the one hosting the service registry.

We confirm that the services work using a GET and POST request using curl from the local machine.

Code: https://github.com/coding-parrot/microservices-core

System Design Video Course: https://get.interviewready.io/courses/system-design-interview-prep
<br/>System Design book - https://amzn.to/2yQIrxH
<br/>System Design Playlist: https://www.youtube.com/playlist?list=PLMCXHnjXnTnvo6alSjVkgxV-VH6EPyvoX
<br/><br/>
You can follow me on:
<br/>Quora: https://www.quora.com/profile/Gaurav-Sen-6
<br/>LinkedIn: https://www.linkedin.com/in/gaurav-sen-56b6a941/
<br/>Twitter: https://twitter.com/gkcs_

# EAD AuthUser v2

> docker run -it --name postgres -e POSTGRES_PASSWORD=root -p 5432:5432 -d postgres

- Rodar o projeto no modo 
> mvn spring-boot.run -Dspring-boot.run.arguments=--logging.level.com.ead=TRACE


#### Executar o projeto

- Rodar o projeto: https://github.com/priscilasanfer/service-registry
- Rodar o projeto: https://github.com/priscilasanfer/api-gateway
- Rodar o projeto: https://github.com/priscilasanfer/ead-course
- Rodar a aplicação

Versão 2 utiliza comunicação sincrona utlizando de eventos e assincrona entre os microservices

Verificar as configs do projeto:
- http://localhost:8888/ead-authuser-service/main

Atualizar as configs sem precisar reiniciar a aplicação:
- http://localhost:8087/ead-authuser/actuator/refresh


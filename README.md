## Java ORM

Vamos a usar los distintos ORM que tenemos para usar con Java:

- [x] JPA with Hibernate
- [x] Mybatis
- [ ] Hibernate Reactive

Lo único que necesitamos es arrancar una base datos de MariaDB con los siguiente comandos

docker cli

```
docker run --detach --name mariadb --env MARIADB_USER=user --env MARIADB_PASSWORD=password --env MARIADB_ROOT_PASSWORD=password --env
MARIADB_DATABASE=demo -p 3306:3306 mariadb:latest

docker logs -f mariadb
```

Docker compose

```
docker-compose up &
```

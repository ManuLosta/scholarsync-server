# ScholarSync Server
### Start PostgreSQL
```bash
docker run --name scholarsync -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=scholarsync -d -p 5432:5432 postgres
```
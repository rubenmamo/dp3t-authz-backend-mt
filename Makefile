######################
#      Makefile      #
######################

RUSTY_SWAGGER = rusty-swagger

all: clean all1
all1: clean updateproject
no: clean updateproject updatedoc swagger
docker-build: updateproject docker
doc: updatedoc swagger

updateproject:
	mvn -f dpppt-authz-backend/pom.xml install -DskipTests
updatedoc:
	mvn springboot-swagger-3:springboot-swagger-3 -f dpppt-authz-backend/pom.xml
	cp dpppt-authz-backend/generated/swagger/swagger.yaml documentation/yaml/sdk.yaml

swagger:
	cd documentation; $(RUSTY_SWAGGER) --file ../dpppt-backend-sdk//generated/swagger/swagger.yaml

docker:
	cp dpppt-authz-backend/target/dpppt-authz-backend-1.0.0-SNAPSHOT.jar ws-authz/ws/bin/dpppt-authz-backend-1.0.0.jar
	docker build -t peppptdweacr.azurecr.io/dpppt-mt-authz:latest ws-authz/

clean:
	@rm -f documentation/*.log documentation/*.aux documentation/*.dvi documentation/*.ps documentation/*.blg documentation/*.bbl documentation/*.out documentation/*.bcf documentation/*.run.xml documentation/*.fdb_latexmk documentation/*.fls documentation/*.toc

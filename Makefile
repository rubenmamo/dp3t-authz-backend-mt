######################
#      Makefile      #
######################

RUSTY_SWAGGER = rusty-swagger
DOCKER_REPO = peppptdweacr.azurecr.io
IMAGE_TAG = latest
TARGET_ENV = dev

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
	docker build --build-arg targetenv=${TARGET_ENV} -t ${DOCKER_REPO}/dpppt-mt-authz:${IMAGE_TAG} ws-authz/

clean:
	@rm -f documentation/*.log documentation/*.aux documentation/*.dvi documentation/*.ps documentation/*.blg documentation/*.bbl documentation/*.out documentation/*.bcf documentation/*.run.xml documentation/*.fdb_latexmk documentation/*.fls documentation/*.toc

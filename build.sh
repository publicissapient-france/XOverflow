#!/bin/bash
set -euo pipefail

echo "[-- Building UI --]"
pushd ./ui

# save package version in env variable
export XOVERFLOW_UI_VERSION=$(npm version --json | jq -r '."xoverflow-ui"')

#cp config/config.template.tpl docker/delivery/config.template.tpl
docker build --no-cache -t="xebiafr/xoverflow-ui:builder" docker/builder/
containerId=$(docker create -e "XOVERFLOW_UI_VERSION=${XOVERFLOW_UI_VERSION}" xebiafr/xoverflow-ui:builder)
docker cp $(pwd)/. ${containerId}:/src/
docker start -a $containerId
docker cp ${containerId}:/target/ $(pwd)/docker/
rc=$(docker inspect -f {{.State.ExitCode}} $containerId)
docker rm $containerId
if [[ $rc != 0 ]]; then
  exit $rc
fi
popd

if [[ "$#" -eq 0 ]]; then
  echo "[-- Building BACK --]"
  rm -rf src/main/resources/webapp || true
  mkdir -p src/main/resources/webapp

  tar zxvf ui/docker/target/xoverflow-ui.tar.gz -C src/main/resources/webapp
  docker run -it --rm  -v "$PWD":/usr/src/mymaven -w /usr/src/mymaven maven:3-jdk-9 /bin/bash -c 'mkdir -p /root/.m2/ && mvn clean install && chmod -R 777 target'

  mkdir -p target/docker/ui | true

  cp src/main/docker/local/Dockerfile target/docker/
  artifact=$(ls runner/target | egrep xoverflow-.*.jar)
  cp runner/target/$artifact target/docker/xoverflow.jar
  docker build -t="xebiafr/xoverflow-ui" target/docker/
fi

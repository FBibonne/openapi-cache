#! /bin/bash

mvn -B install --file metadata-oas/pom.xml
mvn -B install --file metadata-modelapi/pom.xml
mvn -B install --file metadata-interfaceapi/pom.xml
mvn -B -Pnative package --file metadata-webimpl/pom.xml

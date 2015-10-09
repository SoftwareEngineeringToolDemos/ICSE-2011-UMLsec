#!/bin/sh
mkdir -p -m 0777 bin/umlsec/umlMetamodel
cp umlMetamodel/* bin/umlsec/umlMetamodel
mkdir -p -m 0777 bin/Prolog
cp Prolog/* bin/Prolog
cp mapping.xml bin/mapping.xml
cp log4j.cfg bin/log4j.cfg
cp umlsec.properties bin/umlsec.properties

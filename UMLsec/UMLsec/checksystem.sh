#!/bin/sh
# set -x
# debian's java
# $JAVA=java
# SUN's java
JAVA=/usr/bin/java



export CLASSPATH=".:../lib/aspectjrt.jar:../lib/junit-4.5.jar:../lib/jdom.jar:../lib/log4j-1.2.15.jar:../lib/jmi.jar:../lib/jmiutils.jar:../lib/mdr.jar:../lib/mdrapi.jar:../lib/mof.jar:../lib/nbmdr.jar:../lib/openide.jar:../lib/servlet.jar:../lib/cos.jar:../lib/JFlex.jar:../lib/crimson.jar:../lib/castor-0.9.3.21-xml.jar:../lib/xerces.jar:javax/jmi/primitivetypes/PrimitiveTypesPackage.class:./tum:../lib/appframework-1.0.3.jar:../lib/swing-worker.jar:../lib/swing-layout.jar:../lib/xml.jar"
#cd `pwd`/bin
cd bin
$JAVA open.umlsec.tools.checksystem.gui.SystemVerificationLoader


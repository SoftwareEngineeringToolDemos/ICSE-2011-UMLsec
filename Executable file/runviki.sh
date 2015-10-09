#!/bin/sh
# set -x
# debian's java
# $JAVA=java
# SUN's java
JAVA=/usr/bin/java



export CLASSPATH=".:../lib/aspectjrt.jar:../lib/junit-4.5.jar:../lib/jdom.jar:../lib/log4j-1.2.15.jar:../lib/jmi.jar:../lib/jmiutils.jar:../lib/mdr.jar:../lib/mdrapi.jar:../lib/mof.jar:../lib/nbmdr.jar:../lib/openide.jar:../lib/servlet.jar:../lib/cos.jar:../lib/JFlex.jar:../lib/crimson.jar:../lib/castor-0.9.3.21-xml.jar:../lib/xerces.jar:javax/jmi/primitivetypes/PrimitiveTypesPackage.class:./tum"
cd `pwd`/bin
$JAVA tum.umlsec.viki.framework.Loader
# java tum.umlsec.viki.framework.Loader %1 %2 %3 %4 %5 %6 %7 %8 %9

exit
#!/bin/sh
set -x 
export CLASSPATH="..:.:../lib/aspectjrt.jar:../lib/junit-4.5.jar:../lib/jdom.jar:../lib/log4j-1.2.15.jar:../lib/jmi.jar:../lib/jmiutils.jar:../lib/mdr.jar:../lib/mdrapi.jar:../lib/mof.jar:../lib/nbmdr.jar:../lib/openide.jar:../lib/servlet.jar:../lib/cos.jar:../lib/jflex.jar:../lib/crimson.jar:../lib/xerces.jar:../lib/xml.jar:../lib/castor-0.9.3.21-xml.jar:../javax/jmi/primitivetypes/PrimitiveTypesPackage.class"
cd bin


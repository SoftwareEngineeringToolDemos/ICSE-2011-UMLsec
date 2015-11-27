How to build and use the UMLsec-Checksystem tool under windows
--------------------------------------------------------------
* the following step (marked with "{") isnt needed anymore due the buildonlinux.xml now uses the provided JUnit libraries (v4.5) from the lib folder
	{* edit the file buildonwin.xml in order to set the path to the
	{eclipse installation in Line 9 (needed for the JUnit libraries)
	{e.g. <property name="ECLIPSE_HOME" value="C:\Development\eclipse"/>

* build it with ant, using the buildonwin.xml script (ant -f path\to\buildonwin.xml)
/!\ don't forget the '-f'

* before running the tool for the first time, execute the copyMetamodel.bat batch file

* run it with the checksystem.bat file (version 2.0 / under developement)
OR
* run it with runonwin.bat (version 1.0 revision 54 / under developement)

How to build the UMLsec-Checksystem tool under GNU/Linux
--------------------------------------------------------------
* the following step (marked with "{") isnt needed anymore due the buildonlinux.xml now uses the provided JUnit libraries (v4.5) from the lib folder
	{* edit the file buildonlinux.xml in order to set the path to the
	{eclipse installation in Line 9 (needed for the JUnit libraries)
	{e.g. <property name="ECLIPSE_HOME" value="/opt/eclipse"/>

* build it with ant, using the buildonlinux.xml script (ant -f path\to\buildonlinux.xml)
/!\ don't forget the '-f'

* before running the tool for the first time, execute the copyMetamodel.sh shell script

* run it with the checksystem.sh file (version 2.0) under developement
OR
* run it with runviki.sh (version 1.0 revision 54) under developement

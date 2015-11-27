set JAVA_HOME=C:\java\j2sdk1.4.1_02
set JAVA_BIN=C:\java\j2sdk1.4.1_02\bin\

set PATH=%PATH%;C:\Java\ant\bin;C:\java\j2sdk1.4.1_02\bin;C:\Java\JFlex\bin;c:\java\classgen\bin



del /Q .\..\src\tum\umlsec\viki\tools\dynaviki\model\scanner\*.*
del /Q .\tum\umlsec\viki\tools\dynaviki\model\scanner\*.*

@echo ===============================================================================
@echo                         JFLEX
@echo ===============================================================================
call jflex umlsec.flex

@echo ===============================================================================
@echo                         CUP
@echo ===============================================================================
java java_cup.Main < umlsec.cup

@echo ===============================================================================
@echo                         CLASSGEN
@echo ===============================================================================
call classgen -f -visitor umlsec.cl


copy Lexer.java .\..\src\tum\umlsec\viki\tools\dynaviki\model\scanner
copy sym.java .\..\src\tum\umlsec\viki\tools\dynaviki\model\scanner
copy parser.java .\..\src\tum\umlsec\viki\tools\dynaviki\model\scanner

copy .\tum\umlsec\viki\tools\dynaviki\model\scanner\*.java .\..\src\tum\umlsec\viki\tools\dynaviki\model\scanner

@pause

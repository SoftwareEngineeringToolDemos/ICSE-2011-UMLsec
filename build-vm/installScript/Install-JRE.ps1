#Set Java path
$JavaFilePath = 'c:\vagrant\software'
$JavaInstallerFile = 'jre-7u79-windows-i586.exe'
$JavaRootPath = 'C:\Program Files\Java\jre7'


##Install prerequired software -- Execute java installation files
set-alias EXECUTE "C:\vagrant\software\jre-7u79-windows-i586.exe"

EXECUTE /s

Write-Host "Finish installation"
Write-Host "Setting successful, JRE has been installed."


##Make the tool visible -- make users can see UMLsec when windows start

Write-Host "Start executing UMLsec."
#Get desptop location

Write-Host "Get desktop."
$desktop = [Environment]::GetFolderPath("Desktop")


Write-Host "Start copy needed files."

# Copy needed Files
# UMLsec
Copy-Item -path c:\vagrant_UMLsec\UMLsec\* -Destination $desktop -Recurse
# License file, youtube link
Copy-Item -path c:\vagrant_UMLsec\build-vm\VM_Essential\* -Destination $desktop -Recurse

# bat for open exploer.exe
Copy-Item -path c:\vagrant_UMLsec\build-vm\installScript\test.bat -Destination "C:\Users\IEUser\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup" -Recurse
Move-Item -path $desktop\eula.lnk -Destination $desktop\License\

#Invoke-Item -path C:\vagrant\UMLsec\UMLsec.bat



Write-Host "create UMLsec shortcut."


# Create shortcut for UMLsec ** fails -- If execute UMLsec from this shortcut, will see a error message that "cannot find main class". (not solved yet). 


Write-Host "Finish preparing UMLsec."

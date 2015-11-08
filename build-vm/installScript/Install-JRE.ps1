$JavaFilePath = 'c:\vagrant\software'
$JavaInstallerFile = 'jre-7u79-windows-i586.exe'

#function Enable-Java
#{

#    $psi = New-Object System.Diagnostics.ProcessStartInfo
#    $psi.WorkingDirectory = "$JavaFilePath"
#    $psi.FileName = "$JavaInstallerFile"
#    $psi.Arguments = "/s /q /norestart /log `'$JavaFilePath\JavaInstall.log`'"
#    $psi.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Minimized;

#    $s = [System.Diagnostics.Process]::Start($psi);
#    $s.WaitForExit();
#	Write-Host "Finish installation"
#}

#function Enable-Java2
#{

#	$installDir = 'C:\Program Files\Java\'
#	$arguments = @(
#		'/s',
#		"/v/qn`" INSTALLDIR=\`"$installDir\`" REBOOT=Supress IEXPLORER=0 MOZILLA=0 /L \`"install.log\`"`""
#	)

#	$proc = Start-Process "$JavaFilePath\$JavaInstallerFile" -ArgumentList $arguments -Wait -PassThru
#	if($proc.ExitCode -ne 0) {
#		Throw "ERROR"
#	}
#	Write-Host "Finish installation"
#}

#Enable-Java2

#Invoke-Item “C:\vagrant\software\jre-7u79-windows-i586.exe /s”

set-alias EXECUTE "C:\vagrant\software\jre-7u79-windows-i586.exe"

EXECUTE /s

Write-Host "Finish installation"
Write-Host "Setting successful, JRE has been installed."
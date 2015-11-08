#README for build_vm

Environment
------
__VM:__ Windows 7 with IE 11 (same with previous hand made VMs)

__Script:__ for Windows only (vagrant + powershell)

__External sources:__ [modernIE/w7-ie11](https://atlas.hashicorp.com/modernIE/boxes/w7-ie11),[Vagrant Provision Reboot Plugin](https://github.com/exratione/vagrant-provision-reboot) 

__prerequisite software:__ jre-7u79-windows-i586


Folder content
-----
This build-vm folder contains following things:
- __README.md file__
- __Vagrantfile__
  This is the script (& configuration) file of the VM
- __vagrant-provision-reboot-plugin.rb__
  This is a reboot plugin for vagrant, which let VMs to reboot during executing provisions.
- __installScript folder__
  This folder contains scripts of settings, installations.
- __software folder__
  The perquisite JRE is in the folder.



How to create
-----
** this version of script only work for windows system.

1. download Vagrant from [this website](https://www.vagrantup.com/)
2. install Vagrant
3. download VirtualBox fomr [this website](https://www.virtualbox.org/wiki/Downloads)
4. install VirtualBox
5. download all files from [build-vm folder](https://github.com/SoftwareEngineeringToolDemos/ICSE-2011-UMLsec/tree/master/build-vm), to where you want to install VM.
6. (If in windows system)Open a command window
7. Enter to the folder where the downloaded files are (VM directory).
8. execute command "__vagrant up__"

Note:
------
- Login is not required.
- If the VM has not been executed in your computer before, it might take longer time to download and load th VM.
- The VM will reboot several time automatically to config environment and settings.

  host name change --> reboot --> Disable UAC --> reboot by plugin -->start installations.
- Please be patient, wait until message "Setting successful, JRE has been installed."
- The JRE is installed in c:\Program Files\Java\jre7

Reference and sources
-----
1. [Microsoft trial machine for vagrant](https://dev.windows.com/en-us/microsoft-edge/tools/vms/windows/)
2. [Setup modern.ie vagrant boxes](https://gist.github.com/andreptb/57e388df5e881937e62a)
3. [modernIE/w7-ie11](https://atlas.hashicorp.com/modernIE/boxes/w7-ie11)
4. [Vagrant environment with Windows 2008 R2 + SQL Server Express 2008](https://github.com/fgrehm/vagrant-mssql-express)
5. [java8-vagrant-box](https://github.com/shekhargulati/java8-vagrant-box/blob/master/cookbooks/java/README.md)
6. [Powershell can also be nice -Or Installing Java silently and waiting](http://ramblingsofaswtester.com/?p=552)
7. [How to install JDK from PowerShell silently](http://blag.koveras.org/2011/12/24/how-to-install-jdk-from-powershell-silently/)
8. [Vagrant Provision Reboot Plugin](https://github.com/exratione/vagrant-provision-reboot)
9. [ADD YOUR OWN CUSTOMIZATION TO VAGRANT BOXES](https://mkrmr.wordpress.com/2012/08/12/add-your-own-customization-to-vagrant-boxes/)
10. [How to install JDK from PowerShell silently](http://blag.koveras.org/2011/12/24/how-to-install-jdk-from-powershell-silently/)
11. [Calling Executable Files in PowerShell](http://www.andyparkhill.co.uk/2012/02/calling-executable-files-in-powershell.html)
12. [Powershell install Java silently](http://stackoverflow.com/questions/29799158/powershell-install-java-silently)
13. [VagrantDocs](http://docs-v1.vagrantup.com/v1/docs/getting-started/index.html)
14. [Windows : Disable UAC (User Account Control) with CMD Script & PowerShell](http://juventusitprofessional.blogspot.com/2014/05/windows-disable-uac-user-account.html)
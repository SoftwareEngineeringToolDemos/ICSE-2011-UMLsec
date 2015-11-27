=====================================
This README document is created by 

Name:Yi-Chun Chen
Unity ID: ychen74

Course: CSC 510, Software Engineering
Team: Onslow
=====================================

This document includes:
0. Execute the UMLsec
1. How to get start -- functions' toturials
2. The supposed functions in paper (execution result and steps described in paper).



=====================
0. Execute the UMLsec
=====================

1. When the VM is all set, the folder of UMLsec will automatically show up

2. For general execution in win:
double click "UMLsec.bat" to execute UMLsec.
(In Desktop\UMLsec\)



===========================================
1. How to get start -- functions' toturials
===========================================

(* the following words are quoted from the paper)
UMLsec - UML security extension is a tool that support the security hardening of evolving UML models.


In order to enforce that the system remains secure despite its later evolution,
one would usually have to apply the secure software development methodology from scratch.
We therefore need a secure software development approach that 
supports maintaining the needed levels of security even through later software evolution.
The work presented in this demo arims to take this into account.


(* end quote).


functions include:
1.  Activity-Diagram Parser 
2.  Activity-Diagram/Permission Analyser 
3.  Activity-Diagram/Permission Analyser II 
4.  Activity-Diagram RBAC Analyser 
5.  RiskFinder 
6.  BusinessProcessMonitor 
7.  Sequence-Diagram/Permission Analyser 
8.  Sequence-Diagram Crypto FOL-Analyser 
9.  Statechart Parser 
10. Security requirements consistency verification 
11. Automated Code Generation of RBAC Mechanisms 
12. UMLsec and UMLseCh Notation Analyser - Command DumpAllModelElement 
13. UMLseCh Static Check - Secure Dependency 

In the paper ICSE 2011- Automated Security Hardening for Evolving UML Models,
they demoed function 13.

Therefore, in this getting start document. Only datials of function 13 in provides.
The tutorial of other functions please see:  https://www-secse.cs.tu-dortmund.de/jj/umlsectool/users_new.html

-------------------------------
Function 13.


UMLseCh Static Check - Secure Dependency


Demo examples of this function  are in Desktop\UMLsec\UMLsec Examples\UMLseCh Static Check Examples\


(* some of the following words are quoted from the tool's website)

"UMLseCh Static Check" verifies if a model is still secure after an evolution of this model. 


1. Load diagram. To achieve this, "Model" and "Load" need to be clicked in this order,

   
   Check success example.
   and select "UMLseCh_SecDep_success.zargo"
   (path: Desktop\UMLsec Examples\UMLseCh Static Check Examples\)
    
   check failure example.
   Or select "UMLseCh_SecDep_failatCheckCNeu.zargo"
   (path: Desktop\UMLsec Examples\UMLseCh Static Check Examples\)


2. Add tool. To add the tool "UmlseCh Static Check - Command: Secure Dependency". Click on "Tools" and "Add". 
   Then click on "close" to finish the process.

3. Subsequently, in "detail" tab, mark the tool "UmlseCh Static Check" as well as the command "Secure Dependency" and click on "Start System Analysis".



The check results will be shown in the textbox. 




==================================
2. The supposed functions in paper
=================================


Result (what showed in textbox): 

STEP 1:                                               
Filling the delta-verctors: add, delete and substitute

STEP 2a:                                              
Checking that the same model element is not          
added and deleted (by UMLseCh) at the same time.

STEP 2b:                                              
Checking that no element of a substition is an element 
of the add or delete vector. 

STEP 3:                                                
Checking the secure dependency UMLseCh conditions. 


-------------------------------
Steps described in paper:

(* these words are quoted from the paper)

Step one is a proprocessing step that creates the delta-vector from the information provided bt the user.

Step two checks the consistency of the UML model.
It is checked that the same model element is not added and deleted by the hardening algorithm at the same time
and that no element of a substition is an element of the add or selete vector.

Step three ckecks the constraint associated with the <<secure dependency>> stereotype.


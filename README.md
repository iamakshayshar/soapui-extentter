# SOAPUI-Extentter
This is an easy utility create to generate extent report based on SOAPUI project execution. This utility supports SOAPUI PROJECT execution report and SOAPUI TESTSUITE execution report.

# What is Extent Report?
Extent Report is a HTML based reporting library which is used for making excellent execution reports and simple to understand. This can be used with TestNG automation framework when using Selenium WebDriver for JAVA projects. In this utility I have utilized the listener capability of SOAPUI to generate Extent Report based on PROJECT execution and TESTSUITE execution. Extent Report provide good detailed execution report with graphical representation of Pass and Failed testcases. 

As an Automation Test Engineer we are supposed to find the defects and report it to the team in a simple way which should be easy to understand. In Automation Testing, importance of reporting is so high. Extent Report provides the easy way to report the Pass and Failed testcases.

Lets understand more on what is required to use this utility and how does it works

# Pre-requisite to use SOAPUI-Extentter:
1.    Java should be installed (by default it comes with SOAPUI installation)

2.    You should have the admin access of SOAPUI installation folder.


# How SOAPUI-Extentter works:
SOAPUI doesn’t support reporting by default and thus with all the good features what SOAPUI provides there is always a limitation that we can't have good report of our execution. With SOAPUI-Extentter library, one can generate extent report with just placing one jar file. All the code/libraries required to generate extent report is handled in this repository.

This utility is create by utilizing the listener feature of SOAPUI. SOAPUI provides three main listeners as below,
* ProjectRunListener
* TestSuiteRunListener
* TestRunListener

ProjectRunListener listens to execution at project level and provides methods as beforeRun, afterRun, beforeTestSuite and afterTestSuite. All we have to do is to understand when this methods are called when we are executing the overall project. Similarly TestSuiteRunListener listens to execution at TestSuite level and provides methods as afterRun, beforeRun, beforeTestCase and afterTestCase. I have implemented these methods to generate extent report at TestSuite level.

# Steps to use SOAPUI-Extentter utility:
There are two ways to use this utility. 
*If one wants to use this as it is follow the steps below,

1. Copy the jar file from "ExportedJar" folder and place it under "${SOAPUI_HOME}/bin/ext" folder.
2. Copy the xml file from "Listener-XML" folder and place it under "${SOAPUI_HOME}/bin/listeners" folder.
3. Execute the project or testsuite and you will have extent report generate in "Reports" folder where your project XML is saved.

*If one wants to customize this utility then follow the steps below,

1. Complete the change as what is required.
2. Build the project 
3. Generate runnable jar of the project.
4. Copy the generate jar file and place it under "${SOAPUI_HOME}/bin/ext" folder.
5. Execute the project or testsuite to validate the change.

# AddOn with this utility:
1. As an addOn to this utility, I have also added code for data driven testing support inside SOAPUI. This git repo also have one sample project (calculator-soapui-project_Updated.xml) which have all the groovy code for data driven testing.

# Support:
1. This utlity is tested with SOAPUI version 5.3.0, 5.4.0, 5.5.0 & 5.6.0.
2. This utlity is testing on JAVA version "1.8.0_221".





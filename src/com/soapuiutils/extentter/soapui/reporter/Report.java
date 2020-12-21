package com.soapuiutils.extentter.soapui.reporter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.testsuite.TestProperty;
import com.eviware.soapui.model.testsuite.TestStepResult;

/*
 * Author : Akshay Sharma
 * Email : akshay.sharma979@gmail.com
 * Description : This class is created for Reporting initialization and report publishing.
 */

public class Report {

	private ExtentReports reports;
	private static Map extentNodeMap;
	private static Map extentTestMap;

	public Report(String reportPath, String reportName) {
		try {
			String fileName = getReportName(reportName);
			new File(reportPath).mkdirs();
			String path = reportPath + File.separator + fileName;
			ExtentSparkReporter spark = new ExtentSparkReporter(path);
			spark.config().setEncoding("utf-8");
			spark.config().setDocumentTitle("Automation Report " + reportName);
			spark.config().setReportName(reportName + " Automation Test Results");
			spark.config().setTheme(Theme.STANDARD);
			spark.config().setTimelineEnabled(true);

			this.reports = new ExtentReports();
			reports.setSystemInfo("Owner", "Akshay Sharma");
			reports.setSystemInfo("Maintainer", "<a href='mailto:akshay.sharma979@gmail.com'>akshay.sharma979@gmail.com</a>");
			reports.setSystemInfo("Executor", System.getProperty("user.name"));
			reports.attachReporter(spark);

			extentTestMap = new HashMap();
			extentNodeMap = new HashMap();
		} catch (Exception e) {
			String exceptionMessage = "Exception occurred for Report object creation as " + e.toString();
			SoapUI.log(exceptionMessage);
			System.exit(1);
		}
	}

	public static String getReportName(String reportName) {
		String fileName = "AutomationReport_" + reportName + "_" + java.time.LocalDate.now() + ".html";
		return fileName;
	}

	public void createNode(String nodeName, String testSuiteId, String testCaseId) {
		ExtentTest testNode = null;
		String testCaseDesc = null;
		try {
			testCaseDesc = "<b> TestCase ID: </b>" + testCaseId;
			testNode = getTest(testSuiteId).createNode(nodeName, testCaseDesc);
			extentNodeMap.put(testCaseId, testNode);
		} catch (Exception e) {
			String exceptionMessage = " Exception occurred for Report method - createNode as ";
			SoapUI.log(exceptionMessage + e.toString());
		}
	}

	public synchronized ExtentTest getTestNode(String testSuiteId, String testCaseId) {
		ExtentTest et = null;
		try {
			if (extentNodeMap != null && !extentNodeMap.isEmpty() && extentNodeMap.get(testCaseId) != null) {
				et = (ExtentTest) extentNodeMap.get(testCaseId);
			} else {
				et = (ExtentTest) extentTestMap.get(testSuiteId);
			}
		} catch (Exception e) {
			String exceptionMessage = " Exception occurred for Report method - getTestNode as ";
			SoapUI.log(exceptionMessage + e.toString());
		}
		return et;
	}

	public synchronized static ExtentTest getTest(String testSuiteId) {
		ExtentTest et = null;
		try {
			et = (ExtentTest) extentTestMap.get(testSuiteId);
		} catch (Exception e) {
			String exceptionMessage = " Exception occurred for Report method - getTest as ";
			SoapUI.log(exceptionMessage + e.toString());
		}
		return et;
	}

	public synchronized void startTestLog(String testSuiteName, String testSuiteDesc, String testSuiteId) {
		ExtentTest currrentTest = null;
		try {
			if (!testSuiteName.isEmpty()) {
				if (!testSuiteDesc.isEmpty()) {
					testSuiteDesc = "<b> Description: </b>" + testSuiteDesc;
				}
				testSuiteDesc = testSuiteDesc + "<br><b> TestSuite ID: </b>" + testSuiteId;
				currrentTest = this.reports.createTest(testSuiteName, testSuiteDesc);
				extentTestMap.put(testSuiteId, currrentTest);
			} else {
				SoapUI.log("Test Suite name should not be null");
			}
		} catch (Exception e) {
			String exceptionMessage = " Exception occurred for Report method - startTestLog as ";
			SoapUI.log(exceptionMessage + e.toString());
		}
	}

	public void logInfo(TestStepResult testStepContext, String testSuiteId, String testCaseId) {
		try {
			String logText = testStepContext.getTestStep().getName();
			getTestNode(testSuiteId, testCaseId).info(logText);
		} catch (Exception e) {
			String exceptionMessage = " Exception occurred for Report method - logPass as ";
			SoapUI.log(exceptionMessage + e.toString());
		}
	}

	public void logPass(TestStepResult testStepContext, String testSuiteId, String testCaseId) {
		TestProperty Request;
		TestProperty Response;
		TestProperty GroovyScript;
		String actualReq = null;
		String actualRes = null;
		String actualData;
		String actualEndPoint = "Unable to fetch the endpoint information";
		String endPoint = "Unable to fetch the endpoint information";
		try {
			String logText = testStepContext.getTestStep().getName();

			if (testStepContext.getTestStep().getProperty("Request") != null
					|| testStepContext.getTestStep().getProperty("Response") != null
					|| testStepContext.getTestStep().getProperty("Result") != null) {
				SoapUI.log("Test Log Started for TestStep " + logText + " of TestCase " + testCaseId + " of TestSuite "
						+ testSuiteId);

				Request = testStepContext.getTestStep().getProperty("RawRequest");
				Response = testStepContext.getTestStep().getProperty("Response");
				GroovyScript = testStepContext.getTestStep().getProperty("Result");

				if (GroovyScript == null) {

					actualReq = Request.getValue();
					actualRes = Response.getValue();
					Markup mReqRes = MarkupHelper.createCodeBlock(actualReq, actualRes);

					endPoint = testStepContext.getTestStep().getProperty("Endpoint").getValue();

					if (endPoint.contains("#")) {
						String[] arrOfendPoint = endPoint.split("#", 0);

						String[] EndpointPartOne = arrOfendPoint[2].split("\\$", 2);
						String[] EndpointPartTwo = arrOfendPoint[4].split("}", 2);

						String envProjName = EndpointPartOne[0];
						String envEndPoint = EndpointPartTwo[0];

						String actualEnvironment = testStepContext.getTestStep().getTestCase().getTestSuite()
								.getProject().getProperty(envEndPoint).getValue();
						String actualEndpointName = envProjName + actualEnvironment;

						actualEndPoint = testStepContext.getTestStep().getTestCase().getTestSuite().getProject()
								.getProperty(actualEndpointName).getValue();

						actualData = "<br><b>\n\n------------- ENDPOINT DETAILS --------------\n\n</b><br>"
								+ actualEndPoint;

						getTestNode(testSuiteId, testCaseId).pass(logText + " <b>Check Details </b> " + actualData
								+ "<br><b>Below are the Actual Request and Response Data.</b>");

						getTestNode(testSuiteId, testCaseId).info(mReqRes);
					} else {
						actualData = "<br><b>\n\n------------- ENDPOINT DETAILS --------------\n\n</b><br>" + endPoint;

						getTestNode(testSuiteId, testCaseId).pass(logText + " <b>Check Details </b> " + actualData
								+ "<br><b>Below are the Actual Request and Response Data.</b>");

						getTestNode(testSuiteId, testCaseId).info(mReqRes);
					}
				} else {
					getTestNode(testSuiteId, testCaseId).pass(logText);
				}
			} else {
				getTestNode(testSuiteId, testCaseId).pass(logText);
			}
		} catch (Exception e) {
			String exceptionMessage = " Exception occurred for Report method - logPass as ";
			SoapUI.log(exceptionMessage + e.toString());
		}
	}

	public void logFail(TestStepResult testStepContext, String testSuiteId, String testCaseId) {
		TestProperty Request;
		TestProperty Response;
		TestProperty GroovyScript;
		String actualReq = null;
		String actualRes = null;
		String actualData;
		String actualEndPoint = "Unable to fetch the endpoint information";
		String endPoint = "Unable to fetch the endpoint information";
		ArrayList<String> failedMessages = new ArrayList<String>();
		try {
			String logText = testStepContext.getTestStep().getName();

			int failMsgLen = testStepContext.getMessages().length;
			for (int failMsgIter = 0; failMsgIter < failMsgLen; failMsgIter++)
				failedMessages.add(testStepContext.getMessages()[failMsgIter]);

			SoapUI.log("Value of testStepContext.getTestStep().getProperty(Reuest) is "
					+ testStepContext.getTestStep().getProperty("Request"));

			if (testStepContext.getTestStep().getProperty("Request") != null
					|| testStepContext.getTestStep().getProperty("Response") != null
					|| testStepContext.getTestStep().getProperty("Result") != null) {
				SoapUI.log("Test Log Started for TestStep " + logText + " of TestCase " + testCaseId + " of TestSuite "
						+ testSuiteId);

				Request = testStepContext.getTestStep().getProperty("RawRequest");
				Response = testStepContext.getTestStep().getProperty("Response");
				GroovyScript = testStepContext.getTestStep().getProperty("Result");

				if (GroovyScript == null) {

					actualReq = Request.getValue();
					actualRes = Response.getValue();
					Markup mReqRes = MarkupHelper.createCodeBlock(actualReq, actualRes);

					endPoint = testStepContext.getTestStep().getProperty("Endpoint").getValue();

					if (endPoint.contains("#")) {

						String[] arrOfendPoint = endPoint.split("#", 0);

						String[] EndpointPartOne = arrOfendPoint[2].split("\\$", 2);
						String[] EndpointPartTwo = arrOfendPoint[4].split("}", 2);

						String envProjName = EndpointPartOne[0];
						String envEndPoint = EndpointPartTwo[0];

						String actualEnvironment = testStepContext.getTestStep().getTestCase().getTestSuite()
								.getProject().getProperty(envEndPoint).getValue();
						String actualEndpointName = envProjName + actualEnvironment;

						actualEndPoint = testStepContext.getTestStep().getTestCase().getTestSuite().getProject()
								.getProperty(actualEndpointName).getValue();

						actualData = "<br><b>\n\n------------- ENDPOINT DETAILS --------------\n\n</b><br>"
								+ actualEndPoint
								+ "<br><b>\n\n------------- FAILURE REASON -----------------\n\n</b><br>"
								+ failedMessages.toString();

						getTestNode(testSuiteId, testCaseId).fail(logText + " <b>Failed. Check Details </b> "
								+ actualData + "<br><b>Below are the Actual Request and Response Data.</b>");

						getTestNode(testSuiteId, testCaseId).info(mReqRes);

						failedMessages.clear();
					} else {
						actualData = "<br><b>\n\n------------- ENDPOINT DETAILS --------------\n\n</b><br>" + endPoint
								+ "<br><b>\n\n------------- FAILURE REASON -----------------\n\n</b><br>"
								+ failedMessages.toString();

						getTestNode(testSuiteId, testCaseId).fail(logText + " <b>Failed. Check Details </b> "
								+ actualData + "<br><b>Below are the Actual Request and Response Data.</b>");

						getTestNode(testSuiteId, testCaseId).info(mReqRes);

						failedMessages.clear();
					}
				} else if (Request == null) {
					SoapUI.log("Test Log Started for TestStep " + logText + " of TestCase " + testCaseId
							+ " of TestSuite " + testSuiteId);

					actualData = "<br><b>\n\n------------- FAILURE REASON -----------------\n\n</b><br>"
							+ failedMessages.toString();

					getTestNode(testSuiteId, testCaseId).fail(logText + " <b>Failed. Check Details </b> " + actualData);
					failedMessages.clear();
				} else {
					getTestNode(testSuiteId, testCaseId).fail(logText + " <b>Failed.<b>");
				}
			} else {
				SoapUI.log("Test Log Started for TestStep " + logText + " of TestCase " + testCaseId + " of TestSuite "
						+ testSuiteId);

				actualData = "<br><b>\n\n------------- FAILURE REASON -----------------\n\n</b><br>"
						+ failedMessages.toString();

				getTestNode(testSuiteId, testCaseId).fail(logText + " <b>Failed. Check Details </b> " + actualData);
				failedMessages.clear();
			}

		} catch (Exception e) {
			String exceptionMessage = " Exception occurred for Report method - logFail as ";
			SoapUI.log(exceptionMessage + e.toString());
			SoapUI.log(e.getLocalizedMessage());
		}
	}

	public void endTestLog() {
		try {
			reports.flush();
			extentTestMap.clear();
			extentNodeMap.clear();
		} catch (Exception e) {
			String exceptionMessage = " Exception occurred for Report method - endTestLog as ";
			SoapUI.log(exceptionMessage + e.toString());
		}
	}

	public void addEnvironmentDetails(List<TestProperty> properties) {
		try {
			int propSize = properties.size();
			if (propSize != 0) {
				for (int propInterator = 0; propInterator < propSize; propInterator++) {
					reports.setSystemInfo(properties.get(propInterator).getName(),
							properties.get(propInterator).getValue());
				}
			}
		} catch (Exception e) {
			String exceptionMessage = " Exception occurred for Report method - addEnvironmentDetails as ";
			SoapUI.log(exceptionMessage + e.toString());
		}
	}
}

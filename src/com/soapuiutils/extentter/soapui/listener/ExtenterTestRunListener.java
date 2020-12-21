package com.soapuiutils.extentter.soapui.listener;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestRunListener;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestStepResult;

/*
 * Author : Akshay Sharma
 * Email : akshay.sharma979@gmail.com
 * Description : This class implements methods of TestRunListener of SOAPUI. This will listen to TestStep level actions.
 */

public class ExtenterTestRunListener implements TestRunListener {

	@Override
	public void beforeRun(TestCaseRunner runner, TestCaseRunContext context) {
		// SoapUI.log("Inside BeforeRun in TestRunListener - 5");
	}

	@Override
	public void afterRun(TestCaseRunner runner, TestCaseRunContext context) {
		// SoapUI.log("Inside AfterRun in TestRunListener - 8");
	}

	@Override
	public void beforeStep(TestCaseRunner arg0, TestCaseRunContext arg1) {
		// This one is not using in current soapui
	}

	@Override
	public void afterStep(TestCaseRunner paramTestCaseRunner, TestCaseRunContext paramTestCaseRunContext,
			TestStepResult paramTestStepResult) {
		// SoapUI.log("Inside AfterStep in TestRunListener - 7");
		try {
			if (ExtenterProjectRunListener.Projservice != null) {
				String testCaseId = paramTestCaseRunContext.getTestCase().getId();
				String testSuiteId = paramTestCaseRunContext.getTestCase().getTestSuite().getId();
				ExtenterProjectRunListener.Projservice.finishTestStepLogging(paramTestStepResult, testSuiteId,
						testCaseId);
			} else {
				String testCaseId = paramTestCaseRunContext.getTestCase().getId();
				String testSuiteId = paramTestCaseRunContext.getTestCase().getTestSuite().getId();
				ExtenterTestSuiteRunListener.TSservice.finishTestStepLogging(paramTestStepResult, testSuiteId,
						testCaseId);
			}
		} catch (Throwable t) {
			SoapUI.log("SOAPUIUtils Extentter Error in  beforeStep of TestRunListener " + t.getMessage());
		}
	}

	@Override
	public void beforeStep(TestCaseRunner paramTestCaseRunner, TestCaseRunContext context, TestStep testStep) {
		// SoapUI.log("Inside BeforeStep in TestRunListener - 6");
	}

}
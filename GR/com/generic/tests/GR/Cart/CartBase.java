package com.generic.tests.GR.Cart;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.xml.XmlTest;
import com.generic.setup.Common;
import com.generic.setup.LoggingMsg;
import com.generic.setup.SelTestCase;
import com.generic.setup.SheetVariables;
import com.generic.tests.GR.Cart.CartValidation;
import com.generic.util.ReportUtil;
import com.generic.util.SASLogger;
import com.generic.util.dataProviderUtils;

public class CartBase extends SelTestCase {

	// used sheet in test
	public static final String testDataSheet = SheetVariables.cartSheet;

	private static XmlTest testObject;
	private static ThreadLocal<SASLogger> Testlogs = new ThreadLocal<SASLogger>();
	LinkedHashMap<String, String> productDetails;

	@BeforeClass
	public static void initialSetUp(XmlTest test) throws Exception {
		testCaseRepotId = SheetVariables.cartCaseId;
		Testlogs.set(new SASLogger(test.getName() + test.getIndex()));
		testObject = test;
	}

	@DataProvider(name = "Carts", parallel = true)
	public static Object[][] loadTestData() throws Exception {
		// concurrency maintenance on sheet reading
		getBrowserWait(testObject.getParameter("browserName"));
		dataProviderUtils TDP = dataProviderUtils.getInstance();
		Object[][] data = TDP.getData(testDataSheet);
		Testlogs.get().debug(Arrays.deepToString(data).replace("\n", "--"));
		return data;
	}

	@Test(dataProvider = "Carts")
	public void CartBaseTest(String caseId, String runTest, String desc, String proprties) throws Exception {

		Testlogs.set(new SASLogger("HP_SC " + getBrowserName()));
		// Important to add this for logging/reporting
		setTestCaseReportName(SheetVariables.cartTestCaseId);
		Testlogs.get().debug("Case Browser: " + testObject.getParameter("browserName"));
		String CaseDescription = MessageFormat.format(LoggingMsg.TEST_CASE_DESC, testDataSheet + "." + caseId,
				this.getClass().getCanonicalName(), desc);
		initReportTime();

		try {
			CartValidation.cartValidation();
			sassert().assertAll();
			logCaseDetailds(CaseDescription);
			Common.testPass();
		} catch (Throwable t) {
			logCaseDetailds(CaseDescription + "<br><b><font color='red'>Failure Reason: </font></b>"
					+ t.getMessage().replace("\n", "").trim());
			setTestCaseDescription(getTestCaseDescription());
			Testlogs.get().debug(MessageFormat.format(LoggingMsg.DEBUGGING_TEXT, t.getMessage()));
			t.printStackTrace();
			String temp = getTestCaseReportName();
			Common.testFail(t, temp);
			ReportUtil.takeScreenShot(getDriver(), testDataSheet + "_" + caseId);
			Assert.assertTrue(false, t.getMessage());
		} // catch

	}

}

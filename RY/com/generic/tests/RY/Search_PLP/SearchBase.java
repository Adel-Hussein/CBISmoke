package com.generic.tests.RY.Search_PLP;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.xml.XmlTest;
import com.generic.page.PDP;
import com.generic.page.PLP;
import com.generic.setup.Common;
import com.generic.setup.LoggingMsg;
import com.generic.setup.SelTestCase;
import com.generic.setup.SheetVariables;
import com.generic.util.ReportUtil;
import com.generic.util.SASLogger;
import com.generic.util.dataProviderUtils;

public class SearchBase extends SelTestCase {

	// used sheet in test
	public static final String testDataSheet = SheetVariables.plpSheet;

	private static XmlTest testObject;

	private static ThreadLocal<SASLogger> Testlogs = new ThreadLocal<SASLogger>();
	private static LinkedHashMap<String, Object> users;
	
	private String RecommendedProductsCase = "Recommended products";
	private String fullSearchCase = "full search";

	@BeforeTest
	public static void initialSetUp(XmlTest test) throws Exception {
		Testlogs.set(new SASLogger(test.getName() + test.getIndex()));
		testObject = test;
		users = Common.readUsers();
	}
	
	@DataProvider(name = "PLP", parallel = true)
	// concurrency maintenance on sheet reading
	public static Object[][] loadTestData() throws Exception {
		getBrowserWait(testObject.getParameter("browserName"));

		dataProviderUtils TDP = dataProviderUtils.getInstance();
		Object[][] data = TDP.getData(testDataSheet);
		Testlogs.get().debug(Arrays.deepToString(data).replace("\n", "--"));
		return data;
	}

	
	@Test(dataProvider = "PLP")
	public void verifyPLP(String caseId, String runTest,String proprties, String desc) throws Exception {
		
		Testlogs.set(new SASLogger("PLP " + getBrowserName()));
		// Important to add this for logging/reporting
		setTestCaseReportName("PLP Case");
		String CaseDescription = MessageFormat.format(LoggingMsg.TEST_CASE_DESC, testDataSheet + "." + caseId,
				this.getClass().getCanonicalName(), desc.replace("\n", "<br>--"));

		try {
			Common.refreshBrowser();

			// validate the suggested items
			if (proprties.contains(RecommendedProductsCase))
				sassert().assertTrue(PLP.searchAndVerifyResults("dress", true), "Serach validation failed");

			if (proprties.contains(fullSearchCase))
				// Validate the direct search
				sassert().assertTrue(PLP.searchAndVerifyResults("mat", false), "Serach validation failed");

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
		}

	}

}

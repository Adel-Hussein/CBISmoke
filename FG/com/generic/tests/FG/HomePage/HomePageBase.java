package com.generic.tests.FG.HomePage;

import java.text.MessageFormat;
import java.util.Arrays;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.xml.XmlTest;

import com.generic.setup.Common;
import com.generic.setup.LoggingMsg;
import com.generic.setup.SelTestCase;
import com.generic.setup.SheetVariables;
import com.generic.tests.FG.HomePage.LogoValidation;
import com.generic.util.ReportUtil;
import com.generic.util.SASLogger;
import com.generic.util.dataProviderUtils;

public class HomePageBase extends SelTestCase {

	// possible scenarios
	public static final String Logo = "Logo validation";
	public static final String miniCart = "Mini cart validation";
	public static final String verify = "verify";
	public static final String header = "header";
	public static final String footer = "footer";
	public static final String body = "body";
	public static final String YAMALCarousels = "YAMA Carousels Verification";

	// used sheet in test
	public static final String testDataSheet = SheetVariables.HPRegressionsheet;

	private static XmlTest testObject;

	private static ThreadLocal<SASLogger> Testlogs = new ThreadLocal<SASLogger>();

	@BeforeTest
	public static void initialSetUp(XmlTest test) throws Exception {
		Testlogs.set(new SASLogger(test.getName() + test.getIndex()));
		testObject = test;
	}

	@DataProvider(name = "HP_SC", parallel = true)
	// concurrency maintenance on sheet reading
	public static Object[][] loadTestData() throws Exception {
		getBrowserWait(testObject.getParameter("browserName"));
		dataProviderUtils TDP = dataProviderUtils.getInstance();
		Object[][] data = TDP.getData(testDataSheet);
		Testlogs.get().debug(Arrays.deepToString(data).replace("\n", "--"));
		return data;
	}

	@Test(dataProvider = "HP_SC")
	public void HomePageRegressionTest(String caseId, String runTest, String desc, String proprties)
			throws Exception {
		Testlogs.set(new SASLogger("HP_SC " + getBrowserName()));
		// Important to add this for logging/reporting
		setTestCaseReportName(SheetVariables.HPTestCaseId);
		Testlogs.get().debug("Case Browser: " + testObject.getParameter("browserName"));
		logCaseDetailds(MessageFormat.format(LoggingMsg.TEST_CASE_DESC, testDataSheet + "." + caseId,
				this.getClass().getCanonicalName(), desc));

		try {

			if (proprties.contains(this.Logo)) {
				LogoValidation.validate();
			} else
				if (proprties.contains(this.miniCart)) {
					MiniCartValidation.validate();
				}
			
					else{
				Testlogs.get().debug("please check proprties provided in excel sheet");
			}
			if (proprties.contains(this.YAMALCarousels)) {
				YAMALCarouselsVerification.validate();
			}else {
				Testlogs.get().debug("please check proprties provided in excel sheet");
			}

			sassert().assertAll();
			Common.testPass();
		} catch (Throwable t) {
			setTestCaseDescription(getTestCaseDescription());
			Testlogs.get().debug(MessageFormat.format(LoggingMsg.DEBUGGING_TEXT, t.getMessage()));
			t.printStackTrace();
			String temp = getTestCaseReportName();
			Common.testFail(t, temp);
			ReportUtil.takeScreenShot(getDriver(), testDataSheet + "_" + caseId);
			Assert.assertTrue(false, t.getMessage());
		} // catch
	}// test
}

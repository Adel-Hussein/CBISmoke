package com.generic.page;

import java.text.MessageFormat;
import java.util.NoSuchElementException;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.generic.selector.PDPSelectors;
import com.generic.setup.ExceptionMsg;
import com.generic.setup.SelTestCase;
import com.generic.util.SelectorUtil;

public class PDP_BD extends SelTestCase{

	
	// done - SMK
	public static String BDgetBottomPrice(String ProductID,boolean bundle) throws Exception {
		try {
			getCurrentFunctionName(true);
			String price = "";
			String selector;
			if(isMobile())
			selector = PDPSelectors.BDallSizes.get();
			else
			if(bundle)
			    selector = MessageFormat.format(PDPSelectors.BDListBoxBundle, ProductID) +">option:nth-child(2)";
			else
				selector = PDPSelectors.BDallSizes.get();
			String optionText = SelectorUtil.getElement(selector).getText();
            price = optionText.substring(optionText.lastIndexOf("$") + 1);
			getCurrentFunctionName(false);
			return price;
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(
					ExceptionMsg.PageFunctionFailed + "Bottom price  selector was not found by seleniuem", new Object() {
					}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	// done - SMK
	public static boolean BDselectSwatches(Boolean bundle, String ProductID) throws Exception {
		try {
			getCurrentFunctionName(true);
			boolean	isPriceShownInOption = false;
				if(bundle) {
					isPriceShownInOption =	BDselectSwatchesBundle(ProductID);
				}else {
					
					Boolean noOptions = true;
					if (SelTestCase.isMobile())
						noOptions = !PDP.getAddToCartClass();
					else
						noOptions = PDP.getSwatchContainersdivClass(0).contains("no-options");
					
					isPriceShownInOption = !noOptions;
					PDP.FGGRselectSwatchesSingle();
				}
			getCurrentFunctionName(false);
			return isPriceShownInOption;
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(ExceptionMsg.PageFunctionFailed + "select swatch was failed", new Object() {
			}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	public static boolean BDselectSwatchesBundle(String ProductID) throws Exception {
		try {
			getCurrentFunctionName(true);
			boolean isSizeShownAsListbox = false;
			int numberOfSwatchContainers = PDP.getNumberofSwatchContainersBundle();
			if ( (numberOfSwatchContainers == 0) || PDP.getSwatchContainersdivClassBundle(0, ProductID).contains("no-options")) {
				logs.debug("No options to select");
			} else {
				String  ListSelector;
				if(isMobile())
				  ListSelector = PDPSelectors.BDallSizes.get();
				else
				  ListSelector = MessageFormat.format(PDPSelectors.BDListBoxBundle, ProductID);

				for (int i = 0; i < numberOfSwatchContainers; i++) {
					
					if (PDP.getSwatchContainersdivClassBundle(i, ProductID).contains("listbox") || !SelectorUtil.isNotDisplayed(PDPSelectors.BDallSizes.get())) {
						isSizeShownAsListbox = true;
						PDP.selectNthListBoxFirstValueBundle(ListSelector, i);
					} else {
							if(isMobile()) {
								SelectorUtil.initializeSelectorsAndDoActions(MessageFormat.format(PDPSelectors.BDimageOption.get(),1, i + 1, 1));
								WebElement element = SelectorUtil.getElement(MessageFormat.format(PDPSelectors.BDimageOption.get(),1, i + 1, 1));
								Actions action = new Actions(getDriver());
								action.moveToElement(element).click().build().perform();
					
							}else {
								PDP.selectNthOptionFirstSwatchBundle("css,#" + ProductID + " "
									+ MessageFormat.format(PDPSelectors.BDimageOption.get(), i + 1, 1).replace("css,", ""));
	
							}
					}
				}

			}
		
			getCurrentFunctionName(false);
			return isSizeShownAsListbox;
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(ExceptionMsg.PageFunctionFailed
					+ "Select swatches has falied, a selector was not found by selenium", new Object() {
					}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	public static String getPersonaliztionCost(String ProductID, Boolean bundle) throws Exception {
		try {
			getCurrentFunctionName(true);
			String cost = "";
			String costSelector = PDPSelectors.BDPersonalizationCost.get();
			if(!isMobile()) {
				if(bundle)
			          costSelector  = "css,#" + ProductID + " "
					+ PDPSelectors.BDPersonalizationCost.get().replace("css,", "");
			    cost = SelectorUtil.getElement(costSelector).getText();
			}else {
				String text = SelectorUtil.getElement(costSelector).getText();
				cost = text.substring(text.lastIndexOf("$") + 1);
			}
			getCurrentFunctionName(false);
			return cost;
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(ExceptionMsg.PageFunctionFailed + "Personalization cost selector was not found by selenium", new Object() {
			}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	public static String isPersonaliztionCostDisplayedInMonogramDetails(String ProductID,Boolean bundle) throws Exception {
		try {
			getCurrentFunctionName(true);
			String cost = "";
			String details = "";
			
			 String monogramDetails = PDPSelectors.BDaddedPersonlizedDetails.get();
			  if(bundle && !isMobile())
              monogramDetails  = "css,#" + ProductID + " "
					+ PDPSelectors.BDaddedPersonlizedDetails.get().replace("css,", "");
			details = SelectorUtil.getElement(monogramDetails).getText();
			if(!details.equalsIgnoreCase("None")) {
			String productMonogramCost = PDPSelectors.BDMonogramCost.get();
				if(bundle && !isMobile()) {
					 productMonogramCost  = "css,#" + ProductID + " "
							+ PDPSelectors.BDMonogramCost.get().replace("css,", "");
				}
				 cost = SelectorUtil.getElement(productMonogramCost).getText();
			}
			getCurrentFunctionName(false);
			return cost;
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(ExceptionMsg.PageFunctionFailed + "Personalization cost selector was not found by selenium", new Object() {
			}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	public static int BDgetNumberOfConfigureItems(boolean bundle, int productNo) throws Exception  {
		try {
			getCurrentFunctionName(true);
			int number = 0;	
				if(isMobile()) {
					if(bundle)
						number = SelectorUtil.getAllElements(MessageFormat.format(PDPSelectors.BDconfigureItemsBundle.get(),productNo)).size();
					else
					    number = SelectorUtil.getAllElements(MessageFormat.format(PDPSelectors.BDconfigureItems.get(),productNo + 1)).size();
				}else {
					if(bundle)
						number = SelectorUtil.getAllElements(MessageFormat.format(PDPSelectors.BDconfigureItemsBundle.get(),productNo)).size();
					else 
				    	number = SelectorUtil.getAllElements(MessageFormat.format(PDPSelectors.BDconfigureItems.get(),productNo)).size();
			       }
			getCurrentFunctionName(false);
		return number;
	  } catch (NoSuchElementException e) {
		logs.debug(MessageFormat.format(ExceptionMsg.PageFunctionFailed
				+ "Get number of configure items has falied, a selector was not found by selenium", new Object() {
				}.getClass().getEnclosingMethod().getName()));
		throw e;
	  }
		
	}
	public static void BDSelectItemsConfigurations(boolean bundle) throws Exception{
		try {
			int productsNo  = 0;
			if(bundle) {
				 productsNo = SelectorUtil.getAllElements(PDPSelectors.BDproductsBundle.get()).size();
			}else {
				 productsNo = SelectorUtil.getAllElements(PDPSelectors.BDproducts.get()).size();
			}
			for(int i = 0; i<productsNo; i++) {
				BDnthProductConfigureItemsSelection(i + 1,bundle);
			}
		}catch(NoSuchElementException e) {
			logs.debug(MessageFormat.format(ExceptionMsg.PageFunctionFailed
					+ "Select Items Configurations has falied, a selector was not found by selenium", new Object() {
					}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	public static boolean BDnthProductConfigureItemsSelection(int productNo,boolean bundle) throws Exception {
		try {
			getCurrentFunctionName(true);
			boolean isSizeShownAsListbox = false;
			int numberOfconfigureItems = PDP_BD.BDgetNumberOfConfigureItems(bundle,productNo);
			if ((numberOfconfigureItems == 0)) {
				logs.debug("No configuration to select");
			} else {

				for (int i = 0; i < numberOfconfigureItems; i++) {
					if(SelectorUtil.isNotDisplayed(MessageFormat.format(PDPSelectors.BDopenedConfigureItem.get(),i + 1))) {
						int index = i + 1 ;
						if (!isMobile() && !bundle)
						SelectorUtil.initializeSelectorsAndDoActions(MessageFormat.format(PDPSelectors.BDconfigureItems.get(),productNo)+":nth-child("+index+")");
						if(isMobile() && bundle && index==1) {
						SelectorUtil.initializeSelectorsAndDoActions(MessageFormat.format(PDPSelectors.BDitemsAccordion.get(),productNo,index));
						}
					}
					if(isMobile()) {
						if(bundle) 
							selectOption(MessageFormat.format(PDPSelectors.BDnthConfigureItemOptionBundle.get(),productNo, i + 1));
						else 
						    SelectorUtil.initializeSelectorsAndDoActions(MessageFormat.format(PDPSelectors.BDnthConfigureItemOption.get(),productNo+1, i + 1));
					}else {
						if(bundle) 
						    selectOption(MessageFormat.format(PDPSelectors.BDnthConfigureItemOptionBundle.get(),productNo, i + 2));
						else 
					     	SelectorUtil.initializeSelectorsAndDoActions(MessageFormat.format(PDPSelectors.BDnthConfigureItemOption.get(),productNo, i + 1));
					}
				}
			}
		
			getCurrentFunctionName(false);
			return isSizeShownAsListbox;
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(ExceptionMsg.PageFunctionFailed
					+ "Product Configure Items Selection has falied, a selector was not found by selenium", new Object() {
					}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	public static boolean validatePriceIsDisplayed() throws Exception {
	try {			
		boolean isDisplayed = false;
		isDisplayed = SelectorUtil.isDisplayed(PDPSelectors.BDVK_PDPPrice.get());
	getCurrentFunctionName(false);
	return isDisplayed;
} catch (NoSuchElementException e) {
	logs.debug(MessageFormat.format(ExceptionMsg.PageFunctionFailed
			+ "price  not displayed, a selector was not found by selenium", new Object() {
			}.getClass().getEnclosingMethod().getName()));
	throw e;
}
	
	}
	
	
	public static void clickOnConfigureBtn(String ProductID) throws Exception {
		try {
			getCurrentFunctionName(true);
			logs.debug("Click on Configure button");
			if(isMobile())
				SelectorUtil.initializeSelectorsAndDoActions(PDPSelectors.BDconfigureBtn.get());
			else
				SelectorUtil.initializeSelectorsAndDoActions("css,#" + ProductID + " "
						+ PDPSelectors.BDconfigureBtn.get().replace("css,", ""));
	
			getCurrentFunctionName(false);
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(ExceptionMsg.PageFunctionFailed
					+ "Configure button selector was not found by seleniuem", new Object() {
					}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	public static void clickOnConfigureModalDoneBtn() throws Exception {
		try {
			getCurrentFunctionName(true);
			logs.debug("Click on Configure modal Done button");
				SelectorUtil.initializeSelectorsAndDoActions(PDPSelectors.BDconfigureModalDoneBtn.get());
			getCurrentFunctionName(false);
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(ExceptionMsg.PageFunctionFailed
					+ "Configure modal done button selector was not found by seleniuem", new Object() {
					}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	
	public static void selectOption(String Selector) throws Exception {
		try {
			getCurrentFunctionName(true);
			Thread.sleep(1000);
			String valuesArr = "FFF1";
			if (!SelectorUtil.isNotDisplayed(Selector)) {
				SelectorUtil.initializeSelectorsAndDoActions(Selector, valuesArr);
			}
			getCurrentFunctionName(false);
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(ExceptionMsg.PageFunctionFailed, new Object() {
			}.getClass().getEnclosingMethod().getName()));
			throw e;
		}

	}
	
	public static boolean validateTheRightPDP(String targetedItem) throws Exception {
		boolean isRightItem = false;
		try {
			getCurrentFunctionName(true);
			logs.debug("Validate if you are in the right PDP");
			
			String itemTitle = SelectorUtil.getElement(PDPSelectors.BDItemTitle.get()).getText();
			isRightItem = itemTitle.contains("Fabric");
			 
			return isRightItem;
		}catch(NoSuchElementException e) {
			logs.debug(MessageFormat.format(
					ExceptionMsg.PageFunctionFailed + "PDP Item Title selector was not found by selenium", new Object() {
					}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
		
	}
	
	public static boolean validateFabricAddToCartIsEnabled() throws Exception {
		try {
			getCurrentFunctionName(true);
			boolean isDisplayed;
			logs.debug("Validate if Add To Cart Is Displayed");
			
			String selectorEnabled = PDPSelectors.FabricAddtoCartBtn.get();
			isDisplayed = SelectorUtil.isDisplayed(selectorEnabled);
			getCurrentFunctionName(false);
			return isDisplayed;
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(
					ExceptionMsg.PageFunctionFailed + "Add to cart button selector was not found by selenium",
					new Object() {
					}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	public static void clickAddToCartButton() throws Exception {
		try {
			getCurrentFunctionName(true);
			String subStrArr = null;
			subStrArr = PDPSelectors.FabricAddtoCartBtn.get();
			SelectorUtil.initializeSelectorsAndDoActions(subStrArr);
			getCurrentFunctionName(false);
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(
					ExceptionMsg.PageFunctionFailed + "Add to cart button selector was not found by selenium",
					new Object() {
					}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	public static void pickOneQuantity() throws Exception {
		try {
			
			String subStrArr = PDPSelectors.pickOneFreeSwatch.get();
			WebElement increment = SelectorUtil.getElement(subStrArr);
			
			SelectorUtil.clickOnWebElement(increment);
			logs.debug("Select One Item");
		
			getCurrentFunctionName(false);
			
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(
					ExceptionMsg.PageFunctionFailed + "Increment selector was not found by seleniuem", new Object() {
					}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
	
	public static void clickAddToFreeSwatch() throws Exception {
		try {
			getCurrentFunctionName(true);
			String subStrArr = null;
			subStrArr = PDPSelectors.FabricAddFreeSwatchtoCartBtn.get();
			SelectorUtil.initializeSelectorsAndDoActions(subStrArr);
			getCurrentFunctionName(false);
		} catch (NoSuchElementException e) {
			logs.debug(MessageFormat.format(
					ExceptionMsg.PageFunctionFailed + "Add free swatch to cart button selector was not found by selenium",
					new Object() {
					}.getClass().getEnclosingMethod().getName()));
			throw e;
		}
	}
}

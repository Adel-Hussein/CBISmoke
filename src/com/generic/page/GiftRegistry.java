package com.generic.page;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.generic.selector.GiftRegistrySelectors;
import com.generic.setup.Common;
import com.generic.setup.GlobalVariables;
import com.generic.setup.LoggingMsg;
import com.generic.setup.SelTestCase;
import com.generic.util.RandomUtilities;
import com.generic.util.ReportUtil;
import com.generic.util.SelectorUtil;

public class GiftRegistry extends SelTestCase {

	public static String GRPageLink = "GiftRegistryHomeView";
	public static final String CartPageLink = "/OrderCalculate";
	public static String type;
	public static String eventDateMonth;
	public static String eventDateDay;
	public static String eventDateYear;
	public static String registryName;
	public static String emptyMessage;
	public static boolean validateContactInformation;

	// Constants.
	public static final String singlePDPSearchTerm = "Rugs";
	public static final String createGiftRegistryString = "Create New Registry";

	// User information.
	public boolean createdAccount = false;
	public String userMail;
	public String userPassword;
	public LinkedHashMap<String, String> userAddressDetails;
	public String userFirstName;
	public String userLastName;
	public String userCompanyName;
	public String userAddressLine1;
	public String userPhone;
	
	// Contact input text values
	public String firstNameInput;
	public String lastNameInput;
	public String emailAddressInput;
	public String streetAddressInput;
	public String cityAddressInput;
	public String regionAddressInput;
	public String zipCodeInput;
	public String phoneInput;

	/**
	* Set initial values for Gift registry.
	* @param registryType
	* @param eventDMonth
	* @param eventDDay
	* @param eventDYear
	* @param emptyMsg
	*
	* @throws Exception
	*/
	public static void setRegistryInformtion(String registryType, String eventDMonth, String eventDDay, String eventDYear, String emptyMsg) {
		type = registryType;
		registryName = generatRegistryName();
		eventDateMonth = eventDMonth;
		eventDateDay = eventDDay;
		eventDateYear = eventDYear;
		emptyMessage= emptyMsg;
	}

	/**
	* Set current registry name.
	* @param name
	*
	* @throws Exception
	*/
	public static void setRegistryName(String name) {
		logs.debug("Set registry value: " + name);
		registryName = name;
	}

	/**
	* Set validation contact information flag.
	* @param validate
	*
	* @throws Exception
	*/
	public static void setValidateContactInformation(boolean validate) {
		validateContactInformation = validate;
	}

	/**
	* Get validation contact information flag.
	*
	* @throws Exception
	*/
	public static boolean getValidateContactInformation() {
		return validateContactInformation;
	}

	/**
	* validate create gift registry.
	* @param email
	*
	* @throws Exception
	*/
	public void validate(String email) throws Exception {
		getCurrentFunctionName(true);
		logs.debug("Validate gift registry.");
		Thread.sleep(1000);
		SelectorUtil.waitGWTLoadedEventPWA();
		navigateToGiftRegistry();
		Thread.sleep(1500);
		SelectorUtil.waitGWTLoadedEventPWA();

		String createRegistryButtonSelector = GiftRegistrySelectors.FGCreateRegistryButton.get();
		if (isGR()) {
			createRegistryButtonSelector = GiftRegistrySelectors.GRCreateRegistryButton.get();
		}

		SelectorUtil.initializeSelectorsAndDoActions(createRegistryButtonSelector);
		Thread.sleep(1500);
		SelectorUtil.waitGWTLoadedEventPWA();

		// Create new gift registry.
		createRegistrySteps(email);

		getCurrentFunctionName(false);
	}

	/**
	* Create gift registry.
	* @param email
	*
	* @throws Exception
	*/
	public void createRegistrySteps(String email) throws Exception {
		getCurrentFunctionName(true);

		logs.debug("Create new registry.");

		// Gift registry step one.
		WebElement stepOneContainer = SelectorUtil.getelement(GiftRegistrySelectors.stepOneContainer.get());
		sassert().assertTrue(stepOneContainer != null,
				"Error user not in step one gift registry.");
		fillRegistryInformation();
		SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.confirmInformtionButton.get());
		SelectorUtil.waitGWTLoadedEventPWA();

		// Gift registry step two.
		WebElement stepTwoContainer = SelectorUtil.getelement(GiftRegistrySelectors.stepTwoContainer.get());
		sassert().assertTrue(stepTwoContainer != null,
				"Error user not in step two gift registry.");
		boolean validateContactInformation = validateConfirmContactInformation(email);
		sassert().assertTrue(validateContactInformation,
				"Error in Gift registry step two contact information.");
		SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.createRegistryButtonStepTwo.get());

		Thread.sleep(1500);
		SelectorUtil.waitGWTLoadedEventPWA();

		// Check the confirmation modal.
		validateConfirmationModal();

		// Check the created gift registry.
		validateCreatedGiftRegistry();

		SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.beginAddingItemsButton.get());

		getCurrentFunctionName(false);
	}

	/**
	* Fill registry information (Step one).
	*
	* @throws Exception
	*/
	public static void fillRegistryInformation() throws Exception {
		getCurrentFunctionName(true);

		logs.debug("Fill Gift Registry information step 1.");

		SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.eventType.get(), type);
		if (registryName.equals("")) {
			registryName = generatRegistryName();
		}
		SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.registryName.get(), registryName);

		if (isMobile()) {
			List<WebElement> eventDataList = SelectorUtil.getElementsList(GiftRegistrySelectors.eventDay.get());
			SelectorUtil.setSelectText(eventDataList.get(0), eventDateMonth);
			SelectorUtil.setSelectText(eventDataList.get(1), eventDateDay);
			SelectorUtil.setSelectText(eventDataList.get(2), eventDateYear);
		} else {
			SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.eventMonth.get(), eventDateMonth);
			SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.eventDay.get(), eventDateDay);
			SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.eventYear.get(), eventDateYear);
		}

		logs.debug("Gift Registry information step 1 {type: " + type + ", registryName:" + registryName + ", event Date Month:" + eventDateMonth + ", event Date Day:" + eventDateDay + ", event Date Year:" +  eventDateYear + "}.");

		getCurrentFunctionName(false);

	}

	/**
	* Navigate to gift registry home page.
	*
	* @throws Exception
	*/
	public static void navigateToGiftRegistry() throws Exception {
		logs.debug("Navigate to Gift registry page.");
		getCurrentFunctionName(true);
		if (isMobile()) {
			WebElement giftRegistryLink = SelectorUtil.getMenuLinkMobilePWA(GRPageLink);
			// Navigate to the Sign in/Create account page.
			SelectorUtil.clickOnWebElement(giftRegistryLink);
		} else {
			SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.GRLink);
		}
		getCurrentFunctionName(false);
	}

	/**
	* Generate a random name for gift registry.
	*
	*/
	public static String generatRegistryName() {
		getCurrentFunctionName(true);
		int stringLength = 6;
	    StringBuilder sb = new StringBuilder(stringLength);
	    String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
	    String CHAR_UPPER = CHAR_LOWER.toUpperCase();
	    String NUMBER = "0123456789";

	    String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
	    SecureRandom random = new SecureRandom();
	    for (int i = 0; i < stringLength; i++) {

			// 0-62 (exclusive), random returns 0-61
	        int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
	        char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

	        // debug
	        System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);

	        sb.append(rndChar);

	    }
	    getCurrentFunctionName(false);
	    return sb.toString();
	}

	/**
	* Get selected text from web element.
	* @param selector
	*
	* @throws Exception
	*/
	public static String getElementText(String selector) throws Exception {
		return SelectorUtil.getelement(selector).getAttribute("value");
	}

	/**
	* Validate registry contact information (Step 2).
	* @param email
	*
	* @throws Exception
	*/
	@SuppressWarnings("unchecked")
	public boolean validateConfirmContactInformation(String email) throws Exception {
		getCurrentFunctionName(true);

		LinkedHashMap<String, String> addressDetails = userAddressDetails;
		setValidateContactInformation(false);
		// Select the addresses from the form.
		firstNameInput = getElementText(GiftRegistrySelectors.firstNameGR.get());
		lastNameInput = getElementText(GiftRegistrySelectors.lastNameGR.get());
		emailAddressInput = getElementText(GiftRegistrySelectors.emailAddressGR.get());
		streetAddressInput = getElementText(GiftRegistrySelectors.streetAddressGR.get());
		cityAddressInput = getElementText(GiftRegistrySelectors.cityAddressGR.get());
		SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.regionAddressGR.get());
		regionAddressInput = SelectorUtil.textValue.get();
		zipCodeInput = getElementText(GiftRegistrySelectors.zipCodeGR.get());
		phoneInput = getElementText(GiftRegistrySelectors.phoneGR.get()).replace("-", "");
		logs.debug("User information: ");
		logs.debug("First name: " + firstNameInput);
		logs.debug("Last name: " + lastNameInput);
		logs.debug("Email address: " + emailAddressInput);
		logs.debug("City: " + cityAddressInput);
		logs.debug("Region: " + regionAddressInput);
		logs.debug("Zip code: " + zipCodeInput);
		logs.debug("Phone: " + phoneInput);

		if (!createdAccount) {
			// Sign in user.
			addresses.forEach((key, address) -> {
				String firstNameValue = (String)((LinkedHashMap<String, Object>) address).get(AddressBook.shippingAddress.keys.firstName);
				String lastNameValue = (String)((LinkedHashMap<String, Object>) address).get(AddressBook.shippingAddress.keys.lastName);
				String streetAddressValue = (String)((LinkedHashMap<String, Object>) address).get(AddressBook.shippingAddress.keys.adddressLine);
				String cityValue = (String)((LinkedHashMap<String, Object>) address).get(AddressBook.shippingAddress.keys.city);
				String zipCodeValue = (String)((LinkedHashMap<String, Object>) address).get(AddressBook.shippingAddress.keys.zipcode);
				String phoneValue = (String)((LinkedHashMap<String, Object>) address).get(AddressBook.shippingAddress.keys.phone);

				boolean validateFirstName = firstNameInput.equals(firstNameValue);
				boolean validateLastName = lastNameInput.equals(lastNameValue);
				boolean validateEmail= emailAddressInput.equals(email);
				boolean validateStreetAddress = streetAddressInput.equals(streetAddressValue);
				boolean validateCity= cityAddressInput.equals(cityValue);
				boolean validateRegion = regionAddressInput.equalsIgnoreCase(cityValue);
				boolean validateZipCode = zipCodeInput.equals(zipCodeValue);
				boolean validatePhone = phoneInput.equals(phoneValue);

				if (validateFirstName && validateLastName && validateEmail && validateStreetAddress && validateCity &&
						validateRegion && validateZipCode && validatePhone) {
					try {
						GiftRegistry.setValidateContactInformation(true);
						validateContactInfo(firstNameValue, lastNameValue, email, streetAddressValue,
								cityValue, cityValue, zipCodeValue, phoneValue);
					} catch (Exception e) {
						setTestCaseDescription(getTestCaseDescription());
						logs.debug(MessageFormat.format(LoggingMsg.DEBUGGING_TEXT, e.getMessage()));
						e.printStackTrace();
						String temp = getTestCaseReportName();
						Common.testFail(e, temp);
						Assert.assertTrue(false, e.getMessage());
					}
				}
	        });

			sassert().assertTrue(getValidateContactInformation(),
				"Error check the user address in excel sheet, user email: " + email);
		} else {
			// The user create a new account.
			boolean validateContactInformation = validateContactInfo(userFirstName, userLastName, userMail,
					userAddressLine1, addressDetails.get(AddressBook.shippingAddress.keys.city),
					addressDetails.get(AddressBook.shippingAddress.keys.city), addressDetails.get(AddressBook.shippingAddress.keys.zipcode),
					userPhone);
			GiftRegistry.setValidateContactInformation(validateContactInformation);

		}
		getCurrentFunctionName(false);
		return getValidateContactInformation();
	}

	/**
	* Validate create gift registry confirmation modal.
	*
	* @throws Exception
	*/
	public static void validateConfirmationModal() throws Exception {
		getCurrentFunctionName(true);
		SelectorUtil.waitGWTLoadedEventPWA();
		Thread.sleep(1000);
		WebElement confirmationModal = SelectorUtil.getelement(GiftRegistrySelectors.confirmationModalGR.get());
		sassert().assertTrue(confirmationModal != null,
				"Error Confirmation gift registry created modal not displayed");
		SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.manageRegistryButton.get());
		getCurrentFunctionName(false);
	}

	/**
	* Validate created gift registry.
	*
	* @throws Exception
	*/
	public static void validateCreatedGiftRegistry() throws Exception {
		getCurrentFunctionName(true);

		// Validate that the page is manage gift registry.
		WebElement giftRegisrtContainer = SelectorUtil.getelement(GiftRegistrySelectors.manageGRContainer.get());
		sassert().assertTrue(giftRegisrtContainer != null,
				"Error this page is not manage gift registry");

		// Validate the selected registry.
		String selectedRegistry;
		SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.registryInfo.get());
		selectedRegistry = SelectorUtil.textValue.get();

		sassert().assertTrue(selectedRegistry.contains(registryName),
				"Error in selected registry, expected " + registryName + " : " + selectedRegistry);

		// Validate the empty registry.
		String emptyMsg = SelectorUtil.getelement(GiftRegistrySelectors.emptyRegistryMsg.get()).getText().trim().toLowerCase();
		sassert().assertTrue(emptyMsg.equals(emptyMessage.toLowerCase().trim()),
				"Error empty messages is not as expected " + emptyMessage.toLowerCase().trim() + " : " + emptyMsg);

		// Validate gift card container.
		WebElement giftCardContainer = SelectorUtil.getelement(GiftRegistrySelectors.giftCardContainer.get());
		sassert().assertTrue(giftCardContainer != null,
				"Error gift card section not displayed at manage gift card page.");
		getCurrentFunctionName(false);
	}

	/**
	* Validate Contact information fields (Step 2).
	* @param firstName
	* @param lastName
	* @param emailAddress
	* @param streetAddress
	* @param cityAddress
	* @param regionAddress
	* @param zipCode
	* @param phone
	*
	* @throws Exception
	*/
	public boolean validateContactInfo(String firstName,String lastName,String emailAddress,String streetAddress,String cityAddress,String regionAddress,String zipCode,String phone) throws Exception {

		getCurrentFunctionName(true);
		boolean validateContactInformation = true;

		// Validate the contact information.
		boolean validateFirstName = firstNameInput.equals(firstName);
		boolean validateLastName = lastNameInput.equals(lastName);
		boolean validateEmail= emailAddressInput.equals(emailAddress);
		boolean validateStreetAddress = streetAddressInput.equals(streetAddress);
		boolean validateCity= cityAddressInput.equals(cityAddress);
		boolean validateRegion = regionAddressInput.toLowerCase().equals(regionAddress.toLowerCase());
		boolean validateZipCode = zipCodeInput.equals(zipCode);
		boolean validatePhone = phoneInput.equals(phone);

		sassert().assertTrue(validateFirstName,
				"Error first name is not as expected" + firstName + " : " + firstNameInput);
		sassert().assertTrue(validateLastName,
				"Error last name is not as expected" + lastName + " : " + lastNameInput);
		sassert().assertTrue(validateEmail,
				"Error email address is not as expected" + emailAddress + " : " + emailAddressInput);
		sassert().assertTrue(validateStreetAddress,
				"Error street address not as expected" + streetAddress + " : " + streetAddressInput);
		sassert().assertTrue(validateCity,
				"Error city address is not as expected" + cityAddress + " : " + cityAddressInput);
		sassert().assertTrue(validateRegion,
				"Error region address is not as expected" + regionAddress + " : " + regionAddressInput);
		sassert().assertTrue(validateZipCode,
				"Error zip code is not as expected" + zipCode + " : " + zipCodeInput);
		sassert().assertTrue(validatePhone,
				"Error phone is not as expected" + phone + " : " + phoneInput);

		if (!validateFirstName || !validateLastName ||
				!validateEmail ||
				!validateStreetAddress ||
				!validateCity ||
				!validateRegion ||
				! validateZipCode||
				!validatePhone
				) {
			validateContactInformation = false;

		}
		getCurrentFunctionName(false);
		return validateContactInformation;
	}

	/**
	* Validate manage gift registry (Step 2).
	* @param email
	*
	* @throws Exception
	*/
	public void manageRegistryValidate(String email) throws Exception {

		getCurrentFunctionName(true);

		// Go to PDP by search and select the swatches.
		goToPDPAndSelectSwatches();

		// Click on save to gift registry button.
		SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.saveToGR.get());
		Thread.sleep(1000);

		// Verify that "Select A Registry Or Wish list" modal is displayed.
		validateSelectGRModal();

		int numberOfItemAddedToCart = Integer.parseInt(SelectorUtil.getelement(GiftRegistrySelectors.miniCartText.get()).getText());
		logs.debug("Number of items before add to cart: " + numberOfItemAddedToCart);

		if (registryName.equals("") || registryName == null) {
			logs.debug("Create new gift registry.");
			setRegistryName(generatRegistryName());
			SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.GRListBox.get(), "FFFS" + createGiftRegistryString);

			// No need to click on select button in PWA because the selected option will submitted when change the select option value.
			if (!isMobile()) {
				// Click select button at desktop and tablet.
				SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.addToGiftRegistySelectButton.get());
			}

			SelectorUtil.waitGWTLoadedEventPWA();
			// Create a gift registry.
			createRegistrySteps(email);

		} else {
			logs.debug("Add product to created gift registry: " + registryName);

			// Make sure that the current selected option is not the target registry.
			SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.GRListBox.get());
			String selectedGiftRegistry = SelectorUtil.textValue.get();

			if (!selectedGiftRegistry.contains(registryName)) {
				// Selected created registry.
				SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.GRListBox.get(), "FFFS" + "\"" + registryName +"\"");
			}

			SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.addToGiftRegistySelectButton.get());
		}

		Thread.sleep(1500);
		SelectorUtil.waitGWTLoadedEventPWA();

		// Validate product added to gift registry modal.
		validateAddToGRModal();

		// Click on view registry button.
		SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.viewRegistryButton.get());

		SelectorUtil.waitGWTLoadedEventPWA();
		Thread.sleep(1500);

		// Verify the gift registry contains products.
		validateAddProductGR();

		SelectorUtil.waitGWTLoadedEventPWA();
		// Click on add to cart.
		SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.addGRProductToCart.get());

		Thread.sleep(1500);
		SelectorUtil.waitGWTLoadedEventPWA();

		// Verify "add to cart" confirmation is displayed.
		validateAddToCartFromGRModal(numberOfItemAddedToCart);

		// Click "Checkout".
		if (isMobile()) {
			WebElement ShoppingCartLink = SelectorUtil.getMenuLinkMobilePWA(CartPageLink);
			// Navigate to the cart page.
			SelectorUtil.clickOnWebElement(ShoppingCartLink);
		} else {
			SelectorUtil.initializeSelectorsAndDoActions(GiftRegistrySelectors.checkoutFromGRModal.get());
		}

		Thread.sleep(500);
		SelectorUtil.waitGWTLoadedEventPWA();

		// Verify that the product is added from Gift registry.
		validateProductAddedFromGR(numberOfItemAddedToCart);
		getCurrentFunctionName(false);
	}

	/**
	* Go to PDP by search and select product swatches.
	*
	* @throws Exception
	*/
	public static void goToPDPAndSelectSwatches() throws Exception {
		getCurrentFunctionName(true);

		logs.debug("Navigate to PDP by search on:" + singlePDPSearchTerm);
		PDP.NavigateToPDP(singlePDPSearchTerm);

		logs.debug("Select product swatched.");
		PDP.selectSwatches();

		getCurrentFunctionName(false);
	}

	/**
	* Validate add product to gift registry from PDP.
	*
	* @throws Exception
	*/
	public static void validateAddToGRModal() throws Exception {
		getCurrentFunctionName(true);
		logs.debug("Validate add product to gift registry modal at PDP.");
		WebElement addToGRModal = SelectorUtil.getelement(GiftRegistrySelectors.addToGiftRegistyModal.get());
		sassert().assertTrue(addToGRModal != null,
				"Error: Product added to gift registry modal displayed.");

		logs.debug("Validate product container in added to gift registry modal at PDP.");
		WebElement productAddedToGRContainer = SelectorUtil.getelement(GiftRegistrySelectors.productAddedToGRContainer.get());
		sassert().assertTrue(productAddedToGRContainer != null,
				"Error: Product container contain in added to gift registry modal.");
		getCurrentFunctionName(false);
	}

	/**
	* Validate the item added to gift registry from PDP.
	*
	* @throws Exception
	*/
	public static void validateAddProductGR() throws Exception {
		getCurrentFunctionName(true);
		logs.debug("Validate added item in gift registry.");
		WebElement productAddedToGRContainer = SelectorUtil.getelement(GiftRegistrySelectors.productListGR.get());
		sassert().assertTrue(productAddedToGRContainer != null,
				"Error: Items in gift registry.");
		getCurrentFunctionName(false);
	}

	/**
	* Validate add product to cart from gift registry.
	* @param oldNumberOfItemAddedToCart
	*
	* @throws Exception
	*/
	public static void validateAddToCartFromGRModal(int oldNumberOfItemAddedToCart) throws Exception {
		getCurrentFunctionName(true);
		logs.debug("Validate add to cart from gift registry.");
		if (!isMobile()) {
			WebElement addToGRModal = SelectorUtil.getelement(GiftRegistrySelectors.addCartFromGRModal.get());
			sassert().assertTrue(addToGRModal != null,
					"Error: Product added to cart from gift registry modal displayed.");
		} else {
			Thread.sleep(500);
			// No add to cart modal displayed at mobile.
			int numberOfItemAddedToCart = Integer.parseInt(SelectorUtil.getelement(GiftRegistrySelectors.miniCartText.get()).getText());
			int tries = 0;
			while (numberOfItemAddedToCart == oldNumberOfItemAddedToCart && tries < 20) {
				Thread.sleep(1000);
				numberOfItemAddedToCart = Integer.parseInt(SelectorUtil.getelement(GiftRegistrySelectors.miniCartText.get()).getText());
				tries ++;
			}
			logs.debug("Number of items after add to cart in mini cart: " + numberOfItemAddedToCart);
			sassert().assertTrue(oldNumberOfItemAddedToCart < numberOfItemAddedToCart,
					"Error: Product added corectlly to cart from Gift registry");
		}
		getCurrentFunctionName(false);
	}

	/**
	* Validate Select gift registry/wishlist modal.
	*
	* @throws Exception
	*/
	public static void validateSelectGRModal() throws Exception {
		getCurrentFunctionName(true);
		logs.debug("Validate select registry/wishlist modal.");
		WebElement selectGRModal = SelectorUtil.getelement(GiftRegistrySelectors.selectGRModal.get());
		sassert().assertTrue(selectGRModal != null,
				"Error: Select gift registry modal displayed.");
		getCurrentFunctionName(false);
	}

	/**
	* Validate cart product and a new items added to cart from gift registry.
	* @param oldNumberOfItemAddedToCart
	*
	* @throws Exception
	*/
	public static void validateProductAddedFromGR(int oldNumberOfItemAddedToCart) throws Exception {
		getCurrentFunctionName(true);
		if (isMobile()) {
			if (!SelectorUtil.isElementExist(By.cssSelector(GiftRegistrySelectors.itemInCart))) {
				boolean gwtValue = SelectorUtil.CheckGWTLoadedEventPWA();
				int tries = 0;
				while (gwtValue) {
					Thread.sleep(1000);
					gwtValue = SelectorUtil.CheckGWTLoadedEventPWA();
					if(tries == 50) {
						throw new NoSuchElementException("Error in Loading GWT.");
					}
					logs.debug("Waiting GWT");
					tries ++;
				}
			}
		}
		logs.debug("Validate product added to cart from gift registry.");
		int numberOfItemAddedToCart = SelectorUtil.getAllElements(GiftRegistrySelectors.cartProductContainer.get()).size();
		logs.debug("Number of items at cart: " + numberOfItemAddedToCart);
		sassert().assertTrue(oldNumberOfItemAddedToCart < numberOfItemAddedToCart,
				"Error: Product added corectlly to shopping cart from Gift registry");

		logs.debug("Validate product added to cart from gift registry label.");
		WebElement selectGRModal = SelectorUtil.getelement(GiftRegistrySelectors.addedFromGR.get());
		sassert().assertTrue(selectGRModal != null,
				"Error: Item added from Gift registry label.");
		getCurrentFunctionName(false);
	}

	public void accessValidAccount(String email,String caseId,String runTest,String desc) throws Exception {
		createdAccount = true;

		//Prepare registration data.
		userMail = RandomUtilities.getRandomEmail();
		userPassword = "P@ssword11";
		Registration registraion = new Registration();
		// Run the registration test case before sign in.
		registraion.freshUserValidate(userMail, userPassword);
		userAddressDetails = registraion.userAddress;
		userFirstName = registraion.userFirstName;
		userLastName = registraion.userLastName;
		userCompanyName = registraion.userCompanyName;
		userAddressLine1 = registraion.addressLine1;
		userPhone = registraion.phone;
	}

}

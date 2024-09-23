package com.karthik178.playwritemanager.pageobjects.sms.vendorportal;

import com.google.common.util.concurrent.Uninterruptibles;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.payload.PayloadBuilder;
import com.karthik178.apimanager.rest.APIExecutor;
import com.karthik178.apimanager.rest.RestRequestDefinition;
import com.karthik178.configservice.common.BasePlaywriteTest;
import com.karthik178.configservice.sms.SMSConfigService;
import com.karthik178.configservice.sms.UserService;
import com.karthik178.playwritemanager.pageobjects.sms.common.BaseModel;
import com.karthik178.playwritemanager.utils.Browserutils;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ManageLearnings extends BasePlaywriteTest {
    private static final Logger logger = LogManager.getLogger(ManageLearnings.class);

    public Page page;
    private Browserutils browserutils;
    public Locator contentTopBar;
    private Locator manageLearningsSearchBar;
    private Locator clearSearchResults;
    private Locator allSpecSearchBar;
    private Locator specTypeDropdown;
    private Locator assignButton;
    private Locator warningMessage;
    private Locator allTabs;
    private Locator allCourseSearchBar;
    private Locator skillClusterContainer;
    private List<Locator> allSpecializations;
    private List<Locator> allSpecTypeDropdownButtons;
    private Locator specializationSearchNoDataFound;
    private Locator allEmployeesToggle;
    private Locator menteeUserContainer;
    private Locator pageNumberButton;
    private Locator rowsPerPage;
    private Locator nameDetails;
    private Locator tableContainer;
    private Locator allEmployeestoggleCaption;
    private Locator rejectTeamRequestTextContainer;
    private Locator recentSpecializationsInsideTable;


    public ManageLearnings(Page page) {
        this.page = page;
        this.browserutils = new Browserutils();
        this.contentTopBar = page.locator(".top-opts");
        this.manageLearningsSearchBar = contentTopBar.getByPlaceholder("Search by name or email");
        this.clearSearchResults = page.locator(".assign-specialization-clear-button");
        this.allSpecSearchBar = page.getByPlaceholder("Search Specialization by name or skills").or(page.getByPlaceholder("Search Skill Cluster by name or skills"));
        this.specTypeDropdown = page.locator(".assign-button-con .mat-select");
        this.assignButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Assign"));
        this.warningMessage = page.getByText("Limit reached");
        this.allTabs = page.locator(".mat-tab-label-container .mat-tab-label-content");
        this.allCourseSearchBar =  page.getByPlaceholder("Search Courses by name or skills");
        this.skillClusterContainer = page.locator(".assign-button-con");
        this.allSpecializations = page.locator(".all-specializations .list-item-con .details-con .details-title").all();
        this.allSpecTypeDropdownButtons = page.locator(".assign-button-con .mat-select span span").all();
        this.specializationSearchNoDataFound = page.locator(".no-data-con .caption");
        this.allEmployeesToggle = page.locator(".page-con .mat-slide-toggle");
        this.menteeUserContainer = page.locator(".name-top-con");
        this.pageNumberButton = page.locator(".page-number-button");
        this.rowsPerPage = page.locator(".limit-con mat-select");
        this.tableContainer = page.locator(".table-con");
        this.nameDetails = tableContainer.locator(".name-details-con .h5");
        this.rejectTeamRequestTextContainer = page.locator(".reject-skill-main-container textarea");
        this.recentSpecializationsInsideTable = page.locator(".mat-column-recentSpecializations .body");
    }

    @Step("Search for User in Manage Learnings Page")
    public void searchForUser(String userDetail) {
//        Secret secret = ConfigHelper.getSecret(clientKey);
//        String userEmailId = secret.getEmail();
        browserutils.clickOn(logger, manageLearningsSearchBar, "Manage learnings search bar");
        browserutils.fillTextBox(logger, manageLearningsSearchBar, userDetail);
        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
        Locator searchResults = page.locator(".search-profile-con").first();
        Assert.assertTrue(browserutils.isVisible(logger, searchResults, 5000),"The User is not Visible/Present");
        browserutils.clickOn(logger, searchResults, "Click on first search result in search bar");
        browserutils.waitForPageLoading(page,40);

    }

    @Step("Click on select specialization button")
    public void clickOnSelectSpecButton() {
        browserutils.clickOn(logger,  page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select Specialization")).first(), "Select Specialization button");
        browserutils.waitForPageLoading(page,10);
    }
    @Step("Click on Select Skill Cluster button")
    public void clickOnSelectSkillClusterButton() {
        browserutils.clickOn(logger,  page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select Skill Cluster")).first(), "Select SkillCluster button");
        browserutils.waitForPageLoading(page,10);
    }

    @Step("Select specialization for user")
    public void selectSpecializationForUser(String specName) {
        Locator specializationLocator = page.getByText(specName,new Page.GetByTextOptions().setExact(true)).first();
        browserutils.scrollDownUntilLocatorIsVisible(specializationLocator);
        browserutils.isVisible(logger,  specializationLocator , 5000);
        browserutils.clickOn(logger,  specializationLocator , String.format("Click on %s",specName));
        browserutils.waitForPageLoading(page, 40);
        Uninterruptibles.sleepUninterruptibly(4, TimeUnit.SECONDS);
    }

    @Step("Select Specialization type in drop down")
    public void setSpecTypeInDropdown(String defaultType,String tobeSelectedType ) {
        Locator defaultDropdownType = page.getByLabel(defaultType,new Page.GetByLabelOptions().setExact(true));
        browserutils.clickOn(logger,defaultDropdownType,String.format("Click On %s",defaultType));
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        Locator specType = page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(tobeSelectedType)).locator("span");
        browserutils.clickOn(logger, specType, "Click on " + tobeSelectedType + " in spec dropdown");
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
    }

    public boolean checkAssignButtonDisabled() {
        return assignButton.isDisabled();
    }

    public boolean checkAssignButtonEnabled() {
        return assignButton.isEnabled();
    }

    public boolean isWarningMessageVisible() {
        Uninterruptibles.sleepUninterruptibly(6, TimeUnit.SECONDS);
        return warningMessage.isVisible();
    }

    public void clearSearchResults() {
        browserutils.clickOn(logger, clearSearchResults, "Click on Clear button");
    }

    @Step("Click On Any Tab On ManangeLearning Page Based On Tab Name")
    public void clickOnTabOnManageLearningPage(String tabName) {
        List<Locator> allTabsLocator = allTabs.all();
        for(Locator locator:allTabsLocator)
        {
            String tabNameText=browserutils.extractText(logger,locator).trim();
            if(tabName.equalsIgnoreCase(tabNameText))
            {
                browserutils.clickOn(logger,locator,String.format(" %s Tab",tabName));
                break;
            }
        }
        browserutils.waitForPageLoading(page, 80);
    }
    @Step("Verify Given Tab Is Present On ManageLearning Page")
    public boolean verifyGivenTabIsPresentOnManageLearningPage(String tabName) {
        boolean flag = false;
        List<Locator> allTabsLocator = allTabs.all();
        for(Locator locator:allTabsLocator)
        {
            String tabNameText=browserutils.extractText(logger,locator).trim();
            if(tabName.equalsIgnoreCase(tabNameText))
            {
                flag = true;
                break;
            }
        }
        return flag;
    }
    @Step("Click on Select Course Button")
    public void clickOnSelectCourseButton() {
        browserutils.clickOn(logger,  page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("SELECT COURSE")), "Select Course button");
        browserutils.waitForPageLoading(page,30);
    }
    @Step("Search and Assign Course for User")
    public void SearchAndAssignCourseForUser(String courseName) {
        browserutils.clickOn(logger, allCourseSearchBar, "Click on course popup search bar");
        browserutils.fillTextBox(logger, allCourseSearchBar, courseName);
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        Locator courseSearchResults = page.locator(".list-item-con").first();
        browserutils.isVisible(logger, courseSearchResults, 50);
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        browserutils.clickOn(logger, courseSearchResults, "Click on first course from search results");
        browserutils.waitForLocator(assignButton,20);
        browserutils.clickOn(logger, assignButton, "Click on first course from search results");
        BaseModel modal =new BaseModel(page);
        modal.clickOnButton("YES","Yes Button");
        if(modal.verifyButtonExist("OK",8))
        {
            modal.clickOnButton("OK","Click On OK Button");
        }
    }
    @Step("Approve the Specialization From Team Request")
    public void approveSpecFromTeamRequest(String skillClusterName) {
        List<Locator> allskillClusterButtonContainer = skillClusterContainer.locator("span div").all();
        int index =0;
        for(Locator skillClusterButton :allskillClusterButtonContainer)
        {
            String extractedSkillClusterName = browserutils.extractText(logger,skillClusterButton).trim();
            if(extractedSkillClusterName.equalsIgnoreCase(skillClusterName))
            {
                Locator assignLocator = page.locator(String.format("//span[normalize-space()='%s']","Assign"));
                browserutils.clickOn(logger,assignLocator.nth(index),"Click On Assign");
                BaseModel modal =new BaseModel(page);
                Uninterruptibles.sleepUninterruptibly(4, TimeUnit.SECONDS);
                modal.clickOnButton("Yes","Click On Yes Button");
                Uninterruptibles.sleepUninterruptibly(4, TimeUnit.SECONDS);
                modal.verifyButtonExist("OK",8);
                modal.clickOnButton("OK","Click On Ok Button");
                break;
            }
            index++;
        }
    }
    @Step("Reject the Specialization From Team Requests Tab")
    public void rejectTheSpecializationFromTeamRequestsTab(String skillClusterName) {
        List<Locator> allskillClusterButtonContainer = skillClusterContainer.locator("span div").all();
        int index =0;
        for(Locator skillClusterButton :allskillClusterButtonContainer)
        {
            String extractedSkillClusterName = browserutils.extractText(logger,skillClusterButton).trim();
            if(extractedSkillClusterName.equalsIgnoreCase(skillClusterName))
            {
                Locator rejectLocator = page.locator(String.format("//span[normalize-space()='%s']","Reject"));
                browserutils.clickOn(logger,rejectLocator.nth(index),"Click On ");
                BaseModel modal =new BaseModel(page);
                Uninterruptibles.sleepUninterruptibly(4, TimeUnit.SECONDS);
                browserutils.fillTextBox(logger,rejectTeamRequestTextContainer,"This skillcluster isn't applicable for you,hence rejecting it");
                modal.clickOnButton("Reject","Click On Reject button");
                if(modal.verifyButtonExist("OK",10)){
                    modal.clickOnButton("OK","Click On OK button");
                }
                break;
            }
            index++;
        }
    }
    @Step("Assign the Specialization given menteeUserEmailId")
    public String assignSpecGivenUserEmailId(String menteeuserEmailId) {
        searchForUser(menteeuserEmailId);
        browserutils.waitForPageLoading(page,40);
        clickOnSelectSpecButton();
        /* Get Specialization Name */
        String specName = getRandomSpecializationFromSelectSpecializationSearch();
        selectSpecializationForUser(specName);
        BaseModel modal = new BaseModel(page);
        modal.clickOnButton("ASSIGN","Click On Assign Button");
        Uninterruptibles.sleepUninterruptibly(4, TimeUnit.SECONDS);
        modal.clickOnButton("Yes","Click On Yes Button");
        Uninterruptibles.sleepUninterruptibly(4, TimeUnit.SECONDS);
        if(modal.verifyButtonExist("OK",2)) {
            modal.clickOnButton("OK", "Click On Ok Button");
        }
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        return specName;
    }
    @Step("Verify Specilaization Search is Working")
    public void verifySpecializationSearchIsWorking(String specializationMode) {
        browserutils.waitForPageLoading(page, 40);
        if(specializationMode.equalsIgnoreCase("standard")) {
            clickOnSelectSpecButton();
        }
        else if(specializationMode.equalsIgnoreCase("skillcluster")) {
            clickOnSelectSkillClusterButton();
        }
        /* Get Specialization Name */
        String specName = getRandomSpecializationFromSelectSpecializationSearch();
        browserutils.clickOn(logger, allSpecSearchBar, "Click on specializations popup search bar");
        browserutils.fillTextBox(logger, allSpecSearchBar, specName);
        browserutils.waitForPageLoading(page,40);
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        Locator specSearchResults = page.locator(".all-specializations .list-item-con").first();
        /*Assertions */
        Assert.assertTrue(specSearchResults.isVisible(),"Specialization search is not working");
        Assert.assertFalse(specializationSearchNoDataFound.isVisible(),"Specialization search is not working");

    }

    @Step("Assign the Specialization given UserEmailId and SpecCategoryType and Assign Flag For Skill Cluster Mode")
    public String assignSpecGivenUserEmailIdAndCategoryTypeForSkillClusterMode(String menteeuserEmailId,String assignFlag,String specType) {
        searchForUser(menteeuserEmailId);
        browserutils.waitForPageLoading(page,40);
        clickOnSelectSkillClusterButton();
        /* Get Specialization Name */
        String specName = getRandomSpecializationFromSelectSpecializationSearch();
        selectSpecializationForUser(specName);

        /* SkillClusterButton from skillCluster Select */
        Locator skillClusterNameSelectButton = page.getByRole(AriaRole.TABLE).locator("mat-form-field div").filter(new Locator.FilterOptions().setHasText("Skill Cluster name")).nth(1);
        browserutils.waitForLocator(skillClusterNameSelectButton,20);
        if(skillClusterNameSelectButton.isVisible()) {
            /* Select the First Option */
            browserutils.clickOn(logger, skillClusterNameSelectButton, "Click On SkillCluster Select Button");
            Locator firstOptionInSkillCluster = page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions()).first();
            browserutils.clickOn(logger, firstOptionInSkillCluster, "Click On First Option");
            Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        }
        /*Select Spec Type in Dropdown */
        setSpecTypeInDropdown("Primary",specType);
        /* Based On AssignFlag  ,if it's No Ignore the below Steps */
        if (assignFlag.equalsIgnoreCase("Yes")) {
            BaseModel modal = new BaseModel(page);
            modal.clickOnButton("ASSIGN", "Click On Assign Button");
            Uninterruptibles.sleepUninterruptibly(4, TimeUnit.SECONDS);
            modal.clickOnButton("Yes", "Click On Yes Button");
            Uninterruptibles.sleepUninterruptibly(4, TimeUnit.SECONDS);
            if (modal.verifyButtonExist("OK", 4)) {
                modal.clickOnButton("OK", "Click On Ok Button");
            }
            Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        }
        return specName;
    }


    @Step("Get Maximum Primary Specialization Limit From getSmsSettings API ")
    public int getMaximumPrimarySpecLimitFromSettingAPI(UserContext userContext)
    {
        SMSConfigService smsConfigService =new SMSConfigService();
        Response response= smsConfigService.getSMSSettingsResponse(userContext);
        String maxLimitCount = response.getBody().jsonPath().getString("client.smsSettings.numActivePrimarySpecializations");
        return Integer.parseInt(maxLimitCount);
    }
    @Step("Get Maximum Secondary Specialization Limit From getSmsSettings API ")
    public int getMaximumSecondarySpecLimitFromSettingAPI(UserContext userContext)
    {
        SMSConfigService smsConfigService =new SMSConfigService();
        Response response= smsConfigService.getSMSSettingsResponse(userContext);
        String maxLimitCount = response.getBody().jsonPath().getString("client.smsSettings.numActiveSecondarySpecializations");
        return Integer.parseInt(maxLimitCount);
    }
    @Step("Get Random Specialization From Select Specialization Search")
    public String getRandomSpecializationFromSelectSpecializationSearch()
    {
        /* Get All Specializations*/
        List<Locator> allSpecialization = page.locator(".all-specializations .list-item-con .details-con .details-title").all();
        int size = allSpecialization.size();
        /* Get RandomInt */
        Random random = new Random();
        int randomInt = random.nextInt(0,size);
        Locator specialization = allSpecialization.get(randomInt);
        String specName = browserutils.extractText(logger,specialization).trim();
        return specName;
    }
    @Step("Create New Mentee User")
    public String createMenteeUser(ITestContext iTestContext,String managerKey)
    {
        UserService userService = new UserService();
        Map<String, Object> newMenteeUserReplaceKeys = new HashMap<>();
        newMenteeUserReplaceKeys.put("employeeId", "Automation-AssignSpec" + getRandomStringId());
        Response response = userService.createUser(managerKey, iTestContext, newMenteeUserReplaceKeys);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("data[0].status"), "Success", "HRMS API - Creating new user is not working");
        iTestContext.setAttribute("newMenteeUserReplaceKeys",newMenteeUserReplaceKeys);
        String menteeUserEmployeeId = (String) newMenteeUserReplaceKeys.get("employeeId");
        /* Get EmployeeId Of Mentee User */
        SMSConfigService smsConfigService=new SMSConfigService(managerKey);
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, menteeUserEmployeeId);
        String menteeUserEmailId = String.valueOf(userDetails.get("email"));
        return menteeUserEmailId;

    }
    @Step("Create New Mentee User With Addidtional HRMS Fields")
    public String createMenteeUserWithAdditionalHRMSFields(ITestContext iTestContext,String managerKey)
    {
        UserService userService = new UserService();
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("employeeId", "Automation-AssignSpec" + getRandomStringId());
        replaceKeys.put("currentLocation", "Pune");
        replaceKeys.put("designationName","AUTOMATION LEAD");
        replaceKeys.put("marketName", "HMT");
        replaceKeys.put("subMarketName","HMT-3");
        replaceKeys.put("designationLevel","5");
        Response response = userService.createUser(managerKey, iTestContext, replaceKeys);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("data[0].status"), "Success", "HRMS API - Creating new user is not working");
        iTestContext.setAttribute("replaceKeys",replaceKeys);
        String menteeUserEmployeeId = (String) replaceKeys.get("employeeId");
        /* Get EmployeeId Of Mentee User */
        SMSConfigService smsConfigService=new SMSConfigService(managerKey);
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, menteeUserEmployeeId);
        String menteeUserEmailId = String.valueOf(userDetails.get("email"));
        return menteeUserEmailId;

    }
    @Step("Create New Mentee User With Selected HRMS Fields For Filters")
    public String createMenteeUserWithSelectedHRMSFieldsForFilters(ITestContext iTestContext,String managerKey)
    {
        UserService userService = new UserService();
        SMSConfigService smsConfigService = new SMSConfigService(managerKey);
        UserContext userContext = smsConfigService.getUserContext();

        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("employeeId", "Automation-AssignSpec" + getRandomStringId());
        Response response = userService.createUserWithSelectedHRMSFieldsForFilters(smsConfigService,userContext, iTestContext, replaceKeys);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getBody().jsonPath().getString("data[0].status"), "Success", "HRMS API - Creating new user is not working");
        iTestContext.setAttribute("newMenteeUserReplaceKeys",replaceKeys);
        String menteeUserEmployeeId = (String) replaceKeys.get("employeeId");
        /* Get EmployeeId Of Mentee User */
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, menteeUserEmployeeId);
        String menteeUserEmailId = String.valueOf(userDetails.get("email"));
        return menteeUserEmailId;

    }
    @Step("Assign Specialization To User With SpecializationId and Category")
    public void assignSpecWithSpecIdAndSpecCategoryThroughAPI(ITestContext iTestContext,UserContext userContext,String managerKey, String menteeEmailId,String specializationId,String category) {

        /* Get Employee Id*/
        SMSConfigService smsConfigService = new SMSConfigService(managerKey);
        Map<String, JsonPath> userDetails = smsConfigService.getUserDetails(iTestContext, menteeEmailId);
        String empId = String.valueOf(userDetails.get("empId"));
        /*Get UserId*/
        String userId = String.valueOf(userDetails.get("userId"));

        RestRequestDefinition assingSpecilizationToUserDef = PayloadBuilder.mapJsonToRestDefinition("sms/myteam/assign.specilization.with.specialization.category.json");
        Map<String, Object> replaceKeys = new HashMap<>();
        replaceKeys.put("userId", userId);
        replaceKeys.put("specializationId", specializationId);
        replaceKeys.put("specializationCategory", category);
        replaceKeys.put("empId", empId);
        PayloadBuilder.getResolvedDefinition(assingSpecilizationToUserDef, replaceKeys);
        Response response = APIExecutor.execute(userContext, assingSpecilizationToUserDef, iTestContext);
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Step("Click on All Employees Toggle on ManageLearnings")
    public void clickOnAllEmployeesToggleOnManageLearnings() {
        browserutils.clickOn(logger,allEmployeesToggle,"Click on All Employees Toggle");
        browserutils.waitForPageLoading(page,60);
    }
    @Step("Verify ManageLearnings Page has More than 5 Mentee Users")
    public boolean verifyManageLearningPageHasMoreThan5MenteeUsers() {
        boolean flag = false;
        List<Locator> allmenteeUserContainer = menteeUserContainer.all();
        System.out.println(allmenteeUserContainer.size());
        if (allmenteeUserContainer.size() > 5) {
            flag = true;
        }
        return flag;
    }
    @Step("Verify Pagination is Working for ManageLearnings Page")
    public void verifyPaginationIsWorkingForManageLearnings(String pageNumberTobeClicked) {
        List<Locator> allPageNumberButtons = pageNumberButton.all();
        for (Locator pageNumberLocator : allPageNumberButtons) {
            String pageNumberText = browserutils.extractText(logger, pageNumberLocator).trim();
            if (pageNumberTobeClicked.equalsIgnoreCase(pageNumberText)) {
                browserutils.clickOn(logger, pageNumberLocator, String.format("Click On %s Page Number", pageNumberTobeClicked));
                browserutils.waitForPageLoading(page,60);
                break;
            }

        }
        List<Locator> allmenteeUsers = menteeUserContainer.all();
        int menteeUsersShown = allmenteeUsers.size();
        boolean flag = false;
        if(menteeUsersShown >=1) {
            flag = true;
        }
        Assert.assertTrue(flag,"Pagination is not working");
    }
    @Step("Select Rows Per Page in ManageLearnings Page")
    public void selectRowsPerPageInManageLearnings(String rowNumber) {

        browserutils.clickOn(logger, rowsPerPage, "Click on Rows Per Page Dropdown");
        Locator option = page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(rowNumber).setExact(true));
        browserutils.clickOn(logger, option, String.format("Click on %s Row in dropdown", rowNumber));
        browserutils.waitForPageLoading(page,40);
    }
    @Step("Verify Alias Settings in Select Specialization Dropdown For Standard Mode")
    public void verifyAliasSettingsInSelectSpecializationDropdownForStandardMode(String menteeuserEmailId,String defaultDropDownType,List<String> expectedDropdownText) {
        searchForUser(menteeuserEmailId);
        browserutils.waitForPageLoading(page,40);
        clickOnSelectSpecButton();
        /* Get Specialization Name */
        String specName = getRandomSpecializationFromSelectSpecializationSearch();
        selectSpecializationForUser(specName);

        Locator defaultDropdownType = page.getByLabel(defaultDropDownType,new Page.GetByLabelOptions().setExact(true));
        browserutils.clickOn(logger,defaultDropdownType,String.format("Click On %s",defaultDropDownType));
        List<Locator> allOptionsUnderDropdown = page.locator("div[role='listbox'] .mat-option-text").all();
        List<String> actualDropdownText = new ArrayList<>();
        for(Locator option: allOptionsUnderDropdown)
        {
            String optionText = browserutils.extractText(logger,option).trim();
            actualDropdownText.add(optionText);
        }

        Assert.assertEquals(actualDropdownText,expectedDropdownText,"Alias Settings are not working for Select Specialization Dropdown");


    }
    @Step("Verify Alias Settings in Select SkillCluster Dropdown for SkillClusterMode")
    public void verifyAliasSettingsInSelectSkillClusterDropdownForSkillClusterMode(String menteeuserEmailId,String defaultDropDownType,List<String> expectedDropdownText) {
        searchForUser(menteeuserEmailId);
        browserutils.waitForPageLoading(page,40);
        clickOnSelectSkillClusterButton();
        /* Get Specialization Name */
        String specName = getRandomSpecializationFromSelectSpecializationSearch();
        selectSpecializationForUser(specName);

        /* SkillClusterButton from skillCluster Select */
        Locator skillClusterNameSelectButton = page.getByRole(AriaRole.TABLE).locator("mat-form-field div").filter(new Locator.FilterOptions().setHasText("Skill Cluster name")).nth(1);
        browserutils.waitForLocator(skillClusterNameSelectButton,20);
        if(skillClusterNameSelectButton.isVisible()) {
            /* Select the First Option */
            browserutils.clickOn(logger, skillClusterNameSelectButton, "Click On SkillCluster Select Button");
            Locator firstOptionInSkillCluster = page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions()).first();
            browserutils.clickOn(logger, firstOptionInSkillCluster, "Click On First Option");
            Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        }

        Locator defaultDropdownType = page.getByLabel(defaultDropDownType,new Page.GetByLabelOptions().setExact(true));
        browserutils.clickOn(logger,defaultDropdownType,String.format("Click On %s",defaultDropDownType));
        List<Locator> allOptionsUnderDropdown = page.locator("div[role='listbox'] .mat-option-text").all();
        List<String> actualDropdownText = new ArrayList<>();
        for(Locator option: allOptionsUnderDropdown)
        {
            String optionText = browserutils.extractText(logger,option).trim();
            actualDropdownText.add(optionText);
        }

        Assert.assertEquals(actualDropdownText,expectedDropdownText,"Alias Settings are not working for Select Specialization Dropdown");


    }
    @Step("Click On Reportee User on Assign Specializations Page Based on UserName")
    public void clickOnReporteeUserOnAssignSpecializationsPagePageBasedOnUserName(String reporteeUserName) {

        List<Locator> allReporteeUser = nameDetails.all();
        for(Locator reporteeUser : allReporteeUser)
        {
            String actualreporteeUserName = browserutils.extractText(logger,reporteeUser).trim();
            if(actualreporteeUserName.equalsIgnoreCase(reporteeUserName))
            {
                browserutils.clickOn(logger,reporteeUser,String.format("Click on %s",reporteeUserName));
                browserutils.waitForPageLoading(page,60);
                break;
            }
        }

    }

    @Step("Verify User Exists in TeamMember Column in AssignSpecializations Page")
    public boolean verifyUserExistsInTeamMemberColumnInAssignSpecializationsPage(String reporteeUserName) {
        boolean flag = false;
        List<Locator> allReporteeUser = nameDetails.all();
        for(Locator reporteeUser : allReporteeUser)
        {
            String actualreporteeUserName = browserutils.extractText(logger,reporteeUser).trim();
            if(actualreporteeUserName.equalsIgnoreCase(reporteeUserName))
            {
                flag = true;
                break;
            }
        }
        return flag;

    }
    @Step("Verify Given Specilaization Is Found In RecentSpecialization Column")
    public boolean verifyGivenSpecializationIsFoundInRecentSpecializationColumn(String specName) {
        boolean flag = false;
        List<Locator> allRecentSpecializations = recentSpecializationsInsideTable.all();
        for(Locator recentSpecialization : allRecentSpecializations)
        {
            String recentSpecializationName = browserutils.extractText(logger,recentSpecialization).trim();
            if(recentSpecializationName.equalsIgnoreCase(specName))
            {
                flag = true;
                break;
            }
        }
        return flag;

    }

    }

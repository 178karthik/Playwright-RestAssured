package com.karthik178.configservice.sms;

import com.karthik178.apimanager.model.UserContext;
import com.karthik178.apimanager.utils.LogHandler;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SMSSettingHelper {

    private Response response;
    private static final Logger logger = LogManager.getLogger(SMSSettingHelper.class);

    public SMSSettingHelper(Response response) {
        this.response = response;
    }

    public SMSSettingHelper(SMSConfigService smsConfigService, UserContext userContext) {
        this.response =  smsConfigService.getSMSSettingsResponse(userContext);;
    }

    @Step("Get Skill Categories Names From Settings")
    public List<String> getSkillCategoriesNames() {
        List<Map<String, String>> skillCategoriesMap =  response.jsonPath().getList("client.smsSettings.skillCategories");
        List<String> skillCategories = new ArrayList<>();
        for(Map<String, String> skillCat : skillCategoriesMap){
            skillCategories.add(skillCat.get("name"));
        }
        LogHandler.logInfo(logger, String.format("Skill Categories :: %s", skillCategories));
        return skillCategories;
    }

    @Step("Get Skill Categories Ids From Settings")
    public List<String> getSkillCategoriesIds() {
        List<Map<String, String>> skillCategoriesMap =  response.jsonPath().getList("client.smsSettings.skillCategories");
        List<String> skillCategories = new ArrayList<>();
        for(Map<String, String> skillCat : skillCategoriesMap){
            skillCategories.add(skillCat.get("categoryId"));
        }
        LogHandler.logInfo(logger, String.format("Skill Category Ids :: %s", skillCategories));
        return skillCategories;
    }

    public List<Map<String, String>> getUsedSkillRatingsBasedOnCategory(String categoryId) {
        List<Map<String, String>> skillCategoryIds =  response.jsonPath().getList("client.smsSettings.categoryWiseSettings.categoryId");
        int targetCategoryIndex =  skillCategoryIds.indexOf(categoryId);
        String path = String.format("client.smsSettings.categoryWiseSettings[%d].skillRatings", targetCategoryIndex);
        List<Map<String, String>> skillCategoryRatings =  response.jsonPath().getList(path);
        LogHandler.logInfo(logger, String.format("Total Skill ratings for Category %s : count = %s value : %s",categoryId, skillCategoryRatings.size(), skillCategoryRatings));
        List<Map<String, String>> usedSkillRatings = skillCategoryRatings.stream().filter(skillCategory ->
                skillCategory.get("useRating").equalsIgnoreCase("yes")).collect(Collectors.toList());
        LogHandler.logInfo(logger, String.format("Used Skill ratings for Category %s : count = %s value : %s",categoryId, usedSkillRatings.size(), usedSkillRatings));
        return usedSkillRatings;
    }

    public List<Map<String, String>> getNotUsedSkillRatingsBasedOnCategory(String categoryId) {
        List<Map<String, String>> skillCategoryIds =  response.jsonPath().getList("client.smsSettings.categoryWiseSettings.categoryId");
        int targetCategoryIndex =  skillCategoryIds.indexOf(categoryId);
        String path = String.format("client.smsSettings.categoryWiseSettings[%d].skillRatings", targetCategoryIndex);
        List<Map<String, String>> skillCategoryRatings =  response.jsonPath().getList(path);
        LogHandler.logInfo(logger, String.format("Total Skill ratings for Category %s : count = %s value : %s",categoryId, skillCategoryRatings.size(), skillCategoryRatings));
        List<Map<String, String>> usedSkillRatings = skillCategoryRatings.stream().filter(skillCategory ->
                skillCategory.get("useRating").equalsIgnoreCase("No")).collect(Collectors.toList());
        LogHandler.logInfo(logger, String.format("Used Skill ratings for Category %s : count = %s value : %s",categoryId, usedSkillRatings.size(), usedSkillRatings));
        return usedSkillRatings;
    }


    public List<String> getUsedSkillRatingNamesFromSettings(String categoryName) {
        List<String> categoryNames = getSkillCategoriesNames();
        int categoryIndex = categoryNames.indexOf(categoryName);
        List<String> categoryIds = getSkillCategoriesIds();
        String categoryId = categoryIds.get(categoryIndex);
        List<Map<String, String>> usedSkillRatings = getUsedSkillRatingsBasedOnCategory(categoryId);
        List<String> skillRatingNames = new ArrayList<>();
        for(Map<String, String> rating : usedSkillRatings){
            skillRatingNames.add(rating.get("title"));
        }
        LogHandler.logInfo(logger, String.format("category :: %s  ,  rating names ::  %s ",categoryName, skillRatingNames));
        return skillRatingNames;
    }

    public List<String> getUsedSkillRatingTypesFromSettings(String categoryName) {
        List<String> categoryNames = getSkillCategoriesNames();
        int categoryIndex = categoryNames.indexOf(categoryName);
        List<String> categoryIds = getSkillCategoriesIds();
        String categoryId = categoryIds.get(categoryIndex);
        List<Map<String, String>> usedSkillRatings = getUsedSkillRatingsBasedOnCategory(categoryId);
        List<String> skillRatingTypes = new ArrayList<>();
        for(Map<String, String> rating : usedSkillRatings){
            skillRatingTypes.add(rating.get("dataKey"));
        }
        LogHandler.logInfo(logger, String.format("category :: %s  ,  dataKey names ::  %s ",categoryName, skillRatingTypes));
        return skillRatingTypes;
    }

    public List<String> getNotUsedSkillRatingNamesFromSettings(String categoryName) {
        List<String> categoryNames = getSkillCategoriesNames();
        int categoryIndex = categoryNames.indexOf(categoryName);
        List<String> categoryIds = getSkillCategoriesIds();
        String categoryId = categoryIds.get(categoryIndex);
        List<Map<String, String>> usedSkillRatings = getNotUsedSkillRatingsBasedOnCategory(categoryId);
        List<String> skillRatingNames = new ArrayList<>();
        for(Map<String, String> rating : usedSkillRatings){
            skillRatingNames.add(rating.get("title"));
        }
        LogHandler.logInfo(logger, String.format("category :: %s  ,  rating names ::  %s ",categoryName, skillRatingNames));
        return skillRatingNames;
    }

    @Step("Get User Proficiency Tooltip")
    public String getUseProficiencyTooltip() {
        Map<String, String> smsSettings =  response.jsonPath().getMap("client.smsSettings");
        String result = "No";
        if (smsSettings.containsKey("useProficiencyTooltip")) {
            result =  smsSettings.get("useProficiencyTooltip");
        }
        LogHandler.logInfo(logger, String.format("User Proficiency Tooltip Setting :: %s", result));
        return result;
    }

    public String getUploadResumeAlias() {
        String resumeAlias =  response.body().jsonPath().getString("client.smsSettings.uploadResumeAlias");
        LogHandler.logInfo(logger, "Upload Resume alias : " + resumeAlias);
        /*  if the Alias is Empty,By Default Upload Resume is Used */
        if (Objects.isNull(resumeAlias))
        {
            resumeAlias = "Upload Resume";
        }
        return resumeAlias;
    }
    public String getAliasForTeamDetails() {
        String teamDetailsAlias =  response.body().jsonPath().getString("client.smsSettings.teamDetailsAlias");
        LogHandler.logInfo(logger, "Team Details Alias: " + teamDetailsAlias);
        /*  if the Alias is Empty,By Default Upload Resume is Used */
        if (Objects.isNull(teamDetailsAlias))
        {
            teamDetailsAlias = "Team Details";
        }
        Assert.assertNotNull(teamDetailsAlias,"getSmsSetting API does't contain client.smsSettings.teamDetailsAlias Json object");
        return teamDetailsAlias;
    }
    public String getSpecializationKeywordAlias() {
        String specializationAlias =  response.body().jsonPath().getString("client.smsSettings.specializationsAlias");
        LogHandler.logInfo(logger, "specializationAlias: " + specializationAlias);
        /*  if the Alias is Empty,By Default Specialization is Used */
        if (Objects.isNull(specializationAlias))
        {
            specializationAlias = "Specialization";
        }
        Assert.assertNotNull(specializationAlias,"getSmsSetting API does't contain client.smsSettings.specializationsAlias Json object");
        return specializationAlias;
    }
    public String getDesignationKeywordAlias() {
        String designationAlias =  response.body().jsonPath().getString("client.smsSettings.designationAlias");
        LogHandler.logInfo(logger, "designationAlias: " + designationAlias);
        /*  if the Alias is Empty,By Default Specialization is Used */
        if (Objects.isNull(designationAlias))
        {
            designationAlias = "Designation";
        }
        Assert.assertNotNull(designationAlias,"getSmsSetting API does't contain client.smsSettings.designationAlias Json object");
        return designationAlias;
    }
    public String getDesignationLevelKeywordAlias() {
        String designationLevelAlias =  response.body().jsonPath().getString("client.smsSettings.designationLevelAlias");
        LogHandler.logInfo(logger, "designationLevelAlias: " + designationLevelAlias);
        /*  if the Alias is Empty,By Default Specialization is Used */
        if (Objects.isNull(designationLevelAlias))
        {
            designationLevelAlias = "Designation Grades";
        }
        Assert.assertNotNull(designationLevelAlias,"getSmsSetting API does't contain client.smsSettings.designationLevelAlias Json object");
        return designationLevelAlias;
    }

    @Step("Supported File Formats For UploadResume")
    public List<String> getSupportedFileFormatsForUploadResume() {
        Map<String, String> smsSettings =  response.jsonPath().getMap("client.smsSettings");
        List<String> result = new ArrayList<>();
        if (smsSettings.containsKey("supportedFileFormatsForUploadResume")) {
            result = response.jsonPath().getList("client.smsSettings.supportedFileFormatsForUploadResume");

        }
        LogHandler.logInfo(logger, String.format("Supported File Formats For UploadResume :: %s", result));
        return result;
    }

    @Step("Get skill Hierachies configured in settings")
    public List<String> getSkillHierarchies() {
        Map<String, String> smsSettings =  response.jsonPath().getMap("client.smsSettings");
        List<String> result = new ArrayList<>();
        if (smsSettings.containsKey("skillHierarchy")) {
            result = response.jsonPath().getList("client.smsSettings.skillHierarchy.name");

        }
        LogHandler.logInfo(logger, String.format("Skill Hierarchy configured in settings :: %s", result));
        return result;
    }

    public List<String> getSkillRatingNamesFromSetting() {
        Map<String, String> smsSettings =  response.jsonPath().getMap("client.smsSettings");
        List<Map<String, String>> result = new ArrayList<>();
        List<String> skillRatingTitles = new ArrayList<>();
        if (smsSettings.containsKey("categoryWiseSettings")) {
            result = response.jsonPath().getList("client.smsSettings.categoryWiseSettings[0].skillRatings");
            System.out.println("Skill Rating : " + result);
            result = result.stream().filter(sRating -> sRating.get("useRating").equalsIgnoreCase("yes")).collect(Collectors.toList());
            for(Map<String, String> eachCategory : result) {
                if (eachCategory.get("useRating").equalsIgnoreCase("yes")) {
                    skillRatingTitles.add(eachCategory.get("title"));
                }
            }
            System.out.println("Skill Rating : " + skillRatingTitles);
        }
        LogHandler.logInfo(logger, String.format("Skill Hierarchy configured in settins :: %s", skillRatingTitles));
        return skillRatingTitles;
    }




    @Step("Get Skill Rating Names from Setting")
    public List<String> getProficiencyTooltipSettings() {
        List<String> result = new ArrayList<>();
        Map<String, String> smsSettings =  response.jsonPath().getMap("client.smsSettings");
        String flag = "NO";
        if (smsSettings.containsKey("useProficiencyTooltip")) {
            flag = smsSettings.get("useProficiencyTooltip");
        }
        LogHandler.logInfo(logger, String.format("use Proficiency Tooltip Setting : %s", flag));
        if (flag.equalsIgnoreCase("YES")) {
            for(int level=0 ;level < 10 ;level++) {
                String fieldname = String.format("configureSkillsTooltip%d", level+1);
                if (smsSettings.containsKey(fieldname)) {
                    result.add(smsSettings.get(fieldname));
                } else {
                    break;
                }
            }
        }
        LogHandler.logInfo(logger, String.format("Applicable Proficiency tool tips : %s", result));
        return result;
    }

    public int getNumActivePrimarySpecializations() {
        try{
            int primarySpecLimit =  response.jsonPath().getInt("client.smsSettings.numActivePrimarySpecializations");
            LogHandler.logInfo(logger,  String.format("Primary spec limit : %d", primarySpecLimit));
            return primarySpecLimit;
        }catch (Error e) {
            throw new RuntimeException("Unable to fetch primary spec limit. Used key:'client.smsSettings.numActivePrimarySpecializations'" + e);
        }
    }

    public int getNumActiveSecondarySpecializations() {
        try{
            int primarySpecLimit =  response.jsonPath().getInt("client.smsSettings.numActiveSecondarySpecializations");
            LogHandler.logInfo(logger,  String.format("Secondary spec limit : %d", primarySpecLimit));
            return primarySpecLimit;
        }catch (Error e) {
            throw new RuntimeException("Unable to fetch primary spec limit. Used key:'client.smsSettings.numActiveSecondarySpecializations'" + e);
        }
    }
    @Step("Verify In Settings Given Feature Is Enabled")
    public boolean verifyInSettingsGivenFeatureIsEnabled(String feature) {
        String requiredJsonPath = "client.features.sms."+feature;
        String featureStatus = response.body().jsonPath().getString(requiredJsonPath);
        boolean flag = false;
        if(featureStatus.equalsIgnoreCase("No Access"))
        {
            flag = false;
        }
        else if(featureStatus.equalsIgnoreCase("Edit") || featureStatus.equalsIgnoreCase("View Only"))
        {
            flag = true;
        }
        return flag;
    }
    public boolean verifyRequestForSpecializationEnabled() {
        String requestForSpecializationStatus =  response.body().jsonPath().getString("client.smsSettings.specializationRequestsEnabled");
        LogHandler.logInfo(logger, "requestForSpecializationStatus: " + requestForSpecializationStatus);
        boolean flag = false;
        if(requestForSpecializationStatus.equalsIgnoreCase("yes"))
        {
            flag = true;
        }
        Assert.assertNotNull(requestForSpecializationStatus,"getSmsSetting API does't contain client.smsSettings.requestForSpecializationStatus Json object");
        return flag;
    }
    public String getSpecializationMode() {
        String specializationMode =  response.body().jsonPath().getString("client.smsSettings.specializationMode");
        LogHandler.logInfo(logger, "specializationMode " + specializationMode);
        Assert.assertNotNull(specializationMode,"getSmsSetting API does't contain client.smsSettings.specializationMode Json object");
        return specializationMode;
    }


}

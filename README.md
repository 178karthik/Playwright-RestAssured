##### Author : Karthik T

* [System Requirement](#System Requirement)
* [Major dependencies](#Major dependencies)
* [Coding guideline](#Coding guideline)
* [How to contribute for API Automation](#how-to-contribute-for-api-automation)
  * [Prepare Payload](#1-prepare-payload)
  * [Initialize API request definition](#2-initialize-rest-request-definition)
  * [Replace Placeholders](#3-replace-placeholders)
  * [Fire API Call](#4-fire-api-cal)
  * [Do Assertions](#5-sample-assertions)
  * [How to run a single API test case from intellij](#how-to-run-a-single-test-case-from-intellij)
* [UI Automation framework overview](#framework-overview)
  * [Browser Utils](#browserutils-class)

## **Systems Requirement**
* Java Latest Version
* Intellij / Eclipse
* Maven

## Major dependencies
* Rest Assured
* Testng
* Jackson
* Jsonpath
* Allure report

## Coding guideline
* All new feature branches should be in the format of `feature/[service]-[description]-tests`

  ex: ```feature/sms-skill-origin-tests```

* methods names, variable names should be camelcase.

* no underscores in any names, variable names, resource files and folder names.

* Create payloads and reusable methods only if needed

* No helper method should present test files. Move to repective packages.


## How to contribute for API Automation

### 1. Prepare Payload
* path : src/test/resources.
* Create required json payload file.
* Add enviroment agnostic payload
* Add replacers endoding {} branckets.
* end point should start with /
* No environment specific hardcoded id's/value should present in payload.

###### Below payload is to update skill Origin
```json
{
  "description": "[Admin->Leafskill->Skill Hierarchy] :: Update Skill Origin",
  "uriPath": "/prismapi/admin/saveSkills",
  "headers": {
    "Content-Type": "application/json",
    "Accept": "application/json"
  },
  "body": {
    "hierarchyElementId": "{hierarchyElementId}",
    "clientId": "{user.clientId}",
    "type": "skillOrigin",
    "name": "{name}",
    "practice": "{randomPractice}"
  },
  "method": "POST"
}

```
###  2. Initialize Rest Request definition

```
 RestRequestDefinition createSkillOriginDefinition = PayloadBuilder
    .mapJsonToRestDefinition("sms/skillOrigin/update-skill-origin.post.json");
```
###  3. Replace Placeholders

```
Map<String, Object> replaceKeys = new HashMap<>();
replaceKeys.put("randomPractice", entities.get("randomPractice"));
replaceKeys.put("name", "SO-AutoTest-"+getRandomStringId());
replaceKeys.put("user.clientId", SMSConfigService.getClientId(userContext));
PayloadBuilder.getResolvedDefinition(createSkillOriginDefinition, replaceKeys);
```
###  4. Fire API Cal

```
Response response = APIExecutor.execute(userContext, createSkillOriginDefinition);
```
###  5. Sample Assertions

```
Assert.assertEquals(response.getBody().jsonPath().getString("status"), "Success", "Response status is success");
Assert.assertTrue(response.getBody().jsonPath().getList("searchData").size() > 0, "Service is not retrieved in search results");
Assert.assertEquals(response.getBody().jsonPath().getString("searchData[0].name"), serviceName, String.format("%s skill not retrieved", serviceName));
```




#### How to run a single test case from intellij

Go to `Run -> Edit Configurations`

* Select `Add New Configuration -> TestNg`
* Select Required Class
* Add env info to  VM options `-Denv=uat` or `-Denv=sit`


##### How to pass data between tests ?
Use `ITestContext` class. add attributes testcontext and reuse in another test.


###  Test data clean up strategy

Goal of Test data clean up : What ever test data created during test executing should be soft deleted and hard delete

##### Soft delete strategy

* Fire Search entity api call
* From search entity api response, Get required attributes for deletion
* Prepare payload
* Fire delete API Call

##### Hard delete strategy
* Write DB queries and execute

#### How to Generate Allure Reports From Windows

* Copy the allure-results folder to target folder
* Open the Command Prompt and Navigate to Project folder:sms_qa_qautomation
* Enter Command: mvn io.qameta.allure:allure-maven:serve


## UI Automation Overview

* Language : Java
* framework : Playwright, Testng
* Official documentation : https://playwright.dev/java/
* Go through below topics 
  * https://playwright.dev/java/docs/locators
  * https://playwright.dev/java/docs/pom

### Framework overview

#### BrowserUtils class

* getPageSession() :  
  * used to launch a browser and get page session
  * We need to pass
* navigateToPage()
  * Navigate to specific url
* fillIn
* clearAndfillIn
* isVisible
* clickOn
* heavyClick
* filterLinkbyName
* verifyNameExistInLocator
* scrollDownUntilLocatorIsVisible
* getLocatorAttribute
* clearTheInputField
* extractText
* getButtonByName
* waitForPageLoading
* hoverOn













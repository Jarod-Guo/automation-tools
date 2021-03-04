/* For create batch REST requests from txt file, features:
 *  1. Create REST request
 *  2. Set Headers
 *  3. Set values for parameters
 *  4. Specify EndPoint
 *  5. Add Assertion
 *  6. Add Custom Properties
 *  Author: Jarod Guo
 *  Date Created: 12 Dec 2020
 */
import com.eviware.soapui.impl.rest.RestResource;
import com.eviware.soapui.impl.wsdl.teststeps.registry.RestRequestStepFactory
import com.eviware.soapui.config.TestStepConfig
import com.eviware.soapui.impl.rest.RestRequest;
import com.eviware.soapui.impl.rest.RestService;
import com.eviware.soapui.support.XmlHolder
import com.eviware.soapui.support.types.StringToStringMap 

def project = testRunner.testCase.testSuite.project// context.getTestCase().getTestSuite().getProject();
def ifaceList = project.getInterfaceList()
def groovyUtils = new com.eviware.soapui.support.GroovyUtils(context)
def directoryName = groovyUtils.projectPath + "/testData/"

//Get records from textFile
//File tickerEnumFile = new File(directoryName+'b2b-api2.txt') //make sure input.txt file already exists and contains different set of values sepearted by new line (CR).
File tickerEnumFile = new File('C:/Users/jarod.guo/Desktop/New Order/Generated TC/B2B-tcFile.csv')
List lines = tickerEnumFile.readLines()

//Loop to create TestCase
testSuite = project.getTestSuiteByName('TestSuite - New Orders') //context.testCase.testSuite.project.getTestSuiteByName('TestSuite 2')
tCaseList = []
for(l in lines){
	tCaseName = l.split(",")[0]
	if(l.split(",")[0] != 'testCase'){
		tCaseList.add(tCaseName)
		tCaseList.unique()
	}
}	

for(t in tCaseList){					//log.info testSuite.getTestCaseList()
	tc = testSuite.addNewTestCase(t)
	tc.addProperty("sessionToken")		//setPropertyValue("sessionToken")  getPropertyValue("sessionToken")
	tc.addProperty("Cookie")	
}

//Loop to create REST Test Steps
for(l in lines){
	domain = l.split(",")[1]
	apiName = l.split(",")[2]
	tStepName = l.split(",")[3]
	paraStr = l.split(",")[4]
	assertStr = l.split(",")[5]

	//Determine to add new TestCase or not
	for(int k; k <testSuite.getTestCaseList().size(); k++){
		if(l.split(",")[0] == testSuite.getTestCaseAt(k).getName()){
			tc = testSuite.getTestCaseByName(testSuite.getTestCaseAt(k).getName())
			}
		}			
	
	for(int i; i < ifaceList.size(); i ++){
		if(domain == ifaceList[i].getName()){
			for(j in ifaceList[i].getAllOperations()){			
				if(apiName == j.getName()){
					restReq = j.getRequestList()[0]

					//Create REST testStep
					TestStepConfig tstepConfig = RestRequestStepFactory.createConfig(restReq, tStepName) //restReq.getName().toString()
					tstep = tc.addTestStep( tstepConfig )

					//Put Headers
					def headers = new StringToStringMap()
					headers.put("_t", "\${#TestCase#sessionToken}");  
					headers.put("Cookie", "\${#TestCase#Cookie}"); 
					tstep.testRequest.setRequestHeaders(headers)

					//set endPoint
					tstep.testRequest.setEndpoint("\${#TestSuite#B2B_QA}")

					//set Assertions
					ValidAssertion = tstep.addAssertion("Valid HTTP Status Codes");
					java.lang.reflect.Field ValidStatusAssertionCodesField = ValidAssertion.getClass().getDeclaredField("codes");
					ValidStatusAssertionCodesField.setAccessible(true);
					ValidStatusAssertionCodesField.set(ValidAssertion, "200")
					for(int k; k < assertStr.count("="); k++){
						assertClass = assertStr.split("&")[k].split(":")[0] //assertStr.split("&")[k].split("=")[0]
						assertName = assertStr.split("&")[k].split(":")[1].split("=")[0]
						assertValue = assertStr.split("&")[k].split(":")[1].split("=")[1]
						Assertion = tstep.addAssertion(assertClass)
						Assertion.setName(assertName)
						Assertion.setToken(assertValue) 
					}
					/*
					//Invalid HTTP Status Codes in an Assertion targeting the named Test Step. Status codes can be added or removed as it suits your needs.
					def InvalidAssertion = tstep.addAssertion("Invalid HTTP Status Codes");
					java.lang.reflect.Field InvalidStatusAssertionCodesField = InvalidAssertion.getClass().getDeclaredField("codes");
					InvalidStatusAssertionCodesField.setAccessible(true);
					InvalidStatusAssertionCodesField.set(InvalidAssertion, "400,401,402,403,404,500")
					*/
					
					//set Parameter Values
					for(int h; h < paraStr.count("="); h++){
						propertyName = paraStr.split("&")[h].split("=")[0]
						propertyValue = paraStr.split("&")[h].split("=")[1]
						tstep.setPropertyValue(propertyName,propertyValue)
					}	
				}
			}
		}
	}
}

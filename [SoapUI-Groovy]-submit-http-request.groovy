//http-builder-jar 
//https://repo1.maven.org/maven2/org/codehaus/groovy/modules/http-builder/http-builder/0.6/http-builder-0.6.jar
import groovyx.net.http.HTTPBuilder

def baseUrl = new URL('https://uggus.qa.xxx.com/mvc/showcases/playSlides?showcaseId=92&startIndex=1&pageSize=10')
def token = testRunner.testCase.getPropertyValue("sessionToken")
def cookie = testRunner.testCase.getPropertyValue("Cookie")

HttpURLConnection connection = (HttpURLConnection) baseUrl.openConnection();
connection.addRequestProperty("_t", token)
connection.addRequestProperty("Cookie", cookie)
connection.addRequestProperty("Accept", "application/json")
connection.with {
 doOutput = true
 requestMethod = 'GET'
 log.info content.text
}

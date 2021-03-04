/* Download har file with Chrome or Firefox developer tools(network)
 * Then extract har file with this script
 *  Author: Jarod Guo
 *  Date Created: 12 Dec 2020
 */
import groovy.json.JsonSlurper 

def slurper = new JsonSlurper()
def fileContent = new File('/har-files/xxx.har').text
def jsonContent = slurper.parseText fileContent
def String hostname = 'xxx.xxx.com'

File file = new File("/api-list-analysis/B2B-api3.csv")
file.write("Page,reqMethod,respStatus,respContSize,respTime,reqName\n")

File tcFile = new File("/xhr-request-steps/B2B-tcFile.csv")
tcFile.write("testCase,domain,apiName,stepName,paraStr,assertStr\n")

for(int i; i<jsonContent['log']['entries'].size();i++){
	resType = jsonContent['log']['entries'][i]['_resourceType']
	reqConn = jsonContent['log']['entries'][i]['connection']
	reqMethod = jsonContent['log']['entries'][i]['request']['method']
	reqName = jsonContent['log']['entries'][i]['request']['url'].replace('https://'+hostname,'')
	reqUrl = jsonContent['log']['entries'][i]['request']['url']
	respStatus = jsonContent['log']['entries'][i]['response']['status']
	respStatusTxt = jsonContent['log']['entries'][i]['response']['statusText']
	respText = jsonContent['log']['entries'][i]['response']['content']['text']
	respContSize = jsonContent['log']['entries'][i]['response']['content']['size']
	respTime = jsonContent['log']['entries'][i]['time']
	respTimeBlocked = jsonContent['log']['entries'][i]['timings']['blocked']
	respTimeDns = jsonContent['log']['entries'][i]['timings']['dns']
	respTimeSsl = jsonContent['log']['entries'][i]['timings']['ssl']
	respTimeConnect = jsonContent['log']['entries'][i]['timings']['connect']
	respTimeSend = jsonContent['log']['entries'][i]['timings']['send']
	respTimeWait = jsonContent['log']['entries'][i]['timings']['wait']
	respTimeReceived = jsonContent['log']['entries'][i]['timings']['receive']

	if(resType == 'xhr' && reqUrl.split('/')[2] == hostname){		
		req = i+','+reqMethod+', '+respStatus+', '+respContSize+', '+respTime +','+reqName+'\n'			
		file.append(req)

		if(reqName.split('/')[3].split('\\?').size()>1){
			tcReq = ',B2B - REST-'+reqName.split('/')[2]+','+reqName.split('/')[3].split('\\?')[0]+',,'+reqName.split('/')[3].split('\\?')[1].replace('%3A',':')+',\n' //.replace('%2C','\\,')
		}else{
			tcReq = ',B2B-'+reqName.split('/')[2]+','+reqName.split('/')[3].split('\\?')[0]+',,'+'0,\n'
		}
		tcFile.append(tcReq)
	}
}

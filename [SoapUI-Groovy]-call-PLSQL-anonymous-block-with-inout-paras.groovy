/* call PLSQL anonymous block with groovy in SoapUI
*    Author: Jarod Guo
*    Date: 1 Mar 21 
*/
import groovy.sql.Sql  

// initialize variables 
int returnReqId = 0 
int orgId = testRunner.testCase.testSuite.getPropertyValue("Org_Id").toInteger() 
int orderSourceId = testRunner.testCase.getPropertyValue("Order_Source_Id").toInteger() 
String b2bOrderNum = testRunner.testCase.getPropertyValue("B2B_Order_Num")   

// create db connection 
def sql = Sql.newInstance('jdbc:oracle:thin:@gl-exadata-scan.xxx.com:1521/EBSQA1','apps','xxx','oracle.jdbc.OracleDriver')  

// run request 
sql.call("""             
	declare                 
		v_request_id  number;             
	Begin                                  
		Apps.Fnd_Request.Set_Org_Id($orgId);                  
		-- Setting the context ----                 
		Apps.Fnd_Global.Apps_Initialize(User_Id         => 21147, --Apps.Fnd_Global.User_Id,    'JAROD.GUO'                                 
								  Resp_Id         => 50744, --Apps.Fnd_Global.Resp_Id,    'xxx Order Management User - US'                                 
								  Resp_Appl_Id    => 660    --Apps.Fnd_Global.Resp_Appl_Id    'Order Management'
								  );  			      
		
		Dbms_Output.Put_Line('Submit <Order Import> Concurrent Program'); 			 			      
		
		v_request_id := Apps.Fnd_Request.Submit_Request ( 			                       
									Application   => 'ONT' 			                      
									,Program       => 'OEOIMP' 			                      
									,Description   => '' 			                      
									,Start_Time    => Sysdate 			                      
									,Sub_Request   => Null 			                      
									,Argument1     => $orgId    		--Operating Unit: Deckers US OU 			                      
									,Argument2     => $orderSourceId         --Order Source: Wholesale 			                      
									,Argument3     => $b2bOrderNum         			                      
									,Argument4     => 'INSERT'        --Operation Code 			                      
									,Argument5     => 'N'         --Validate Only 			                      
									,Argument6     => 1           --Debug Level 			                      
									,Argument7     => 4           --Instance: 4 			                      
									,Argument8     => Null        --Sold To Org ID 			                      
									,Argument9     => Null        --Sold To Org Name 			                      
									,Argument10    => Null        --Change Sequence 			                      
									,Argument11    => 'Y'         --Perf Parameter 			                      
									,Argument12    => 'N'         --Trim Trailing Blanks 			                      
									,Argument13    => 'Y'         --Process Order with Null Org 			                      
									,Argument14    => $orgId    		--Default Org Id 			                      
									,Argument15    => 'Y'         --Validate DFF 			                     
									);                 
			Dbms_Output.Put_Line('Request Id: '||v_request_id);                 
		Commit;                 
			${Sql.NUMERIC} := v_request_id;             
	End;         
	""") {v_request_id ->             
	returnReqId = v_request_id             
	log.info "${v_request_id}"             
	}  

return returnReqId

sql.close()

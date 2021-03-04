# Need to get JIRA Cloud token firstly
# Then config credentials in config.ini file.
#   Author: Jarod Guo
#   Date: 20 Dec 20
# 

import requests,os
from pandas import DataFrame
import pandas as pd
import configparser



currDir = os.path.dirname(os.path.realpath(__file__)):

outputDir = currDir.replace("\\","/") + "/2. exportReport/"
fileName = "Sprint Tasks.xlsx"

sprintTask = "project = B2B AND Sprint in (136)  ORDER BY created DESC"


def get_jira_list(JQL_string):
    
    config = configparser.ConfigParser()
    config.read('config.ini')    
    serverURL = config['credentials']['server']
    user_name = config['credentials']['username']
    password =  config['credentials']['token']

    r = requests.get(serverURL+"/rest/api/2/search?jql="+JQL_string+"&maxResults=100&fields=*all", auth=(user_name, password))
    issue_list = r.json()['issues']
    
    return issue_list


def get_sprint_tasks(JQL_string):

    issue_list = get_jira_list(JQL_string)

    key = []
    summary=[]
    type=[]
    module=[]
    sprint=[]
    release=[]
    assignee=[]
    storyPoint=[]
    priority=[]
    status=[]
    assignedQA=[]
    sdlcStatus=[]
    
    for i in range(len(issue_list)):
        
        #fields definition
        key.append(issue_list[i]['key'])
        summary.append(issue_list[i]['fields']['summary'])
        type.append(issue_list[i]['fields']['issuetype']['name'])
        
        if len(issue_list[i]['fields']['components']) == 0:
            module.append("")
        else:
            module.append(issue_list[i]['fields']['components'][0]['name'])

        if issue_list[i]['fields']['customfield_10020']  is None:
            sprint.append("")
        else:
            sprint.append(issue_list[i]['fields']['customfield_10020'][0]['name'])   

        if len(issue_list[i]['fields']['fixVersions']) == 0:
            release.append("")
        else:
            release.append(issue_list[i]['fields']['fixVersions'][0]['name'])
               
        if issue_list[i]['fields']['assignee'] is None:
            assignee.append("")
        else:
            assignee.append(issue_list[i]['fields']['assignee']['displayName'])
            
        storyPoint.append(issue_list[i]['fields']['customfield_10024'])
        priority.append(issue_list[i]['fields']['priority']['name'])
        status.append(issue_list[i]['fields']['status']['name'])
        
        if issue_list[i]['fields']['customfield_10035'] is None:
            assignedQA.append("")
        else:
            assignedQA.append(issue_list[i]['fields']['customfield_10035']['displayName'])

        sdlcStatus.append(issue_list[i]['fields']['status']['statusCategory']['name'])

    issue_data = {'Issue Key':key
                  ,'Summary':summary
                  ,'Issue Type':type
                  ,'Module':module
                  ,'Sprint':sprint                  
                  ,'Release':release
                  ,'Assignee':assignee
                  ,'storyPoint':storyPoint
                  ,'Priority':priority
                  ,'Status':status
                  ,'assignedQA':assignedQA
                  ,'statusCate':sdlcStatus
                  }
    return issue_data


def generate_report(Task, Dir, FileName):

    task_list = get_sprint_tasks(Task)   
    df1=DataFrame(task_list)
    
    #Write to excel file
    with pd.ExcelWriter(Dir+FileName) as writer:
        df1.to_excel(writer, sheet_name='storyList',index_label=None,index=False)

if __name__ == "__main__":
    generate_report(sprintTask, outputDir, fileName)

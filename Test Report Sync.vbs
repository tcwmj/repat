'==========================================================================
'
' VBScript Source File 
' Copyright 2007, www.51Testing.com
'
' NAME: StartupScript.vbs
'
' AUTHOR: Sincky.Zhang
' DATE  : 2007-11-8
' Blog  : http://sinckyzhang.blog.sohu.com
' COMMENT: Version 1.0
'
'==========================================================================
Option Explicit

Const ADO_FWDONLY = 0, ADO_KEYSET = 1, ADO_DYNAMIC = 2, ADO_STATIC = 3
Const SW_NORMAL = 1, LOG_STATUS_ERROR = 1, LOG_STATUS_WARN = 2, LOG_STATUS_INFO = 3, LOG_STATUS_DEBUG = 4

Dim GnLogLevel, GsWorkPath, GsConfigPath, GsConfigFile, GsTestSuites, GsLogFile
Dim oTestReport, qcApp

Call StartSync()	'Start the test report syncronization

'==========================================================================
' Name: StartSync
' Summary: start the whole test
' Parameters: 
'	None
' Return: 
'	None
' Author: Kenny Wong
'==========================================================================
Function StartSync()
	GnLogLevel = LOG_STATUS_WARN
	GsWorkPath = GetWorkPath()
	GsConfigPath = GsWorkPath & "\config"
	GsConfigFile = GsConfigPath & "\Config.xls"
	Dim oConfig : Set oConfig = GetConfig(GsConfigFile, "QCSetting")
	GsTestSuites = GsWorkPath  & "\" & oConfig.Item("ResultFile")
	GsLogFile = GsWorkPath & "\target\Test Report Sync.log"
	
	Dim oTestCaseDict
	Call PrintLog(Now, LOG_STATUS_INFO, "############################Test Report Syncronization started############################")
	If LoadSettings(GsConfigFile) Then
		Set oTestCaseDict = GetTestCaseDict()
		
		If qcApp.Connect Then
			If qcApp.FindTestSet Then Call qcApp.SyncTestReport(oTestCaseDict)
			qcApp.DisConnect
		End If
		
		Set oTestCaseDict = Nothing
		Set qcApp = Nothing
		Set oTestReport = Nothing
	End If
	
	Call PrintLog(Now, LOG_STATUS_INFO, "############################Test Report Syncronization finished############################")
	Msgbox "Test Report Syncronization was finished at " & Now
End Function

'==========================================================================
' Name: GetTestCaseDict
' Summary: get test case id key and status item pair into dictionary object
' Parameters: 
'	None
' Return: 
'	Dictionary object
' Author: Kenny Wong
'==========================================================================
Function GetTestCaseDict()
	Call PrintLog(Now, LOG_STATUS_INFO, "GetTestCaseDict()")
	Dim aCase, i, oTestCaseDict
	Set oTestCaseDict = CreateObject("Scripting.Dictionary")
	aCase = oTestReport.GetCaseList()
	For i = 0 To UBound(aCase)
		If IsEmpty(aCase(i, 0)) Then
			Call PrintLog(Now, LOG_STATUS_WARN, "Found a empty test case id")
		Else
			If IsNumeric(aCase(i, 0)) Then
				If oTestCaseDict.Exists(aCase(i, 0)) Then
					Call PrintLog(Now, LOG_STATUS_WARN, "Found a duplicated test case id " & aCase(i, 0))
				Else
					oTestCaseDict.Add aCase(i, 0), aCase(i, 1)
				End If
			Else
				Call PrintLog(Now, LOG_STATUS_WARN, "Found a non-numeric test case id " & aCase(i, 0))
			End If
		End If
	Next
	Set GetTestCaseDict = oTestCaseDict
End Function

Class clsQCControl
	Private oConnection, oTestSet
	Private sServer, sAccount, sPassword, sDomain, sProject, sTestSetPath, sTestSetName, sRCVersion
	
	Private Sub Class_Initialize()
	End Sub
	
	Private Sub Class_Terminate()
	End Sub
	
	'==========================================================================
	' Name: InitVariable
	' Summary: initialize the variables after create object immediately
	' Parameters: 
	'	Server, Account, Password, Domain, Project, TestSetPath, TestSetName, RCVersion
	' Return: None
	'==========================================================================
	Public Default Function InitVariable(Server, Account, Password, Domain, Project, TestSetPath, TestSetName, RCVersion)
		sServer = Server
		sAccount = Account
		sPassword = Password
		sDomain = Domain
		sProject = Project
		sTestSetPath = TestSetPath
		sTestSetName = TestSetName
		sRCVersion = RCVersion
		Set InitVariable = Me
	End Function
	
	'==========================================================================
	' Name: Connect
	' Summary: Do Connection to QC server
	' Parameters: None
	' Return:
	'	Boolean: indicate connected or disconnected
	' Comment:
	'==========================================================================
	Function Connect()
		Call PrintLog(Now, LOG_STATUS_INFO, "QCControl.Connect")
		Dim bReturn : bReturn = False
		Set oConnection = CreateObject("TDApiOle80.TDConnection")
		oConnection.InitConnectionEx sServer
		oConnection.login sAccount, sPassword
		oConnection.Connect sDomain, sProject
		If oConnection.Connected Then
			bReturn = True
			Call PrintLog(Now, LOG_STATUS_DEBUG, "Connect to QC successfully")
		Else
			Call PrintLog(Now, LOG_STATUS_ERROR, "Connect to QC failed")
		End If
		Connect = bReturn
	End Function
	
	'==========================================================================
	' Name: Disconnect
	' Summary: Disconnect from QC
	' Parameters: None
	' Return: None
	' Comment:
	'==========================================================================
	Function Disconnect()
		Call PrintLog(Now, LOG_STATUS_INFO, "QCControl.Disconnect")
		If oConnection.Connected Then oConnection.DisconnectProject 		'Disconnect from the project
		If oConnection.LoggedIn Then oConnection.Logout		'Log off the server
		oConnection.ReleaseConnection	'Release the TDConnection object.
		Set oConnection = Nothing
		Call PrintLog(Now, LOG_STATUS_DEBUG, "Disconnect from QC")
	End Function

	'==========================================================================
	' Name: SyncTestReport
	' Summary: Sync Test Report to QC
	' Parameters: 
	'	oTestCaseReport
	' Return: none
	' Comment:
	'==========================================================================  
	Function SyncTestReport(oTestCaseReport)
		Call PrintLog(Now, LOG_STATUS_INFO, "QCControl.SyncTestReport(oTestCaseReport)")
		Dim oTestSetFilter, oTestCase
		Set oTestSetFilter = oTestSet.TSTestFactory.Filter
		Set oTestCase = oTestSetFilter.NewList()
		
		Dim j, sTestCaseId, oTestRunObj
		Dim dtCurrentDate, sNewRunName, oRunobj, oRunStepFactory, oStepList, nCount, oStep
		For j = 1 To oTestCase.Count
			sTestCaseId = Trim(oTestCase(j).Field("TC_TEST_ID"))
			If oTestCaseReport.Exists(sTestCaseId) Then
				Call PrintLog(Now, LOG_STATUS_DEBUG, "Test Case " & sTestCaseId & " was mapped between QC and test report")
'				oTestCase(j).Field("TC_ACTUAL_TESTER") = "QTP"
'				oTestCase(j).Post
				If UCase(oTestCaseReport(sTestCaseId)) = UCase("Warning") Then oTestCaseReport(sTestCaseId) = "Failed"
				If oTestCase(j).Status <> oTestCaseReport(sTestCaseId) Then
					set oTestRunObj = oTestCase(j).RunFactory
					dtCurrentDate = Now()
					sNewRunName = Month(dtCurrentDate) & "-" & Day(dtCurrentDate) & "_" & Hour(dtCurrentDate) & "-" & Minute(dtCurrentDate) & "-" & Second(dtCurrentDate)
					sNewRunName = "Run_" & SNewRunName
					Set oRunobj = oTestRunObj.AddItem(sNewRunName)
					oRunobj.Name = sNewRunName
					oRunobj.Status = oTestCaseReport(sTestCaseId)
					oRunobj.CopyDesignSteps
					oRunobj.Field("RN_TESTER_NAME") = "QTP"
					oRunobj.Field("RN_USER_01") = sRCVersion
					'oRunobj.Field("RN_EXECUTION_DATE") = Trim(Split(strEndTime, "-")(0))
					'oRunobj.Field("RN_EXECUTION_TIME") = Trim(Split(strEndTime, "-")(1))
					oRunobj.Post
					
					'Set up the steps of the run
					Set oRunStepFactory = oRunobj.StepFactory
					Set oStepList = oRunStepFactory.NewList("")
					nCount = 1
					For Each oStep In oStepList
						oStep.Status = "Passed"
						'oStep.Field("ST_EXECUTION_DATE") = Trim(Split(strEndTime, "-")(0))
						'oStep.Field("ST_EXECUTION_TIME") = Trim(Split(strEndTime, "-")(1))
						'If the status of this run is "Failed", set the status of last step "Failed"
						If nCount = oStepList.Count And oTestCaseReport(sTestCaseId) = "Failed" Then oStep.Status = "Failed"
						nCount = nCount + 1
						oStep.Post
					Next
					
					Set oStepList = Nothing
					Set oRunStepFactory = Nothing
					set oTestRunObj = Nothing
					
					Call PrintLog(Now, LOG_STATUS_DEBUG, "Test Case " & sTestCaseId & " was updated successfully")
				End If
				oTestCaseReport.Remove sTestCaseId
			Else
				Call PrintLog(Now, LOG_STATUS_WARN, "Test Case " & sTestCaseId & " was not found in test report")
			End If
		Next
		
		Set oTestCase = Nothing
		Set oTestSetFilter = Nothing
		
		For Each sTestCaseId In oTestCaseReport
			Call PrintLog(Now, LOG_STATUS_WARN, "Test Case " & sTestCaseId & " > " & oTestCaseReport(sTestCaseId) & " was not found in QC")
		Next
	End Function
	
	'==========================================================================
	' Name: FindTestSet
	' Summary: Find Test Set by parent path and name
	' Parameters: None
	' Return: None
	'==========================================================================
	Function FindTestSet()
		Call PrintLog(Now, LOG_STATUS_INFO, "oQCControl.FindTestSet")
		Dim tsTreeMgr, tsFolder, tsList
		Dim bReturn : bReturn = False
		Set tsTreeMgr = oConnection.TestSetTreeManager
		Set tsFolder = tsTreeMgr.NodeByPath(sTestSetPath)
		Set tsList = tsFolder.FindTestSets(sTestSetName)
		If tsList.Count < 1 Then
			Call PrintLog(Now, LOG_STATUS_WARN, "Test Set [" & sTestSetName & "] was not found")
		Else
			bReturn = True
			Set oTestSet = tsList.Item(1)
		End If
		FindTestSet = bReturn
	End Function
End Class

Class clsTestReport
	Private Sub Class_Initialize()
	End Sub
	
	Private Sub Class_Terminate()
	End Sub
	
	'==========================================================================
	' Name: GetCaseList
	' Summary: Gets test case from sModule sheet in suite file
	' Parameters: None
	' Return: 
	'   a string which contains test cases of sModule needed to run
	'==========================================================================
	Function GetCaseList()
		Call PrintLog(Now, LOG_STATUS_INFO, "oTestReport.GetCaseList()")
		Dim aTemp, ssql
		ssql = "Select TestCaseID, Status from [Sheet1$] where Status in ('Passed', 'Failed', 'Warning')"
		aTemp = GetCaseItems(GsTestSuites, ssql)
		GetCaseList = aTemp
	End Function
	
	'==========================================================================
	' Name: GetConfigItems
	' Summary: Gets the items from suite file
	' Parameters:
	'	sFilePath: suite file's path
	'	sSQL: query sentence, such as "select * from [Sheet1$]"
	' Return: 
	'   an array which contains items queried from suite file
	'==========================================================================
	Function GetConfigItems(sFilePath, sSQL)
		Call PrintLog(Now, LOG_STATUS_DEBUG, "oTestReport.GetConfigItems(" & sFilePath & "," & sSQL & ")")	
		Dim oConnection, oRecordSet, i
		Set oConnection = CreateObject("ADODB.Connection")
		oConnection.Open "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & sFilePath & ";Extended Properties=Excel 8.0;Persist Security Info=False;Jet OLEDB"
		Set oRecordSet = CreateObject("ADODB.RecordSet")
		oRecordSet.Open sSQL, oConnection, ADO_KEYSET
		ReDim aReturn(oRecordSet.RecordCount - 1)	' Goes through data
		For i = 0 to oRecordSet.RecordCount - 1
			If Not IsNull(oRecordSet(0)) Then
				aReturn(i) = cstr(oRecordSet(0))
			End If
			oRecordSet.MoveNext
		Next
		oRecordSet.Close	' Closes database connections
		oConnection.Close
		Set oRecordSet = Nothing
		Set oConnection = Nothing
		GetConfigItems = aReturn
	End Function
	
	'==========================================================================
	' Name: GetCaseItems
	' Summary: Gets the case items from suite file
	' Parameters:
	'	sFilePath: suite file's path
	'	sSQL: query sentence, such as "select * from [Sheet1$]"
	' Return: 
	'   an array which contains items queried from suite file
	'==========================================================================
	Function GetCaseItems(sFilePath, sSQL)
		Call PrintLog(Now, LOG_STATUS_DEBUG, "oTestReport.GetCaseItems(" & sFilePath & "," & sSQL & ")")	
		Dim oConnection, oRecordSet, i
		Set oConnection = CreateObject("ADODB.Connection")
		oConnection.Open "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & sFilePath & ";Extended Properties=Excel 8.0;Persist Security Info=False;Jet OLEDB"
		Set oRecordSet = CreateObject("ADODB.RecordSet")
		oRecordSet.Open sSQL, oConnection, ADO_KEYSET
		ReDim aReturn(oRecordSet.RecordCount - 1, 1)	' Goes through data
		For i = 0 to oRecordSet.RecordCount - 1
			aReturn(i, 0) = GetCaseId(oRecordSet(0))
			aReturn(i, 1) = Trim(oRecordSet(1))
			oRecordSet.MoveNext
		Next
		oRecordSet.Close	' Closes database connections
		oConnection.Close
		Set oRecordSet = Nothing
		Set oConnection = Nothing
		GetCaseItems = aReturn
	End Function
	
	'==========================================================================
	' Name: GetCaseId
	' Summary: Get test case id from case id string
	' Parameters:
	'	str: test case id string
	' Return: 
	'   String: test case id
	'==========================================================================
	Function GetCaseId(str)
		Dim bReturn, Char
		Char = Left(Trim(str), 1)
		If IsNumeric(Char) Then GetCaseId = GetCaseId & Char & GetCaseId(Right(str, Len(str) - 1))
	End Function
End Class

'==========================================================================
' Name: WriteLogFile
' Summary: wirte log file, such as ".\TestResult\Log\log.txt"
' Parameters: 
'	sLine: string
' Return: none
'==========================================================================
Function WriteLogFile(sFile, sLine)
	Dim fso, oFile
	On Error Resume Next
		Set fso = CreateObject("Scripting.FileSystemObject")
		Set oFile = fso.OpenTextFile(sFile, 8, true)	'for append
		oFile.WriteLine sLine
		oFile.Close
		Set oFile = Nothing
		Set fso = Nothing
	On Error Goto 0
End Function

'==========================================================================
' Name: PrintLog
' Summary: writes log to file
' Parameters: 
'	sContent: log message
' Return: none
'==========================================================================
Function PrintLog(dtDate, nLogLevel, sContent)
	If nLogLevel <= CInt(GnLogLevel) Then
		Dim sLogLevel
		Select Case nLogLevel
			Case LOG_STATUS_DEBUG
				sLogLevel = "DEBUG"
			Case LOG_STATUS_WARN
				sLogLevel = "WARNING"
			Case LOG_STATUS_ERROR
				sLogLevel = "ERROR"
			Case LOG_STATUS_INFO
				sLogLevel = "INFO"
		End Select
		
		Dim sLog
		sLog = dtDate
		sLog = sLog & Space(1) & sLogLevel
		sLog = sLog & Space(1) & sContent
		Call WriteLogFile(GsLogFile, sLog)
	End If
End Function

'==========================================================================
' Name: IsFileExist
' Summary: Is File Exists
' Parameters: 
'	sFile, file absolute path
' Return: 
'	Boolean
' Author: Kenny Wong
'==========================================================================
Function IsFileExist(sFile)
	Dim fso
	Set fso = CreateObject("Scripting.FileSystemObject")
	IsFileExist = fso.FileExists(sFile)
	Set fso = Nothing
End Function

'==========================================================================
' Name: GetWorkPath
' Summary: Gets current path
' Parameters: none
' Return: Current path, such as "d:\temp"
'==========================================================================
Function GetWorkPath()
	GetWorkPath = Left(wscript.scriptfullname, InStrRev(wscript.ScriptFullName, "\") - 1)
End Function

'==========================================================================
' Name: LoadSettings
' Summary: Gets config from config file
' Parameters: 
'	sConfigFile: config file's path, such as "d:\temp\config.xls"
' Return: none
'==========================================================================
Function LoadSettings(sConfigFile)
	Call PrintLog(Now, LOG_STATUS_INFO, "LoadSettings(" & sConfigFile & ")")
	Dim bReturn : bReturn = True
	
	Dim Server, Account, Password, Domain, Project, TestSetPath, TestSetName, RCVersion
	If IsFileExist(sConfigFile) Then
		Dim oConfig : Set oConfig = GetConfig(sConfigFile, "QCSetting")'Gets config infomation from QCSetting sheet
		Server = oConfig.Item("QCServer")
		Account = oConfig.Item("QCUser")
		Password = oConfig.Item("QCPassword")
		Domain = oConfig.Item("Domain")
		Project = oConfig.Item("Project")
		TestSetPath = oConfig.Item("TestSetPath")
		TestSetName = oConfig.Item("TestSetName")
		RCVersion = oConfig.Item("RunRCVersion")
		
		Call PrintLog(Now, LOG_STATUS_DEBUG, "Initializing qcApp object")
		Set qcApp = (New clsQCControl)(Server, Account, Password, Domain, Project, TestSetPath, TestSetName, RCVersion)
		
		Call PrintLog(Now, LOG_STATUS_DEBUG, "Initializing oTestReport object")
		Set oTestReport = New clsTestReport
	Else
		bReturn = False
		Call PrintLog(Now, LOG_STATUS_WARN, "config file was not found")
	End If
	
	LoadSettings = bReturn
End Function

'==========================================================================
' Name: GetConfig
' Summary: Gets config information from specified sheet of config file
' Parameters: 
' 	sConfigFile: config file's path, such as "d:\temp\config.xls"
'	sSheet: the sheet name, such as "Sheet1"
' Return: 
'	A Dictionary which contains "item", "value"
'==========================================================================
Function GetConfig(sConfigFile, sSheet)
	Call PrintLog(Now, LOG_STATUS_INFO, "GetConfig(" & sConfigFile & "," & sSheet & ")")
	Dim oDictionary, oConnection
	Set oDictionary = CreateObject("Scripting.Dictionary")
	Set oConnection = CreateObject("ADODB.Connection")
	oConnection.Open "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & sConfigFile & ";Extended Properties=Excel 8.0;Persist Security Info=False;Jet OLEDB"
	
	Dim oRecordSet, sSQL
	Set oRecordSet = CreateObject("ADODB.RecordSet")
	sSQL = "Select Item, Value from [" & sSheet & "$]"
	oRecordSet.Open sSQL, oConnection, ADO_FWDONLY
	Do Until oRecordSet.EOF		'Goes through data
		If Not IsNull(oRecordSet("Item")) Then
			If IsNull(oRecordSet("Value")) Then
				oDictionary.add CStr(oRecordSet("Item")), ""
			Else
				oDictionary.add CStr(oRecordSet("Item")), CStr(oRecordSet("Value"))
			End If
		End If
		oRecordSet.MoveNext
	Loop
	
	Set GetConfig = oDictionary	' Returns
	Set oDictionary = Nothing
	oRecordSet.Close		'Closes database connections
	oConnection.Close
	Set oRecordSet = Nothing
	Set oConnection = Nothing
End Function
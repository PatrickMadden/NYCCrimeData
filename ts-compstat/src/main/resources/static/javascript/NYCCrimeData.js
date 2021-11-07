var projectID="NYCCrimeData";
var modelID="NYCCrimeDataModel";
var moduleName="NYCCrimeData";

var sevenMajorFelonyOffensesChartViewName = "SevenMajorFelonyOffensesChart";
var sevenMajorFelonyOffensesChartViewID = sevenMajorFelonyOffensesChartViewName + "ID";

var sevenMajorFelonyOffensesTimeLineViewName = "SevenMajorFelonyOffensesTimeLine";
var sevenMajorFelonyOffensesTimeLineViewID = sevenMajorFelonyOffensesTimeLineViewName + "ID";

var sevenMajorFelonyOffensesTableViewName = "SevenMajorFelonyOffensesTable";
var sevenMajorFelonyOffensesTableViewID = sevenMajorFelonyOffensesTableViewName + "ID";

var defaultIntegratorName = "" + "Dummy Integrator";
var defaultIntegratorID = defaultIntegratorName + "ID";

var onViewsLoadedCommand =
    {
        "command":"Custom",
        "onsuccess":"onLoadCompstatModelCommandSuccess",
        "onfailure":"onPerspectivesCommandFailure",
        "data":
            {
                "project": projectID,
                "module": moduleName,
                "modelID": modelID,
                "viewID": sevenMajorFelonyOffensesChartViewID,
                "viewName": sevenMajorFelonyOffensesChartViewName,
                "serverClassName": "com.tomsawyer.nypd.compstat.commands.LoadCompstatModelCommand",
                "args": []
            }
    };


/**
 * Called by the Tom Sawyer Perspectives framework when the CompStat model has been loaded.
 * @param successfulCommand
 * @param commandResult
 */
function onLoadCompstatModelCommandSuccess(successfulCommand, commandResult)
{
    onPerspectivesCommandSuccess(successfulCommand,  commandResult);
    refreshApplicationViews(
        successfulCommand.data.project,
        successfulCommand.data.module,
        successfulCommand.data.modelID);
}




//	Initialize the web view based on a TSP project
//	Define an array of commands using JSON and tell TSP to invoke
var initialWebProjectCommand =
    {
        "command":"WebProject",
        "onload":"onPerspectivesProjectLoad",
        "onready":"onPerspectivesProjectReady",
        "onfailure":"onPerspectivesCommandFailure",
        "onsuccess":"onPerspectivesCommandSuccess",
        "data":
            [
                {"command":"LoadProject","data":{"project":projectID,"filename":"project/NYCCrimeData.10.tsp"}},
                {"command":"NewDefaultModel","data":{"project":projectID,"module":moduleName,"modelID":modelID}},
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsChartView","viewName":sevenMajorFelonyOffensesChartViewName,"viewID":sevenMajorFelonyOffensesChartViewID}},
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsTimeLineView","viewName":sevenMajorFelonyOffensesTimeLineViewName,"viewID":sevenMajorFelonyOffensesTimeLineViewID}},
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsTableView","viewName":sevenMajorFelonyOffensesTableViewName,"viewID":sevenMajorFelonyOffensesTableViewID}},
                {"command":"NewTabPanel","data":{"project":projectID,"module":moduleName,"modelID":modelID,"id":"tsMainContentDivID","widgetIDs":[sevenMajorFelonyOffensesChartViewID,sevenMajorFelonyOffensesTimeLineViewID,sevenMajorFelonyOffensesTableViewID]}}
            ]
    };

function onPerspectivesProjectReady(projectID)
{
    console.log("All project RPC calls are complete and you can now access all project resources via the DOM. " + projectID);

    // since everything is ready - lets just update for the user.
    invokePerspectivesCommand(onViewsLoadedCommand);
}


function onPerspectivesMouseClickOnObject(toolEventJsData)
{
    if (toolEventJsData != null)
    {
        console.log("Mouse clicked on object with ID=" + toolEventJsData.objectID +
            " at mouseX=" + toolEventJsData.mouseX +
            " at mouseY=" + toolEventJsData.mouseY +
            ". ViewID=" + toolEventJsData.viewID +
            " ModelID=" + toolEventJsData.modelID);

        // if (toolEventJsData.objectID.toString().startsWith("N node.ResultSet"))
        // {
        //     let resultSetId = toolEventJsData.objectID.toString().substr(2);
        //
        //     // alert("You clicked on a ResultSet node with ID= " +
        //     //     resultSetId);
        //
        //     window.parent.loadResultSetModal(
        //         toolEventJsData.project,
        //         toolEventJsData.module,
        //         toolEventJsData.modelID,
        //         toolEventJsData.viewID,
        //         resultSetId);
        // }
    }
}


/**
 * Called by the framework to refesh application views.
 * @param projectID
 * @param moduleName
 * @param modelID
 */
function refreshApplicationViews(projectID, moduleName, modelID)
{
    let viewIDs = [
        sevenMajorFelonyOffensesChartViewID,
        sevenMajorFelonyOffensesTimeLineViewID,
        sevenMajorFelonyOffensesTableViewID
    ];

    refreshApplicationViewsWithIds(projectID, moduleName, modelID, viewIDs);
}

function refreshApplicationViewsWithIds(projectId, moduleName, modelID, viewIDs)
{
    let refreshApplicationViewsCommand =
        {
            command: "RefreshViews",
            data:
                {
                    "project": projectId,
                    "module": moduleName,
                    "modelID": modelID,
                    "viewIDs": viewIDs,
                    "viewNames": []
                },
            // there is a bug in TSP where it tries to cast the command rather than the
            // data. By putting the attributes here we can bypass it.
            project: projectId,
            module: moduleName,
            modelID: modelID,
            viewIDs: viewIDs
        };

    let refreshApplicationViewsGroupCommand =
        {
            command: "Group",
            onsuccess: "onPerspectivesCommandSuccess",
            onfailure: "onPerspectivesCommandFailure",
            data: [refreshApplicationViewsCommand]
        };

    console.log("About to call:\n" + JSON.stringify(refreshApplicationViewsGroupCommand));
    invokePerspectivesCommand(refreshApplicationViewsGroupCommand);
}


/**
 * Called by the framework when the project was fully loaded.
 * @param projectID
 */
function onPerspectivesProjectLoad(projectID)
{
    console.log("All project UI elements should now be on the DOM for " + projectID);
}


/**
 Called by the Perspectives Framework when licensing has been validated
 and the invokePerspectivesCommand has been published to the DOM
 */
function onPerspectivesLicensedAndReady()
{
    invokePerspectivesCommand(initialWebProjectCommand);
}


/**
 * Called by the framework when there is a command failure.
 * @param failedCommand
 * @param message
 * @param callstack
 */
function onPerspectivesCommandSuccess(successfulCommand, commandResult)
{
    console.log("Perspectives command: " +
        successfulCommand.command +
        " completed successfully with a result of " + commandResult);
}


/**
 * Called by the framework when there is a command failure.
 * @param failedCommand
 * @param message
 * @param callstack
 */
function onPerspectivesCommandFailure(failedCommand, message, callstack)
{
    console.log("Perspectives command " + failedCommand.commmand +
        " failed. Reason is: " + message);
    console.log(callstack);
}

var projectID="NYCCrimeData";
var modelID="NYPDShootingInidendts";
var moduleName="NYCCrimeData";

var shootingsTreeViewName = "ShootingsTreeView";
var shootingsGoogleMapViewName = "ShootingsGoogleMapView";
var shootingsDrawingViewName = "ShootingsDrawingView";
var shootingsTimeLineName = "ShootingsTimeLine";
var inspectorViewName = "InspectorView";

var perpGenderChartViewName = "PerpGenderChart";
var perpRaceChartViewName = "PerpRaceChart";
var perpAgeGroupChartViewName = "PerpAgeGroupChart";

var shootingsTreeViewID = shootingsTreeViewName + "ID";
var shootingsGoogleMapID = shootingsGoogleMapViewName + "ID";
var shootingsDrawingID = shootingsDrawingViewName + "ID";
var shootingsTimeLineID = shootingsTimeLineName + "ID";
var inspectorViewID = inspectorViewName + "ID";

var perpGenderChartViewID = perpGenderChartViewName + "ID";
var perpRaceChartViewID = perpRaceChartViewName + "ID";
var perpAgeGroupChartViewID = perpAgeGroupChartViewName + "ID";

var defaultIntegratorName = "" + "Dummy Integrator";
var defaultIntegratorID = defaultIntegratorName + "ID";

var onViewsLoadedCommand =
    {
        "command":"Custom",
        "onsuccess":"onLoadShootingsModelCommandSuccess",
        "onfailure":"onPerspectivesCommandFailure",
        "data":
            {
                "project": projectID,
                "module": moduleName,
                "modelID": modelID,
                "viewID": shootingsDrawingID,
                "viewName": shootingsDrawingViewName,
                "serverClassName": "com.tomsawyer.nypd.compstat.commands.LoadIncidentModelCommand",
                "args": []
            }
    };


/**
 * Called by the Tom Sawyer Perspectives framework when the CompStat model has been loaded.
 * @param successfulCommand
 * @param commandResult
 */
function onLoadShootingsModelCommandSuccess(successfulCommand, commandResult)
{
    onPerspectivesCommandSuccess(successfulCommand,  commandResult);
    refreshIncidentViews(
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
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsTreeView","viewName":shootingsTreeViewName,"viewID":shootingsTreeViewID}},
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsTimeLineView","viewName":shootingsTimeLineName,"viewID":shootingsTimeLineID}},
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsGoogleMapView","viewName":shootingsGoogleMapViewName,"viewID":shootingsGoogleMapID}},
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsDrawingView","viewName":shootingsDrawingViewName,"viewID":shootingsDrawingID}},
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsInspectorView","viewName":inspectorViewName,"viewID":inspectorViewID}},
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsChartView","viewName":perpGenderChartViewName,"viewID":perpGenderChartViewID}},
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsChartView","viewName":perpRaceChartViewName,"viewID":perpRaceChartViewID}},
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsChartView","viewName":perpAgeGroupChartViewName,"viewID":perpAgeGroupChartViewID}},
                {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsTableView","viewName":"PerpetratorTable","viewID":"PerpetratorTableID"}},
                {"command":"NewTabPanel","data":{"project":projectID,"module":moduleName,"modelID":modelID,"id":"tsTopLeftContentTabID","widgetIDs":[shootingsTreeViewID]}},
                {"command":"NewTabPanel","data":{"project":projectID,"module":moduleName,"modelID":modelID,"id":"tsBottomLeftContentTabID","widgetIDs":[inspectorViewID]}},
                {"command":"NewTabPanel","data":{"project":projectID,"module":moduleName,"modelID":modelID,"id":"tsTopRightContentTabID","widgetIDs":[shootingsDrawingID,shootingsGoogleMapID]}},
                {"command":"NewTabPanel","data":{"project":projectID,"module":moduleName,"modelID":modelID,"id":"tsBottomRightContentTabID","widgetIDs":[shootingsTimeLineID]}},
                {"command":"NewSplitPanel","data":{"project":projectID,"module":moduleName,"modelID":modelID,"id":"tsLeftVerticalSplitPaneID","firstWidgetID":"tsTopLeftContentTabID","secondWidgetID":"tsBottomLeftContentTabID","orientation":"vertical","splitPosition":"25%","panelName":null}},
                {"command":"NewSplitPanel","data":{"project":projectID,"module":moduleName,"modelID":modelID,"id":"tsRightVerticalSplitPaneID","firstWidgetID":"tsTopRightContentTabID","secondWidgetID":"tsBottomRightContentTabID","orientation":"vertical","splitPosition":"25%","panelName":null}},
                {"command":"NewSplitPanel","data":{"project":projectID,"module":moduleName,"modelID":modelID,"id":"tsMainContentDivID","firstWidgetID":"tsLeftVerticalSplitPaneID","secondWidgetID":"tsRightVerticalSplitPaneID","orientation":"horizontal","splitPosition":"82%","panelName":null}}
            ]
    };


// //	Initialize the web view based on a TSP project
// //	Define an array of commands using JSON and tell TSP to invoke
// var secondWebProjectCommand =
//     {
//         "command":"WebProject",
//         "onload":"onPerspectivesProjectLoad",
//         "onready":"onDashboardPerspectivesProjectReady",
//         "onfailure":"onPerspectivesCommandFailure",
//         "onsuccess":"onPerspectivesCommandSuccess",
//         "data":
//             [
//                 {"command":"LoadProject","data":{"project":projectID,"filename":"project/NYCCrimeData.10.tsp"}},
//                 {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsChartView","viewName":perpGenderChartViewName,"viewID":perpGenderChartViewID}},
//                 {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsChartView","viewName":perpRaceChartViewName,"viewID":perpRaceChartViewID}},
//                 {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsChartView","viewName":perpAgeGroupChartViewName,"viewID":perpAgeGroupChartViewID}},
//                 {"command":"NewView","data":{"project":projectID,"module":moduleName,"modelID":modelID,"viewClass":"tsTableView","viewName":"PerpetratorTable","viewID":"PerpetratorTableID"}}
//             ]
//     };


// function onIncidentProjectReady(projectId)
// {
//     invokePerspectivesCommand(secondWebProjectCommand);
// }

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
function refreshIncidentViews(projectID, moduleName, modelID)
{
    let viewIDs = [
        shootingsTreeViewID,
        shootingsGoogleMapID,
        shootingsDrawingID,
        shootingsTimeLineID,
        inspectorViewID,
        perpGenderChartViewID,
        perpRaceChartViewID,
        perpAgeGroupChartViewID,
        "PerpetratorTableID"
    ];

    refreshViewsWithIds(projectID, moduleName, modelID, viewIDs);
}


/**
 * Called by the framework to refesh application views.
 * @param projectID
 * @param moduleName
 * @param modelID
 */
function refreshDashboardViews(projectID, moduleName, modelID)
{
    let viewIDs = [
        perpGenderChartViewID,
        perpRaceChartViewID,
        perpAgeGroupChartViewID
    ];

    refreshViewsWithIds(projectID, moduleName, modelID, viewIDs);
}

function refreshViewsWithIds(projectId, moduleName, modelID, viewIDs)
{
    let refreshIncidentViewsCommand =
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

    let refreshIncidentViewsGroupCommand =
        {
            command: "Group",
            onsuccess: "onPerspectivesCommandSuccess",
            onfailure: "onPerspectivesCommandFailure",
            data: [refreshIncidentViewsCommand]
        };

    console.log("About to call:\n" + JSON.stringify(refreshIncidentViewsGroupCommand));
    invokePerspectivesCommand(refreshIncidentViewsGroupCommand);
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

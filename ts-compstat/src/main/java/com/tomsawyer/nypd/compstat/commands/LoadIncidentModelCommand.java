//
// Tom Sawyer Software
// Copyright 2007 - 2021
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.commands;


import com.tomsawyer.model.TSModelNotification;
import com.tomsawyer.model.events.TSModelChangeEvent;
import com.tomsawyer.model.graphmodel.TSGraphModel;
import com.tomsawyer.nypd.compstat.parser.IncidentParser;
import com.tomsawyer.util.gwtclient.command.TSServiceCommand;
import com.tomsawyer.util.shared.TSServiceException;
import com.tomsawyer.view.manager.TSModelViewManager;
import com.tomsawyer.web.client.command.TSCustomCommand;
import com.tomsawyer.web.server.command.TSServiceCommandImpl;
import com.tomsawyer.web.server.service.TSPerspectivesViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import java.io.Serializable;


@Component
@RequestScope
public class LoadIncidentModelCommand implements TSServiceCommandImpl
{
	@Autowired
	public LoadIncidentModelCommand(IncidentParser incidentParser)
	{
		this.incidentParser = incidentParser;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable doAction(TSPerspectivesViewService perspectivesViewService,
		TSServiceCommand serviceCommand) throws TSServiceException
	{
		TSCustomCommand customCommand = (TSCustomCommand) serviceCommand;

		final TSGraphModel model = (TSGraphModel)
			perspectivesViewService.getModel(customCommand.getProjectID(),
				customCommand.getModelID());

		final boolean wasFiringEvents = model.getEventManager().isFiringEvents();

		try
		{
			model.getEventManager().setFireEvents(false);

			this.incidentParser.parseIncidents(model);
		}
		catch (Throwable th)
		{
			throw new TSServiceException(th.getMessage(), th);
		}
		finally
		{
			model.getEventManager().setFireEvents(wasFiringEvents);

			TSModelNotification.fireModelChangeEvent(model,
				TSModelChangeEvent.GLOBAL_CHANGE |
					TSModelChangeEvent.INTEGRATOR_UPDATED);

			TSModelViewManager.getInstance().updateViews(model, true);
		}

		return Boolean.TRUE;
	}

	IncidentParser incidentParser;
}

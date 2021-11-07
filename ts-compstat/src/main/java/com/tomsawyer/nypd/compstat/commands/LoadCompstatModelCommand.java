//
// Tom Sawyer Software
// Copyright 2007 - 2021
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.commands;


import com.tomsawyer.model.TSModel;
import com.tomsawyer.model.TSModelElement;
import com.tomsawyer.model.TSModelNotification;
import com.tomsawyer.model.events.TSModelChangeEvent;
import com.tomsawyer.nypd.compstat.model.FelonyOffense;
import com.tomsawyer.nypd.compstat.parser.SevenMajorFelonyParser;
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
import java.util.Calendar;
import java.util.List;


@Component
@RequestScope
public class LoadCompstatModelCommand implements TSServiceCommandImpl
{
	/**
	 *
	 * @param sevenMajorFelonyParser
	 */
	@Autowired
	public LoadCompstatModelCommand(SevenMajorFelonyParser sevenMajorFelonyParser)
	{
		this.sevenMajorFelonyParser = sevenMajorFelonyParser;
	}


	/**
	 *
	 * @param perspectivesViewService
	 * @param serviceCommand
	 * @return
	 * @throws TSServiceException
	 */
	@Override
	public Serializable doAction(TSPerspectivesViewService perspectivesViewService,
		TSServiceCommand serviceCommand) throws TSServiceException
	{
		TSCustomCommand customCommand = (TSCustomCommand) serviceCommand;

		final TSModel model =
			perspectivesViewService.getModel(customCommand.getProjectID(),
				customCommand.getModelID());

		final boolean wasFiringEvents = model.getEventManager().isFiringEvents();

		try
		{
			model.getEventManager().setFireEvents(false);

			List<FelonyOffense> offenseElementList =
				sevenMajorFelonyParser.parseSevenMajorFeloniesCsv();

			Calendar calendar = Calendar.getInstance();

			for (FelonyOffense offenseElement : offenseElementList)
			{
				TSModelElement modelElement = model.newModelElement("Offense");

				modelElement.setAttribute("id",
					offenseElement.getName() +
						"." +
						offenseElement.getYear());
				modelElement.setAttribute("name", offenseElement.getName());

				// assume December 31 of the year
				calendar.set(offenseElement.getYear(),
					0,
					0,
					0,
					0);

				modelElement.setAttribute("year", calendar.getTime());
				modelElement.setAttribute("count", offenseElement.getCount());

				model.addElement(modelElement);
			}
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


	SevenMajorFelonyParser sevenMajorFelonyParser;
}

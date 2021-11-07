//
// Tom Sawyer Software
// Copyright 2007 - 2021
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.parser;


import com.tomsawyer.model.TSModel;
import com.tomsawyer.model.TSModelElement;
import com.tomsawyer.model.graphmodel.TSGraphModel;
import com.tomsawyer.model.graphmodel.TSVertexElement;
import com.tomsawyer.performance.TSTimePeriodUnits;
import com.tomsawyer.util.logging.TSLogger;
import com.tomsawyer.util.shared.TSServiceException;
import com.tomsawyer.util.shared.TSSharedUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.SneakyThrows;


@Component
@Scope("prototype")
public class IncidentParser
{

	/**
	 *
	 * @return
	 */
	@SneakyThrows
	public void parseIncidents(TSGraphModel graphModel)
	{
		final long start = System.currentTimeMillis();

		try
		{
			final Reader fileReader = new FileReader(
				"data/NYPDShootingIncidentDataHistoric.csv");
			final Iterable<CSVRecord> records =
				CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(fileReader);

			final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

			final String incidentType = "Shooting";

			final Map<String, TSVertexElement> boroMap = new HashMap<>();
			final Map<Integer, TSVertexElement> precinctMap = new HashMap<>();
			final Map<Integer, TSVertexElement> jurisdictionMap = new HashMap<>();

			final ZoneId zoneId = ZoneId.of("America/New_York");
			final Calendar calendar =
				Calendar.getInstance(TimeZone.getTimeZone(zoneId));

			for (CSVRecord record : records)
			{
				String incidentKey = record.get("INCIDENT_KEY");
				Date occurDate = dateFormat.parse(record.get("OCCUR_DATE"));
				LocalDateTime localDateTime =
					LocalDateTime.ofInstant(occurDate.toInstant(), zoneId);

				String occurTime = record.get("OCCUR_TIME");

				int hourOfDay = 0;
				int minute = 0;

				if (TSSharedUtils.isNotEmpty(occurTime))
				{
					StringTokenizer tokenizer =
						new StringTokenizer(occurTime, ":", false);

					String hourString = tokenizer.nextToken();

					if (hourString.startsWith("0"))
					{
						hourString = hourString.substring(1);

						if (TSSharedUtils.isNotEmpty(hourString))
						{
							hourOfDay = Integer.valueOf(hourString);
						}
						else
						{
							hourOfDay = 0;
						}
					}
					else
					{
						hourOfDay = Integer.valueOf(hourString);
					}

					String minuteString = tokenizer.nextToken();

					if (minuteString.equals("00"))
					{
						minute = 0;
					}
					else if (minuteString.startsWith("0"))
					{
						minute = Integer.valueOf(minuteString.substring(1));
					}
					else
					{
						minute = Integer.valueOf(minuteString);
					}

					int year = localDateTime.getYear();
					int month = localDateTime.getMonthValue();
					int day = localDateTime.getDayOfMonth();

					calendar.set(year, month, day, hourOfDay, minute);
				}
				else
				{
					calendar.set(localDateTime.getYear(),
						localDateTime.getMonthValue(),
						localDateTime.getDayOfMonth());
				}

				// Get the date and time.
				Date date = calendar.getTime();

				String boro = record.get("BORO");
				Integer precinctNumber = Integer.valueOf(record.get("PRECINCT"));
				Integer jurisdictionCode;

				try
				{
					jurisdictionCode = Integer.valueOf(record.get("JURISDICTION_CODE"));
				}
				catch(NumberFormatException numberFormatException)
				{
					jurisdictionCode = Integer.valueOf(-1);
				}

				String locationDescription = record.get("LOCATION_DESC");

				if (TSSharedUtils.isEmpty(locationDescription))
				{
					locationDescription = "unknown";
				}

				Boolean statisticalMurderFlag =
					Boolean.valueOf(record.get("STATISTICAL_MURDER_FLAG"));

				String perpAgeGroup = record.get("PERP_AGE_GROUP");
				String perpSex = record.get("PERP_SEX");
				String perpRace = record.get("PERP_RACE");

				colatePerpStatistics(
					TSSharedUtils.isNotEmpty(perpAgeGroup) ? perpAgeGroup : "UNKNOWN",
					TSSharedUtils.isNotEmpty(perpSex) ? perpSex : "U",
					TSSharedUtils.isNotEmpty(perpRace) ? perpRace : "UNKNOWN");

				String victimAgeGroup = record.get("VIC_AGE_GROUP");
				String victimSex = record.get("VIC_SEX");
				String victimRace = record.get("VIC_RACE");
				
				colateVictimStatistics(
					TSSharedUtils.isNotEmpty(victimAgeGroup) ? victimAgeGroup : "UNKNOWN",
					TSSharedUtils.isNotEmpty(victimSex) ? victimSex : "U",
					TSSharedUtils.isNotEmpty(victimRace) ? victimRace : "UNKNOWN");

				Double latitude = Double.valueOf(record.get("Latitude"));
				Double longitude = Double.valueOf(record.get("Longitude"));

				TSVertexElement incidentV = graphModel.newVertex("Incident");
				incidentV.setAttribute("id", incidentKey);
				incidentV.setAttribute("latitude", latitude);
				incidentV.setAttribute("longitude", longitude);
				incidentV.setAttribute("statisticalMurderFlag", statisticalMurderFlag);
				incidentV.setAttribute("type", incidentType);
				incidentV.setAttribute("locationDescription", locationDescription);
				incidentV.setAttribute("date", date);
				incidentV.setAttribute("precinctNumber", precinctNumber);
				incidentV.setAttribute("boro", boro);
				graphModel.addElement(incidentV);

				TSVertexElement boroV = boroMap.get(boro);

				if (boroV == null)
				{
					// Boro has never been seen before. Lets create one!
					boroV = graphModel.newVertex("Boro");
					boroV.setAttribute("name", boro);

					graphModel.addElement(boroV);
					boroMap.put(boro, boroV);

					final TSVertexElement boroFilterElement =
						this.createBoroFilter(graphModel,
							boro,
							boro.equals("STATEN ISLAND"));

					graphModel.addElement(boroFilterElement);
				}

				// See if we've ever encountered this precinct.
				TSVertexElement precinctV = precinctMap.get(precinctNumber);

				if (precinctV == null)
				{
					precinctV = graphModel.newVertex("Precinct");
					precinctV.setAttribute("number", precinctNumber);
					precinctV.setAttribute("boro", boro);

					graphModel.addElement(precinctV);
					precinctMap.put(precinctNumber, precinctV);

					graphModel.addRelation("HAS_PRECINCT", boroV, precinctV);

					TSVertexElement precinctFilterV =
						createPrecinctFilter(graphModel,
							precinctNumber,
							boro,
							boro.equals("STATEN ISLAND"));

					// Add the filter to the model.
					graphModel.addElement(precinctFilterV);
				}

				TSVertexElement jurisdictionV = jurisdictionMap.get(jurisdictionCode);

				if (jurisdictionV == null)
				{
					jurisdictionV = graphModel.newVertex("Jurisdiction");
					jurisdictionV.setAttribute("code", jurisdictionCode);

					graphModel.addElement(jurisdictionV);
					jurisdictionMap.put(jurisdictionCode, jurisdictionV);
				}

				TSVertexElement perpV = graphModel.newVertex("Perpetrator");
				perpV.setAttribute("id", UUID.randomUUID().toString());
				perpV.setAttribute("ageGroup", TSSharedUtils.isNotEmpty(perpAgeGroup) ? perpAgeGroup : "unknown");
				perpV.setAttribute("sex", TSSharedUtils.isNotEmpty(perpSex) ? perpSex : "unknown");
				perpV.setAttribute("race", TSSharedUtils.isNotEmpty(perpRace) ? perpRace : "unknown");
				perpV.setAttribute("precinctNumber", precinctNumber);
				perpV.setAttribute("boro", boro);
				graphModel.addElement(perpV);

				TSVertexElement victimV =  graphModel.newVertex("Victim");
				victimV.setAttribute("id", UUID.randomUUID().toString());
				victimV.setAttribute("ageGroup", TSSharedUtils.isNotEmpty(victimAgeGroup) ? victimAgeGroup : "unknown");
				victimV.setAttribute("sex", TSSharedUtils.isNotEmpty(victimSex) ? victimSex : "unknown");
				victimV.setAttribute("race", TSSharedUtils.isNotEmpty(victimRace) ? victimRace : "unknown");
				victimV.setAttribute("precinctNumber", precinctNumber);
				victimV.setAttribute("boro", boro);
				graphModel.addElement(victimV);

				graphModel.addRelation("HAS_JURISDICTION", incidentV, jurisdictionV);
				graphModel.addRelation("INCIDENT_HAS_PRECINCT", incidentV, victimV);
				graphModel.addRelation("HAS_VICTIM", incidentV, victimV);
				graphModel.addRelation("HAS_PERPETRATOR", incidentV, perpV);
			}

			createCountOfElements(graphModel);
		}
		catch (Exception e)
		{
			TSLogger.error(this.getClass(),
				e.getMessage(),
				e);

			throw new TSServiceException(e.getMessage(), e);
		}
		finally
		{
			final long finish = System.currentTimeMillis();

			String time = TSTimePeriodUnits.auto.toString(finish - start);

			TSLogger.info(this.getClass(),
				"Incidence Parser took #0.",
				time);


		}
	}


	protected void createCountOfElements(TSGraphModel graphModel)
	{
		TSLogger.info(this.getClass(),
			"Perp Age Group Map: #0",
			this.perpAgeGroupsMap);

		Set<String> perpAgeGroups = this.perpAgeGroupsMap.keySet();

		perpAgeGroups.forEach(ageGroup ->
			{
				final TSVertexElement perpAgeGroup =
					graphModel.newVertex("PerpAgeGroup");
				perpAgeGroup.setAttribute("count",
					this.perpAgeGroupsMap.get(ageGroup).intValue());
				perpAgeGroup.setAttribute("name",
					ageGroup);

				graphModel.addElement(perpAgeGroup);
			});

		TSLogger.info(this.getClass(),
			"Perp Sex Map: #0",
			this.perpSexMap);

		Set<String> perpGenders = this.perpSexMap.keySet();

		perpGenders.forEach(perpGender ->
		{
			final TSVertexElement perpGenderVertex =
				graphModel.newVertex("PerpGender");
			perpGenderVertex.setAttribute("count",
				this.perpSexMap.get(perpGender).intValue());
			perpGenderVertex.setAttribute("name",
				perpGender);

			graphModel.addElement(perpGenderVertex);
		});

		TSLogger.info(this.getClass(),
			"Perp Race Map: #0",
			this.perpRaceMap);

		Set<String> perpRaces = this.perpRaceMap.keySet();

		perpRaces.forEach(perpRace ->
		{
			final TSVertexElement perpRaceVertex =
				graphModel.newVertex("PerpRace");
			perpRaceVertex.setAttribute("count",
				this.perpRaceMap.get(perpRace).intValue());
			perpRaceVertex.setAttribute("name",
				perpRace);

			graphModel.addElement(perpRaceVertex);
		});


		TSLogger.info(this.getClass(),
			"Victim Age Group Map: #0",
			this.vicAgeGroupsMap);

		Set<String> vicAgeGroups = this.vicAgeGroupsMap.keySet();

		vicAgeGroups.forEach(ageGroup ->
		{
			final TSVertexElement vicAgeGroup =
				graphModel.newVertex("VictimAgeGroup");
			vicAgeGroup.setAttribute("count",
				this.vicAgeGroupsMap.get(ageGroup).intValue());
			vicAgeGroup.setAttribute("name",
				ageGroup);

			graphModel.addElement(vicAgeGroup);
		});

		TSLogger.info(this.getClass(),
			"Victim Sex Map: #0",
			this.vicSexMap);

		Set<String> vicGenders = this.vicSexMap.keySet();

		vicGenders.forEach(vicGender ->
		{
			final TSVertexElement vicGenderVertex =
				graphModel.newVertex("VictimGender");
			vicGenderVertex.setAttribute("count",
				this.vicSexMap.get(vicGender).intValue());
			vicGenderVertex.setAttribute("name",
				vicGender);

			graphModel.addElement(vicGenderVertex);
		});

		TSLogger.info(this.getClass(),
			"Victim Race Map: #0",
			this.vicRaceMap);

		Set<String> vicRaces = this.vicRaceMap.keySet();

		vicRaces.forEach(vicRace ->
		{
			final TSVertexElement vicRaceVertex =
				graphModel.newVertex("VictimRace");
			vicRaceVertex.setAttribute("count",
				this.vicRaceMap.get(vicRace).intValue());
			vicRaceVertex.setAttribute("name",
				vicRace);

			graphModel.addElement(vicRaceVertex);
		});
	}

	private void colatePerpStatistics(String perpAgeGroup, String perpSex, String perpRace)
	{
		AtomicInteger ageGroupInteger = perpAgeGroupsMap.get(perpAgeGroup);

		if (ageGroupInteger == null)
		{
			ageGroupInteger = new AtomicInteger(1);
			perpAgeGroupsMap.put(perpAgeGroup, ageGroupInteger);
		}
		else
		{
			ageGroupInteger.incrementAndGet();
		}

		AtomicInteger sexInteger = perpSexMap.get(perpSex);

		if (sexInteger == null)
		{
			sexInteger = new AtomicInteger(1);
			perpSexMap.put(perpSex, sexInteger);
		}
		else
		{
			sexInteger.incrementAndGet();
		}

		AtomicInteger raceInteger = perpRaceMap.get(perpRace);

		if (raceInteger == null)
		{
			raceInteger = new AtomicInteger(1);
			perpRaceMap.put(perpRace, raceInteger);
		}
		else
		{
			raceInteger.incrementAndGet();
		}
	}


	private void colateVictimStatistics(String vicAgeGroup, String vicSex, String vicRace)
	{
		AtomicInteger ageGroupInteger = vicAgeGroupsMap.get(vicAgeGroup);

		if (ageGroupInteger == null)
		{
			ageGroupInteger = new AtomicInteger(1);
			vicAgeGroupsMap.put(vicAgeGroup, ageGroupInteger);
		}
		else
		{
			ageGroupInteger.incrementAndGet();
		}

		AtomicInteger sexInteger = vicSexMap.get(vicSex);

		if (sexInteger == null)
		{
			sexInteger = new AtomicInteger(1);
			vicSexMap.put(vicSex, sexInteger);
		}
		else
		{
			sexInteger.incrementAndGet();
		}

		AtomicInteger raceInteger = vicRaceMap.get(vicRace);

		if (raceInteger == null)
		{
			raceInteger = new AtomicInteger(1);
			vicRaceMap.put(vicRace, raceInteger);
		}
		else
		{
			raceInteger.incrementAndGet();
		}
	}

	private TSVertexElement createPrecinctFilter(
		TSGraphModel graphModel,
		Integer number,
		String boroName,
		Boolean enabled)
	{
		TSVertexElement precinctFilterV = createPrecinctFilter(
			graphModel,
			number,
			boroName);

		precinctFilterV.setAttribute("enabled",
			enabled);

		TSLogger.info(this.getClass(),
			"Loading Precinct Filter Boro:#0, Number:#1, Enabled:#2",
			boroName,
			number,
			enabled);

		return precinctFilterV;
	}


	private TSVertexElement createPrecinctFilter(
		TSGraphModel graphModel,
		Integer number,
		String boroName)
	{
		// Create a Filter for the boro with the given name..
		TSVertexElement precinctFilterV =
			graphModel.newVertex("PrecinctFilter");
		precinctFilterV.setAttribute("number", number);
		precinctFilterV.setAttribute("boro", boroName);

		return precinctFilterV;
	}


	private TSVertexElement createBoroFilter(
		TSGraphModel graphModel,
		String boroName,
		Boolean enabled)
	{
		TSVertexElement boroFilterV = createBoroFilter(graphModel, boroName);

		boroFilterV.setAttribute("enabled",
			enabled);

		TSLogger.info(this.getClass(),
			"Loading Boro Filter Boro:#0, Enabled:#1",
			boroName,
			enabled);


		return boroFilterV;
	}


	private TSVertexElement createBoroFilter(TSGraphModel graphModel, String boroName)
	{
		// Create a Filter for the boro with the given name..
		TSVertexElement boroFilterV =
			graphModel.newVertex("BoroFilter");
		boroFilterV.setAttribute("name", boroName);

		return boroFilterV;
	}


	Map<String, AtomicInteger> perpAgeGroupsMap = new HashMap<>();
	Map<String, AtomicInteger> perpSexMap = new HashMap<>();
	Map<String, AtomicInteger> perpRaceMap = new HashMap<>();


	Map<String, AtomicInteger> vicAgeGroupsMap = new HashMap<>();
	Map<String, AtomicInteger> vicSexMap = new HashMap<>();
	Map<String, AtomicInteger> vicRaceMap = new HashMap<>();
}

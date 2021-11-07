//
// Tom Sawyer Software
// Copyright 2007 - 2021
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.parser;


import com.tomsawyer.nypd.compstat.model.FelonyOffense;
import com.tomsawyer.util.logging.TSLogger;
import com.tomsawyer.util.shared.TSServiceException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;


@Component
@Scope("prototype")
public class SevenMajorFelonyParser
{
	public SevenMajorFelonyParser()
	{
	}


	/**
	 *
	 * @return
	 */
	@SneakyThrows
	public List<FelonyOffense> parseSevenMajorFeloniesCsv()
	{
		try
		{
			List<FelonyOffense> offenses = new ArrayList<>();

			Reader fileReader = new FileReader("data/seven-major-felony-offenses-2000-2020.csv");
			Iterable<CSVRecord> records =
				CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(fileReader);

			for (CSVRecord record : records)
			{
				int recordSize = record.size();
				String offense = record.get("OFFENSE");

				if (!"TOTAL SEVEN MAJOR FELONY OFFENSES".equals(offense))
				{
					System.out.println(
						"The number of years of data for " + offense + " is " +
							String.valueOf(recordSize - 1));

					for (String year : SevenMajorFelonyParser.years)
					{
						String countString = record.get(year);

						// remove commas from the count value. Can be "2,068" for example.
						Integer count = Integer.valueOf(
							countString.replaceAll(",", ""));

						FelonyOffense offenseElement = new FelonyOffense();
						offenseElement.setYear(Integer.valueOf(year));
						offenseElement.setCount(count);
						offenseElement.setName(record.get("OFFENSE"));

						System.out.println("\tThe number of " + offense + " for the year "
							+ year + " equals " + count);

						offenses.add(offenseElement);
					}
				}
			}

			return offenses;
		}
		catch (IOException e)
		{
			TSLogger.error(this.getClass(),
				e.getMessage(),
				e);

			throw new TSServiceException(e.getMessage(), e);
		}
	}

	public static String[] years =
		{
			"2000",
			"2001",
			"2002",
			"2003",
			"2004",
			"2005",
			"2006",
			"2007",
			"2008",
			"2009",
			"2010",
			"2011",
			"2012",
			"2013",
			"2014",
			"2015",
			"2016",
			"2017",
			"2018",
			"2019",
			"2020"
		};

	public Map<String, Integer> yearToIndexMap = new HashMap<>();
}

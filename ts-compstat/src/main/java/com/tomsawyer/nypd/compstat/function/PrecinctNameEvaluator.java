//
// Tom Sawyer Software
// Copyright 2007 - 2021
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.function;


import com.tomsawyer.util.evaluator.shared.TSEvaluationException;
import com.tomsawyer.util.evaluator.shared.TSEvaluator;
import com.tomsawyer.util.evaluator.shared.TSEvaluatorData;


/**
 * Evaluates to a friendly precinct name based on a number.
 */
public class PrecinctNameEvaluator implements TSEvaluator
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(TSEvaluatorData evaluatorData) throws TSEvaluationException
	{
		Object[] arguments = evaluatorData.getArguments();

		if (arguments.length == 0)
		{
			throw new TSEvaluationException(
				"No precinct number passed in to evaluator function " +
					this.getClass().getName());
		}

		Object precinctObject = arguments[0];

		Integer precinctNumber = Integer.valueOf(precinctObject.toString());

		String precinctString = String.valueOf(precinctNumber);

		Object result;

		if (precinctString.endsWith("1"))
		{
			result = precinctString + "st Precinct";
		}
		else
		if (precinctString.endsWith("2"))
		{
			result = precinctString + "nd Precinct";
		}
		else if (precinctString.endsWith("3"))
		{
			result = precinctString + "rd Precinct";
		}
		else
		{
			result = precinctString + "th Precinct";
		}

		return result;
	}


	/**
	 * We are assuming they will pass something like <number> so we want it
	 * evaluated.
	 * @param s
	 * @param i
	 * @return
	 */
	@Override
	public boolean isEvaluateArgument(String s, int i)
	{
		return true;
	}
}

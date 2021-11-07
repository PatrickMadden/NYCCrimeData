//
// Tom Sawyer Software
// Copyright 2007-2019
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.function.graph;


import com.tomsawyer.model.graphmodel.TSRelationElement;
import com.tomsawyer.model.graphmodel.TSVertexElement;
import com.tomsawyer.model.shared.evaluator.TSModelEvaluatorManager;
import com.tomsawyer.util.evaluator.shared.TSEvaluationException;
import com.tomsawyer.util.evaluator.shared.TSEvaluator;
import com.tomsawyer.util.evaluator.shared.TSEvaluatorData;
import com.tomsawyer.util.logging.TSLogger;
import com.tomsawyer.util.shared.TSAttributedObject;


/**
 * Evaluates expression for a source vertex of a relationship.
 */
public class TSGetSourceNodePropertyEvaluator implements TSEvaluator
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(TSEvaluatorData data) throws TSEvaluationException
	{
		Object[] args = data.getArguments();

		Object result;

		if ("getSourceNodeProperty".equals(data.getFunctionName()))
		{
			if (args.length != 1)
			{
				throw new TSEvaluationException("Invalid number of arguments");
			}

			String returnExpression = args[0].toString();

			TSAttributedObject attributedObject = data.getAttributedObject();

			result = getSourceNodeProperty(data, returnExpression, attributedObject);
		}
		else if ("getSourceNodePropertyOf".equals(data.getFunctionName()))
		{
			if (args.length != 2)
			{
				throw new TSEvaluationException("Invalid number of arguments for " +
					data.getFunctionName());
			}

			TSAttributedObject attributedObject = (TSAttributedObject) args[0];

			String returnExpression = args[1].toString();

			result = getSourceNodeProperty(data, returnExpression, attributedObject);
		}
		else
		{
			result = null;
		}
		
		return result;
	}


	/**
	 * This method gets the source node property for afor the given attributed object. It
	 * must be an instance of TSRelationElement.
	 * @param data The {@link TSEvaluatorData} instance.
	 * @param expression The expression to evaluate on the input
	 * {@link TSAttributedObject} instance.
	 * @param attributedObject The attributed object that must be an instance of
	 * {@link TSRelationElement}.
	 * @return The value of the expression.
	 * @throws TSEvaluationException Thrown if an error occurs.
	 */
	protected Object getSourceNodeProperty(TSEvaluatorData data,
		String expression,
		TSAttributedObject attributedObject) throws TSEvaluationException
	{
		String returnExpression;

		// this can happen from designer when the property we evaluating does not not
		// belong to the context object. When that is the case, Designer flags it as
		// an error but by removing the brackets it allows it.

		if (!expression.startsWith("<") && !expression.endsWith(">"))
		{
			returnExpression = "<" + expression + ">";
		}
		else
		{
			returnExpression = expression;
		}


		if (!(attributedObject instanceof TSRelationElement))
		{
			throw new TSEvaluationException("Not instance of TSRelationElement");
		}

		TSRelationElement rel = (TSRelationElement) attributedObject;

		TSVertexElement source = rel.getSourceElement();

		Object result = TSModelEvaluatorManager.getInstance().evaluate(
			returnExpression,
			source,
			data.getContext());

		if (TSLogger.isDebugEnabled(this.getClass()))
		{
			TSLogger.debug(this.getClass(),
				"Get Source Node Property for vertex #0 of relation #1 and " +
					"expression #2 results in #3",
				source.getTypeName(),
				rel.getTypeName(),
				returnExpression,
				result);
		}

		return result;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEvaluateArgument(String s, int i)
	{
		boolean result;

		if ("getSourceNodePropertyOf".equals(s))
		{
			result = i == 0;
		}
		else
		{
			result = false;
		}

		return result;
	}


	/**
	 * Java Serialization ID.
	 */
	private static final long serialVersionUID = 1L;
}

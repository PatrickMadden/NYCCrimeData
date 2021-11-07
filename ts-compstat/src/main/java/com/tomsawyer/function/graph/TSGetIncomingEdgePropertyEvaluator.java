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
import com.tomsawyer.util.shared.TSAttributedObject;


/**
 * Evaluates expression for an incoming node found by edge type with given node type.
 */
public class TSGetIncomingEdgePropertyEvaluator implements TSEvaluator
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(TSEvaluatorData data) throws TSEvaluationException
	{
		if ("getIncomingEdgeProperty".equals(data.getFunctionName()))
		{
			return getIncomingEdgeProperty(data);
		}

		return null;
	}


	/**
	 * Gets the incomming edge property.
	 * @param data The evaluator data.
	 * @return The evaluated result.
	 * @throws TSEvaluationException thrown if an error occurs.
	 */
	protected Object getIncomingEdgeProperty(TSEvaluatorData data)
		throws TSEvaluationException
	{
		Object[] args = data.getArguments();

		if (args.length != 2)
		{
			throw new TSEvaluationException("Invalid number of arguments");
		}

		String edgeType = args[0].toString();
		String returnExpression = args[1].toString();

		TSAttributedObject attributedObject = data.getAttributedObject();

		if (!(attributedObject instanceof TSVertexElement))
		{
			throw new TSEvaluationException("Not instance of TSVertexElement");
		}

		TSVertexElement element = (TSVertexElement) attributedObject;

		for (TSRelationElement rel : element.inRelationElements(edgeType))
		{
			return TSModelEvaluatorManager.getInstance().evaluate(
				returnExpression,
				rel,
				data.getContext());
		}

		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEvaluateArgument(String s, int i)
	{
		return i == 0 ? true : false;
	}


	/**
	 * Java Serialization ID.
	 */
	private static final long serialVersionUID = 1L;
}

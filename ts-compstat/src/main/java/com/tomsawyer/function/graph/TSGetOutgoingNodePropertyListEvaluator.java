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
import java.util.ArrayList;
import java.util.List;


/**
 * Evaluates expression for a neighbor node found by edge type with given node type
 */
public class TSGetOutgoingNodePropertyListEvaluator implements TSEvaluator
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(TSEvaluatorData data) throws TSEvaluationException
	{
		Object[] args = data.getArguments();

		if (args.length != 3)
		{
			throw new TSEvaluationException("Invalid number of arguments");
		}

		String edgeType = args[0].toString();
		String nodeType = args[1].toString();
		String returnExpression = args[2].toString();

		TSAttributedObject attributedObject = data.getAttributedObject();

		if (!(attributedObject instanceof TSVertexElement))
		{
			throw new TSEvaluationException("Not instance of TSVertexElement");
		}

		TSVertexElement element = (TSVertexElement) attributedObject;
		List<TSRelationElement> rels = element.outRelationElements(edgeType);

		List<Object> results = new ArrayList<>(rels.size());

		for (TSRelationElement rel : rels)
		{
			TSVertexElement target = rel.getTargetElement();

			if (nodeType.equals(target.getTypeName()))
			{
				results.add(TSModelEvaluatorManager.getInstance().evaluate(
					returnExpression,
					target,
					data.getContext()));
			}
		}

		return results;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEvaluateArgument(String s, int i)
	{
		return i == 2 ? false : true;
	}


	/**
	 * Java Serialization ID.
	 */
	private static final long serialVersionUID = 1L;
}

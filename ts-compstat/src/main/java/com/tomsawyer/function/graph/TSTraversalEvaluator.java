//
// Tom Sawyer Software
// Copyright 2007-2020
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.function.graph;


import com.tomsawyer.graph.TSEdge;
import com.tomsawyer.graph.TSNode;
import com.tomsawyer.model.graphmodel.TSRelationElement;
import com.tomsawyer.model.graphmodel.TSVertexElement;
import com.tomsawyer.model.shared.evaluator.TSModelEvaluatorManager;
import com.tomsawyer.nypd.compstat.util.TSLists;
import com.tomsawyer.util.datastructures.TSArrayList;
import com.tomsawyer.util.datastructures.TSLinkedList;
import com.tomsawyer.util.evaluator.shared.TSEvaluationException;
import com.tomsawyer.util.evaluator.shared.TSEvaluator;
import com.tomsawyer.util.evaluator.shared.TSEvaluatorData;
import com.tomsawyer.util.logging.TSLogger;
import com.tomsawyer.util.shared.TSAttributedObject;
import com.tomsawyer.util.shared.TSContextInterface;
import com.tomsawyer.util.shared.TSSharedUtils;
import java.util.*;


/**
 * Performs various function evaluations relating to graphs and graph traversals.
 */
public class TSTraversalEvaluator implements TSEvaluator
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(TSEvaluatorData data) throws TSEvaluationException
	{
		Object[] args = data.getArguments();

		Object result;

		if ("degree".equals(data.getFunctionName()))
		{
			result = degree(Direction.BOTH, data);
		}
		else if ("inDegree".equals(data.getFunctionName()))
		{
			result = degree(Direction.IN, data);
		}
		else if ("outDegree".equals(data.getFunctionName()))
		{
			result = degree(Direction.OUT, data);
		}
		else if ("sourceNodeDegree".equals(data.getFunctionName()))
		{
			result = sourceNodeDegree(Direction.BOTH, data);
		}
		else if ("sourceNodeInDegree".equals(data.getFunctionName()))
		{
			result = sourceNodeDegree(Direction.IN, data);
		}
		else if ("sourceNodeOutDegree".equals(data.getFunctionName()))
		{
			result = sourceNodeDegree(Direction.OUT, data);
		}
		else if ("targetNodeDegree".equals(data.getFunctionName()))
		{
			result = targetNodeDegree(Direction.BOTH, data);
		}
		else if ("targetNodeInDegree".equals(data.getFunctionName()))
		{
			result = targetNodeDegree(Direction.IN, data);
		}
		else if ("targetNodeOutDegree".equals(data.getFunctionName()))
		{
			result = targetNodeDegree(Direction.OUT, data);
		}
		else if ("edges".equals(data.getFunctionName()))
		{
			result = edges(Direction.BOTH, data);
		}
		else if ("outEdges".equals(data.getFunctionName()))
		{
			result = edges(Direction.OUT, data);
		}
		else if ("inEdges".equals(data.getFunctionName()))
		{
			result = edges(Direction.IN, data);
		}
		else if ("edgesOf".equals(data.getFunctionName()))
		{
			result = edgesOf((TSAttributedObject) data.getArguments()[0],
				Direction.BOTH,
				data,
				shiftArgs(data.getArguments()));
		}
		else if ("outEdgesOf".equals(data.getFunctionName()))
		{
			result = edgesOf((TSAttributedObject) data.getArguments()[0],
				Direction.OUT,
				data,
				shiftArgs(data.getArguments()));
		}
		else if ("inEdgesOf".equals(data.getFunctionName()))
		{
			result = edgesOf((TSAttributedObject) data.getArguments()[0],
				Direction.IN,
				data,
				shiftArgs(data.getArguments()));
		}
		else if ("outNodes".equals(data.getFunctionName()))
		{
			result = nodes(Direction.OUT, data);
		}
		else if ("inNodes".equals(data.getFunctionName()))
		{
			result = nodes(Direction.IN, data);
		}
		else if ("getUpstreamNodeProperty".equals(data.getFunctionName()))
		{
			if (args.length != 2 && args.length != 3)
			{
				throw new TSEvaluationException(
					"Invalid number of arguments for getUpstreamNodeProperty");
			}

			TSVertexElement vertexElement;

			TSAttributedObject attributedObject = data.getAttributedObject();

			if (attributedObject instanceof TSVertexElement)
			{
				vertexElement = (TSVertexElement) attributedObject;
			}
			else
			{
				vertexElement = ((TSRelationElement) attributedObject).getSourceElement();
			}

			String nodeType = args[0].toString();
			String returnExpression = args[1].toString();
			List<String> path;

			List<TSRelationElement> tsRelationElements;

			int pathIndex = 0;

			if (args.length == 3)
			{
				path = (List<String>) args[2];

				tsRelationElements =
					vertexElement.inRelationElements(path.get(pathIndex));
				pathIndex++;
			}
			else
			{
				path = null;
				pathIndex = -1;

				tsRelationElements = toRelationElements(vertexElement.asNode().inEdges());
			}

			TSLogger.debug(this.getClass(),
				"Traversing in edges of #0.",
				vertexElement.getTypeName());

			result = getUpstreamNodePropertyFrom(
				tsRelationElements,
				data.getContext(),
				nodeType,
				returnExpression,
				path,
				pathIndex);

			TSLogger.info(this.getClass(),
				"#0 for #1 yields #2",
				data.getFunctionName(),
				vertexElement.getTypeName(),
				result);
		}
		else if ("getUpstreamNodePropertyOf".equals(data.getFunctionName()))
		{
			if (args.length != 3 && args.length != 4)
			{
				throw new TSEvaluationException(
					"Invalid number of arguments for getDownstreamNodePropertyFrom");
			}

			Object attributedObject = args[0];

			TSVertexElement vertexElement;

			if (attributedObject instanceof TSVertexElement)
			{
				vertexElement = (TSVertexElement) attributedObject;
			}
			else if (attributedObject instanceof TSRelationElement)
			{
				vertexElement = ((TSRelationElement) attributedObject).getSourceElement();
			}
			else
			{
				vertexElement = null;
			}

			if (vertexElement != null)
			{
				List<String> path;
				List<TSRelationElement> tsRelationElements;
				int pathIndex = 0;

				if (args.length == 4)
				{
					path = (List<String>) args[3];

					tsRelationElements =
						vertexElement.inRelationElements(path.get(pathIndex));
					pathIndex++;
				}
				else
				{
					path = null;
					pathIndex = -1;

					tsRelationElements =
						toRelationElements(vertexElement.asNode().inEdges());
				}

				String nodeType = args[0].toString();
				String returnExpression = args[1].toString();

				TSLogger.debug(this.getClass(),
					"Traversing in edges of #0 for #1.",
					vertexElement.getTypeName(),
					nodeType);

				result = getUpstreamNodePropertyFrom(
					tsRelationElements,
					data.getContext(),
					nodeType,
					returnExpression,
					path,
					pathIndex);
			}
			else
			{
				result = 0;
			}

			TSLogger.info(this.getClass(),
				"#0 for #1 yields #2",
				data.getFunctionName(),
				vertexElement.getTypeName(),
				result);
		}
		else if ("getDownstreamNodeProperty".equals(data.getFunctionName()))
		{
			if (args.length != 2 && args.length != 3)
			{
				throw new TSEvaluationException(
					"Invalid number of arguments for getUpstreamNodeProperty");
			}

			TSAttributedObject attributedObject = data.getAttributedObject();

			TSVertexElement vertexElement;

			if (attributedObject instanceof TSVertexElement)
			{
				vertexElement = (TSVertexElement) attributedObject;
			}
			else
			{
				vertexElement = ((TSRelationElement) attributedObject).getTargetElement();
			}

			String nodeType = args[0].toString();
			String returnExpression = args[1].toString();
			List<String> path;

			List<TSRelationElement> tsRelationElements;

			int pathIndex = 0;

			if (args.length == 3)
			{
				path = (List<String>) args[2];

				tsRelationElements =
					vertexElement.outRelationElements(path.get(pathIndex));
				pathIndex++;
			}
			else
			{
				path = null;
				pathIndex = -1;

				tsRelationElements =
					toRelationElements(vertexElement.asNode().outEdges());
			}

			TSLogger.debug(this.getClass(),
				"Traversing out edges of #0.",
				vertexElement.getTypeName());

			result = getDownstreamNodePropertyFrom(
				tsRelationElements,
				data.getContext(),
				nodeType,
				returnExpression,
				path,
				pathIndex);

			TSLogger.info(this.getClass(),
				"#0 for #1 yields #2",
				data.getFunctionName(),
				vertexElement.getTypeName(),
				result);
		}
		else if ("getDownstreamNodePropertyOf".equals(data.getFunctionName()))
		{
			if (args.length != 3 && args.length != 4)
			{
				throw new TSEvaluationException(
					"Invalid number of arguments for getDownstreamNodePropertyFrom");
			}

			Object attributedObject = args[0];

			TSVertexElement vertexElement;

			if (attributedObject instanceof TSVertexElement)
			{
				vertexElement = (TSVertexElement) attributedObject;
			}
			else if (attributedObject instanceof TSRelationElement)
			{
				vertexElement = ((TSRelationElement) attributedObject).getTargetElement();
			}
			else
			{
				vertexElement = null;
			}

			if (vertexElement != null)
			{
				List<String> path;
				List<TSRelationElement> tsRelationElements;
				int pathIndex = 0;

				if (args.length == 4)
				{
					path = (List<String>) args[3];

					tsRelationElements =
						vertexElement.outRelationElements(path.get(pathIndex));
					pathIndex++;
				}
				else
				{
					path = null;
					pathIndex = -1;

					tsRelationElements =
						toRelationElements(vertexElement.asNode().outEdges());
				}

				String nodeType = args[0].toString();
				String returnExpression = args[1].toString();

				TSLogger.debug(this.getClass(),
					"Traversing out edges of #0 for #1.",
					vertexElement.getTypeName(),
					nodeType);

				result = getDownstreamNodePropertyFrom(
					tsRelationElements,
					data.getContext(),
					nodeType,
					returnExpression,
					path,
					pathIndex);
		}
		else
		{
			result = 0;
		}

			TSLogger.info(this.getClass(),
				"#0 for #1 yields #2",
				data.getFunctionName(),
				vertexElement.getTypeName(),
				result);
		}
		else if ("hasUpstreamNodeType".equals(data.getFunctionName()))
		{
			if (args.length != 1 && args.length != 2)
			{
				throw new TSEvaluationException(
					"Invalid number of arguments for hasUpstreamNodeType");
			}

			TSAttributedObject attributedObject = data.getAttributedObject();

			TSVertexElement vertexElement;

			if (attributedObject instanceof TSVertexElement)
			{
				vertexElement = (TSVertexElement) attributedObject;
			}
			else
			{
				vertexElement = ((TSRelationElement) attributedObject).getSourceElement();
			}

			if (vertexElement != null)
			{
				List<String> path;
				List<TSRelationElement> tsRelationElements;
				int pathIndex = 0;

				if (args.length == 2)
				{
					path = (List<String>) args[1];

					tsRelationElements =
						vertexElement.inRelationElements(path.get(pathIndex));
					pathIndex++;
				}
				else
				{
					path = null;
					pathIndex = -1;

					tsRelationElements =
						toRelationElements(vertexElement.asNode().inEdges());
				}

				String nodeType = args[0].toString();

		TSLogger.debug(this.getClass(),
					"Traversing in edges of #0.",
					vertexElement.getTypeName());

				result = hasUpstreamTypeFrom(
					tsRelationElements,
					data.getContext(),
					nodeType,
					path,
					pathIndex);
			}
			else
			{
				result = Boolean.FALSE;
			}

			TSLogger.info(this.getClass(),
				"#0 for #1 yields #2",
			data.getFunctionName(),
				vertexElement.getTypeName(),
			result);
		}
		else if ("hasDownstreamNodeType".equals(data.getFunctionName()))
		{
			if (args.length != 1 && args.length != 2)
			{
				throw new TSEvaluationException(
					"Invalid number of arguments for hasDownstreamNodeType");
			}

			TSAttributedObject attributedObject = data.getAttributedObject();

			TSVertexElement vertexElement;

			if (attributedObject instanceof TSVertexElement)
			{
				vertexElement = (TSVertexElement) attributedObject;
			}
			else
			{
				vertexElement = ((TSRelationElement) attributedObject).getTargetElement();
	}

			if (vertexElement != null)
			{
				List<String> path;
				List<TSRelationElement> tsRelationElements;
				int pathIndex = 0;

				if (args.length == 2)
	{
					path = (List<String>) args[1];

					tsRelationElements =
						vertexElement.outRelationElements(path.get(pathIndex));
					pathIndex++;
				}
				else
		{
					path = null;
					pathIndex = -1;

					tsRelationElements =
						toRelationElements(vertexElement.asNode().outEdges());
				}

				String nodeType = args[0].toString();

				TSLogger.debug(this.getClass(),
					"Traversing out edges of #0.",
					vertexElement.getTypeName());

				result = hasDownstreamTypeFrom(
					tsRelationElements,
					data.getContext(),
					nodeType,
					path,
					pathIndex);
		}
		else
		{
				result = Boolean.FALSE;
			}

			TSLogger.info(this.getClass(),
				"#0 for #1 yields #2",
				data.getFunctionName(),
				vertexElement.getTypeName(),
				result);
		}
		else
			{
			result = 0;
			}

		TSLogger.debug(this.getClass(),
			"#0 for #1 = #2",
			data.getFunctionName(),
			data.getArguments() != null ? data.getArguments() : "",
			result);

		return result;
	}


	/**
	 * Gets the upstream node property.
	 * @param relationElements The edges to traverse.
	 * @param context Evaluation context.
	 * @param nodeType The target node type.
	 * @param returnExpression The expression to evaluate.
	 * @param path
	 * @param pathIndex
	 * @return The evaluated expression.
	 * @throws TSEvaluationException Thrown if there is an error.
	 */
	protected Object getUpstreamNodePropertyFrom(
		List<TSRelationElement> relationElements,
		TSContextInterface context,
		String nodeType,
		String returnExpression,
		List<String> path,
		int pathIndex) throws TSEvaluationException
	{
		List<TSVertexElement> vertexElements = new TSLinkedList<>();
		Object result = null;

		for (TSRelationElement rel : relationElements)
		{
			TSVertexElement source = rel.getSourceElement();

			if (nodeType.equals(source.getTypeName()))
			{
				TSLogger.debug(this.getClass(),
					"Evaluating #0 for #1.",
					source.getTypeName(),
					returnExpression);

				result = TSModelEvaluatorManager.getInstance().evaluate(
					returnExpression,
					source,
					context);

				break;
			}
			else
			{
				vertexElements.add(source);
			}
		}

		if (result == null)
		{
			TSVertexElement upstreamVertex;

			Iterator<TSVertexElement> upstreamVertexIter = vertexElements.iterator();

			while (upstreamVertexIter.hasNext())
			{
				upstreamVertex = upstreamVertexIter.next();

				TSLogger.info(this.getClass(),
					"Traversing in edges of #0 for #1.",
					upstreamVertex.getTypeName(),
					nodeType);

				List<TSRelationElement> upstreadmRelationElements = null;

				if (TSSharedUtils.isEmpty(path))
				{
					upstreadmRelationElements =
						toRelationElements(upstreamVertex.asNode().inEdges());
				}
				else
				{
					if (pathIndex < path.size())
					{
						upstreadmRelationElements =
							upstreamVertex.inRelationElements(path.get(pathIndex));
					}
				}

				if (!TSSharedUtils.isEmpty(upstreadmRelationElements))
				{
					result = getUpstreamNodePropertyFrom(
						upstreadmRelationElements,
						context,
						nodeType,
						returnExpression,
						path,
						pathIndex + 1);

					if (result != null)
					{
						break;
					}
				}
			}
		}

		return result;
	}


	/**
	 * Method return true if inputs have a downstream type.
	 * @param relationElements The relation elements.
	 * @param context The context.
	 * @param nodeType The node type.
	 * @param path The path to traverse.
	 * @param pathIndex The path index.
	 * @return True if has downstream type, false otherwise.
	 */
	protected Boolean hasDownstreamTypeFrom(
		List<TSRelationElement> relationElements,
		TSContextInterface context,
		String nodeType,
		List<String> path,
		int pathIndex)
	{
		List<TSVertexElement> vertexElements = new TSLinkedList<>();
		Boolean result = null;

		for (TSRelationElement rel : relationElements)
		{
			TSVertexElement target = rel.getTargetElement();

			if (nodeType.equals(target.getTypeName()))
			{
				result = Boolean.TRUE;
				break;
			}
			else
			{
				vertexElements.add(target);
			}
		}

		if (result == null)
		{
			TSVertexElement downstreamVertex;

			Iterator<TSVertexElement> downstreamVertexIter = vertexElements.iterator();

			while (downstreamVertexIter.hasNext())
			{
				downstreamVertex = downstreamVertexIter.next();

				TSLogger.debug(this.getClass(),
					"Traversing out edges of #0 for #1.",
					downstreamVertex.getTypeName(),
					nodeType);

				List<TSRelationElement> downstreamRelationElements = null;

				if (TSSharedUtils.isEmpty(path))
				{
					downstreamRelationElements =
						toRelationElements(downstreamVertex.asNode().outEdges());
				}
				else
				{
					if (pathIndex < path.size())
					{
						downstreamRelationElements =
							downstreamVertex.outRelationElements(path.get(pathIndex));
					}
				}

				if (!TSSharedUtils.isEmpty(downstreamRelationElements))
				{
					result = hasDownstreamTypeFrom(
						downstreamRelationElements,
						context,
						nodeType,
						path,
						pathIndex + 1);

					if (result != null)
					{
						break;
					}
				}
			}
		}

		return result;
	}


	/**
	 * Method return true if inputs have a upstream type.
	 * @param relationElements The relation elements.
	 * @param context The context.
	 * @param nodeType The node type.
	 * @param path The path to traverse.
	 * @param pathIndex The path index.
	 * @return True if has upstream type, false otherwise.
	 */
	protected Boolean hasUpstreamTypeFrom(
		List<TSRelationElement> relationElements,
		TSContextInterface context,
		String nodeType,
		List<String> path,
		int pathIndex)
	{
		List<TSVertexElement> vertexElements = new TSLinkedList<>();
		Boolean result = null;

		for (TSRelationElement rel : relationElements)
		{
			TSVertexElement target = rel.getSourceElement();

			if (nodeType.equals(target.getTypeName()))
			{
				result = Boolean.TRUE;
				break;
			}
			else
			{
				vertexElements.add(target);
			}
		}

		if (result == null)
		{
			TSVertexElement upstreamVertex;

			Iterator<TSVertexElement> upstreamVertexIter = vertexElements.iterator();

			while (upstreamVertexIter.hasNext())
			{
				upstreamVertex = upstreamVertexIter.next();

				TSLogger.debug(this.getClass(),
					"Traversing in edges of #0.",
					upstreamVertex.getTypeName());

				List<TSRelationElement> upstreadmRelationElements = null;

				if (TSSharedUtils.isEmpty(path))
				{
					upstreadmRelationElements =
						toRelationElements(upstreamVertex.asNode().inEdges());
				}
				else
				{
					if (pathIndex < path.size())
					{
						upstreadmRelationElements =
							upstreamVertex.inRelationElements(path.get(pathIndex));
					}
				}

				if (!TSSharedUtils.isEmpty(upstreadmRelationElements))
				{
					result = hasUpstreamTypeFrom(
						upstreadmRelationElements,
					context,
					nodeType,
						path,
						pathIndex + 1);

				if (result != null)
				{
					break;
				}
			}
		}
		}

		return result;
	}


	/**
	 * Gets the downstream node property.
	 * @param relationElements The edges to traverse.
	 * @param context Evaluation context.
	 * @param nodeType The target node type.
	 * @param returnExpression The expression to evaluate.
	 * @param path
	 * @param pathIndex
	 * @return The evaluated expression.
	 * @throws TSEvaluationException Thrown if there is an error.
	 */
	protected Object getDownstreamNodePropertyFrom(
		List<TSRelationElement> relationElements,
		TSContextInterface context,
		String nodeType,
		String returnExpression,
		List<String> path,
		int pathIndex) throws TSEvaluationException
	{
		List<TSVertexElement> vertexElements = new TSLinkedList<>();
		Object result = null;

		for (TSRelationElement rel : relationElements)
		{
			TSVertexElement target = rel.getTargetElement();

			if (nodeType.equals(target.getTypeName()))
			{
				TSLogger.debug(this.getClass(),
					"Evaluating #0 for #1.",
					target.getTypeName(),
					returnExpression);

				result = TSModelEvaluatorManager.getInstance().evaluate(
					returnExpression,
					target,
					context);

				break;
			}
			else
			{
				vertexElements.add(target);
			}
		}

		if (result == null)
		{
			TSVertexElement downstreamVertex;

			Iterator<TSVertexElement> downstreamVertexIter = vertexElements.iterator();

			while (downstreamVertexIter.hasNext())
			{
				downstreamVertex = downstreamVertexIter.next();

				TSLogger.debug(this.getClass(),
					"Traversing out edges of #0.",
					downstreamVertex.getTypeName());

				List<TSRelationElement> downstreamRelationElements = null;

				if (TSSharedUtils.isEmpty(path))
				{
					downstreamRelationElements =
						toRelationElements(downstreamVertex.asNode().outEdges());
				}
				else
				{
					if (pathIndex < path.size())
					{
						downstreamRelationElements =
							downstreamVertex.outRelationElements(path.get(pathIndex));
					}
				}

				if (!TSSharedUtils.isEmpty(downstreamRelationElements))
				{
				result = getDownstreamNodePropertyFrom(
					toRelationElements(downstreamVertex.asNode().outEdges()),
					context,
					nodeType,
						returnExpression,
						path,
						pathIndex + 1);

				if (result != null)
				{
					break;
				}
			}
		}
		}

		return result;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEvaluateArgument(String s, int i)
	{
		boolean rc;

		if ("edgesOf".equals(s) ||
			"outEdgesOf".equals(s) ||
			"inEdgesOf".equals(s))
		{
			rc = true;
		}
		else if ("getDownstreamNodeProperty".equals(s) ||
			"getUpstreamNodeProperty".equals(s))
		{
			rc = i == 2;
		}
		else if ("getDownstreamNodePropertyOf".equals(s) ||
			"getUpstreamNodePropertyOf".equals(s))
		{
			rc = i == 0 || i == 3;
		}
		else if ("hasUpstreamNodeType".equals(s) ||
			"hasDownstreamNodeType".equals(s))
		{
			rc = i == 1;
		}
		else
		{
			rc = false;
		}

		return rc;
	}


	/**
	 * Expects the attributed object to be of type TSVertexElement and arguments are
	 * array of TSRelation types. If no types are passed, it counts all incident edges.
	 * Otherwise allows you to count the number of one or more TSRelation types.
	 * @param direction Traversal direction.
	 * @param data The evaluator data.
	 * @return An Integer value.
	 * @throws TSEvaluationException Thrown if there is an error.
	 */
	public Integer degree(Direction direction, TSEvaluatorData data)
		throws TSEvaluationException
	{
		TSAttributedObject attributedObject = data.getAttributedObject();

		if (!(attributedObject instanceof TSVertexElement))
		{
			throw new TSEvaluationException("Not instance of TSVertexElement");
		}

		TSVertexElement vertexElement = (TSVertexElement) attributedObject;

		return getDegree(direction, data, vertexElement);
	}


	/**
	 * Expects the attributed object to be of type TSRelationElement and arguments are
	 * array of TSRelation types. If no types are passed, it counts all incident edges.
	 * Otherwise allows you to count the number of one or more TSRelation types.
	 * @param direction Traversal direction.
	 * @param data The evaluator data.
	 * @return An Integer value.
	 * @throws TSEvaluationException Thrown if there is an error.
	 */
	public Integer sourceNodeDegree(Direction direction, TSEvaluatorData data)
		throws TSEvaluationException
	{
		TSAttributedObject attributedObject = data.getAttributedObject();

		if (!(attributedObject instanceof TSRelationElement))
		{
			throw new TSEvaluationException("Not instance of TSRelationElement");
		}

		TSRelationElement relationElement = (TSRelationElement) attributedObject;

		return getDegree(direction, data, relationElement.getSourceElement());
	}


	/**
	 * Expects the attributed object to be of type TSRelationElement and arguments are
	 * array of TSRelation types. If no types are passed, it counts all incident edges.
	 * Otherwise allows you to count the number of one or more TSRelation types.
	 * @param direction Traversal direction.
	 * @param data The evaluator data.
	 * @return An Integer value.
	 * @throws TSEvaluationException Thrown if there is an error.
	 */
	public Integer targetNodeDegree(Direction direction, TSEvaluatorData data)
		throws TSEvaluationException
	{
		TSAttributedObject attributedObject = data.getAttributedObject();

		if (!(attributedObject instanceof TSRelationElement))
		{
			throw new TSEvaluationException("Not instance of TSRelationElement");
		}

		TSRelationElement relationElement = (TSRelationElement) attributedObject;

		return getDegree(direction, data, relationElement.getTargetElement());
	}


	/**
	 * This method evaluates to a list of edges.
	 * @param direction The direction of edges.
	 * @param data The evaluator data.
	 * @return The evaluated list.
	 * @throws TSEvaluationException thrown if an error occurs.
	 */
	public List<TSRelationElement> edges(Direction direction,
		TSEvaluatorData data) throws TSEvaluationException
	{
		TSAttributedObject attributedObject = data.getAttributedObject();

		return edgesOf(attributedObject, direction, data, data.getArguments());
	}


	/**
	 * This method evaluates to a list of vertices.
	 * @param direction The direction of edges.
	 * @param data The evaluator data.
	 * @return The evaluated list.
	 * @throws TSEvaluationException if an error occurs.
	 */
	public List<TSVertexElement> nodes(Direction direction,
		TSEvaluatorData data) throws TSEvaluationException
	{
		TSAttributedObject attributedObject = data.getAttributedObject();

		return nodesOf(attributedObject, direction, data, data.getArguments());
	}


	/**
	 * This method evaluates to a list of edges.
	 * @param attributedObject Evaluation context. Must be an instance of
	 * {@link TSVertexElement}.
	 * @param direction The direction of edges.
	 * @param data The evaluator data.
	 * @param args The arguments to process.
	 * @return The evaluated list.
	 * @throws {@link TSEvaluationException} if an error occurs.
	 */
	public List<TSRelationElement> edgesOf(TSAttributedObject attributedObject,
		Direction direction,
		TSEvaluatorData data,
		Object[] args) throws TSEvaluationException
	{
		if (!(attributedObject instanceof TSVertexElement))
		{
			throw new TSEvaluationException("Not instance of TSVertexElement");
		}

		TSVertexElement vertexElement = (TSVertexElement) attributedObject;

		List<TSRelationElement> result = new ArrayList<>();

		//Object[] args = data.getArguments();

		if (args.length == 0)
		{
			switch (direction)
			{
				case IN:
					result.addAll(
						TSLists.toArrayList(vertexElement.inRelationIterator()));
					break;
				case OUT:
					result.addAll(
						TSLists.toArrayList(vertexElement.outRelationIterator()));
					break;
				case BOTH:
					result.addAll(
						TSLists.toArrayList(vertexElement.inRelationIterator()));
					result.addAll(
						TSLists.toArrayList(vertexElement.outRelationIterator()));
					break;
				default:
					break;
			}
		}
		else
		{
			// Assume each argument is an out edge type of this vertex

			for (int i = 0; i < args.length; i++)
			{
				try
				{
					switch (direction)
					{
						case IN:
							result.addAll(
								vertexElement.inRelationElements(args[i].toString()));
							break;
						case OUT:
							result.addAll(
								vertexElement.outRelationElements(args[i].toString()));
							break;
						case BOTH:
							result.addAll(
								vertexElement.inRelationElements(args[i].toString()));
							result.addAll(
								vertexElement.outRelationElements(args[i].toString()));
							break;
						default:
							break;
					}
				}
				catch (Throwable th)
				{
					TSLogger.warn(this.getClass(),
						"Failed to calculate #1 edges for #0",
						args[i].toString(),
						direction.name());
				}
			}
		}

		return result;
	}


	/**
	 * This method evaluates to a list of edges.
	 * @param attributedObject Evaluation context. Must be an instance of
	 * {@link TSVertexElement}.
	 * @param direction The direction of edges.
	 * @param data The evaluator data.
	 * @param args The arguments to process.
	 * @return The evaluated list.
	 * @throws {@link TSEvaluationException} if an error occurs.
	 */
	public List<TSVertexElement> nodesOf(TSAttributedObject attributedObject,
		Direction direction,
		TSEvaluatorData data,
		Object[] args) throws TSEvaluationException
	{
		if (!(attributedObject instanceof TSVertexElement))
		{
			throw new TSEvaluationException("Not instance of TSVertexElement");
		}

		TSVertexElement vertexElement = (TSVertexElement) attributedObject;

		List<TSVertexElement> result = new ArrayList<>();

		//Object[] args = data.getArguments();

		if (args.length == 0)
		{
			switch (direction)
			{
				case IN:
					result.addAll(getOthers(vertexElement,
						vertexElement.inRelationIterator()));
					break;
				case OUT:
					result.addAll(
						getOthers(vertexElement,
							vertexElement.outRelationIterator()));
					break;
				case BOTH:
					result.addAll(
						getOthers(vertexElement, vertexElement.inRelationIterator()));
					result.addAll(
						getOthers(vertexElement, vertexElement.outRelationIterator()));
					break;
				default:
					break;
			}
		}
		else
		{
			// Assume each argument is an out edge type of this vertex

			for (int i = 0; i < args.length; i++)
			{
				try
				{
					switch (direction)
					{
						case IN:
							result.addAll(getOthers(vertexElement,
								vertexElement.inRelationElements(
									args[i].toString()).iterator()));
							break;
						case OUT:
							result.addAll(getOthers(vertexElement,
								vertexElement.outRelationElements(
									args[i].toString()).iterator()));
							break;
						case BOTH:
							result.addAll(getOthers(vertexElement,
								vertexElement.inRelationElements(
									args[i].toString()).iterator()));
							result.addAll(getOthers(vertexElement,
								vertexElement.outRelationElements(
									args[i].toString()).iterator()));
							break;
						default:
							break;
					}
				}
				catch (Throwable th)
				{
					TSLogger.warn(this.getClass(),
						"Failed to calculate #1 edges for #0",
						args[i].toString(),
						direction.name());
				}
			}
		}

		return result;
	}


	/**
	 * This method returns a collection of vertices that represent other nodes of edges.
	 * @param vertexElement The context to which other is applied.
	 * @param relationIterator The iterator of edges.
	 * @return The collection of 'other' nodes from the input edges.
	 */
	private Collection<? extends TSVertexElement> getOthers(
		TSVertexElement vertexElement,
		Iterator<TSRelationElement> relationIterator)
	{
		List<TSVertexElement> others = new TSArrayList<>();

		TSNode thisNode = vertexElement.asNode();
		TSNode otherNode;

		while (relationIterator.hasNext())
		{
			TSRelationElement relationElement = relationIterator.next();

			otherNode = relationElement.asEdge().getOtherNode(thisNode);

			if (otherNode instanceof TSVertexElement)
			{
				others.add((TSVertexElement) otherNode);
			}
			else
			{
				others.add((TSVertexElement) otherNode.getUserObject());
			}
		}

		return others;
	}


	/**
	 * Helper method used to get the degree of a vertex element.
	 * @param direction The direction of edges.
	 * @param data The evaluator data.
	 * @param vertexElement The vertex of interest.
	 * @return The degree for the input expression.
	 */
	protected Integer getDegree(Direction direction,
		TSEvaluatorData data,
		TSVertexElement vertexElement)
	{
		Object[] args = data.getArguments();

		// Initial value is zero.
		Integer result = 0;

		if (args.length == 0)
		{
			switch (direction)
			{
				case IN:
					result = vertexElement.inDegree();
					break;
				case OUT:
					result = vertexElement.outDegree();
					break;
				case BOTH:
					result = vertexElement.degree();
					break;
				default:
					break;
			}
		}
		else
		{
			// Assume each argument is an out edge type of this vertex

			for (int i = 0; i < args.length; i++)
			{
				try
				{
					switch (direction)
					{
						case IN:
							result += vertexElement.inRelationElements(
								args[i].toString()).size();
							break;
						case OUT:
							result += vertexElement.outRelationElements(
								args[i].toString()).size();
							break;
						case BOTH:
							result += vertexElement.inRelationElements(
								args[i].toString()).size();
							result += vertexElement.outRelationElements(
								args[i].toString()).size();
							break;
						default:
							break;
					}
				}
				catch (Throwable th)
				{
					TSLogger.warn(this.getClass(),
						"Failed to calculate the count of #1 edges for #0",
						args[i].toString(),
						direction.name());
				}
			}
		}

		return result;
	}


	/**
	 * This method shifts the arguments by one.
	 * @param arguments The initial arguments.
	 * @return The shifted arguments.
	 */
	protected Object[] shiftArgs(Object[] arguments)
	{
		Object[] shiftedArgs = new Object[arguments.length - 1];

		for (int i = 1; i < arguments.length; i++)
		{
			shiftedArgs[i - 1] = arguments[i];
		}

		return shiftedArgs;
	}


	/**
	 * This method converts the input {@link TSEdge} list to a {@link TSRelationElement}
	 * list.
	 *
	 * @param edges The input {@link TSEdge} list.
	 * @return The list of {@link TSRelationElement} instances.
	 */
	public static List<TSRelationElement> toRelationElements(List<TSEdge> edges)
	{
		if (TSSharedUtils.isEmpty(edges))
		{
			return Collections.emptyList();
		}
		else
		{
			List<TSRelationElement> relationElements =
				new ArrayList<>(edges.size());

			for (int i = 0; i < edges.size(); i++)
			{
				relationElements.add((TSRelationElement) edges.get(i).getUserObject());
			}

			return relationElements;
		}
	}



	/**
	 * Direction of traversal.
	 */
	public enum Direction
	{
		IN,
		OUT,
		BOTH
	}


	/**
	 * Java Serialization ID.
	 */
	private static final long serialVersionUID = -8422293880357164855L;
}

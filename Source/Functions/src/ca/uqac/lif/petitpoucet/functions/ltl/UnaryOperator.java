package ca.uqac.lif.petitpoucet.functions.ltl;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;

@SuppressWarnings("rawtypes")
public abstract class UnaryOperator extends UnaryFunction<List,Boolean>
{
	protected boolean m_startValue;
	
	public UnaryOperator(boolean start_value)
	{
		super(List.class, Boolean.class);
		m_startValue = start_value;
	}
	
	@Override
	public LtlQueryable evaluate(Object[] inputs, Object[] outputs, Context c)
	{
		List<Integer> positions = new ArrayList<Integer>();
		List<?> list = (List<?>) inputs[0];
		boolean value = m_startValue;
		int pos = 0;
		for (Object o : list)
		{
			boolean b = false;
			if (o instanceof Boolean)
			{
				b = (Boolean) o;
			}
			if (b == !m_startValue)
			{
				value = !m_startValue;
				positions.add(pos);
			}
			pos++;
		}
		outputs[0] = value;
		return new LtlQueryable(toString(), list.size(), positions);
	}
	
	public static class LtlQueryable extends FunctionQueryable
	{
		protected List<Integer> m_positions;
		
		protected int m_inputLength;
		
		public LtlQueryable(String reference, int input_length, List<Integer> positions)
		{
			super(reference, 1, 1);
			m_positions = positions;
			m_inputLength = input_length;
		}
		
		@Override
		public LtlQueryable duplicate(boolean with_state)
		{
			LtlQueryable ltlq = new LtlQueryable(m_reference, m_inputLength, m_positions);
			return ltlq;
		}
		
		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory)
		{
			if (!(q instanceof CausalityQuery))
			{
				return answerQueryDefault(q, output_nb, d, root, factory, Quality.EXACT);
			}
			if (m_positions.isEmpty())
			{
				return answerQueryDefault(q, output_nb, d, root, factory, Quality.EXACT);
			}
			TraceabilityNode or = factory.getOrNode();
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
			for (int p : m_positions)
			{
				ComposedDesignator cd = new ComposedDesignator(d, new NthElement(p), new NthInput(0));
				TraceabilityNode child = factory.getObjectNode(cd, this);
				or.addChild(child, Quality.EXACT);
				leaves.add(child);
			}
			root.addChild(or, Quality.EXACT);
			return leaves;
		}
		
		protected List<TraceabilityNode> answerQueryDefault(TraceabilityQuery q, int output_nb, Designator d,
				TraceabilityNode root, Tracer factory, Quality quality)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>();
			TraceabilityNode and = factory.getAndNode();
			for (int i = 0; i < m_inputLength; i++)
			{
				ComposedDesignator cd = new ComposedDesignator(d, new NthElement(i), new NthInput(0));
				TraceabilityNode child = factory.getObjectNode(cd, this);
				and.addChild(child, quality);
				leaves.add(child);
			}
			root.addChild(and, quality);
			return leaves;
		}
	}
}
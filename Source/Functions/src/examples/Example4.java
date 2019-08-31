package examples;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.circuit.functions.Add;
import ca.uqac.lif.petitpoucet.circuit.functions.ApplyToAll;
import ca.uqac.lif.petitpoucet.circuit.functions.ComposedFunction;
import ca.uqac.lif.petitpoucet.circuit.functions.Connector;
import ca.uqac.lif.petitpoucet.circuit.functions.Constant;
import ca.uqac.lif.petitpoucet.circuit.functions.Fork;
import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;
import ca.uqac.lif.petitpoucet.graph.Tracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

@SuppressWarnings("unused")
public class Example4
{
	public static void main(String[] args)
	{
		ComposedFunction comp = new ComposedFunction(1, 1).setName("Double");
		Fork fork = new Fork();
		Add add = new Add();
		Connector.connect(fork, 0, add, 0);
		Connector.connect(fork, 1, add, 1);
		comp.add(fork, add);
		comp.associateInput(0, fork, 0);
		comp.associateOutput(0, add, 0);
		Constant x = new Constant(Example2.createList(3, 1, 4, 1, 5, 9, 2));
		ApplyToAll ata = new ApplyToAll(comp);
		Connector.connect(x, ata);
		Object[] out = ata.evaluate();
		System.out.println(out[0]);
		Tracer tracer = new Tracer();
		ComposedDesignator cd = new ComposedDesignator(new NthElement(1), new NthOutput(0));
		ConcreteTraceabilityNode root = tracer.getTree(ProvenanceQuery.instance, cd, ata);
		TraceabilityNodeDotRenderer renderer = new TraceabilityNodeDotRenderer();
		String dot_code = renderer.render(root);
		System.out.println(dot_code);
	}
}
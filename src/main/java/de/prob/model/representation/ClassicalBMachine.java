package de.prob.model.representation;

import java.util.ArrayList;
import java.util.List;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.prob.model.StateSpace;

public class ClassicalBMachine extends AbstractModel {

	private final NodeIdAssignment astMapping;

	public ClassicalBMachine(final StateSpace statespace,
			final NodeIdAssignment nodeIdAssignment) {
		this.statespace = statespace;
		this.astMapping = nodeIdAssignment;
	}

	private String name;

	private final List<NamedEntity> variables = new ArrayList<NamedEntity>();
	private final List<NamedEntity> constants = new ArrayList<NamedEntity>();
	private final List<NamedEntity> invariant = new ArrayList<NamedEntity>();
	private final List<NamedEntity> assertions = new ArrayList<NamedEntity>();
	private final List<Operation> operations = new ArrayList<Operation>();

	public List<NamedEntity> getConstants() {
		return constants;
	}

	public List<NamedEntity> getVariables() {
		return variables;
	}

	public List<NamedEntity> getInvariant() {
		return invariant;
	}

	public List<NamedEntity> getAssertions() {
		return assertions;
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void addVariable(final NamedEntity v) {
		this.variables.add(v);
	}

	public void addConstant(final NamedEntity v) {
		this.constants.add(v);
	}

	public void addAssertion(final NamedEntity p) {
		this.assertions.add(p);
	}

	public void addInvariant(final NamedEntity p) {
		this.assertions.add(p);
	}

	public void addOperation(final Operation o) {
		this.operations.add(o);
	}

}

package de.prob.statespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.ProBException;
import de.prob.animator.IAnimator;
import de.prob.animator.command.EvaluateFormulasCommand;
import de.prob.animator.command.ExploreStateCommand;
import de.prob.animator.command.GetOperationByPredicateCommand;
import de.prob.animator.command.ICommand;
import de.prob.animator.domainobjects.ClassicalBEvalElement;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.OpInfo;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class StateSpace extends StateSpaceGraph implements IAnimator,
		DirectedGraph<String, String>, MultiGraph<String, String> {

	Logger logger = LoggerFactory.getLogger(StateSpace.class);

	private final IAnimator animator;
	private final HashSet<String> explored = new HashSet<String>();
	private final History history;
	private final HashMap<String, Operation> ops = new HashMap<String, Operation>();
	private final HashMap<String, HashMap<String, String>> variables = new HashMap<String, HashMap<String, String>>();
	private final HashMap<String, Boolean> invariantOk = new HashMap<String, Boolean>();
	private final HashMap<String, Boolean> timeoutOccured = new HashMap<String, Boolean>();
	private final HashMap<String, Set<String>> operationsWithTimeout = new HashMap<String, Set<String>>();

	private final List<String> formulas = new ArrayList<String>();
	private final List<IAnimationListener> animationListeners = new ArrayList<IAnimationListener>();
	private final List<IStateSpaceChangeListener> stateSpaceListeners = new ArrayList<IStateSpaceChangeListener>();

	private final Random randomGenerator;

	@Inject
	public StateSpace(final IAnimator animator,
			final DirectedSparseMultigraph<String, String> graph,
			final Random randomGenerator, final History history) {
		super(graph);
		this.animator = animator;
		this.randomGenerator = randomGenerator;
		this.history = history;
		addVertex("root");
	}

	/**
	 * Takes a state id and calculates the successor states, the invariant,
	 * timeout, etc.
	 * 
	 * @param stateId
	 * @throws ProBException
	 */
	public void explore(final String stateId) throws ProBException {
		ExploreStateCommand command = new ExploreStateCommand(stateId);
		animator.execute(command);
		explored.add(stateId);
		List<OpInfo> enabledOperations = command.getEnabledOperations();
		// (id,name,src,dest,args)
		for (OpInfo operations : enabledOperations) {
			Operation op = new Operation(operations.id, operations.name,
					operations.params);
			if (!containsEdge(op.getId())) {
				ops.put(operations.id, op);
				notifyStateSpaceChange(operations.id,
						containsVertex(operations.dest));
				addEdge(op.getId(), operations.src, operations.dest);
			}
		}

		variables.put(stateId, command.getVariables());
		invariantOk.put(stateId, command.isInvariantOk());
		timeoutOccured.put(stateId, command.isTimeoutOccured());
		operationsWithTimeout.put(stateId, command.getOperationsWithTimeout());
	}

	public void opFromPredicate(final String stateId, final String name,
			final String predicate, final int nrOfSolutions)
			throws ProBException {
		GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(
				stateId, name, predicate, nrOfSolutions);
		animator.execute(command);
		List<OpInfo> newOps = command.getOperations();
		// (id,name,src,dest,args)
		for (OpInfo operations : newOps) {
			Operation op = new Operation(operations.id, operations.name,
					operations.params);
			if (!containsEdge(op.getId())) {
				ops.put(operations.id, op);
				notifyStateSpaceChange(operations.id,
						containsVertex(operations.dest));
				addEdge(op.getId(), operations.src, operations.dest);
			}
		}
	}

	public Operation execOneOp(String opName, String predicate)
			throws ProBException {
		GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(
				getCurrentState(), opName, predicate, 1);
		animator.execute(command);
		OpInfo newOp = command.getOperations().get(0);
		Operation op = new Operation(newOp.id, newOp.name, newOp.params);
		if (!containsEdge(op.getId())) {
			ops.put(newOp.id, op);
			notifyStateSpaceChange(newOp.id, containsVertex(newOp.dest));
			addEdge(op.getId(), newOp.src, newOp.dest);
		}

		return op;
	}

	public void stepWithOp(String opName, String predicate)
			throws ProBException {
		Operation op = execOneOp(opName, predicate);
		step(op.getId());
	}

	public void goToState(final int id) throws ProBException {
		goToState(String.valueOf(id));
	}

	public void goToState(final String stateId) throws ProBException {
		if (!containsVertex(stateId))
			throw new IllegalArgumentException("state does not exist");
		if (!isExplored(stateId)) {
			try {
				explore(stateId);
			} catch (ProBException e) {
				logger.error("Could not explore state with StateId " + stateId);
				throw new ProBException();
			}
		}

		evaluateFormulas();
		notifyAnimationChange(getCurrentState(), stateId, null);
		history.add(stateId, null);
	}

	// step takes a operationId
	// If the opId is contained in the outgoing edges (it is enabled)
	// explore it (if not explored) and add state to history
	public void step(final String opId) throws ProBException {
		if (getOutEdges(getCurrentState()).contains(opId)) {
			String newState = getDest(opId);
			if (!isExplored(newState)) {
				try {
					explore(newState);
				} catch (ProBException e) {
					logger.error("Could not explore state with StateId "
							+ newState);
					throw new ProBException();
				}
			}
			history.add(newState, opId);
			evaluateFormulas();
			notifyAnimationChange(getSource(opId), getDest(opId), opId);
		}
	}

	public void back() {
		if (history.canGoBack()) {
			String oldState = getCurrentState();
			String opId = history.getCurrentTransition();

			history.back();
			evaluateFormulas();

			if (opId != null) {
				notifyAnimationChange(getDest(opId), getSource(opId), opId);
			} else {
				notifyAnimationChange(oldState, getCurrentState(), null);
			}
		}
	}

	public void forward() {
		if (history.canGoForward()) {
			String oldState = getCurrentState();

			history.forward();
			evaluateFormulas();

			String opId = history.getCurrentTransition();
			if (opId != null) {
				notifyAnimationChange(getSource(opId), getDest(opId), opId);
			} else {
				notifyAnimationChange(oldState, getCurrentState(), null);
			}
		}
	}

	public void step(final int i) throws ProBException {
		String opId = String.valueOf(i);
		step(opId);
	}

	public void explore(final int i) throws ProBException {
		String si = String.valueOf(i);
		explore(si);
	}

	@Override
	public void execute(final ICommand command) throws ProBException {
		animator.execute(command);
	}

	@Override
	public void execute(final ICommand... commands) throws ProBException {
		animator.execute(commands);
	}

	public String getCurrentState() {
		return history.getCurrentState();
	}

	public boolean isDeadlock(final String stateid) throws ProBException {
		if (!isExplored(stateid)) {
			explore(stateid);
		}
		return getOutEdges(stateid).isEmpty();
	}

	private boolean isExplored(final String stateid) {
		if (!containsVertex(stateid))
			throw new IllegalArgumentException("Unknown State id");
		return explored.contains(stateid);
	}

	@Override
	public boolean addEdge(final String opId, final String src,
			final String dest) {
		return addEdge(opId, src, dest, EdgeType.DIRECTED);
	}

	public HashMap<String, String> getState(final String stateId) {
		return variables.get(stateId);
	}

	public HashMap<String, String> getState(final int stateId) {
		String id = String.valueOf(stateId);
		return getState(id);
	}

	public HashMap<String, Boolean> getInvariantOk() {
		return invariantOk;
	}

	public HashMap<String, Boolean> getTimeoutOccured() {
		return timeoutOccured;
	}

	public HashMap<String, Set<String>> getOperationsWithTimeout() {
		return operationsWithTimeout;
	}

	public void registerAnimationListener(final IAnimationListener l) {
		animationListeners.add(l);
	}

	public void registerStateSpaceListener(final IStateSpaceChangeListener l) {
		stateSpaceListeners.add(l);
	}

	private void notifyAnimationChange(final String fromState,
			final String toState, final String withOp) {
		for (IAnimationListener listener : animationListeners) {
			listener.currentStateChanged(fromState, toState, withOp);
		}
	}

	private void notifyStateSpaceChange(final String opName,
			final boolean isDestStateNew) {
		for (IStateSpaceChangeListener listener : stateSpaceListeners) {
			listener.newTransition(opName, isDestStateNew);
		}
	}

	public String printOps() {
		StringBuilder sb = new StringBuilder();
		String current = getCurrentState();
		Collection<String> opIds = getOutEdges(current);
		sb.append("Operations: \n");
		for (String opId : opIds) {
			Operation op = ops.get(opId);
			sb.append("  " + op.getId() + ": " + op.toString() + "\n");
		}
		return sb.toString();
	}

	public String printState() {
		StringBuilder sb = new StringBuilder();
		sb.append("Current State Id: " + getCurrentState() + "\n");
		HashMap<String, String> currentState = variables.get(getCurrentState());
		// FIXME: Find a way to get the names of the variables so that they can
		// be retrieved from the map
		Set<Entry<String, String>> entrySet = currentState.entrySet();
		for (Entry<String, String> entry : entrySet) {
			sb.append(entry.getKey());
			sb.append(" -> ");
			sb.append(entry.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}

	public List<EvaluationResult> evaluate(final String... code)
			throws ProBException {
		return eval(getCurrentState(), code);
	}

	public List<EvaluationResult> eval(final String state, final String... code)
			throws ProBException {
		List<ClassicalBEvalElement> list = new ArrayList<ClassicalBEvalElement>(
				code.length);
		for (String c : code) {
			list.add(new ClassicalBEvalElement(c));
		}
		EvaluateFormulasCommand command = new EvaluateFormulasCommand(list,
				state);
		execute(command);

		return command.getValues();
	}

	public void randomAnim(final int steps) throws ProBException {
		if (steps <= 0)
			return;

		final String state = getCurrentState();

		boolean deadlock = true;
		try {
			deadlock = isDeadlock(state);
		} catch (ProBException e) {
			logger.error("Could not explore state with StateId " + state);
		}

		if (deadlock)
			return;

		final Collection<String> operations = getOutEdges(state);
		int size = operations.size();
		String[] op = operations.toArray(new String[size]);
		int thresh = randomGenerator.nextInt(size);
		String nextOp = op[thresh];

		final boolean invariantPreserved = invariantOk.get(state);

		if (!invariantPreserved)
			return;

		step(nextOp);

		randomAnim(steps - 1);
	}

	public void addUserFormula(final String formula) {
		formulas.add(formula);
		try {
			List<EvaluationResult> result = evaluate(formula);
			HashMap<String, String> varsAtState = variables
					.get(getCurrentState());
			for (EvaluationResult evaluationResult : result) {
				varsAtState.put(evaluationResult.code, evaluationResult.value);
			}
		} catch (ProBException e) {
			logger.error("Formula not added successfully", e);
		}
	}

	public void evaluateFormulas() {
		String[] array = formulas.toArray(new String[formulas.size()]);

		try {
			List<EvaluationResult> evaluate = evaluate(array);
			HashMap<String, String> varsAtCurrentState = variables
					.get(getCurrentState());
			for (EvaluationResult result : evaluate) {
				if (!varsAtCurrentState.containsKey(result.code)) {
					varsAtCurrentState.put(result.code, result.value);
				}
			}
		} catch (ProBException e) {
			logger.error("Could not evaluate user formulas for state "
					+ getCurrentState());
		}
	}

}

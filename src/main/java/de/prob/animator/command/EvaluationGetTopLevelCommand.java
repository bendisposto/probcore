/**
 * 
 */
package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * This command retrieves the IDs of the top-level expressions and their labels
 * and the IDs of their children.
 * 
 * @see EvaluationExpandCommand
 * @see EvaluationGetValuesCommand
 * @author plagge
 */
public class EvaluationGetTopLevelCommand implements ICommand {

	@Override
	public void writeCommand(IPrologTermOutput pto)
			throws de.prob.ProBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings)
			throws de.prob.ProBException {
		// TODO Auto-generated method stub
		
	}
//	FIXME REFACTOR FOR EvaluationElement
//	private static final String FIRST_EXPANSION_VARNAME = "FE";
//
//	public static EvaluationElement[] retrieveTopLevelElements()
//			throws ProBException {
//		final Animator animator = Animator.getAnimator();
//		final EvaluationGetTopLevelCommand cmd = new EvaluationGetTopLevelCommand(
//				animator);
//		animator.execute(cmd);
//		return cmd.getTopLevelElements();
//	}
//
//	private final Animator animator;
//	private EvaluationElement[] tops;
//
//	public EvaluationGetTopLevelCommand(final Animator animator) {
//		this.animator = animator;
//	}
//
//	public void processResult(
//			final ISimplifiedROMap<String, PrologTerm> bindings)
//			throws CommandException {
//		ListPrologTerm resultList = (ListPrologTerm) bindings
//				.get(FIRST_EXPANSION_VARNAME);
//		tops = new EvaluationElement[resultList.size()];
//		int i = 0;
//		for (final PrologTerm elemTerm : resultList) {
//			tops[i] = createElement(elemTerm);
//			i++;
//		}
//	}
//
//	private EvaluationElement createElement(final PrologTerm elemTerm)
//			throws CommandException {
//		final EvaluationElement top;
//		if (elemTerm.hasFunctor("top", 3)) {
//			final CompoundPrologTerm elem = (CompoundPrologTerm) elemTerm;
//			final PrologTerm id = elem.getArgument(1);
//			final String label = ((CompoundPrologTerm) elem.getArgument(2))
//					.getFunctor();
//			final List<PrologTerm> childrenIds = ((ListPrologTerm) elem
//					.getArgument(3));
//			top = new EvaluationElement(animator, id, label, childrenIds);
//		} else
//			throw new CommandException("ProB core sent unexpected term "
//					+ elemTerm);
//		return top;
//	}
//
//	public void writeCommand(final IPrologTermOutput pto) {
//		pto.openTerm("evaluation_get_top_level");
//		pto.printVariable(FIRST_EXPANSION_VARNAME);
//		pto.closeTerm();
//	}
//
//	public EvaluationElement[] getTopLevelElements() {
//		return tops;
//	}
}

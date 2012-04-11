package de.prob.animator.command;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.ProBException;
import de.prob.animator.domainobjects.ClassicalBEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class EvaluateRawExpressionsCommand implements ICommand {

	Logger logger = LoggerFactory
			.getLogger(EvaluateRawExpressionsCommand.class);

	private static final String EVALUATE_TERM_VARIABLE = "Val";
	private final List<ClassicalBEvalElement> evalElements;
	private final String stateId;
	private List<String> values;

	public EvaluateRawExpressionsCommand(
			final List<ClassicalBEvalElement> evalElements, final String id) {
		this.evalElements = evalElements;
		this.stateId = id;
	}

	public List<String> getValues() {
		return values;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		try {
			ListPrologTerm prologTerm = BindingGenerator.getList(bindings
					.get(EVALUATE_TERM_VARIABLE));
			values = PrologTerm.atomicStrings(prologTerm);
		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) throws ProBException {
		pout.openTerm("evaluate_raw_expressions");
		pout.printAtomOrNumber(stateId);
		pout.openList();

		// print parsed expressions/predicates
		try {
			for (ClassicalBEvalElement term : evalElements) {
				final ASTProlog prolog = new ASTProlog(pout, null);
				term.parse().apply(prolog);
			}
		} catch (BException e) {
			logger.error("Parse error", e);
			throw new ProBException();
		} finally {
			pout.closeList();
			pout.printVariable(EVALUATE_TERM_VARIABLE);
			pout.closeTerm();
		}
	}

}

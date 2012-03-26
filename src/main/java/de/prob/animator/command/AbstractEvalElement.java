/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.IAnimator;

public abstract class AbstractEvalElement {

	private String value = null;

	// private IAnimator animator;

	public abstract Start getPrologAst();

	public abstract boolean hasChildren();

	public boolean isAtomic() {
		return !hasChildren();
	};

	public abstract String getLabel();

	protected Start parse(final String prefix, final String code)
			throws BException {
		final BParser parser = new BParser();
		final Start modelAst = parser.parse(prefix + code, false);
		return modelAst;
	}

	public synchronized String getValue(final IAnimator animator,
			final String stateId) {
		// this.animator = animator;
		if (value == null) {
			value = evaluate(stateId);
		}
		return value;
	}

	private synchronized String evaluate(final String stateId) {
		// try {
		// FIXME: REFACTOR!!!
		// return EvaluateRawExpressionsCommand.evaluate(animator, this,
		// stateId);
		// } catch (ProBException e) {
		//
		// }
		return "unknown value";
	}
}

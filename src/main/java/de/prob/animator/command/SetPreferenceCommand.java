/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class SetPreferenceCommand implements ICommand {

	private final String key;
	private final String value;

	public SetPreferenceCommand(final String key, final String value) {
		this.key = key;
		this.value = value;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings){
		// no result
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("set_eclipse_preference").printAtom(key).printAtom(value)
				.closeTerm();
	}
}

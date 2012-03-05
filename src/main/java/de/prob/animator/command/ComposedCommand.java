package de.prob.animator.command;

/**
 * 
 */

import java.util.List;

import de.prob.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.PrologTermDelegate;
import de.prob.prolog.term.PrologTerm;

/**
 * A ComposedCommand contains several other commands and writes their query
 * strings in one pass to the ProB process. It ensures that no name clashes of
 * the variables of the several commands occur.
 * 
 * @author plagge
 * 
 */
public class ComposedCommand implements ICommand {
	private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	private final ICommand[] cmds;

	public ComposedCommand(final ICommand... cmds) {
		this.cmds = cmds;
	}

	public ComposedCommand(final List<ICommand> cmds) {
		this.cmds = cmds.toArray(new ICommand[cmds.size()]);
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		final PrefixMap<PrologTerm> prefixMap = new PrefixMap<PrologTerm>(
				bindings);
		for (int i = 0; i < cmds.length; i++) {
			processPrefixedCommand(prefixMap, i);
		}
	}

	private void processPrefixedCommand(final PrefixMap<PrologTerm> prefixMap,
			final int i) throws ProBException {
		prefixMap.prefix = createPrefix(i);
		cmds[i].processResult(prefixMap);
	}

	@Override
	public void writeCommand(final IPrologTermOutput orig) throws ProBException {
		PrologPrefixVarOutput pto = new PrologPrefixVarOutput(orig);
		for (int i = 0; i < cmds.length; i++) {
			writePrefixedCommand(pto, i);
		}
	}

	private void writePrefixedCommand(final PrologPrefixVarOutput pto,
			final int i) throws ProBException {
		pto.prefix = createPrefix(i);
		cmds[i].writeCommand(pto);
	}

	public String createPrefix(final int i) {
		if (i < LETTERS.length)
			return String.valueOf(LETTERS[i]);
		else {
			final int letternum = i % LETTERS.length;
			final int number = i / LETTERS.length;
			return String.valueOf(LETTERS[letternum]) + number;
		}
	}

	public void getResultForCommand(final ICommand command,
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		final int index = indexOf(command);
		if (index >= 0) {
			final PrefixMap<PrologTerm> prefixMap = new PrefixMap<PrologTerm>(
					bindings);
			processPrefixedCommand(prefixMap, index);
		} else
			throw new IllegalArgumentException(
					"cannot reprocess command, command unknown");
	}

	private int indexOf(final ICommand command) {
		int index;
		for (index = 0; index < cmds.length; index++) {
			if (cmds[index].equals(command)) {
				break;
			}
		}
		return index;
	}

	/**
	 * This PrologTermDelegate prefixes every variable with a given string.
	 */
	private static final class PrologPrefixVarOutput extends PrologTermDelegate {
		private String prefix;

		public PrologPrefixVarOutput(final IPrologTermOutput pto) {
			super(pto);
		}

		@Override
		public IPrologTermOutput printVariable(final String var) {
			pto.printVariable(prefix == null ? var : prefix + var);
			return this;
		}

		@Override
		public IPrologTermOutput fullstop() {
			// ignore the fullstop
			return this;
		}
	}

	/**
	 * This simplified map prefixes every query to the map with a given string.
	 */
	private static final class PrefixMap<V> implements
			ISimplifiedROMap<String, V> {
		private final ISimplifiedROMap<String, V> map;
		private String prefix;

		public PrefixMap(final ISimplifiedROMap<String, V> map) {
			this.map = map;
		}

		@Override
		public V get(final String key) {
			return map.get(prefix == null ? key : prefix + key);
		}

		@Override
		public String toString() {
			return map.toString();
		}

	}
}

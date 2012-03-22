/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

//import java.util.Collection;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.eventb.core.IContextRoot;
//import org.eventb.core.IEventBRoot;
//import org.eventb.core.IMachineRoot;
//import org.osgi.service.prefs.BackingStoreException;
//import org.osgi.service.prefs.Preferences;
//import org.rodinp.core.RodinDBException;
//
//import de.prob.core.Animator;
//import de.prob.core.LanguageDependendAnimationPart;
//import de.prob.core.domainobjects.MachineDescription;
//import de.prob.core.domainobjects.Operation;
//import de.prob.core.domainobjects.ProBPreference;
//import de.prob.core.domainobjects.State;
//import de.prob.core.langdep.EventBAnimatorPart;
//import de.prob.core.translator.TranslationFailedException;
//import de.prob.eventb.translator.TranslatorFactory;
//import de.prob.exceptions.ProBException;
//import de.prob.logging.Logger;
//import de.prob.parser.ISimplifiedROMap;
//import de.prob.prolog.output.IPrologTermOutput;
//import de.prob.prolog.output.StructuredPrologOutput;
//import de.prob.prolog.term.PrologTerm;

/**
 * Command to load a new Event B Model for animation.
 */
public final class LoadEventBModelCommand {
//	//FIXME: TO IMPLEMENT!!!!
//	private static boolean preferencesAlreadyCleanedUp = false;
//
//	private LoadEventBModelCommand() {
//		throw new UnsupportedOperationException("Do not instantiate this class");
//	}
//
//	private LoadEventBModelCommand(final IEventBRoot model) {
//	}
//
//	private static boolean checkForContexts(final IEventBRoot model) {
//		if (model instanceof IContextRoot) {
//			return true;
//		}
//		try {
//			if (model instanceof IMachineRoot) {
//				IMachineRoot r = (IMachineRoot) model;
//				return r.getSeesClauses().length != 0;
//			}
//		} catch (RodinDBException e) {
//			// ignore
//		}
//		return false;
//	}
//
//	public static PrologTerm toPrologTerm(IEventBRoot model)
//			throws CommandException {
//		StructuredPrologOutput out = new StructuredPrologOutput();
//
//		final InternalLoadCommand load = new InternalLoadCommand(model);
//		load.writeCommand(out);
//		out.fullstop();
//		return out.getSentences().iterator().next();
//	}
//
//	public static void load(final Animator animator, final IEventBRoot model)
//			throws ProBException {
//		boolean context = checkForContexts(model);
//		animator.resetDirty();
//		removeObsoletePreferences(animator);
//
//		final LanguageDependendAnimationPart ldp = new EventBAnimatorPart(model);
//
//		final ClearMachineCommand clear = new ClearMachineCommand();
//		final SetPreferencesCommand setPrefs = SetPreferencesCommand
//				.createSetPreferencesCommand(animator);
//		final InternalLoadCommand load = new InternalLoadCommand(model);
//		final StartAnimationCommand start = new StartAnimationCommand();
//		final SetMachineObjectsCommand getMObjects = new SetMachineObjectsCommand(
//				animator, ldp);
//		final ExploreStateCommand explore = new ExploreStateCommand("root");
//
//		final ComposedCommand composed = new ComposedCommand(clear, setPrefs,
//				load, start, getMObjects, explore);
//
//		animator.execute(composed);
//
//		final State commandResult = explore.getState();
//		animator.announceCurrentStateChanged(commandResult,
//				Operation.NULL_OPERATION);
//
//		if (commandResult.isTimeoutOccured() && context) {
//			final String message = "A timeout occured when finding constants. Typically this means, that your axioms are too complicated for automatical solving. You might create an animation refinement using the context menu to help ProB finding a solution";
//			Logger.notifyUserWithoutBugreport(message);
//		}
//
//	}
//
//	private static void removeObsoletePreferences(final Animator animator)
//			throws ProBException {
//		if (!preferencesAlreadyCleanedUp) {
//			// get all preference names from ProB
//			Collection<ProBPreference> prefs = GetPreferencesCommand
//					.getPreferences(animator);
//			Set<String> probPrefNames = new HashSet<String>();
//			for (ProBPreference probpref : prefs) {
//				probPrefNames.add(probpref.name);
//			}
//			// now check all stored (in Eclipse's store) preferences
//			// if they still exist
//			Preferences preferences = SetPreferencesCommand.getPreferences();
//			try {
//				boolean foundObsoletePreference = false;
//				for (String prefname : preferences.keys()) {
//					if (!probPrefNames.contains(prefname)) {
//						// preference does not exists anymore
//						preferences.remove(prefname);
//						foundObsoletePreference = true;
//						String message = "removed obsolete preference from preferences store: "
//								+ prefname;
//						Logger.info(message);
//					}
//				}
//				if (foundObsoletePreference) {
//					preferences.flush();
//				}
//			} catch (BackingStoreException e) {
//				Logger.notifyUser("Error while accessing ProB Preferences", e);
//			}
//			preferencesAlreadyCleanedUp = true;
//		}
//	}
//
//	private static class SetMachineObjectsCommand extends
//			GetMachineObjectsCommand {
//		private final Animator animator;
//		private final LanguageDependendAnimationPart ldp;
//
//		public SetMachineObjectsCommand(final Animator animator,
//				final LanguageDependendAnimationPart ldp) {
//			super();
//			this.animator = animator;
//			this.ldp = ldp;
//		}
//
//		@Override
//		public void processResult(
//				final ISimplifiedROMap<String, PrologTerm> bindings)
//				throws CommandException {
//			super.processResult(bindings);
//			animator.setMachineDescription(new MachineDescription(getResult()));
//			animator.setLanguageDependendPart(ldp);
//			animator.announceReset();
//		}
//	}
//
//	private static class InternalLoadCommand implements IComposableCommand {
//		private final IEventBRoot model;
//
//		public InternalLoadCommand(final IEventBRoot model) {
//			this.model = model;
//		}
//
//		@Override
//		public void writeCommand(final IPrologTermOutput pto)
//				throws CommandException {
//			try {
//				TranslatorFactory.translate(model, pto);
//			} catch (TranslationFailedException e) {
//				throw new CommandException(
//						"Translation from Event-B to ProB's internal representation failed",
//						e);
//			}
//		}
//
//		@Override
//		public void processResult(
//				final ISimplifiedROMap<String, PrologTerm> bindings)
//				throws CommandException {
//			// there are no results to process
//		}
//
//	}
}

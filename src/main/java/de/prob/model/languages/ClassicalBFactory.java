package de.prob.model.languages;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.ProBException;
import de.prob.animator.command.GetInvariantsCommand;
import de.prob.animator.command.ICommand;
import de.prob.animator.command.LoadBProjectCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.StateSpace;

public class ClassicalBFactory {

	private final Provider<StateSpace> statespaceProvider;
	private final Logger logger = LoggerFactory
			.getLogger(ClassicalBFactory.class);

	@Inject
	public ClassicalBFactory(final Provider<StateSpace> statespaceProvider) {
		this.statespaceProvider = statespaceProvider;
	}

	public ClassicalBMachine load(final File f) throws ProBException {
		ICommand loadCommand = new LoadBProjectCommand(f, "scheduler");
		return load(loadCommand);
	}

	public ClassicalBMachine load(final String s) throws ProBException {
		ICommand loadCommand = new LoadBProjectFromStringCommand(s, "scheduler");
		return load(loadCommand);
	}

	public ClassicalBMachine load(final ICommand loadCommand)
			throws ProBException {
		StateSpace stateSpace = statespaceProvider.get();
		ClassicalBMachine classicalBMachine = new ClassicalBMachine(stateSpace);

		GetInvariantsCommand getInvariantsCommand = new GetInvariantsCommand();
		stateSpace.execute(loadCommand, new StartAnimationCommand(),
				getInvariantsCommand);

		classicalBMachine.setInvariant(new Predicate(getInvariantsCommand
				.getInvariant()));

		return classicalBMachine;

	}

}

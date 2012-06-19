s = api.s()
s.explore "root"
s.step 0
s.step 4
s.step 6
println 'hier'
a = s.getCurrentState()
println 'daaaaa'
assert a == s.states.get("4")
assert a.class == de.prob.statespace.StateId
assert !s.info.stateHasVariable(a,"waiting\\/ready")
s.addUserFormula("waiting\\/ready")
assert s.info.stateHasVariable(s.getCurrentState(),"waiting\\/ready")
assert s.info.getVariable(s.getCurrentState(),"waiting\\/ready")=="{PID1,PID3}"
assert s.formulas.size() == 1
s.goToState(3)
assert s.info.stateHasVariable(s.states.get("3"),"waiting\\/ready")
assert s.info.getVariable(s.getCurrentState(),"waiting\\/ready")=="{PID3}"
s.back()
s.back()
s.back()
assert s.getCurrentState() == s.states.get("0")
assert s.info.stateHasVariable(s.getCurrentState(),"waiting\\/ready")
assert s.info.getVariable(s.states.get("0"),"waiting\\/ready")=="{}"
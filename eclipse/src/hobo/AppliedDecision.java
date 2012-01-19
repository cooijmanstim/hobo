package hobo;

// carries information for undoing a decision
public abstract class AppliedDecision {
	protected Decision decision;
	protected State state; // state to which we were applied
	protected MersenneTwisterFast old_random; // random state before
	protected int old_player; // current player before
	
	public AppliedDecision(Decision decision, State state) {
		this.decision = decision;
		this.state = state;
		this.old_random = state.random.clone();
		this.old_player = state.currentPlayer();
	}
	
	public void undo() {
		state.random = old_random.clone();
		state.switchToPlayer(old_player);
	}
}

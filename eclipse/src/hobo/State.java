package hobo;

import java.util.*;

public class State implements Cloneable {
	private final LinkedList<PlayerState> players = new LinkedList<PlayerState>();
	
	public final int size = 3;
	private final String[][] board = new String[size][size];

	private boolean gameOver = false;
	private List<int[]> winningPosition;

	public State() {}
	
	public State(List<String> player_names) {
		for (String pn: player_names)
			players.add(new PlayerState(pn));
	}
	
	public State clone() {
		State that = new State();
		for (PlayerState p: this.players)
			that.players.add(p.clone());
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				that.board[x][y] = this.board[x][y];
		that.gameOver = this.gameOver;
		that.winningPosition = this.winningPosition;
		return that;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				sb.append(board[x][y] != null ? board[x][y] : ".");
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public void applyDecision(Decision d) {
		assert(isLegal(d));
		board[d.x][d.y] = currentPlayer();
		detectGameOver(d);
	}
	
	public boolean isLegal(Decision d) {
		return d != null && withinBounds(d.x, d.y) && board[d.x][d.y] == null;
	}
	
	// if the last decision completed a row of three, set winningPosition
	// to the coordinates denoting the row.
	private void detectGameOver(Decision d) {
		boolean gameOver = true;
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				if (board[x][y] == null)
					gameOver = false;

		List<int[]> horizontal = new ArrayList<int[]>(),
					vertical   = new ArrayList<int[]>(),
					diagonal1  = new ArrayList<int[]>(),
					diagonal2  = new ArrayList<int[]>();
		for (int i = 0; i < size; i++) {
			if (horizontal != null && board[i][d.y]      == board[d.x][d.y])
				horizontal.add(new int[]{ i, d.y });
			else
				horizontal = null;
			if (vertical   != null && board[d.x][i]      == board[d.x][d.y])
				vertical.add(new int[]{ d.x, i });
			else
				vertical = null;
			if (diagonal1  != null && board[i][i]        == board[d.x][d.y] && d.x == d.y)
				diagonal1.add(new int[]{ i, i });
			else
				diagonal1 = null;
			if (diagonal2  != null && board[i][size-i-1] == board[d.x][d.y] && d.x == size-d.y-1)
				diagonal2.add(new int[]{ i, size-i-1 });
			else
				diagonal2 = null;
		}
		
		winningPosition = horizontal != null ? horizontal :
			              vertical   != null ? vertical   :
			              diagonal1  != null ? diagonal1  :
			              diagonal2  != null ? diagonal2  : null;
		if (winningPosition != null)
			gameOver = true;
		
		this.gameOver = gameOver;
	}
	
	public boolean gameOver() {
		return gameOver;
	}
	
	public boolean isDraw() {
		return gameOver && winningPosition == null;
	}
	
	public void switchTurns() {
		Collections.rotate(players, 1);
	}
	
	public String currentPlayer() {
		return players.getFirst().name;
	}
	
	public String winner() {
		int[] xy = winningPosition.get(0);
		return board[xy[0]][xy[1]];
	}
	
	public String symbolAt(int x, int y) {
		return withinBounds(x, y) ? board[x][y] : null;
	}

	public boolean withinBounds(int x, int y) {
		return 0 <= x && x < size && 0 <= y && y < size;
	}
}

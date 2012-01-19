package hobo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class TestingEnviroment {

	FileHandler fileHandler;
	
	public TestingEnviroment() {
		Game g = null;
		fileHandler = new FileHandler("src/fileOutput/test.txt");
		PlayerState[] players = new PlayerState[2];
		int[] winCounter = new int[2];
		for (int i = 0; i < 100; i++) {
			g = new Game("",
//			         MonteCarloPlayer.fromConfiguration("montecarlo name:carlo  decision_time:5"),
//			         MinimaxPlayer.fromConfiguration("minimax    name:joshua decision_time:5"));
					RandomPlayer.fromConfiguration("name:igor"), RandomPlayer.fromConfiguration("name:ivan"));
			g.play();
			if(players[0] == null && players[1] == null) {players[0] = g.getState().playerState(0); players[1] = g.getState().playerState(1);}
			if(g.whoWon().name.equals(players[0].name))
				winCounter[0]++;
			else
				winCounter[1]++;
		}
		fileHandler.writeFile(winCounter[0]+" : "+winCounter[1]);
	}
	
	public static void main(String[] args) {
		new TestingEnviroment();
	}
}

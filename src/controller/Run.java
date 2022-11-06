package controller;

import view.UserInterface;

public class Run {
	GameController g;
	UserInterface ui;
	
	public Run() {
		g = new GameController();
		ui = new UserInterface(g);
	}
	
	public static void main(String[] args) {
		Run Solitaire = new Run();
	}
	
}

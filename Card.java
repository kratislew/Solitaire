package model;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class Card extends JPanel {

	//Initialize class variables
	private Suit suit;
	private int value;
	private Boolean showBack;
	private BufferedImage cardFront;
	private BufferedImage cardBack;
	
	//create suit enumeration which will be used to create and 
	//determine suits as well as which suits are red and which are black.
	public enum Suit{
		Hearts(1, true),
		Diamonds(2, true),
		Spades(3, false),
		Clubs(4, false);
		
		private int value;
		private Boolean red;
		
		Suit(int v, Boolean r){
			v = value;
			r = red;
		}
	};
	
	//this will transform high cards into their respective 
	//values as a string (A, J, K, Q), as well as turn the 
	//lower cards into strings
	public String cardAsString(int v) {
		if(v == 1) {return "A";}
		if(v == 11) {return "J";}
		if(v == 12) {return "Q";}
		if(v == 13) {return "K";}
		return Integer.toString(v);
	}
	
	//this will transform the high card string values into ints
	//and transform low card values back into ints
	public int cardAsInt(String v) {
		if(v.equals("A")) {return 1;}
		if(v.equals("J")) {return 11;}
		if(v.equals("Q")) {return 12;}
		if(v.equals("K")) {return 13;}
		return Integer.parseInt(v);
	}
	
	//constructor call
	public Card (Suit s, int v)
	{
		suit = s;
		value = v;
		showBack = false;
	}

	//this will show the back of the card
	public void displayBack() {
		showBack = true;
	}
	
	//this will show the face of the card
	public void displayFront( ) {
		showBack = false;
	}
	
}

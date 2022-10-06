package model;

import javax.swing.JPanel;


public class Card extends JPanel {

	private Suit suit;
	private int value;
	private Boolean showBack;
	
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
	
	public String cardAsString(int v) {
		if(v == 1) {return "A";}
		if(v == 11) {return "J";}
		if(v == 12) {return "Q";}
		if(v == 13) {return "K";}
		return Integer.toString(v);
	}
	
	public int cardAsInt(String v) {
		if(v.equals("A")) {return 1;}
		if(v.equals("J")) {return 11;}
		if(v.equals("Q")) {return 12;}
		if(v.equals("K")) {return 13;}
		return Integer.parseInt(v);
	}
	
	public Card (Suit s, int v)
	{
		suit = s;
		value = v;
	}
	
	
	
}

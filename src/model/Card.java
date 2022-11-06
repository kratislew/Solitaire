package model;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Graphics;


public class Card extends JPanel {

	//Initialize class variables
	public Suit suit;
	public int value;
	public boolean showBack;
	private BufferedImage cardFront;
	private BufferedImage cardBack;
	Point cardOffset;
	public boolean hasBeenMoved; 
	
	//create suit enumeration which will be used to create and 
	//determine suits as well as which suits are red and which are black.
	public enum Suit{
		hearts(1, true),
		diamonds(2, true),
		spades(3, false),
		clubs(4, false);
		
		public int value;
		public boolean red;
		
		Suit(int v, boolean r){
			value = v;
			red = r;
		}
	};
	
	//this will transform high cards into their respective 
	//values as a string (A, J, K, Q), as well as turn the 
	//lower cards into strings
	public String cardAsString(int v) {
		if(v == 1) {return "ace";}
		if(v == 11) {return "jack";}
		if(v == 12) {return "queen";}
		if(v == 13) {return "king";}
		return Integer.toString(v);
	}
	
	//this will transform the high card string values into ints
	//and transform low card values back into ints
	public int cardAsInt(String v) {
		if(v.equals("ace")) {return 1;}
		if(v.equals("jack")) {return 11;}
		if(v.equals("queen")) {return 12;}
		if(v.equals("king")) {return 13;}
		return Integer.parseInt(v);
	}
	
	//toString to allow for ease of finding card image, eg: returns "KD" for king of diamonds.
	public String toString() {
		return cardAsString(value) + "_of_" + suit.name();
	}
	
	//constructor call
	public Card (Suit s, int v)
	{
		suit = s;
		value = v;
		showBack = false;
		hasBeenMoved = false;
		
		try {
			//get image of current card face
			//URL url = getClass().getResource("../images/cards/" + this.toString() + ".png");
			cardFront = ImageIO.read(new FileInputStream("images/cards/" + this.toString() + ".png"));
			
			//get image of card back
			//url = getClass().getResource("../images/cards/1B.png");
			cardBack = ImageIO.read(new FileInputStream("images/cards/1B.png"));
		
			setBounds(0, 0, cardFront.getWidth(), cardFront.getHeight());
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		
		//set initial position and size of card
		cardOffset = new Point(0, 0);
		setSize(new Dimension(100, 145));
		setOpaque(false);
		
	}

	//this will show the back of the card
	public void displayBack() {
		showBack = true;
	}
	
	//this will show the face of the card
	public void displayFront( ) {
		showBack = false;
	}
	
	public void setMoved() {
		hasBeenMoved = true;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		BufferedImage displayCard = cardFront;
		if(showBack) {
			displayCard = cardBack;
		}
		
		g.drawImage(displayCard, 0, 0, this.getWidth(), this.getHeight(), null);
	}
}
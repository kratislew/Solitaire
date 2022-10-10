package model;

import java.util.ArrayList;
import java.util.Random;

import model.Card.Suit;

public class Deck 
{
	//create arraylist
	ArrayList<Card> deck;
	
	public Deck() 
	{
		//initialize arraylist
		deck = new ArrayList<Card>();
		
		//fill ArrayList with 52 cards of appropriate suit and value
		for (Suit suit : Suit.values()) 
		{
			for(int cardNum = 1; cardNum <= 13; cardNum++)
			{
				deck.add(new Card(suit, cardNum));
			}
		}
	}
	
	//shuffle method will perform a shuffle of the entire deck 10 times.
	//this is done by going through the entire deck and assigning each position
	//a new randomized position. This is performed 10 times to ensure the deck
	//is suitably shuffled without reducing performance.
	
	public void shuffle()
	{
		Random rnd = new Random();
		
		for (int i = 0; i < 10; i++) 
		{
			for (int j = 0; j < deck.size(); j++)
			{
				//this will switch the position of the card at position j 
				//with the randomized position rand.
				int rand = rnd.nextInt(deck.size());
				Card temp = deck.get(j);
				deck.set(j, deck.get(rand));
				deck.set(rand, temp);
			}
		}
	}
	
	//this will be used to draw cards from the deck
	//both for dealing and cycling through draw pile.
	public Card draw() {
		Card drawn = deck.get(0);
		deck.remove(0);
		
		return drawn;
	}
	
	//returns deck size
	public int deckSize() {
		return deck.size();
	}
}

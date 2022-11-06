package controller;


import java.util.ArrayList;

import javax.swing.JLabel;

import model.CardPile;
import model.CardPile.PileType;
import model.Deck;
import model.Card;
import model.Card.Suit;

//This class will contol all objects within the game and create functionality.
public class GameController {
	public ArrayList<CardPile> tableauPiles;
	public ArrayList<CardPile> foundationPiles;
	public CardPile talonPile;
	public CardPile stockPile;
	ArrayList<CardPile> totalPiles;
	public final int numPiles = 7;
	public Deck deck;
	
	//Default Constructor
	public GameController() {
		reset();
	}
	
	//reset game
	public void reset() {
		
		//create new deck and shuffle
		deck = new Deck();
		deck.shuffle();
		
		//set new piles with respective widths
		stockPile = new CardPile(120);
		stockPile.setDefaultOffset(0);
		
		talonPile = new CardPile(180);
		talonPile.setDefaultOffset(0);
		
		foundationPiles = new ArrayList<CardPile>();
		tableauPiles = new ArrayList<CardPile>();
		
		totalPiles = new ArrayList<CardPile>();
		totalPiles.add(stockPile);
		totalPiles.add(talonPile);	
		
	}
	
	//setup game
	public void setup() {
		//create initial piles
		stockPile.typeOfPile = PileType.Stockpile;
		talonPile.typeOfPile = PileType.Talon;
		
		for(int i = 1; i <= numPiles; i++) {
			CardPile p = new CardPile(120);
			
			//add i num of cards to respective tableau pile
			for(int j = 1; j <= i; j++) {
				Card drawCard = deck.draw();
				p.addCard(drawCard);

				//only display last card on the pile
				if(j != i) {
					drawCard.displayBack();
				}
				else {
					drawCard.displayFront();
				}
			}
			
			tableauPiles.add(p);
			totalPiles.add(p);
		}
		
		//set up foundation pile for each suit
		for(Suit s : Suit.values()) {
			CardPile p = new CardPile(100);
			p.setDefaultOffset(0);
			p.typeOfPile = PileType.Foundation;
			foundationPiles.add(p);
			totalPiles.add(p);
		}
		
		//draw card to talon pile from deck (initial stockpile)
		while(deck.deckSize() > 0) {
			Card c = deck.draw();
			c.displayBack();
			talonPile.addCard(c);
		}
	}
	
	//this method will draw from stockpile into talon pile
	public void drawCard() {
		if(!stockPile.cards.isEmpty()) {
			Card drawCard = stockPile.drawCard();
			drawCard.displayFront();
			talonPile.addCard(drawCard);
		}
	}
	
	//this method will flip over a Tableau card that has not yet been flipped
	public void flipCard(CardPile p) {
		if(!p.cards.isEmpty()) {
			Card c = p.cards.get(p.cards.size()-1);
			if(c.showBack) {
				c.displayFront();
			}
		}
	}
	
	//This method will allow for stockpile to reset when all cards have been moved to the talon pile
	public void resetStock() {
		if(!stockPile.cards.isEmpty()) return;
		
		while(!talonPile.cards.isEmpty()) {
			Card c = talonPile.drawCard();
			c.displayBack();
			
			stockPile.addCard(c);
		}
	}
	
	
	//this method ensures win condition has been met
	public boolean winCon() {
		for(CardPile p : foundationPiles) {
			if(p.cards.size() != 13) {
				return false;
			}
		}
		return true;
	}
}

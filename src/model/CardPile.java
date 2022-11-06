package model;

import java.awt.Dimension;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JLayeredPane;

import model.Card.Suit;

public class CardPile extends JLayeredPane {
	
	//Bottom card is the card image when no other cards are on pile
	Card bottom;
	
	//ArrayList for pile
	public ArrayList<Card> cards;
	
	//width and offset allows for pile to visually expand as more cards are added
	int width;
	int defaultOffset = 20;
	
	//suit checker to ensure that card moves are valid
	Suit suitCheck;
	
	//home pile for splits
	public CardPile topPile;
	
	public PileType typeOfPile;
	
	//Four different types of piles based on rules
	public enum PileType {Tableau, Stockpile, Talon, Foundation};
	
	//Scoring values for card movements
	final int scoreFoundation = 10;
	final int scoreStockToTableau = 5;
	final int scoreFirstMovement = 3;
	final int scoreFromFoundation = -15;
	
	//default constructor
	public CardPile(int w) {
		cards = new ArrayList<Card>();
		width = w;
		
		//create backing card for when pile empty
		bottom = new Card(Suit.spades, 20);
		add(bottom, 1, 0);
		
		//default to Tableau pile
		typeOfPile = PileType.Tableau;
	}
	
	//updates visual of pile based on num of cards in pile
	public void updatePileSize() {
		int h = bottom.getSize().height;
		
		if(!cards.isEmpty()) {
			h += defaultOffset * (cards.size() - 1);
		}
		
		this.setPreferredSize(new Dimension(width, h));
		this.setSize(width, h);
	}
	
	//updates defaultOffset
	public void setDefaultOffset(int o) {
		defaultOffset = o;
		updatePileSize();
	}
	
	//update width 
	public void setWidth(int w) {
		width = w;
		updatePileSize();
	}
	
	// check if pile is empty
	public boolean isEmpty() {
		return cards.size() == 0;
	}
	
	// add card to pile with no check
	public void addCard(Card c) {
		c.setLocation(0, defaultOffset * cards.size());
		cards.add(c);
		
		this.add(c, 1, 0);
		updatePileSize();
	}
	
	//remove card without check
	public void removeCard(Card c) {
		cards.remove(c);
		this.remove(c);
		
		updatePileSize();
	}
	
	//return top card without removing
	public Card seeTopCard() {
		return  cards.get(cards.size() - 1);
	}
	
	//draw card from pile - removes drawn card from pile
	public Card drawCard() {
		Card c = cards.get(0);
		removeCard(c);
		return c;
	}
	
	//split pile (use for moving > 1 card from one pile to another)
	
	public CardPile splitPile(Card base) {
		CardPile newPile = new CardPile(100);
		
		//looks for selected card
		for(int i = 0; i < cards.size(); i++) {
			if(cards.get(i) == base) {
				//splits all cards after selected card into new pile
				for(; i < cards.size();) {
					newPile.addCard(cards.get(i));
					removeCard(cards.get(i));
				}
			}
		}
		
		//sets new pile into topPile
		newPile.topPile = this;
	
		return newPile;
	}
	
	//merge two piles, the accepted pile will be stacked on top
	public void merge(CardPile p) {
		for (Card c : p.cards) {
			addCard(c);
		}
		updatePileSize();
	}
	
	//Find and return card from pile based on card details
	public Card findCard(int v, String s) {
		for(Card c : cards) {
			if(c.value == v && c.suit.name().equals(s)) {
				return c;
			}
		}
		return null;
	}
	
	//check if movement of card or pile is valid
	public boolean validMovement(CardPile p) {
		//check to see if pile is being added on top of itself
		if(this == p) {
			return false;
		}
		
		Card heldCard = p.cards.get(0);
		Card top;
		
		//specific pile validation
		
		switch(typeOfPile) {
			//specific rules for Tableau piles
			case Tableau:
				//Spare Tableau spots can only be filled with kings
				if(cards.isEmpty()) {
					if(heldCard.value == 13) {
						return true;
					}
					return false;
				}
				
				//If tableau spot has not been turned over, placement is invalid
				top = cards.get(cards.size()-1);
				if(top.showBack) {
					return false;
				}
				
				//To successfully place cards on a current stack/card on tableau
				//cards must be alternating colour, in descending, consecutive order
				if(top.suit.red != heldCard.suit.red) {
					if(top.value == heldCard.value + 1) {
						return true;
					}
				}
				
			break;
			
			//rules for foundation piles
			case Foundation:
				
				//Foundation piles must start with an ace
				if(cards.isEmpty() && heldCard.value == 1) {
					suitCheck = heldCard.suit;
					return true;
				}
				
				//foundation piles must be of the same suit
				if(suitCheck != heldCard.suit) {
					return false;
				}
				
				//foundation piles must be in ascending order
				top = cards.get(cards.size() - 1);
				if(top.value == heldCard.value - 1) {
					return true;
				}
				
			break;		
		}
		return false;
		
	}
	
	//check to see score value for moved card - assumes valid movement
	public int scoreValue(CardPile held) {
			
		PileType heldPile = held.typeOfPile;
		Card heldCard = held.cards.get(0);
		//specific scoring for each pile type
		
		switch(typeOfPile) {
			//scoring for cards being moved to tableau
			case Tableau:
				//if card is being moved from the talon pile to the tableau return 5 and set card to moved
				if(heldPile == PileType.Talon) {
					heldCard.setMoved();
					return scoreStockToTableau;
				}
				//if card is being moved for the first time within the tableau return 3 and set card to moved
				if(heldPile == PileType.Tableau && !heldCard.hasBeenMoved) {
					heldCard.setMoved();
					return scoreFirstMovement;
				}
				//if card is being moved from the Foundation pile *back* to the Tableau return -15
				if(heldPile == PileType.Foundation) 
					return scoreFromFoundation;
				
			break;
			
			//scoring for cards sent to foundation
			case Foundation:
				
				//if card is moved to Foundation Pile return 10
				return scoreFoundation;					
		}
		return 0;
		
	}
	
	
	@Override
	//toString method for pile
	public String toString() {
		String result = "";
		
		result += bottom.toString() + "-";
		
		for(Card c : cards) {
			result += c.toString() + "-";
		}
		
		return result;
	}
	
		// Align pile to top
		@Override
		public Component.BaselineResizeBehavior getBaselineResizeBehavior() {
		    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
		}

		@Override
		public int getBaseline(int w, int h) {
		    return 0;
		}
	
}

package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;

import controller.GameController;
import model.Card;
import model.CardPile;
public class UserInterface extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
	
	private JMenuBar menuBar;
	
	Map<String, String> displayText;
	JPanel gameZone;
	JPanel pileCol;
	JPanel topCol;
	JLayeredPane lp;
	GameController solitaire;
	
	//timer elements
	JLabel timerLabel;
	int second;
	int minute;
	String tSecond;
	String tMinute;
	DecimalFormat timeFormat = new DecimalFormat("00");
	Timer gameTimer;
	
	//score elements
	JLabel scoreLabel;
	int totalScore;
	final int scoreFlip = 5;
	final int scoreTimeDecay = -2;
	
	//elements while under mouse drag
	CardPile tempPile;
	Point mouseOffset;
	
	//default constructor
	public UserInterface(GameController g) {
		solitaire = g;
		
		//Initialization
		createTextMap();
		setLayout(new BorderLayout());
		mouseOffset = new Point(0,0);

		//initialize Game Area
		gameZone = new JPanel();
		gameZone.setOpaque(false);
		gameZone.setLayout(new BoxLayout(gameZone, BoxLayout.PAGE_AXIS));
		try {
			setContentPane((new backgroundLoad("images/background.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Set window size and title and settings
		setTitle("Solitaire");
		setSize(900, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		//set timer location
		timerLabel = new JLabel("00:00");
		timerLabel.setBounds(300,200,200,100);
		gameZone.add(timerLabel);
		
		//add ui to allow for new game
		createTopMenu();
		
		//create column organization for card piles
		FlowLayout flow = new FlowLayout(FlowLayout.CENTER);
		flow.setAlignOnBaseline(true);
		pileCol = new JPanel();
		pileCol.setOpaque(false);
		pileCol.setLayout(flow);
		pileCol.setMinimumSize(new Dimension(200, 900));
		
		//top columns
		FlowLayout topFlow = new FlowLayout(FlowLayout.LEFT);
		topFlow.setAlignOnBaseline(true);
		topCol = new JPanel();
		topCol.setOpaque(false);
		topCol.setLayout(topFlow);
		
		//set score location
		scoreLabel = new JLabel("SCORE: 0");
		scoreLabel.setBounds(300,200,200,100);
		gameZone.add(scoreLabel);
		
		
		//add to game zone
		gameZone.add(topCol);
		gameZone.add(pileCol);
		
		add(gameZone);
		
		//Display game
		lp = getLayeredPane();
		setVisible(true);
		
		initialize();
	}
	
	//create game
	private void initialize() {
		topCol.removeAll();
		pileCol.removeAll();
		
		//start timer
		second = 0;
		minute = 0;
		timerSetup();
		gameTimer.start();
		
		//set score
		totalScore = 0;
		scoreLabel.setText("SCORE: 0");		
		
		//add listener to all cards
		for(Card c : solitaire.deck.deck) {
			c.addMouseListener(this);
			c.addMouseMotionListener(this);
		}
		
		//setup piles
		solitaire.setup();
		for(CardPile p : solitaire.tableauPiles) {
			pileCol.add(p);
		}
		
		topCol.add(solitaire.stockPile);
		topCol.add(solitaire.talonPile);
		
		for(CardPile p : solitaire.foundationPiles) {
			topCol.add(p);
		}
		
		validate();
	}
	
	//reset game
	public void reset() {
		solitaire.reset();
		initialize();
		repaint();
	}
	
	//creates option menu to restart game
	private void createTextMap() {
		displayText = new HashMap<String, String>();
		
		displayText.put("New", "New");
		displayText.put("Exit", "Exit");			
	}
	
	//Top Menu to restart Game
	private void createTopMenu() {
		menuBar = new JMenuBar();
		
		JMenu mainMenu = new JMenu("Menu");
		mainMenu.setMnemonic(KeyEvent.VK_M);
		menuBar.add(mainMenu);
		
		menuOption[] menuOptions = new menuOption[] {
			new menuOption(displayText.get("New"), KeyEvent.VK_N),
			new menuOption(displayText.get("Exit"), KeyEvent.VK_E)
		};
		
		
		for(menuOption option: menuOptions) {	
			JMenuItem opt = new JMenuItem(option.name);
			if(option.shorcut != 0) opt.setMnemonic(option.shorcut);
			
			opt.addActionListener(this);
			mainMenu.add(opt);				
		}

		
		setJMenuBar(menuBar);
	}
	
	//This class will be used for each menu option, holds name and shortcut of menu
	class menuOption {
		public String name;
		public Integer shorcut = 0;
		
		public menuOption(String n, Integer s) {
			name = n;
			shorcut = s;
		}
	}
	
	//This will handle events performed by user
	public void actionPerformed(ActionEvent event) {
		
		// menu interactions
		if(event.getSource() instanceof JMenuItem) handleMenuInteraction(event);
		
	}
	
	//handles when user selects menu item for new game or exit
	private void handleMenuInteraction(ActionEvent e) {
		JMenuItem item = (JMenuItem)e.getSource();
		
		//user selects exit
		if(item.getText().equals(displayText.get("Exit"))) {
			this.dispose();
			return;
		}
		
		//user selects new game
		if(item.getText().equals(displayText.get("New"))) {
			reset();
			return;
		}
	}

	//handle what happens when mouse drags pile
	@Override
	public void mouseDragged(MouseEvent event) {
		if(tempPile != null) {
			Point p = getLocationOnScreen();
			p.x = event.getLocationOnScreen().x - p.x - mouseOffset.x;
			p.y = event.getLocationOnScreen().y - p.y - mouseOffset.y;
			
			tempPile.setLocation(p);
		}
		repaint();
	}
	
	//handle what happens when mouse clicks
	@Override
	public void mouseClicked(MouseEvent event) {
		if(event.getComponent() instanceof Card) {
			Card c = (Card)event.getComponent();
			CardPile p = (CardPile)c.getParent();
			
			switch(p.typeOfPile) {
			//if stockpile is clicked, card will be drawn
				case Stockpile:
					solitaire.drawCard();
				break;
				//if tableau is clicked card will be flipped over if back is showing
				case Tableau:
					solitaire.flipCard(p);
					totalScore += scoreFlip;
					scoreLabel.setText("SCORE: " + totalScore);
				break;
				//if talon pile is clicked, it will be reorganized back into stock if stock pile is out
				case Talon:
					solitaire.resetStock();
				break;
			}	
			repaint();
		}
	}
	

	@Override
	public void mousePressed(MouseEvent event) {
		if(event.getComponent() instanceof Card) {
			Card c = (Card)event.getComponent();
			
			// Do nothing if card is reversed
			if(c.showBack)
				return;
			
			CardPile p  = (CardPile)c.getParent();
			
			if(p.cards.isEmpty()) return;
			
			//split pile if card dragged is in middle of pile
			tempPile = p.splitPile(c);
			tempPile.typeOfPile = p.typeOfPile;

			lp.add(tempPile, JLayeredPane.DRAG_LAYER);

			Point poi = getLocationOnScreen();
			mouseOffset = event.getPoint();
			poi.x = event.getLocationOnScreen().x - poi.x - mouseOffset.x;
			poi.y = event.getLocationOnScreen().y - poi.y - mouseOffset.y;
			
			tempPile.setLocation(poi);
			
			repaint();
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent event) {
		if(tempPile != null) {
			
			Point mPoi = event.getLocationOnScreen();
			boolean eq = false;
			
			// check for validity of pile movement
			ArrayList<CardPile> currPiles = new ArrayList<CardPile>(solitaire.tableauPiles);
			currPiles.addAll(solitaire.foundationPiles);
			
			for(CardPile p: currPiles) {
				Point pPoi = p.getLocationOnScreen();
				Rectangle r = p.getBounds();
				r.x = pPoi.x;
				r.y = pPoi.y;
				
				if(r.contains(mPoi) && p.validMovement(tempPile)) {
					totalScore += p.scoreValue(tempPile);
					p.merge(tempPile);
					eq = true;
					scoreLabel.setText("SCORE: " + totalScore);
					break;
				}
			}
			
			// send pile back to original spot if invalid
			if(!eq) tempPile.topPile.merge(tempPile);
				
			lp.remove(tempPile);
			tempPile = null;

			repaint();
			
			if(solitaire.winCon()) {
				JOptionPane.showMessageDialog(this, "YOU WON IN " + timerLabel.getText() + "WITH A SCORE OF" + totalScore + "!!!");
				//stop timer
				gameTimer.stop();
				reset();
			}
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {

	}
	
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	
	public class backgroundLoad extends JPanel {
		  private Image backgroundImage;

		  // initialize background image
		  public backgroundLoad(String n) throws IOException {
			  backgroundImage = ImageIO.read(new FileInputStream(n));
		  }

		  public void paintComponent(Graphics g) {
		    super.paintComponent(g);

		    // Draw the background image.
		    g.drawImage(backgroundImage, 0, 0, this);
		  }
	}
	
	public void timerSetup() {
		//setup timer
		gameTimer = new Timer(1000, new ActionListener() {
		
			@Override
			//will handle actions from the timer
			public void actionPerformed(ActionEvent e) {
				second++;
				tSecond = timeFormat.format(second);
				tMinute = timeFormat.format(minute);
				
				timerLabel.setText(tMinute + ":" + tSecond);
				
				//reduce score by 2 for every 10s elapsed
				if(second % 10 == 0) {
					totalScore += scoreTimeDecay;
					scoreLabel.setText("SCORE: " + totalScore);
				}
				//handle second overflow
				if(second == 60) {
					second = 0;
					minute ++;
					
					tSecond = timeFormat.format(second);
					tMinute = timeFormat.format(minute);
					timerLabel.setText(tMinute + ":" + tSecond);
				}
			}
		});
		
	}
}

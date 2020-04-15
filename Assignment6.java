import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputAdapter;

public class Assignment6 {

   public static void main(String[] args) {
      CardView cardView = new CardView("Table", 1, 2);
      CardModel cardModel = new CardModel();
      CardController cardController = new CardController(cardModel, cardView);

      cardView.setVisible(true);
   }
}

class CardController {
   private CardView cardView;
   private CardModel cardModel;

   public CardController(CardModel cardModel, CardView cardView) {
      this.cardView = cardView;
      this.cardView.addActionListener(new ButtonListener());
      this.cardModel = cardModel;
      this.cardView.addButtonListener(new ButtonListener());

      this.refreshBoard();
   }

   private void refreshBoard() {
      cardView.clearHands();

      // add cards for computer and player hands
      for (int i = 0; i < cardModel.getPlayerHand().getNumCards(); i++) {
         cardView.addPlayerCard(cardModel.getPlayerHandIcon(i), i);
      }
      for (int i = 0; i < cardModel.getComputerHand().getNumCards(); i++) {
         cardView.addComputerCard(cardModel.getCardBack(), i);
      }

      for (int i = 0; i < cardModel.getPlayAreaHand().getNumCards(); i++) {
         cardView.addPlayAreaCard(cardModel.getPlayAreaHandIcon(i), i);
      }
   }

   void clickedPlayerCard(int cardIndex) {
      System.out.println("cardIndex " + cardIndex);
   }

   void clickedComputerCard(int cardIndex) {
      System.out.println("cardIndex " + cardIndex);
   }

   void clickedPlayAreaCard(int cardIndex) {
      System.out.println("cardIndex " + cardIndex);
   }

   class ButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {

         try {
            if (e.getActionCommand().equals("Can't play card")) {
               System.out.println("Clicked can't play button");
               cardModel.setPass();
               cardModel.playerTurnOver();

            } else {
               if (!e.getActionCommand().equals("PlayArea")) {
                  cardModel.playCard(e.getID());
               } else {
                  // check if card has been selected
                  cardModel.getPlayAreaHand().inspectCard(e.getID());
                  System.out.println("Player selected " + cardModel.getSelectedCard() + "; selected Play Area Index "
                        + e.getID() + ", " + cardModel.getPlayAreaHand().inspectCard(e.getID()));

                  // checks if the card is can be played on a stack
                  if (cardModel.getRanking(cardModel.getSelectedCard().getValue(),
                        cardModel.getPlayAreaHand().inspectCard(e.getID()).getValue()) == true) {
                     // replaces card in playarea
                     cardModel.setCard("PlayArea", e.getID(), cardModel.getSelectedCard());
                     cardModel.playerTurnOver();
                  }
               }

            }

            refreshBoard();
            cardModel.checkTurns();
            refreshBoard();
            boolean endGame = cardModel.checkGameEnd();

            if (endGame == true) {
               cardView.displayEndScreen(cardModel.getResults());
            }

         } catch (NumberFormatException ex) {
            System.out.println(ex);

         }
      }
   }

}

class CardModel {
   GUICard card = new GUICard();
   Deck deck = new Deck(1);
   Hand playerHand = new Hand();
   Hand computerHand = new Hand();
   // playAreaHand = the "stacks" in sequential order, within a Hand object
   Hand playAreaHand = new Hand();
   Card selectedCard = null;
   int selectedCardIndex = 0;
   String playerHandString = new String();
   int computerCantPlay = 0;
   int humanCantPlay = 0;
   boolean playerPassed = false;
   boolean computerPassed = false;
   boolean isPlayerTurn = true;
   boolean isPlayerTurnDone = false;

   public CardModel() {
      deck.shuffle();

      // Deal 7 cards to the computer and player hands
      for (int i = 0; i < 7; i++) {
         playerHand.takeCard(deck.dealCard());
         computerHand.takeCard(deck.dealCard());
      }
      // Deal 3 cards to the play area hand
      for (int i = 0; i < 3; i++) {
         playAreaHand.takeCard(deck.dealCard());
      }
   }

   public void setCard(String string, int id, Card selectedCard2) {
      playAreaHand.setCard(id, selectedCard2);
      if (isPlayerTurn == true) {
         playerHand.playCard(selectedCardIndex);
         this.takeCard(playerHandString);
      } else {
         computerHand.playCard(selectedCardIndex);
         this.takeCard(playerHandString);
      }
   }

   public Icon getCardBack() {
      return card.getBackCardIcon();
   }

   public boolean placeCard(Card card) {

      return true;
   }

   public Card getSelectedCard() {
      return selectedCard;
   }

   public void takeCard(String hand) {
      if (deck.getTopCard() > 0) {
         if (hand.equals("Player")) {
            playerHand.takeCard(deck.dealCard());
         } else if (hand.equals("Computer")) {
            computerHand.takeCard(deck.dealCard());
         }
      }
   }

   public void playCard(int id) {
      selectedCardIndex = id;

      if (isPlayerTurn == true) {
         playerHandString = "Player";
         selectedCard = playerHand.inspectCard(id);
         System.out.println("Player Card - " + selectedCard);
      } else if (isPlayerTurn == false) {
         playerHandString = "Computer";
         selectedCard = computerHand.inspectCard(id);
         // computerHand.playCard(id);
         System.out.println("Computer Card - " + selectedCard);
      }
   }

   public void resetPlayAreaHand(Hand playAreaHand) {
      System.out.println("Dealing three new cards to play area...");
      playAreaHand.resetHand();
      for (int i = 0; i < 3; i++) {
         playAreaHand.takeCard(deck.dealCard());
      }
   }

   public void setPass() {
      if (isPlayerTurn == true) {
         playerPassed = true;
         humanCantPlay++;
         System.out.println("Player has passed");
      } else if (isPlayerTurn == false) {
         computerPassed = true;
         computerCantPlay++;
         System.out.println("Computer has passed");
      }
   }

   public void playerTurnOver() {
      if (isPlayerTurn == true) {
         isPlayerTurn = false;
      }
      if (isPlayerTurnDone == false) {
         isPlayerTurnDone = true;
      }
   }

   public void checkTurns() {
      System.out.println("Checking turns...");
      if (playerPassed && computerPassed == true) {
         System.out.println("Both players have passed! Reset play area");
         resetPlayAreaHand(playAreaHand);
         playerPassed = false;
         computerPassed = false;
      }
      if (playerPassed == true && computerPassed == false) {
         System.out.println("Player has passed! Computer gets two turns");
         computerTurn();
         isPlayerTurn = false;
         computerTurn();
         playerPassed = false;
      } else if (computerPassed == true && playerPassed == false) {
         System.out.println("Computer had passed! Player is on an additional turn");
         isPlayerTurn = true;
         if (isPlayerTurnDone == true) {
            computerPassed = false;
         }
      } else {
         computerTurn();
      }
   }

   public boolean checkGameEnd() {
      if (deck.getNumCards() <= 0) {
         System.out.println("No more cards in the deck!");
         return true;
      }
      return false;
   }

   public String getResults() {
      String results = "";
      if (computerCantPlay < humanCantPlay)
         results = "You lose!";
      else if (humanCantPlay < computerCantPlay)
         results = "You win!";
      else
         results = "The game is tied!";

      results += "\n Player passes: " + humanCantPlay + ", Computer passes: " + computerCantPlay;
      return results;
   }

   public void computerTurn() {
      if (isPlayerTurn == false) {
         System.out.println("Initiate computer turn.");
         for (int i = 0; i < computerHand.getNumCards(); i++) {
            for (int j = 0; j < 3; j++) {
               if (getRanking(computerHand.inspectCard(i).getValue(), playAreaHand.inspectCard(j).getValue()) == true) {
                  System.out.println("Computer plays a card!");
                  System.out.println("Computer puts " + computerHand.inspectCard(i) + " on Play Area Index " + j
                        + " over " + playAreaHand.inspectCard(j));
                  playCard(i);
                  setCard("PlayArea", j, computerHand.inspectCard(i));
                  computerPassed = false;
                  isPlayerTurn = true;
                  return;
               }
            }
         }
         System.out.println("Computer passed...");
         computerPassed = true;
         isPlayerTurn = true;
         isPlayerTurnDone = false;
         return;
      }
   }

   public boolean getRanking(char stackVal, char handVal) {
      Card cardObj = new Card();

      boolean result = cardObj.ranking(stackVal, handVal);

      if (result == true) {
         return true;
      }

      return false;
   }

   public Icon getComputerHandIcon(int card) {
      return getIcon(computerHand.inspectCard(card));
   }

   public Icon getPlayerHandIcon(int card) {
      return getIcon(playerHand.inspectCard(card));
   }

   public Icon getPlayAreaHandIcon(int i) {
      return getIcon(playAreaHand.inspectCard(i));
   }

   public Hand getComputerHand() {
      return computerHand;
   }

   public Hand getPlayerHand() {
      return playerHand;
   }

   public Hand getPlayAreaHand() {
      return playAreaHand;
   }

   public Card getCard() {
      return new Card();
   }

   public Icon getIcon(Card card) {
      return GUICard.getIcon(card);
   }

}

class CardView extends JFrame {
   static int MAX_CARDS_PER_HAND = 56;
   static int MAX_PLAYERS = 2; // for now, we only allow 2 person games
   JButton playerPass = new JButton("Can't play card");
   JLabel timerLabel = new JLabel("Timer:", JLabel.CENTER);
   JButton timerButton = new JButton("Stop Timer");
   Object syncObject = new Object();
   Timer timer = new Timer(timerLabel, syncObject);

   public JPanel pnlComputerHand, pnlHumanHand, pnlPlayArea, pnlButtons, pnlTimer;

   private int numCardsPerHand;
   private int numPlayers;

   ActionListener action;

   public CardView(String title, int numCardsPerHand, int numPlayers) {
      super();
      this.setSize(800, 600);
      this.setLocationRelativeTo(null);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      this.setTitle(title);
      this.numCardsPerHand = numCardsPerHand;
      this.numPlayers = numPlayers;
      pnlComputerHand = new JPanel();
      pnlHumanHand = new JPanel();
      pnlPlayArea = new JPanel();
      pnlButtons = new JPanel();
      pnlTimer = new JPanel();

      TitledBorder playerBorderTitle = BorderFactory.createTitledBorder("Player Hand");
      TitledBorder playAreaBorderTitle = BorderFactory.createTitledBorder("Play Area");
      TitledBorder computerBorderTitle = BorderFactory.createTitledBorder("Computer Hand");

      FlowLayout plyHandLayout = new FlowLayout();
      FlowLayout cmpHandLayout = new FlowLayout();
      FlowLayout playAreaLayout = new FlowLayout(FlowLayout.CENTER, 50, 50);
      FlowLayout cmpButtonsLayout = new FlowLayout();
      FlowLayout timerAreaLayout = new FlowLayout();

      pnlComputerHand.setLayout(cmpHandLayout);
      pnlHumanHand.setLayout(plyHandLayout);
      pnlPlayArea.setLayout(playAreaLayout);
      pnlButtons.setLayout(cmpButtonsLayout);
      pnlTimer.setLayout(timerAreaLayout);

      pnlPlayArea.setBorder(playAreaBorderTitle);
      pnlHumanHand.setBorder(playerBorderTitle);
      pnlComputerHand.setBorder(computerBorderTitle);

      pnlButtons.add(playerPass);
      pnlTimer.add(timerLabel);
      pnlTimer.add(timerButton);

      this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

      pnlComputerHand.setAlignmentX(Component.CENTER_ALIGNMENT);
      pnlComputerHand.setPreferredSize(new Dimension(50, 70));
      pnlHumanHand.setAlignmentX(Component.CENTER_ALIGNMENT);
      pnlHumanHand.setPreferredSize(new Dimension(60, 70));
      pnlButtons.setAlignmentX(Component.CENTER_ALIGNMENT);
      pnlButtons.setPreferredSize(new Dimension(20, 10));
      pnlPlayArea.setAlignmentX(Component.CENTER_ALIGNMENT);
      pnlPlayArea.setPreferredSize(new Dimension(60, 150));
      pnlTimer.setAlignmentX(Component.CENTER_ALIGNMENT);
      pnlTimer.setPreferredSize(new Dimension(5, 5));

      this.add(pnlComputerHand);
      this.add(pnlPlayArea);
      this.add(pnlHumanHand);
      this.add(pnlButtons);
      this.add(pnlTimer);
      this.setTimerListener();
      timer.start();
   }

   private void setTimerListener() {
      timerButton.addMouseListener(new MouseInputAdapter() {
         @Override
         public void mousePressed(MouseEvent click) {
            boolean shouldRun = timer.getShouldRun();

            if (shouldRun) {
               timer.stopTimer();
               timerButton.setText("Start Timer");
            } else {
               timer.startTimer();
               synchronized (syncObject) {
                  syncObject.notify();
               }
               timerButton.setText("Stop Timer");
            }
         }
      });
   }

   public void setActionListener(ActionListener listener) {
      this.action = listener;
   }

   public void clearHands() {
      // TODO Auto-generated method stub
      pnlComputerHand.removeAll();
      pnlHumanHand.removeAll();
      pnlPlayArea.removeAll();
   }

   void addButtonListener(ActionListener listenForButton) {
      playerPass.addActionListener(listenForButton);
   }

   void addActionListener(ActionListener mouseListener) {
      this.action = mouseListener;
   }

   public void addPlayAreaCard(Icon cardBack, int i) {
      // TODO Auto-generated method stub
      JLabel newCardLable = new JLabel(cardBack);

      newCardLable.addMouseListener(new MouseInputAdapter() {
         public void mousePressed(MouseEvent click) {
            ActionEvent e = new ActionEvent((JLabel) click.getSource(), i, "PlayArea");
            action.actionPerformed(e);
         }
      });

      pnlPlayArea.add(newCardLable);
      this.revalidate();
      this.repaint();

   }

   public void addComputerCard(Icon icon, int index) {
      JLabel newCardLable = new JLabel(icon);

      newCardLable.addMouseListener(new MouseInputAdapter() {
         public void mousePressed(MouseEvent click) {
            ActionEvent e = new ActionEvent((JLabel) click.getSource(), index, "Computer");
            action.actionPerformed(e);
         }
      });

      pnlComputerHand.add(newCardLable);
      this.revalidate();
      this.repaint();
   }

   public void addPlayerCard(Icon icon, int index) {
      JLabel newCardLable = new JLabel(icon);

      newCardLable.addMouseListener(new MouseInputAdapter() {
         public void mousePressed(MouseEvent click) {
            ActionEvent e = new ActionEvent((JLabel) click.getSource(), index, "Player");
            action.actionPerformed(e);
         }
      });

      pnlHumanHand.add(newCardLable);
      this.revalidate();
      this.repaint();
   }

   void displayErrorMessage(String errorMessage) {
      JOptionPane.showMessageDialog(this, errorMessage);
   }

   void displayEndScreen(String results) {
      JOptionPane.showMessageDialog(null, results);
      System.exit(0);
   }
}

class Timer extends Thread {
   long startTime;
   boolean shouldRun;
   JLabel timerLabel;
   Object syncObject;
   long timePaused;
   long totalTimePaused;

   Timer(JLabel timerLabel, Object syncObject) {
      this.setSyncObject(syncObject);
      this.setTimerLabel(timerLabel);
      this.setShouldRun(true);
      this.setTotalTimePaused(0);
   }

   public long getTimePaused() {
      return this.timePaused;
   }

   public void setTimePaused(long timePaused) {
      this.timePaused = timePaused;
   }

   public long getTotalTimePaused() {
      return this.totalTimePaused;
   }

   public void setTotalTimePaused(long totalTimePaused) {
      this.totalTimePaused = totalTimePaused;
   }

   private void setSyncObject(Object syncObject) {
      this.syncObject = syncObject;
   }

   private void setTimerLabel(JLabel timerLabel) {
      this.timerLabel = timerLabel;
   }

   public void stopTimer() {
      this.setShouldRun(false);
   }

   public void startTimer() {
      this.setShouldRun(true);
   }

   public boolean getShouldRun() {
      return this.shouldRun;
   }

   private void setShouldRun(boolean shouldRun) {
      this.shouldRun = shouldRun;
   }

   private void setStartTime(long startTime) {
      this.startTime = startTime;
   }

   private long getElapsedSeconds() {
      long elapsedTime = (System.currentTimeMillis() - this.startTime) - this.totalTimePaused;
      long elapsedSeconds = elapsedTime / 1000;
      return elapsedSeconds;
   }

   private long getDisplaySeconds() {
      return this.getElapsedSeconds() % 60;
   }

   private long getElapsedMinutes() {
      return this.getElapsedSeconds() / 60;
   }

   public void run() {
      this.setStartTime(System.currentTimeMillis());

      while (true) {
         if (!this.getShouldRun()) {
            synchronized (syncObject) {
               try {
                  this.setTimePaused(System.currentTimeMillis());
                  syncObject.wait();
                  long currentPausedTime = this.getTotalTimePaused();
                  this.setTotalTimePaused((System.currentTimeMillis() - this.getTimePaused()) + currentPausedTime);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }

         long elapsedTime = System.currentTimeMillis() - startTime;
         long timeTillNextDisplayChange = 1000 - (elapsedTime % 1000);
         try {
            Thread.sleep(timeTillNextDisplayChange);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         long minutes = this.getElapsedMinutes();
         long seconds = this.getDisplaySeconds();
         String displayMinutes = Long.toString(minutes);
         String displaySeconds = Long.toString(seconds);

         if (minutes < 10) {
            displayMinutes = "0" + displayMinutes;
         }
         if (seconds < 10) {
            displaySeconds = "0" + displaySeconds;
         }

         this.timerLabel.setText("Timer: " + displayMinutes + ":" + displaySeconds);
      }
   }
}

class GUICard {
   // 14 = A thru K + joker
   private static Icon[][] iconCards = new ImageIcon[14][4];
   private static Icon iconBack;
   static boolean iconsLoaded = false;

   public GUICard() {
      if (!iconsLoaded) {
         loadCardIcons();
      }
   }

   static void loadCardIcons() {
      String path = new String();
      int i = 0;

      for (int j = 0; j < 4; j++) {
         for (int k = 0; k < 14; k++) {
            path += "images/" + turnIntIntoCardValue(k) + turnIntIntoCardSuit(j) + ".gif";
            iconCards[k][j] = new ImageIcon(path);
            path = "";
         }
      }

      iconBack = new ImageIcon("images/BK.gif");
      iconsLoaded = true;
   }

   // turns 0 - 13 into "A", "2", "3", ... "Q", "K", "X"
   static String turnIntIntoCardValue(int k) {
      switch (k) {
         case 0:
            return "A";
         case 1:
            return "X";
         case 2:
            return "2";
         case 3:
            return "3";
         case 4:
            return "4";
         case 5:
            return "5";
         case 6:
            return "6";
         case 7:
            return "7";
         case 8:
            return "8";
         case 9:
            return "9";
         case 10:
            return "T";
         case 11:
            return "J";
         case 12:
            return "Q";
         case 13:
            return "K";
      }
      return "E";
   }

   // turns 0 - 3 into "C", "D", "H", "S"
   static String turnIntIntoCardSuit(int j) {
      switch (j) {
         case 0:
            return "C";
         case 1:
            return "D";
         case 2:
            return "H";
         case 3:
            return "S";
      }
      return "E";
   }

   static private int valueAsInt(Card card) {
      char value = card.getValue();
      switch (value) {
         case 'X':
            return 1;
         case 'A':
            return 0;
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
            return Integer.parseInt(String.valueOf(value));
         case 'T':
            return 10;
         case 'J':
            return 11;
         case 'Q':
            return 12;
         case 'K':
            return 13;
         default:
            return 0;
      }
   }

   static private int suitAsInt(Card card) {
      Suit suit = card.getSuit();

      switch (suit) {
         case clubs:
            return 0;
         case diamonds:
            return 1;
         case hearts:
            return 2;
         case spades:
            return 3;
         default:
            return 0;
      }
   }

   static public Icon getIcon(Card card) {

      return iconCards[valueAsInt(card)][suitAsInt(card)];
   }

   static public Icon getBackCardIcon() {
      return iconBack;
   }
}

enum Suit {
   clubs, diamonds, hearts, spades
}

class Card {

   char value;
   Suit suit;
   boolean errorFlag;

   public static char[] valueRanks = new char[] { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K',
         'X' };

   Card(char value, Suit suit) {
      set(value, suit);
   }

   // overload for no parameters case
   Card() {
      set('A', Suit.spades);
   }

   public boolean set(char value, Suit suit) {
      if (isValid(value, suit)) { // only set if input is valid
         this.value = value;
         this.suit = suit;
         this.errorFlag = false;
         return true;
      }
      this.errorFlag = true;
      return false;
   }

   public String toString() {
      if (errorFlag) {
         return "[invalid]";
      }
      return value + " of " + suit;
   }

   public boolean equals(Card card) {
      boolean equalValues = value == card.getValue();
      boolean sameSuit = suit == card.getSuit();

      if (equalValues == false || sameSuit == false) {
         this.errorFlag = false;
      }
      return equalValues && sameSuit;
   }

   public char getValue() {
      return value;
   }

   public Suit getSuit() {
      return suit;
   }

   public boolean isErrorFlag() {
      return errorFlag;
   }

   private boolean isValid(char value, Suit suit) {
      char[] validValues = new char[] { '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'A', 'K', 'Q', 'J', 'X' };

      // search every valid value for a match -
      // return true as soon as match found
      for (int i = 0; i < validValues.length; i++) {
         if (value == validValues[i]) {
            return true;
         }
      }
      return false;
   }

   // checks if card val is 1+ 1- the selected card
   public boolean ranking(char stackValue, char handValue) {
      int stack = 0;
      int hand = 0;

      // sets index vars for comparision
      for (int i = 0; i < valueRanks.length; i++) {

         if (stackValue == valueRanks[i]) {
            stack = i;
         }

         if (handValue == valueRanks[i]) {
            hand = i;
         }
      }

      if (hand == (stack + 1) || hand == (stack - 1)) {
         return true;
      }

      return false;
   }

   static Card[] arraySort(Card[] cards, int arraySize) {
      Card temp;

      for (int i = 0; i < arraySize; i++) {
         for (int j = 0; j < arraySize - 1; j++) {

            for (char valueRank : valueRanks) {
               if (cards[j].getValue() == valueRank) {
                  temp = cards[j];
                  cards[j] = cards[j + 1];
                  cards[j + 1] = temp;
               }
            }
         }
      }
      return cards;
   }
}

class Hand {
   public int MAX_CARDS = 100;

   private Card[] myCards;
   private int numCards;

   public Hand() {
      myCards = new Card[MAX_CARDS];
      numCards = 0;
   }

   public void resetHand() {
      numCards = 0;
   }

   public boolean takeCard(Card card) {

      Card newCard = new Card(card.getValue(), card.getSuit());
      // System.out.println(newCard);

      if (numCards > MAX_CARDS) {
         return false;
      } else {
         myCards[numCards] = newCard;
         numCards++;
         return true;
      }
   }

   public Card playCard(int cardIndex) {
      if (numCards == 0) // error
      {
         // Creates a card that does not work
         return new Card('M', Suit.spades);
      }
      // Decreases numCards.
      Card card = myCards[cardIndex];

      numCards--;
      for (int i = cardIndex; i < numCards; i++) {
         myCards[i] = myCards[i + 1];
      }

      myCards[numCards] = null;

      return card;
   }

   public void setCard(int cardIndex, Card card) {
      myCards[cardIndex] = card;

   }

   public Card playCard() {

      if (numCards <= 0) {
         return new Card('?', Suit.spades);
      }

      numCards--;
      Card card = new Card(myCards[numCards].getValue(), myCards[numCards].getSuit());
      return card;
   }

   public String toString() {
      String output = new String();
      output = "Hand: ( ";
      // add each card to output string
      for (int i = 0; i < numCards; i++) {
         output += myCards[i];
         if (i + 1 != numCards) {
            output += ", ";
         }
      }
      output += " )\n";
      // add newline every 100 characters
      for (int i = 100; i <= output.length(); i += 100) {
         output = output.substring(0, i) + "\n" + output.substring(i);
      }
      return output;
   }

   public int getNumCards() {
      return numCards;
   }

   public Card inspectCard(int k) {
      Card card = new Card();

      if (k > numCards) {
         card.errorFlag = true;
      } else {
         card.set(myCards[k].getValue(), myCards[k].getSuit());
      }

      return card;
   }

   public void sort() {
      Card.arraySort(myCards, numCards);
   }
}

class Deck {
   public static final int MAX_CARDS = 6 * 56;

   private static Card[] masterPack;
   private Card[] cards = new Card[MAX_CARDS];
   private int topCard;

   // Constructor that populates the Card array
   public Deck(int numPacks) {
      allocateMasterPack();
      init(numPacks);
   }

   // Overload when no parameters
   public Deck() {
      allocateMasterPack();
      init(1);
   }

   // Re-populates cards[] with the designated number of packs of cards
   public void init(int numPacks) {
      // Find total number of cards
      topCard = (56 * numPacks);
      if (topCard <= MAX_CARDS) {
         // Create number of cards required from how many packs needed
         cards = new Card[56 * numPacks];
         int j = 0;
         // Loop for the amount of packs required
         for (int i = 0; i < numPacks; i++) {
            // Loop through every Card object of masterPack array to add to deck
            for (Card card : masterPack) {
               cards[j] = card;
               j++;
            }
         }
      }
   }

   // Shuffling the cards using random number generator
   public void shuffle() {
      Random rand = new Random();
      for (int j = 0; j <= cards.length - 1; j++) {
         // Find next random card position between 0 and total # of cards
         int randIndex = rand.nextInt(cards.length);
         // Swap selected card with current card
         Card temp = cards[randIndex];
         cards[randIndex] = cards[j];
         cards[j] = temp;
      }
   }

   // Returns and removes the card at top position of cards[]
   public Card dealCard() {
      // Check if cards are still available
      if (topCard < 0) {
         System.out.println("out of cards");
         return null;
      }
      // Move onto next card
      topCard--;
      // Get card information
      Card dealtCard = cards[topCard];
      // Delete card info and return it
      cards[topCard] = null;
      return dealtCard;
   }

   // Accessor for topCard
   public int getTopCard() {
      return topCard;
   }

   // Access for an individual card
   public Card inspectCard(int k) {
      Card card = new Card();
      if (k < 0 || k > topCard) {
         card.errorFlag = true;
      } else {
         card = cards[k];
      }

      return card;
   }

   public boolean addCard(Card card) {

      if (cards.length > topCard) {
         cards[++topCard] = card;
         return true;
      }
      return false;
   }

   public boolean removeCard(Card card) {
      for (Card cardss : cards) {
      }
      for (int i = 0; i < cards.length; i++) {
         if (cards[i].equals(card)) {
            cards[i] = cards[topCard - 1];

            topCard--;
            return true;
         }
      }
      return false;
   }

   public void sort() {
      Card.arraySort(cards, topCard + 1);
   }

   public int getNumCards() {
      return topCard;
   }

   // Generating the deck
   private static void allocateMasterPack() {
      if (masterPack != null) {
         return;
      }

      masterPack = new Card[56];
      char[] valueArray = new char[] { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'X' };

      int i = 0;
      // Use for-each loop to go through all suits in the enum
      for (Suit suit : Suit.values()) {
         // Use for-each loop to assign a card with each
         // value in the current suit
         for (char value : valueArray) {
            masterPack[i] = new Card(value, suit);
            i++;
         }
      }

   }
}
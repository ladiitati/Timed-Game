import java.awt.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;

/**************************************************************
 Tatiana Adams, Ryan Barrett, Matthew Taylor, Rowena Terrado
 7 April 2020
 CST 338 Software Design
 Assignment 5: GUI Low Card Game
 This program is a Low Card Game that uses a swing GUI. This game was built from our Deck of Card program that handles the functionalities of instantiating decks of cards, dealing cards to players, removing/adding cards, and playing cards. For the GUI we created a play window that has 4 panels that displays the users hand, displays the backs of the computers cards, displays the score board and displays the two cards being played by both the user and computer. To play the game the players are dealt 7 cards. Each round both players play a card hoping to have the lowest value resulting in a point to their total score. After each play the players draw a card until the deck runs out. The game continues until all the cards have been played. 
 ***************************************************************/

public class Assignment5 {
    static int NUM_CARDS_PER_HAND = 7;
    static int NUM_PLAYERS = 2;
    static JLabel[] computerLabels = new JLabel[NUM_CARDS_PER_HAND];
    static JLabel[] humanLabels = new JLabel[NUM_CARDS_PER_HAND];
    static JLabel[] playedCardLabels = new JLabel[NUM_PLAYERS];
    static JLabel[] playLabelText = new JLabel[NUM_PLAYERS];

    public static void main(String[] args) {
        int numPacksPerDeck = 1;
        int numJokersPerPack = 2;
        int numUnusedCardsPerPack = 0;
        Card[] unusedCardsPerPack = null;
        ScoreCard scoreCard = new ScoreCard();

        CardGameFramework LowCardGame = new CardGameFramework(numPacksPerDeck, numJokersPerPack, numUnusedCardsPerPack,
                unusedCardsPerPack, NUM_PLAYERS, NUM_CARDS_PER_HAND);
        LowCardGame.deal();
        Hand playerHand = LowCardGame.getHand(0);
        Hand computerHand = LowCardGame.getHand(1);
        LowCardGame.sortHands();

        GUICard guiCard = new GUICard();

        // establish main frame in which program will run
        CardTable myCardTable = new CardTable("CardTable", NUM_CARDS_PER_HAND, NUM_PLAYERS);
        myCardTable.setSize(800, 600);
        myCardTable.setLocationRelativeTo(null);
        myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel playerLabel = new JLabel("Player", JLabel.CENTER);
        JLabel computerLabel = new JLabel("Computer", JLabel.CENTER);
        JLabel playerCardLabel = new JLabel("", JLabel.CENTER);
        JLabel computerCardLabel = new JLabel("", JLabel.CENTER);
        JLabel scoreBoardLabel = new JLabel("", JLabel.CENTER);

        // CREATE LABELS AND ADD TO PANELS
        // ----------------------------------------------------
        for (int i = 0; i < playerHand.getNumCards(); i++) {
            final int handIndex = i;
            computerLabels[i] = new JLabel();
            computerLabels[i].setIcon(new ImageIcon("images/BK.gif")); // computer card backs
            myCardTable.pnlComputerHand.add(computerLabels[i], JLabel.CENTER);

            humanLabels[i] = new JLabel();
            // set the icon for player card
            humanLabels[i].setIcon(GUICard.getIcon(playerHand.inspectCard(i)));
            myCardTable.pnlHumanHand.add(humanLabels[i], JLabel.CENTER);

            // event listener to handle player clicking on a card
            humanLabels[i].addMouseListener(new MouseInputAdapter() {
                @Override
                public void mousePressed(MouseEvent click) {
                    // lookup the current card and set it in the GUI
                    playerCardLabel.setIcon(GUICard.getIcon(playerHand.inspectCard(handIndex)));
                    // humanLabels[handIndex].setIcon(null);
                    computerLabels[0].setIcon(null);

                    int computerCardIndex = findLowestComputerCard(computerHand, computerCardLabel);

                    // determine who had the lower card
                    boolean playerWins = playRound(LowCardGame.playCard(0, handIndex),
                            LowCardGame.playCard(1, computerCardIndex));
                    // update the scorecard according to the result of the round
                    if (playerWins) {
                        scoreCard.setPlayerScore((scoreCard.getPlayerScore() + 1));
                        System.out.println("You win! Current score is " + scoreCard.getCurrentScore());
                        scoreBoardLabel.setText(scoreCard.getCurrentScore());
                    } else {
                        scoreCard.setComputerScore((scoreCard.getComputerScore() + 1));
                        System.out.println("The computer wins... Current score is " + scoreCard.getCurrentScore());
                        scoreBoardLabel.setText(scoreCard.getCurrentScore());
                    }
                    LowCardGame.takeCard(0);
                    LowCardGame.takeCard(1);
                    LowCardGame.sortHands();
                    clearHand(playerHand);
                    renderHand(playerHand, computerHand, humanLabels, computerLabels, myCardTable);

                    if (playerHand.getNumCards() == 0) {
                        gameOver(scoreCard);
                    }
                }
            });
        }

        // and two random cards in the play region (simulating a computer/hum ply)
        // code goes here ...
        myCardTable.pnlPlayArea.add(playerCardLabel);
        myCardTable.pnlPlayArea.add(computerCardLabel);
        myCardTable.pnlPlayArea.add(playerLabel);
        myCardTable.pnlPlayArea.add(computerLabel);
        myCardTable.pnlScoreBoard.add(scoreBoardLabel);

        // show everything to the user
        myCardTable.setVisible(true);
    }

    public static void clearHand(Hand playerHand) {
        for (int i = 0; i < NUM_CARDS_PER_HAND; i++) {
            try {
                humanLabels[i].setIcon(null);
            } catch (Exception e) {
            }
        }
    }

    private static void gameOver(ScoreCard scoreCard) {
        String results = "";
        if (scoreCard.getPlayerScore() < scoreCard.getComputerScore())
            results = "You lose!";
        else if (scoreCard.getPlayerScore() > scoreCard.getComputerScore())
            results = "You win!";
        else
            results = "The game is tied!";

        results += "\n Player: " + scoreCard.getPlayerScore() + ", Computer: " + scoreCard.getComputerScore();
        JOptionPane.showMessageDialog(null, results);
        System.exit(0);
    }

    public static void renderHand(Hand playerHand, Hand computerHand, JLabel[] humanLabels, JLabel[] computerLabels,
            CardTable myCardTable) {

        for (int i = 0; i < NUM_CARDS_PER_HAND; i++) {
            if (i < computerHand.getNumCards()) {
                computerLabels[i].setIcon(new ImageIcon("images/BK.gif"));
                myCardTable.pnlComputerHand.add(computerLabels[i], JLabel.CENTER);
            }

            if (i < playerHand.getNumCards()) {
                // set the icon for player card
                humanLabels[i].setIcon(GUICard.getIcon(playerHand.inspectCard(i)));
                myCardTable.pnlHumanHand.add(humanLabels[i], JLabel.CENTER);
            }
        }
    }

    public static boolean playRound(Card playerCard, Card computerCard) {
        Card[] playedCards = Card.arraySort(new Card[] { playerCard, computerCard }, 2);
        return playerCard.value == playedCards[0].value;
    }

    // find the lowest card in the computer's hand
    public static int findLowestComputerCard(Hand computerHand, JLabel computerCardLabel) {
        Card lowestCard = computerHand.inspectCard(0);
        int lowestCardIndex = 0;
        int handSize = computerHand.getNumCards();

        for (int i = 1; i < handSize; i++) {
            Card currentCard = computerHand.inspectCard(i);

            if (lowestCard.getValue() > currentCard.getValue()) {
                lowestCard = currentCard;
                lowestCardIndex = i;
            }
        }

        setComputerCard(lowestCard, computerCardLabel);
        return lowestCardIndex;
    }

    // set the computer's chosen card on the board
    public static void setComputerCard(Card computerCard, JLabel computerCardLabel) {
        computerCardLabel.setIcon(GUICard.getIcon(computerCard));
    }
}

// class for tracking player and computer scores
class ScoreCard {
    private int playerScore;
    private int computerScore;

    public ScoreCard() {
        this.setPlayerScore(0);
        this.setComputerScore(0);
    }

    public String getCurrentScore() {
        return "Player: " + this.getPlayerScore() + " Computer: " + this.getComputerScore();
    }

    public void setPlayerScore(int newScore) {
        this.playerScore = newScore;
    }

    public int getPlayerScore() {
        return this.playerScore;
    }

    public void setComputerScore(int newScore) {
        this.computerScore = newScore;
    }

    public int getComputerScore() {
        return this.computerScore;
    }
}

class CardTable extends JFrame {
    static int MAX_CARDS_PER_HAND = 56;
    static int MAX_PLAYERS = 2; // for now, we only allow 2 person games

    public JPanel pnlComputerHand, pnlHumanHand, pnlPlayArea, pnlScoreBoard;

    private int numCardsPerHand;
    private int numPlayers;

    public CardTable(String title, int numCardsPerHand, int numPlayers) {
        super();
        this.setTitle(title);
        this.numCardsPerHand = numCardsPerHand;
        this.numPlayers = numPlayers;
        pnlComputerHand = new JPanel();
        pnlHumanHand = new JPanel();
        pnlPlayArea = new JPanel();
        pnlScoreBoard = new JPanel();

        TitledBorder playerBorderTitle = BorderFactory.createTitledBorder("Player Hand");
        TitledBorder playAreaBorderTitle = BorderFactory.createTitledBorder("Play Area");
        TitledBorder computerBorderTitle = BorderFactory.createTitledBorder("Computer Hand");
        TitledBorder scoreBoardBorderTitle = BorderFactory.createTitledBorder("Score Board");

        FlowLayout plyHandLayout = new FlowLayout();
        FlowLayout cmpHandLayout = new FlowLayout();
        GridLayout playAreaLayout = new GridLayout(2, 2);
        FlowLayout cmpScoreLayout = new FlowLayout();

        pnlComputerHand.setLayout(cmpHandLayout);
        pnlHumanHand.setLayout(plyHandLayout);
        pnlPlayArea.setLayout(playAreaLayout);
        pnlScoreBoard.setLayout(cmpScoreLayout);

        pnlPlayArea.setBorder(playAreaBorderTitle);
        pnlHumanHand.setBorder(playerBorderTitle);
        pnlComputerHand.setBorder(computerBorderTitle);
        pnlScoreBoard.setBorder(scoreBoardBorderTitle);

        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        pnlComputerHand.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlComputerHand.setPreferredSize(new Dimension(50, 70));
        pnlHumanHand.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlHumanHand.setPreferredSize(new Dimension(50, 70));
        pnlScoreBoard.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlScoreBoard.setPreferredSize(new Dimension(50, 10));
        pnlPlayArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlPlayArea.setPreferredSize(new Dimension(50, 150));

        this.add(pnlComputerHand);
        this.add(pnlPlayArea);
        this.add(pnlHumanHand);
        this.add(pnlScoreBoard);
    }

    public int getNumCardsPerHand() {
        return numCardsPerHand;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}

class GUICard {
    private static Icon[][] iconCards = new ImageIcon[14][4]; // 14 = A thru K + joker
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

        // search every valid value for a match - return true as soon as match found
        for (int i = 0; i < validValues.length; i++) {
            if (value == validValues[i]) {
                return true;
            }
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
            // Use for-each loop to assign a card with each value in the current suit
            for (char value : valueArray) {
                masterPack[i] = new Card(value, suit);
                i++;
            }
        }

    }
}

// class CardGameFramework ----------------------------------------------------
class CardGameFramework {
    private static final int MAX_PLAYERS = 50;

    private int numPlayers;
    private int numPacks; // # standard 52-card packs per deck
    // ignoring jokers or unused cards
    private int numJokersPerPack; // if 2 per pack & 3 packs per deck, get 6
    private int numUnusedCardsPerPack; // # cards removed from each pack
    private int numCardsPerHand; // # cards to deal each player
    private Deck deck; // holds the initial full deck and gets
    // smaller (usually) during play
    private Hand[] hand; // one Hand for each player
    private Card[] unusedCardsPerPack; // an array holding the cards not used
    // in the game. e.g. pinochle does not
    // use cards 2-8 of any suit

    public CardGameFramework(int numPacks, int numJokersPerPack, int numUnusedCardsPerPack, Card[] unusedCardsPerPack,
            int numPlayers, int numCardsPerHand) {
        int k;

        // filter bad values
        if (numPacks < 1 || numPacks > 6)
            numPacks = 1;
        if (numJokersPerPack < 0 || numJokersPerPack > 4)
            numJokersPerPack = 0;
        if (numUnusedCardsPerPack < 0 || numUnusedCardsPerPack > 50) // > 1 card
            numUnusedCardsPerPack = 0;
        if (numPlayers < 1 || numPlayers > MAX_PLAYERS)
            numPlayers = 4;
        // one of many ways to assure at least one full deal to all players
        if (numCardsPerHand < 1 || numCardsPerHand > numPacks * (52 - numUnusedCardsPerPack) / numPlayers)
            numCardsPerHand = numPacks * (52 - numUnusedCardsPerPack) / numPlayers;

        // allocate
        this.unusedCardsPerPack = new Card[numUnusedCardsPerPack];
        this.hand = new Hand[numPlayers];
        for (k = 0; k < numPlayers; k++)
            this.hand[k] = new Hand();
        deck = new Deck(numPacks);

        // assign to members
        this.numPacks = numPacks;
        this.numJokersPerPack = numJokersPerPack;
        this.numUnusedCardsPerPack = numUnusedCardsPerPack;
        this.numPlayers = numPlayers;
        this.numCardsPerHand = numCardsPerHand;
        for (k = 0; k < numUnusedCardsPerPack; k++)
            this.unusedCardsPerPack[k] = unusedCardsPerPack[k];

        // prepare deck and shuffle
        newGame();
    }

    // constructor overload/default for game like bridge
    public CardGameFramework() {
        this(1, 0, 0, null, 4, 13);
    }

    public Hand getHand(int k) {
        // hands start from 0 like arrays

        // on error return automatic empty hand
        if (k < 0 || k >= numPlayers)
            return new Hand();

        return hand[k];
    }

    public Card getCardFromDeck() {
        return deck.dealCard();
    }

    public int getNumCardsRemainingInDeck() {
        return deck.getNumCards();
    }

    public void newGame() {
        int k, j;

        // clear the hands
        for (k = 0; k < numPlayers; k++)
            hand[k].resetHand();

        // restock the deck
        deck.init(numPacks);

        // remove unused cards
        for (k = 0; k < numUnusedCardsPerPack; k++)
            deck.removeCard(unusedCardsPerPack[k]);

        // add jokers
        for (k = 0; k < numPacks; k++)
            for (j = 0; j < numJokersPerPack; j++)
                deck.addCard(new Card('X', Suit.values()[j]));

        // shuffle the cards
        deck.shuffle();
    }

    public boolean deal() {
        // returns false if not enough cards, but deals what it can
        int k, j;
        boolean enoughCards;

        // clear all hands
        for (j = 0; j < numPlayers; j++)
            hand[j].resetHand();

        enoughCards = true;
        for (k = 0; k < numCardsPerHand && enoughCards; k++) {
            for (j = 0; j < numPlayers; j++)
                if (deck.getNumCards() > 0)
                    hand[j].takeCard(deck.dealCard());
                else {
                    enoughCards = false;
                    break;
                }
        }

        return enoughCards;
    }

    void sortHands() {
        int k;

        for (k = 0; k < numPlayers; k++)
            hand[k].sort();
    }

    Card playCard(int playerIndex, int cardIndex) {
        // returns bad card if either argument is bad
        if (playerIndex < 0 || playerIndex > numPlayers - 1 || cardIndex < 0 || cardIndex > numCardsPerHand - 1) {
            // Creates a card that does not work
            return new Card('M', Suit.spades);
        }

        // return the card played
        return hand[playerIndex].playCard(cardIndex);

    }

    boolean takeCard(int playerIndex) {
        // returns false if either argument is bad
        if (playerIndex < 0 || playerIndex > numPlayers - 1)
            return false;

        // Are there enough Cards?
        if (deck.getNumCards() <= 0)
            return false;

        return hand[playerIndex].takeCard(deck.dealCard());
    }

}
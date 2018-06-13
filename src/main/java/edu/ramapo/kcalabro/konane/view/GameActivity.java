//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project: Two Player Konane - Project 3                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/28/18                                           *
//     ************************************************************

package edu.ramapo.kcalabro.konane.view;

/**
 * Created by KyleCalabro on 1/17/18.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.util.Pair;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.Vector;

import java.util.Stack;

import edu.ramapo.kcalabro.konane.R;
import edu.ramapo.kcalabro.konane.model.Game;
import edu.ramapo.kcalabro.konane.model.Board;
import edu.ramapo.kcalabro.konane.model.Position;
import edu.ramapo.kcalabro.konane.model.Serializer;
import edu.ramapo.kcalabro.konane.model.Move;

public class GameActivity extends AppCompatActivity
{
    //------------------------Data Members------------------------

    public final static String WHITE_PLAYER = "White";
    public final static String BLACK_PLAYER = "Black";

    private Boolean stonePositionClicked;
    private Boolean vacantPositionClicked;
    private Boolean stonesBlinking;
    private Boolean isCompMoveClicked;
    private Boolean isPruningEnabled;
    private Boolean isHelpButtonClicked;

    private int stoneRowPosition, stoneColPosition;
    private int vacantRowPosition, vacantColPosition;

    private BoardView boardView;

    private TextView blackScoreView;
    private TextView whiteScoreView;
    private TextView playerPassedView;
    private TextView currentPlayerView;
    private TextView stoneToMove;
    private TextView vacantPosition;
    private TextView humanPointsGained;
    private TextView compPointsGained;

    private EditText plyCutoffView;

    private Button alphaBetaPruningButton;
    private Button makeMoveButton;
    private Button unselectAllButton;
    private Button passPlayButton;
    private Button saveGameButton;
    private Button helpButton;
    private Button compMakeMoveButton;

    private Move move;

    private Spinner algoSpinner;

    private Game game;
    private Board board;

    private String currentPlayer;

    private Vector<Position> selectedOpenPositions = new Vector<Position>();

    //------------------------Member Functions------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Removes the tile bar, for aesthetic purposes.
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        // Determine if the user wants to start a new game.
        Intent mainIntent = getIntent();
        boolean newGame = mainIntent.getExtras().getBoolean(MainActivity.EXTRA_NEWGAME);
        int boardSize = mainIntent.getExtras().getInt("selectedBoardSize");
        String fileName = mainIntent.getExtras().getString("selectedFile");

        switch (boardSize)
        {
            case 6:
                setContentView(R.layout.activity_game);
                break;
            case 8:
                setContentView(R.layout.activity_game_8);
                break;
            case 10:
                setContentView(R.layout.activity_game_10);
                break;
            default:
                setContentView(R.layout.activity_game);
                boardSize = 6;
                break;
        }

        boardView = new BoardView(this);

        blackScoreView = findViewById(R.id.blackStoneScore);
        whiteScoreView = findViewById(R.id.whiteStoneScore);
        playerPassedView = findViewById(R.id.playerPassed);
        currentPlayerView = findViewById(R.id.currentPlayer);
        compPointsGained = findViewById(R.id.compScoreGained);
        humanPointsGained = findViewById(R.id.humanScoreGained);

        plyCutoffView = findViewById(R.id.plyCutoffBox);

        alphaBetaPruningButton = findViewById(R.id.alphaBetaPruningButton);
        alphaBetaPruningButton.setOnClickListener(alphaBetaPruningButtonListener);

        makeMoveButton = findViewById(R.id.makeMoveButton);
        makeMoveButton.setOnClickListener(makeMoveButtonHandler);

        compMakeMoveButton = findViewById(R.id.compMakeMoveButton);
        compMakeMoveButton.setOnClickListener(compMakeMoveHandler);

        passPlayButton = findViewById(R.id.passPlayButton);
        passPlayButton.setOnClickListener(passPlayButtonHandler);

        helpButton = findViewById(R.id.branchBoundButton);
        helpButton.setOnClickListener(helpButtonHandler);

        unselectAllButton = findViewById(R.id.unselectButton);
        unselectAllButton.setOnClickListener(unselectAllButtonHandler);

        saveGameButton = findViewById(R.id.saveGameButton);
        saveGameButton.setOnClickListener(saveGameButtonHandler);

        move = null;

        // If the user wishes to start a new game.
        if(newGame)
        {
            game = new Game(boardSize);

            guessStonesRemoved();
        }

        // Otherwise, the user wishes to load a game from a saved file.
        else
        {
            game = new Game(boardSize);

            // Attempt to load a game from a saved file.
            if(game.getSerializer().restoreFile(game, fileName, boardSize))
            {
                updateView(true);
            }
            else
            {
                generateToastMessage("That file does not exist! Try again.");
                Intent mainMenu = new Intent(GameActivity.this, MainActivity.class);
                startActivity(mainMenu);
                finish();
            }
        }

        isPruningEnabled = false;
        stonePositionClicked = false;
        vacantPositionClicked = false;
        stonesBlinking = false;
        isCompMoveClicked = false;
        isHelpButtonClicked = false;

        stoneRowPosition = 0;
        stoneColPosition = 0;

        vacantRowPosition = 0;
        vacantColPosition = 0;

        updateView(false);
    }

    /**
     * To place data in a bundle to be read from by an EndRoundActivity object.
     */

    public void endActivity()
    {
        Intent endGame = new Intent(GameActivity.this, EndGameActivity.class);
        endGame.putExtra("blackStonesScore", game.getPlayers()[0].getRoundScore());
        endGame.putExtra("whiteStonesScore", game.getPlayers()[1].getRoundScore());

        startActivity(endGame);
        finish();
    }

    //------------------------Output/Display Updating Functions------------------------

    /**
     * To display a toast message to the screen.
     *
     * @param message The String to be displayed to the screen via toast.
     */

    public void generateToastMessage(String message)
    {
        Toast toastToDisplay = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toastToDisplay.setGravity(Gravity.CENTER, 0, 0);
        toastToDisplay.show();
    }

    /**
     * To prompt the human player to choose between two coordinates, of which represents
     * the black stone removed from the board is.
     */

    public void guessStonesRemoved()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Select what you believe to be the black stone removed (row, col):");

        int whiteStoneRow = game.getBoard().getWhiteStoneRemoved().getRowPosition() + 1;
        int whiteStoneCol = game.getBoard().getWhiteStoneRemoved().getColPosition() + 1;

        int blackStoneRow = game.getBoard().getBlackStoneRemoved().getRowPosition() + 1;
        int blackStoneCol = game.getBoard().getBlackStoneRemoved().getColPosition() + 1;

        String whiteStoneCoordinates = whiteStoneRow + ", " + whiteStoneCol;
        String blackStoneCoordinates = blackStoneRow + ", " + blackStoneCol;

        dialogBuilder.setPositiveButton(blackStoneCoordinates, stonesRemovedListener);
        dialogBuilder.setNegativeButton(whiteStoneCoordinates, stonesRemovedListener);
        dialogBuilder.create().show();
    }

    /**
     * To update the view with all the proper information.
     *
     * @param isActive Boolean value indicating if the board is to be displayed.
     */

    public void updateView(boolean isActive)
    {
        // Determine if it is the human player's turn.
        if(game.getCurrentPlayer().equals(Game.HUMAN_PLAYER))
        {
            if(game.getIsHumanUsingBlack())
            {
                currentPlayerView.setText("Current Player: Human (Black)");
            }
            else
            {
                currentPlayerView.setText("Current Player: Human (White)");
            }

            // Determine if the white player passed their previous play.
            if(game.getPlayers()[1].isPlayerPassed())
            {
                playerPassedView.setText("Previous Player Passed: Yes");
            }
            else
            {
                playerPassedView.setText("Previous Player Passed: No");
            }

            if(!game.getPlayers()[0].canMakePlay(game, game.getIsHumanUsingBlack()))
            {
                generateToastMessage("Sorry, you cannot make a move and must pass your play!");
                makeMoveButton.setEnabled(false);
                unselectAllButton.setEnabled(false);
                passPlayButton.setEnabled(true);
                helpButton.setEnabled(false);
            }
            else
            {
                helpButton.setEnabled(true);
                makeMoveButton.setEnabled(true);
                unselectAllButton.setEnabled(true);
            }

            compMakeMoveButton.setEnabled(false);
        }

        // Otherwise, it is the computer player's turn.
        else
        {
            passPlayButton.setEnabled(false);
            makeMoveButton.setEnabled(false);
            unselectAllButton.setEnabled(false);
            helpButton.setEnabled(false);
            compMakeMoveButton.setEnabled(true);

            if(game.getIsHumanUsingBlack())
            {
                currentPlayerView.setText("Current Player: Computer (White Stones)");
            }
            else
            {
                currentPlayerView.setText("Current Player: Computer (Black Stones)");
            }

            // Determine if the human player passed their previous play.
            if(game.getPlayers()[0].isPlayerPassed())
            {
                playerPassedView.setText("Previous Player Passed: Yes");
            }
            else
            {
                playerPassedView.setText("Previous Player Passed: No");
            }

            if(!game.getPlayers()[1].canMakePlay(game, !game.getIsHumanUsingBlack()))
            {
                generateToastMessage("The computer could not make a move and has passed its play!");
                game.swapCurrentPlayer();
                updateView(true);
            }
        }

        humanPointsGained.setText("Human Points Gained: ");
        compPointsGained.setText("Comp. Points Gained: ");

        // Update all the remaining TextView's to display proper information.
        blackScoreView.setText("Black Score: " + game.getPlayers()[0].getRoundScore());
        whiteScoreView.setText("White Score: " + game.getPlayers()[1].getRoundScore());

        // Update the board view.
        boardView.updateBoardView(game.getBoard(), isActive);

        stonePositionClicked = false;
        vacantPositionClicked = false;
    }

    /**
     * To handle a human player clicking on a position of the grid.
     */

    public void makeMove(View view)
    {
        board = game.getBoard();
        currentPlayer = game.getCurrentPlayer();

        // Get the row and column positions of the clicked slot of the grid.
        int[] coordinates = getPositionCoordinates(view);

        int rowPosition = coordinates[0];
        int colPosition = coordinates[1];

        // If a stone's position has already been clicked, the next position must be a vacant one.
        if(stonePositionClicked)
        {
            if(board.isPositionOpen(rowPosition, colPosition))
            {
                vacantRowPosition = rowPosition;
                vacantColPosition = colPosition;

                vacantPositionClicked = true;

                highlightPosition(rowPosition, colPosition, false, true);

                Position newOpenPosition = new Position(rowPosition, colPosition);
                selectedOpenPositions.add(newOpenPosition);
            }

            else
            {
                String message = "Sorry, you must select a vacant puka for movement!";

                generateToastMessage(message);
            }
        }

        // Otherwise, the user is selecting a stone that they will use to make a jump.
        else
        {
            if (currentPlayer.equals(Game.HUMAN_PLAYER) && board.isPositionBlack(rowPosition, colPosition) && game.getIsHumanUsingBlack())
            {
                stonePositionClicked = true;

                stoneRowPosition= rowPosition;
                stoneColPosition = colPosition;

                highlightPosition(rowPosition, colPosition, true, false);
            }

            else if (currentPlayer.equals(Game.HUMAN_PLAYER) && board.isPositionWhite(rowPosition, colPosition) && !game.getIsHumanUsingBlack())
            {
                stonePositionClicked = true;

                stoneRowPosition= rowPosition;
                stoneColPosition = colPosition;

                highlightPosition(rowPosition, colPosition, false, false);
            }

            else
            {
                String message = "Sorry, you are not able to select that stone for movement!";

                generateToastMessage(message);
            }
        }
    }

    /**
     * To unhighlight a given row and column position on the grid.
     *
     * @param row The row position.
     * @param col The column position.
     */

    private void unhighlightPosition(int row, int col)
    {
        // Find the id of the given slot in the grid representing the board.
        int slotId = getResources().getIdentifier("position_" +
                Integer.toString(row) + "_" + Integer.toString(col), "id", getPackageName());

        TextView slotView = findViewById(slotId);

        // Unhighlight that given position.
        slotView.setBackgroundResource(R.drawable.buttonborder);

        if(game.getBoard().isPositionBlack(row, col))
        {
            slotView.setBackgroundResource(R.drawable.black_stone_border);
        }
        else if(game.getBoard().isPositionWhite(row, col))
        {
            slotView.setBackgroundResource(R.drawable.white_stone_border);
        }
        else
        {
            slotView.setBackgroundResource(R.drawable.buttonborder);
        }
    }

    /**
     * To highlight a given row and column position on the grid.
     *
     * @param row The row position.
     * @param col The column position.
     * @param isBlack Boolean value indicating if the current player is using black stones.
     * @param isVacant Boolean value indicating if the given row/col position is open.
     */

    private void highlightPosition(int row, int col, boolean isBlack, boolean isVacant)
    {
        // Find the id of the given slot in the grid representing the board.
        int slotId = getResources().getIdentifier("position_" +
                Integer.toString(row) + "_" + Integer.toString(col), "id", getPackageName());

        TextView slotView = findViewById(slotId);

        if(isBlack)
        {
            slotView.setBackgroundResource(R.drawable.black_stone_border_hl);
        }

        else if(isVacant)
        {
            slotView.setBackgroundResource(R.drawable.buttonborder_hl);
        }

        // Otherwise, the position is a white stone.
        else
        {
            slotView.setBackgroundResource(R.drawable.white_stone_border_hl);
        }

    }

    /**
     * To get the coordinates of the grid position clicked on by the user.
     *
     * @param view The view from which the user clicked.
     * @return Array of two integers, [0] holds the row, [1] holds the column.
     */

    private int[] getPositionCoordinates(View view)
    {
        // Get the id string from the view.
        String positionCoordinates = getResources().getResourceEntryName(view.getId());

        // The pattern to be matched for position coordinates of the grid.
        String pattern = "(position_([0-9])_([0-9]))";

        // The regular expression to use in conjunction with the pattern.
        Pattern regex = Pattern.compile(pattern);

        // Parse the coordinates from the grid.
        Matcher matcher = regex.matcher(positionCoordinates);

        matcher.find();

        int rowPosition = Integer.parseInt(matcher.group(2));
        int colPosition = Integer.parseInt(matcher.group(3));

        int[] coordinates = new int[2];

        coordinates[0] = rowPosition;
        coordinates[1] = colPosition;

        return coordinates;
    }

    /**
     * To prompt the user to enter a file name to save a game to.
     */

    private void getFileName()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        final EditText filename = new EditText(GameActivity.this);

        dialogBuilder.setTitle("Serializing Game");
        dialogBuilder.setMessage("Please enter a file name to save the game as (without .txt): ");
        dialogBuilder.setView(filename);

        // Take in the tournament score from the user.
        dialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
               String fileName = filename.getText().toString() + ".txt";
                try
                {
                    // Attempt to save the file.
                    game.getSerializer().serializeFile(game, fileName);
                }
                catch(IOException ioException)
                {
                    ioException.printStackTrace();
                }
                finish();
            }
        });

        // Display the dialog to the user.
        AlertDialog inputBox = dialogBuilder.create();
        inputBox.setCanceledOnTouchOutside(false);
        inputBox.show();
    }

    /**
     * To blink a given pair of positions representing stones when using an AI algorithm.
     *
     * @param move The move containing positions to blink.
     */

    private void blinkStones(Move move)
    {
        for (Position s : move.getMovePath())
        {
            Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(300);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);

            // Find the id of the given slot in the grid representing the board.
            int stoneId = getResources().getIdentifier("position_" +
                    Integer.toString(s.getRowPosition()) + "_" + Integer.toString(s.getColPosition()), "id", getPackageName());

            stoneToMove = findViewById(stoneId);

            stoneToMove.startAnimation(animation);

            System.out.println(s.getRowPosition() + "-" + s.getColPosition());
        }
    }

    /**
     * To stop the stones from blinking when using a search algorithm.
     */

    private void stopBlinkingStones(Move move)
    {
        for (Position s : move.getMovePath())
        {
            // Find the id of the given slot in the grid representing the board.
            int stoneId = getResources().getIdentifier("position_" +
                    Integer.toString(s.getRowPosition()) + "_" + Integer.toString(s.getColPosition()), "id", getPackageName());

            TextView stoneToClear = findViewById(stoneId);

            stoneToClear.clearAnimation();
        }

    }

    /**
     * To retrieve the cutoff value from the text box of the layout.
     *
     * @return Integer value representing the cutoff value.
     */

    private int getCutoffValue()
    {
        int cutoff = 0;

        if(!plyCutoffView.getText().toString().isEmpty())
        {
            cutoff = Integer.parseInt(plyCutoffView.getText().toString());
        }
        else
        {
            generateToastMessage("You must enter a value for the ply cutoff!");
        }
        return cutoff;
    }

    /**
     * To modify the current based on the move being displayed to the user.
     *
     * @param move The move to modify the board with.
     */

    private void makeMoveAlgo(Move move)
    {
        Vector<Position> positionsVector = new Vector<>();

        for (Position s : move.getMovePath())
        {
            positionsVector.add(new Position(s.getRowPosition(), s.getColPosition()));
        }

        Position startingPosition = positionsVector.elementAt(0);
        positionsVector.removeElementAt(0);

        int[] stonePosition = new int[]{startingPosition.getRowPosition(), startingPosition.getColPosition()};

        Boolean isBlack = false;
        if(game.turnColor == 'B')
        {
            isBlack = true;
        }

        game.getBoard().modifyBoard(isBlack, stonePosition, positionsVector, true);

        game.swapCurrentPlayer();

        updateView(true);
    }

    /**
     * To handle when the user clicks the Make Move button.
     */

    View.OnClickListener makeMoveButtonHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if(stonePositionClicked && vacantPositionClicked)
            {
                int[] stonePosition = new int[]{stoneRowPosition, stoneColPosition};
                int[] vacantPosition = new int[]{vacantRowPosition, vacantColPosition};

                if(game.getCurrentPlayer() == Game.HUMAN_PLAYER)
                {
                    if(game.getPlayers()[0].isMoveValid(game, game.getIsHumanUsingBlack(), stonePosition, selectedOpenPositions))
                    {
                        updateView(true);
                        selectedOpenPositions.clear();
                    }
                    else
                    {
                        generateToastMessage("Sorry, but that move is not valid! Try again.");
                    }
                }

                if(stonesBlinking)
                {
                    //stopBlinkingStones();
                }

                unhighlightPosition(stoneRowPosition, stoneColPosition);

                for(int pos = 0; pos < selectedOpenPositions.size(); pos++)
                {
                    int row = selectedOpenPositions.elementAt(pos).getRowPosition();
                    int col = selectedOpenPositions.elementAt(pos).getColPosition();

                    unhighlightPosition(row, col);
                }

                stonePositionClicked = false;
                vacantPositionClicked = false;
                selectedOpenPositions.clear();

            }

            else
            {
                String message = "Sorry, you must select a vacant position to move to!";

                generateToastMessage(message);
            }
        }
    });

    /**
     * To handle when the user clicks the Unselect All button.
     */

    View.OnClickListener unselectAllButtonHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            stonePositionClicked = false;
            vacantPositionClicked = false;

            unhighlightPosition(stoneRowPosition, stoneColPosition);

            for(int pos = 0; pos < selectedOpenPositions.size(); pos++)
            {
                int row = selectedOpenPositions.elementAt(pos).getRowPosition();
                int col = selectedOpenPositions.elementAt(pos).getColPosition();

                unhighlightPosition(row, col);
            }

            selectedOpenPositions.clear();
        }
    });

    /**
     * To handle when the user clicks the Pass Play button.
     */

    View.OnClickListener passPlayButtonHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if(game.hasGameEnded())
            {
                endActivity();
            }
            else
            {
                passPlayButton.setEnabled(false);
                game.swapCurrentPlayer();
                updateView(true);
            }
        }
    });

    /**
     * To handle when the user clicks the Save Game button.
     */

    View.OnClickListener saveGameButtonHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
           getFileName();
        }
    });

    /**
     * To handle when the user clicks the Help button.
     */

    View.OnClickListener helpButtonHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if(isHelpButtonClicked)
            {
                move = game.getMinimaxMove();
                stopBlinkingStones(move);

                makeMoveAlgo(game.getMinimaxMove());

                if(game.getIsHumanUsingBlack())
                {
                    game.getPlayers()[0].setRoundScore(game.getBoard().getNumWhiteStonesCaptured());
                }
                else
                {
                    game.getPlayers()[0].setRoundScore(game.getBoard().getNumBlackStonesCaptured());
                }

                // Have the computer make its best move.
                helpButton.setBackgroundResource(R.drawable.buttonborder);

                updateView(true);

                isHelpButtonClicked = false;
            }
            else
            {
                if (getCutoffValue() >= 0)
                {
                    System.out.println(getCutoffValue());

                    helpButton.setBackgroundResource(R.drawable.buttonborder_hl);
                    isHelpButtonClicked = true;

                    long start = System.currentTimeMillis();

                    game.initiateMiniMax(game.getPlayers()[0], getCutoffValue(), isPruningEnabled);

                    long end = System.currentTimeMillis();

                    generateToastMessage("Minimax took: " + (end - start) + " ms to run!");

                    Move move = game.getMinimaxMove();

                    blinkStones(move);

                    compPointsGained.setText("Comp. Points Gained: 0");
                    humanPointsGained.setText("Human Points Gained: " + (move.getMovePath().size() - 1));

                    plyCutoffView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable)
                        {
                            if(isHelpButtonClicked)
                            {
                                isHelpButtonClicked = false;
                                helpButton.setBackgroundResource(R.drawable.buttonborder);
                                stopBlinkingStones(game.getMinimaxMove());
                            }
                        }
                    });

                }
                else
                {
                    generateToastMessage("Please enter a higher value for the cutoff!");
                }
            }
        }
    });

    /**
     * To handle when the user clicks the computer play button.
     */

    View.OnClickListener compMakeMoveHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            // On the first click of the button, the computer's best move will be displayed.
            // On the second click of the button, the computer's best move will be made.
            if(isCompMoveClicked)
            {
                move = game.getMinimaxMove();
                stopBlinkingStones(move);

                makeMoveAlgo(game.getMinimaxMove());

                if(!game.getIsHumanUsingBlack())
                {
                    game.getPlayers()[1].setRoundScore(game.getBoard().getNumWhiteStonesCaptured());
                }
                else
                {
                    game.getPlayers()[1].setRoundScore(game.getBoard().getNumBlackStonesCaptured());
                }

                // Have the computer make its best move.
                compMakeMoveButton.setBackgroundResource(R.drawable.buttonborder);
                isCompMoveClicked = false;

                updateView(true);
            }
            else
            {
                if(getCutoffValue() >= 0)
                {
                    compMakeMoveButton.setBackgroundResource(R.drawable.buttonborder_hl);

                    isCompMoveClicked = true;

                    long start = System.currentTimeMillis();

                    game.initiateMiniMax(game.getPlayers()[1], getCutoffValue(), isPruningEnabled);

                    long end = System.currentTimeMillis();

                    Move move = game.getMinimaxMove();

                    generateToastMessage("Minimax took: " + (end - start) + " ms to run!");

                    blinkStones(move);

                    humanPointsGained.setText("Human Points Gained: 0");
                    compPointsGained.setText("Comp. Points Gained: " + (move.getMovePath().size() - 1));

                    plyCutoffView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable)
                        {
                            if(isCompMoveClicked)
                            {
                                isCompMoveClicked = false;
                                compMakeMoveButton.setBackgroundResource(R.drawable.buttonborder);
                                stopBlinkingStones(game.getMinimaxMove());
                            }
                        }
                    });

                }
                else
                {
                    generateToastMessage("Please enter a higher value for the cutoff!");
                }
            }
        }
    });

    /**
     * To prompt the user to select a side to place a tile on.
     */

    DialogInterface.OnClickListener stonesRemovedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {

            switch(i)
            {
                // If the user did not select the black stone.
                case DialogInterface.BUTTON_NEGATIVE:
                    game.setIsHumanUsingBlack(false);
                    game.getPlayers()[0].setStoneColor(Game.WHITE_PLAYER);
                    game.getPlayers()[1].setStoneColor(Game.BLACK_PLAYER);
                    game.swapCurrentPlayer();
                    updateView(true);
                    break;

                // If the user did select the black stone.
                case DialogInterface.BUTTON_POSITIVE:
                    game.setIsHumanUsingBlack(true);
                    updateView(true);
                    break;
            }

            updateView(true);
        }
    };

    /**
     * To handle when the user clicks the computer play button.
     */

    View.OnClickListener alphaBetaPruningButtonListener = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if(isPruningEnabled)
            {
                if(isCompMoveClicked)
                {
                    isCompMoveClicked = false;
                    compMakeMoveButton.setBackgroundResource(R.drawable.buttonborder);
                    stopBlinkingStones(game.getMinimaxMove());
                }

                if(isHelpButtonClicked)
                {
                    isHelpButtonClicked = false;
                    helpButton.setBackgroundResource(R.drawable.buttonborder);
                    stopBlinkingStones(game.getMinimaxMove());
                }

                // If the button has been clicked already, the user wishes to disable alpha beta pruning.
                alphaBetaPruningButton.setBackgroundResource(R.drawable.buttonborder);
                isPruningEnabled = false;
            }
            else
            {
                if(isCompMoveClicked)
                {
                    isCompMoveClicked = false;
                    compMakeMoveButton.setBackgroundResource(R.drawable.buttonborder);
                    stopBlinkingStones(game.getMinimaxMove());
                }

                if(isHelpButtonClicked)
                {
                    isHelpButtonClicked = false;
                    helpButton.setBackgroundResource(R.drawable.buttonborder);
                    stopBlinkingStones(game.getMinimaxMove());
                }

                // If the button has not yet been clicked, the user wishes to enable alpha beta pruning.
                alphaBetaPruningButton.setBackgroundResource(R.drawable.buttonborder_hl);
                isPruningEnabled = true;
            }

        }
    });
}

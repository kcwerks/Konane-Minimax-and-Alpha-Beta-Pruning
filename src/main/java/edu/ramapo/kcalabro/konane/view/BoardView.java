//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project: Two Player Konane - Project 3                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/28/18                                           *
//     ************************************************************

package edu.ramapo.kcalabro.konane.view;

/**
 * Created by KyleCalabro on 1/18/18.
 */

import android.widget.TextView;

import edu.ramapo.kcalabro.konane.model.Board;

import edu.ramapo.kcalabro.konane.R;

public class BoardView
{
    //------------------------Data Members------------------------

    public final static char WHITE_STONE = 'W';

    public final static char BLACK_STONE = 'B';

    public final static char OPEN_SLOT = 'O';

    private GameActivity activity;

    //------------------------Member Functions------------------------

    /**
     * Default constructor for the BoardView class.
     *
     * @param activity RoundActivity object representing current round.
     */

    public BoardView(GameActivity activity)
    {
        this.activity = activity;
    }

    /**
     * To update the board view being displayed to the screen.
     *
     * @param board Object of the board class being used by the current game of Konane.
     */

    public void updateBoardView(Board board, Boolean isActive)
    {
        // Iterate through the given board.
        if (isActive)
        {
            for (int row = 0; row < board.getBoardSize(); row++)
            {
                for (int col = 0; col < board.getBoardSize(); col++)
                {
                    // Find the id of the given slot in the grid representing the board.
                    int slotId = activity.getResources().getIdentifier("position_" +
                            Integer.toString(row) + "_" + Integer.toString(col), "id", activity.getPackageName());

                    TextView slotView = activity.findViewById(slotId);

                    // Get the color of the stone at the given position of the board.
                    char stoneColor = board.getStoneColorAtPosition(row, col);

                    // Properly display the color of the stone at the given position.
                    switch (stoneColor)
                    {
                        case BLACK_STONE:
                            slotView.setBackgroundResource(R.drawable.black_stone_border);
                            break;
                        case WHITE_STONE:
                            slotView.setBackgroundResource(R.drawable.white_stone_border);
                            break;
                        case OPEN_SLOT:
                            slotView.setBackgroundResource(R.drawable.buttonborder);
                            break;
                    }
                }
            }
        }
    }
}

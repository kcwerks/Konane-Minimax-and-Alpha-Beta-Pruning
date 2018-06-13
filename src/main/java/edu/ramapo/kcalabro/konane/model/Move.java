//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project: Two Player Konane - Project 3                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/28/18                                           *
//     ************************************************************

package edu.ramapo.kcalabro.konane.model;

/**
 * Created by KyleCalabro on 3/19/18.
 */

import java.util.ArrayList;

public class Move
{
    //------------------------Data Members------------------------

    // The position from which a move is to be made.
    private Position source;

    // The position to make a move to.
    private Position dest;

    // The score gained in a given move.
    private int score;

    // The minimax value/score attached to a given move.
    private int minimaxValue;

    // The path of any given move (multi-hop, etc.)
    private ArrayList<Position> movePath = new ArrayList<>();

    //------------------------Member Functions------------------------

    /**
     * Default constructor for the Move class.
     *
     * @param source The position from which to make a move from.
     * @param dest The position to make a move to.
     */

    Move(Position source, Position dest)
    {
        this.source = source;
        this.dest = dest;
    }

    /**
     * To get the score gained of any given move.
     *
     * @return
     */

    public int getScore()
    {
        return score;
    }

    /**
     * To get the minimax value/score attached to a move.
     *
     * @return Integer value representing the minimax value/score.
     */

    public int getMinimaxValue()
    {
        return minimaxValue;
    }

    /**
     * To set the minimax value/score attached to a move.
     *
     * @param minimaxValue Integer value which to set the minimax value/score to.
     */

    public void setMinimaxValue(int minimaxValue)
    {
        this.minimaxValue = minimaxValue;
    }

    /**
     * To get the path of a multi-hop move.
     *
     * @return ArrayList of Positions representing the path of a multi-hop move.
     */

    public ArrayList<Position> getMovePath()
    {
        return movePath;
    }

    /**
     * To set the path of a multi-hop move.
     *
     * @param movePath ArrayList of Positions representing the path of a multi-hop move.
     */

    public void setMovePath(ArrayList<Position> movePath)
    {
        score = movePath.size() - 1;
        this.movePath = movePath;
    }
}

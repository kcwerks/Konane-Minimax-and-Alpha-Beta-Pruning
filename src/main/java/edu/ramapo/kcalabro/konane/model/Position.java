//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project: Two Player Konane - Project 3                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/28/18                                           *
//     ************************************************************

package edu.ramapo.kcalabro.konane.model;

/**
 * Created by KyleCalabro on 2/13/18.
 */

public class Position
{
    //------------------------Data Members------------------------

    private int row;

    private int col;

    //------------------------Member Functions------------------------

    /**
     * Default constructor for the Position class.
     *
     * @param row the row position of the position.
     * @param col the column position of the position.
     */

    public Position(int row, int col)
    {
        setRowPosition(row);
        setColPosition(col);
    }

    @Override
    public boolean equals(Object position)
    {
        Position tempPosition = (Position) position;
        return row == tempPosition.row && col == tempPosition.col;
    }

    /**
     * Getter function for the row position.
     *
     * @return The row position.
     */

    public int getRowPosition()
    {
        return row;
    }

    /**
     * Getter function for the column position.
     *
     * @return The column position.
     */

    public int getColPosition()
    {
        return col;
    }

    /**
     * Setter function for the row position.
     *
     * @param row The row position.
     */

    public void setRowPosition(int row)
    {
        this.row = row;
    }

    /**
     * Setter function for the column position.
     *
     * @param col The column position.
     */

    public void setColPosition(int col)
    {
        this.col = col;
    }

    public Position getSouthPosition(Position currentPosition, Board board, char opposingColor)
    {
        if(currentPosition.getRowPosition() + 2 < board.getBoardSize())
        {
            if(board.getStoneColorAtPosition(currentPosition.getRowPosition() + 2, currentPosition.getColPosition()) == Board.OPEN_SLOT)
            {
                if(board.getStoneColorAtPosition(currentPosition.getRowPosition() + 1, currentPosition.getColPosition()) == opposingColor)
                {
                    return new Position(currentPosition.getRowPosition() + 2, currentPosition.getColPosition());
                }
            }
        }
        return null;
    }
    public Position getNorthPosition(Position currentPosition, Board board, char opposingColor)
    {
        if(currentPosition.getRowPosition() - 2 >= 0)
        {
            if(board.getStoneColorAtPosition(currentPosition.getRowPosition() - 2, currentPosition.getColPosition()) == Board.OPEN_SLOT)
            {
                if(board.getStoneColorAtPosition(currentPosition.getRowPosition() - 1, currentPosition.getColPosition()) == opposingColor)
                {
                    return new Position(currentPosition.getRowPosition() - 2, currentPosition.getColPosition());
                }
            }
        }
        return null;
    }
    public Position getWestPosition(Position currentPosition, Board board, char opposingColor)
    {
        if(currentPosition.getColPosition() - 2 >= 0)
        {
            if(board.getStoneColorAtPosition(currentPosition.getRowPosition(), currentPosition.getColPosition() - 2) == Board.OPEN_SLOT)
            {
                if(board.getStoneColorAtPosition(currentPosition.getRowPosition(), currentPosition.getColPosition() - 1) == opposingColor)
                {
                    return new Position(currentPosition.getRowPosition(), currentPosition.getColPosition() - 2);
                }
            }
        }
        return null;
    }
    public Position getEastPosition(Position currentPosition, Board board, char opposingColor)
    {
        if(currentPosition.getColPosition() + 2 < board.getBoardSize())
        {
            if(board.getStoneColorAtPosition(currentPosition.getRowPosition(), currentPosition.getColPosition() + 2) == Board.OPEN_SLOT)
            {
                if(board.getStoneColorAtPosition(currentPosition.getRowPosition(), currentPosition.getColPosition() + 1) == opposingColor)
                {
                    return new Position(currentPosition.getRowPosition(), currentPosition.getColPosition() + 2);
                }
            }
        }
        return null;
    }
}

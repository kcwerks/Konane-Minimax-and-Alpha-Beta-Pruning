//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project: Two Player Konane - Project 3                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/28/18                                           *
//     ************************************************************

package edu.ramapo.kcalabro.konane.model;

/**
 * Created by KyleCalabro on 3/11/18.
 */

public class Computer extends Player
{
    //------------------------Member Functions------------------------

    /**
     * Default constructor for the Computer class.
     *
     * @param stoneColor The color of stone the player is using for the game.
     */

    public Computer(String stoneColor)
    {
        setStoneColor(stoneColor);
    }

    /**
     * To determine if the computer player can move a stone.
     *
     * @param game game object representing the current round.
     * @return Boolean value indicating if the human player has a tile that can be placed in their hand.
     */

    public boolean canMakePlay(Game game, boolean isBlack) {
        // If the player can make a move, they must play.
        if (game.getBoard().canMakePlay(isBlack)) {
            return true;
        }

        // If the player cannot make a move, they must pass their turn.
        else {
            setPlayerPassed(true);
            return false;
        }
    }
}

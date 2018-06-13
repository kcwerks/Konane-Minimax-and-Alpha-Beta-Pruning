//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project: Two Player Konane - Project 3                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/28/18                                           *
//     ************************************************************

package edu.ramapo.kcalabro.konane.model;

import java.util.Collections;
import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Pair;

/**
 * Created by KyleCalabro on 1/17/18.
 */

public class Game
{
    //------------------------Data Members------------------------

    public final static String WHITE_PLAYER = "White";

    public final static String BLACK_PLAYER = "Black";

    public final static String COMPUTER_PLAYER = "Computer";

    public final static String HUMAN_PLAYER = "Human";

    // The board dimensions, as per the project description.
    public int boardSize;

    // The board used throughout the game.
    private Board board;

    // Determine if the human is playing with black stones.
    private Boolean isHumanUsingBlack;

    // The current player of the game.
    private String currentPlayer;

    // Array of players current playing the game.
    private Player[] players;

    // The root value for the best move for minimax.
    private ArrayList<Move> root;

    // Best move available for minimax.
    private Move minimaxBestMove;

    // The stone color of the current player.
    public char turnColor;

    //private Move move = new Move(-1,-1);
    private Serializer serializer;

    //------------------------Member Functions------------------------

    /**
     * Default constructor for the Game class.
     *
     * @param boardSize Integer holding the dimensions of the board.
     */

    public Game(int boardSize)
    {
        // The first player is the one using black stones.
        currentPlayer = HUMAN_PLAYER;

        board = new Board(boardSize);

        this.boardSize = boardSize;

        serializer = new Serializer();

        players = new Player[2];
        players[0] = new Human(BLACK_PLAYER);
        players[1] = new Computer(WHITE_PLAYER);
        isHumanUsingBlack = false;

        minimaxBestMove = null;

        turnColor = 'B';

        board.removeInitialStones();

        root = new ArrayList<>();
    }

    public Game(Game copyGame)
    {
        this.board = copyGame.board;
    }

    /**
     * Getter function for the board being used by the game class.
     *
     * @return Object of the board class representing the current board of the game.
     */

    public Board getBoard()
    {
        return board;
    }

    /**
     * Getter function for the array of players used by the game class.
     *
     * @return Array of objects of the player class. 0 = Black Stones, 1 = White Stones.
     */

    public Player[] getPlayers()
    {
        return players;
    }

    /**
     * To get the Player object using white stones.
     *
     * @return Object of the Player class using white stones.
     */

    private Player getWhitePlayer()
    {
        if(players[0].getStoneColor().equals("White"))
        {
            return players[0];
        }
        else
        {
            return players[1];
        }
    }

    /**
     * To get the Player object using black stones.
     *
     * @return Object of the Player class using black stones.
     */

    private Player getBlackPlayer()
    {
        if(players[0].getStoneColor().equals("Black"))
        {
            return players[0];
        }
        else
        {
            return players[1];
        }
    }

    /**
     * To swap the current player.
     */

    public void swapCurrentPlayer() {
        // If the current player is using black stones, swap the current player to the one
        // using white stones.
        if (currentPlayer.equals(HUMAN_PLAYER))
        {
            this.currentPlayer = COMPUTER_PLAYER;

            if(isHumanUsingBlack)
            {
                this.turnColor = 'W';
            }
            else
            {
                this.turnColor = 'B';
            }
        }

        // And vice-versa.
        else
        {
            this.currentPlayer = HUMAN_PLAYER;

            if(isHumanUsingBlack)
            {
                this.turnColor = 'B';
            }
            else
            {
                this.turnColor = 'W';
            }
        }
    }

    /**
     * To determine if a game has ended.
     *
     * @return Boolean value indicating if a game has ended.
     */

    public Boolean hasGameEnded()
    {
        if(players[0].isPlayerPassed() && players[1].isPlayerPassed())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Getter function for the current player of the game.
     *
     * @return String indicating the current player of the game, "Black", "White".
     */

    public String getCurrentPlayer()
    {
        return currentPlayer;
    }

    /**
     * To set the current player.
     *
     * @param currentPlayer the current player of the game.
     */

    public void setCurrentPlayer(String currentPlayer)
    {
        this.currentPlayer = currentPlayer;
    }

    /**
     * To get the serializer object for use in restoring or saving a game.
     *
     * @return Object of the serializer class.
     */

    public Serializer getSerializer()
    {
        return serializer;
    }

    /**
     * Setter function for the isHumanUsingBlack flag.
     *
     * @param isHumanUsingBlack Boolean value indicating if the human player is using black stones.
     */

    public void setIsHumanUsingBlack(Boolean isHumanUsingBlack)
    {
        this.isHumanUsingBlack = isHumanUsingBlack;
    }

    /**
     * Getter function for the isHumanUsingBlack flag.
     *
     * @return Boolean value indicating if the human player is using black stones.
     */

    public Boolean getIsHumanUsingBlack()
    {
        return isHumanUsingBlack;
    }

    /**
     * To get the best avilable move for minimax algorithm.
     *
     * @return Object of the Move class representing the best minimax move.
     */

    public Move getMinimaxMove()
    {
        return minimaxBestMove;
    }

    /**
     * To call the minimax function based on the given player's stone color.
     *
     * @param player The player which to call minimax for.
     * @param plyCutoff The ply cutoff value.
     * @param isAlphaBetaEnabled Boolean variable indicating if alpa-beta pruning is enabled.
     */

    public void initiateMiniMax(Player player, int plyCutoff, Boolean isAlphaBetaEnabled)
    {
        root.clear();

        if (player.getStoneColor().equals("White"))
        {
            whitePlayerMiniMax(0, player, Integer.MIN_VALUE, Integer.MAX_VALUE, plyCutoff, isAlphaBetaEnabled);
        }
        else
        {
            blackPlayerMinimax(0, player, Integer.MIN_VALUE , Integer.MAX_VALUE, plyCutoff, isAlphaBetaEnabled);
        }

        minimaxBestMove = getBestMove();
    }

    /**
     * To reset the current board to a saved board.
     *
     * @param savedBoard The saved board to reset the current board to.
     */

    private void resetBoard(Board savedBoard)
    {
        for (int row = 0; row < boardSize; row++)
        {
            for(int col = 0; col < boardSize; col++)
            {
                board.setBoardAtPosition(row, col, savedBoard.getStoneColorAtPosition(row, col));
            }
        }
    }

    /**
     * To make a deep copy of the current board.
     *
     * @return New Board object that is a deep copy of the given board.
     */

    private Board makeBoardCopy()
    {
        Board copyBoard = new Board(boardSize);

        // Make a copy of the board.
        for(int row = 0; row < boardSize; row++)
        {
            for(int col = 0; col < boardSize; col++)
            {
                copyBoard.setBoardAtPosition(row, col, board.getStoneColorAtPosition(row, col));
            }
        }
        return copyBoard;
    }

    /**
     * To determine if a given move is valid based on the current board for the Minimax algorithm.
     *
     * @param source The source position from which to make a move from.
     * @param dest The destination position to make a move to.
     * @param intermediatePositions Array of Positions that make up the multi-hop move.
     * @param sourceColor The stone color of the source position.
     * @return Boolean variable to indicate if a given move is valid.
     */

    private boolean isValidMoveMiniMax(Position source, Position dest, Position[] intermediatePositions, char sourceColor)
    {
        char opposingColor;
        boolean isValidMove = false;
        String directionMoving = "";

        if(sourceColor == Board.BLACK_STONE)
        {
            opposingColor = Board.WHITE_STONE;
        }
        else
        {
            opposingColor = Board.BLACK_STONE;
        }

        if(source.getRowPosition() == dest.getRowPosition() && source.getColPosition() == dest.getColPosition())
        {
            return false;
        }

        if (source.getRowPosition() == dest.getRowPosition())
        {
            if (source.getColPosition() + 2 == dest.getColPosition())
            {
                directionMoving = "west";
            }
            else if(source.getColPosition() - 2 == dest.getColPosition())
            {
                directionMoving = "east";
            }
        }
        else
        {
            if (source.getRowPosition() + 2 == dest.getRowPosition())
            {
                directionMoving = "south";
            }
            else if(source.getRowPosition() - 2 == dest.getRowPosition())
            {
                directionMoving = "north";
            }
        }

        if (opposingColor ==
                board.getStoneColorAtPosition(dest.getRowPosition(), dest.getColPosition()) &&
                board.isPositionOpen(dest.getRowPosition(), dest.getColPosition()))
        {

            if (directionMoving.equals("east"))
            {
                if(board.getStoneColorAtPosition(source.getRowPosition(), source.getColPosition() + 1) == opposingColor)
                {
                    board.setBoardAtPosition(source.getRowPosition(), source.getColPosition() + 1, Board.OPEN_SLOT);

                    intermediatePositions[0] = new Position(source.getRowPosition(), source.getColPosition() + 1);

                    isValidMove = true;
                }
            }
            else if (directionMoving.equals("west"))
            {
                if(board.getStoneColorAtPosition(source.getRowPosition(), source.getColPosition() - 1) == opposingColor)
                {
                    board.setBoardAtPosition(source.getRowPosition(), source.getColPosition() - 1, Board.OPEN_SLOT);

                    intermediatePositions[0] = new Position(source.getRowPosition(), source.getColPosition() - 1);

                    isValidMove = true;
                }
            }
            else if (directionMoving.equals("north"))
            {
                if(board.getStoneColorAtPosition(source.getRowPosition() - 1, source.getColPosition()) == opposingColor)
                {
                    board.setBoardAtPosition(source.getRowPosition() - 1, source.getColPosition(), Board.OPEN_SLOT);

                    intermediatePositions[0] = new Position(source.getRowPosition() - 1, source.getColPosition());

                    isValidMove = true;
                }
            }
            else
            {
                if(board.getStoneColorAtPosition(source.getRowPosition() + 1, source.getColPosition()) == opposingColor)
                {
                    board.setBoardAtPosition(source.getRowPosition() + 1, source.getColPosition(), Board.OPEN_SLOT);

                    intermediatePositions[0] = new Position(source.getRowPosition() + 1, source.getColPosition());

                    isValidMove = true;
                }
            }
        }
        return isValidMove;
    }

    /**
     * To execute the minimax algorithm for the Player using white stones.
     *
     * @param depth The current depth of the game tree.
     * @param player The player for which to run the minimax algorithm based off of.
     * @param alpha The alpha value for alpha-beta pruning.
     * @param beta The beta value for alpha-beta pruning.
     * @param plyCutoff The ply cutoff value to execute the minimax algorithm with.
     * @param isAlphaBetaEnabled Boolean variable indicating if alpha-beta pruning is enabled.
     * @return The minimizer or maximizer value for a given player.
     */

    private int whitePlayerMiniMax(int depth, Player player, int alpha, int beta, int plyCutoff, Boolean isAlphaBetaEnabled)
    {
        if ((!board.canMakePlay(true) && !board.canMakePlay(false)) || depth > plyCutoff)
        {
            return getHeuristicValue(getWhitePlayer());
        }

        ArrayList<Move> moves = getAllPossibleMoves(player.getStoneColor().charAt(0));

        if (moves.isEmpty())
        {
            return getHeuristicValue(getWhitePlayer());
        }

        ArrayList<Integer> scores = new ArrayList<>();

        Board savedBoard = new Board(boardSize);

        if (plyCutoff >= depth)
        {
            savedBoard = makeBoardCopy();
        }

        for (int i = 0; i < moves.size(); i++)
        {
            Move move = moves.get(i);

            if (player.getStoneColor().equals(Game.WHITE_PLAYER))
            {
                makeMoveForMinimax(move);

                int currentScore = whitePlayerMiniMax(depth + 1, getBlackPlayer(), alpha, beta, plyCutoff ,isAlphaBetaEnabled);
                System.out.println(currentScore);
                scores.add(currentScore);

                if (isAlphaBetaEnabled)
                {
                    if (move.getMinimaxValue() > alpha)
                    {
                        alpha = move.getMinimaxValue();
                    }

                    if (beta <= alpha)
                    {
                        break;
                    }
                }

                if (depth == 0)
                {
                    move.setMinimaxValue(currentScore);
                    root.add(move);
                }
            }
            else if (player.getStoneColor().equals(Game.BLACK_PLAYER))
            {
                makeMoveForMinimax(move);

                int currentScore = whitePlayerMiniMax(depth + 1, getWhitePlayer(), alpha, beta, plyCutoff, isAlphaBetaEnabled);
                scores.add(currentScore);

                if (isAlphaBetaEnabled)
                {
                    if (move.getMinimaxValue() < beta)
                    {
                        beta = move.getMinimaxValue();
                    }

                    if (alpha >= beta)
                    {
                        break;
                    }
                }
            }

            resetBoard(savedBoard);
        }

        if (player.getStoneColor().equals(Game.WHITE_PLAYER))
        {
            return Collections.max(scores);
        }

        return Collections.min(scores);
    }

    /**
     * To execute the minimax algorithm for the Player using black stones.
     *
     * @param depth The current depth of the game tree.
     * @param player The player for which to run the minimax algorithm based off of.
     * @param alpha The alpha value for alpha-beta pruning.
     * @param beta The beta value for alpha-beta pruning.
     * @param plyCutoff The ply cutoff value to execute the minimax algorithm with.
     * @param isAlphaBetaEnabled Boolean variable indicating if alpha-beta pruning is enabled.
     * @return The minimizer or maximizer value for a given player.
     */

    private int blackPlayerMinimax(int depth, Player player, int alpha, int beta, int plyCutoff, Boolean isAlphaBetaEnabled)
    {
        if ((!board.canMakePlay(true) && !board.canMakePlay(false)) || depth > plyCutoff)
        {
            return getHeuristicValue(getBlackPlayer());
        }

        ArrayList<Move> moves = getAllPossibleMoves(player.getStoneColor().charAt(0));

        if (moves.isEmpty())
        {
            return getHeuristicValue(getBlackPlayer());
        }

        ArrayList<Integer> scores = new ArrayList<>();

        Board savedBoard = new Board(boardSize);

        if (plyCutoff >= depth)
        {
            savedBoard = makeBoardCopy();
        }

        for (int i = 0; i < moves.size(); i++)
        {
            Move move = moves.get(i);

            if (player.getStoneColor().equals(Game.BLACK_PLAYER))
            {
                makeMoveForMinimax(move);

                int currentScore = blackPlayerMinimax(depth + 1, getWhitePlayer(), alpha, beta, plyCutoff, isAlphaBetaEnabled);
                scores.add(currentScore);

                if (isAlphaBetaEnabled)
                {
                    if (move.getMinimaxValue() > alpha)
                    {
                        alpha = move.getMinimaxValue();
                    }

                    if (beta <= alpha)
                    {
                        break;
                    }
                }

                if (depth == 0)
                {
                    move.setMinimaxValue(currentScore);
                    root.add(move);
                }
            }
            else if (player.getStoneColor().equals(Game.WHITE_PLAYER))
            {
                makeMoveForMinimax(move);

                int currentScore = blackPlayerMinimax(depth + 1, getBlackPlayer(), alpha, beta, plyCutoff, isAlphaBetaEnabled);
                scores.add(currentScore);

                if (isAlphaBetaEnabled)
                {
                    if (move.getMinimaxValue() < beta)
                    {
                        beta = move.getMinimaxValue();
                    }

                    if (alpha >= beta)
                    {
                        break;
                    }
                }
            }
            resetBoard(savedBoard);
        }

        if (player.getStoneColor().equals(Game.BLACK_PLAYER))
        {
            return Collections.max(scores);
        }

        return Collections.min(scores);
    }

    /**
     * To make a move on the board for the minimax algorithm.
     *
     * @param move The move to modify the board based on.
     */

    private void makeMoveForMinimax(Move move)
    {
        ArrayList<Position> path = move.getMovePath();

        Position source = path.get(0);
        Position last = path.get(1);

        char sourceColor = board.getStoneColorAtPosition(source.getRowPosition(), source.getColPosition());

        board.setBoardAtPosition(source.getRowPosition(), source.getColPosition(), Board.OPEN_SLOT);

        for (int i = 1; i < path.size(); i++)
        {
            Position[] intermiatePositionsTemp = new Position[1];
            Position dest = path.get(i);

            if (isValidMoveMiniMax(source, dest, intermiatePositionsTemp, sourceColor))
            {
                board.setBoardAtPosition(dest.getRowPosition(), dest.getColPosition(), Board.OPEN_SLOT);
                last = dest;

                source = new Position(dest.getRowPosition(), dest.getColPosition());
            }
        }
        board.setBoardAtPosition(last.getRowPosition(), last.getColPosition(), sourceColor);
    }

    /**
     * To get the best available move for the minimax algorithm.
     *
     * @return The best move available for the minimax algorithm.
     */

    private Move getBestMove()
    {
        if (root.isEmpty())
        {
            return null;
        }

        Move baseMove = root.get(0);
        int maxScore = baseMove.getScore();

        for(Move m : root)
        {
            if(m.getScore() > maxScore)
            {
                baseMove = m;
                maxScore = m.getScore();
            }
        }
        return baseMove;
    }

    /**
     * To get the path for a move.
     *
     * @param parents The parent nodes for a given move.
     * @param moves List of Positions that make up a move.
     * @param availableMoves List of moves that make up the path of a complex move.
     */

    private void getPath(HashMap<Position, Position> parents, ArrayList<Pair<Position, Position>> moves, ArrayList<Move> availableMoves)
    {
        ArrayList<ArrayList<Position>> path = new ArrayList<>();

        for (int i = 0; i < moves.size(); i++)
        {
            ArrayList<Position> possiblePath = new ArrayList<>();
            Position slot = moves.get(i).second;

            while (parents.containsKey(slot))
            {
                Position parent = parents.get(slot);
                possiblePath.add(slot);
                slot = parent;
            }

            possiblePath.add(slot);

            Collections.reverse(possiblePath);
            path.add(possiblePath);
        }

        for (int i = 0; i < path.size(); i++)
        {
            ArrayList<Position> possiblePath = path.get(i);
            Position source = possiblePath.get(0);
            Position dest = possiblePath.get(possiblePath.size() - 1);

            Move moveNode = new Move(source, dest);
            moveNode.setMovePath(possiblePath);
            availableMoves.add(moveNode);
        }
    }

    /**
     * To get the heuristic value for a given player.
     *
     * @param player The player for which to base the heuristic value off of.
     * @return Integer value representing the heuristic value for the given player.
     */

    private int getHeuristicValue(Player player)
    {
        int numWhiteStones = 0;
        int numBlackStones = 0;

        for (int r = 0; r < boardSize; r++)
        {
            for (int c = 0; c < boardSize; c++)
            {
                if (board.isPositionWhite(r, c))
                {
                    numWhiteStones++;
                }
                else if (board.isPositionBlack(r, c))
                {
                    numBlackStones++;
                }
            }
        }

        if (player.getStoneColor().equals(Game.BLACK_PLAYER))
        {
            return numBlackStones - numWhiteStones;
        }
        else
        {
            return numWhiteStones - numBlackStones;
        }
    }

    /**
     * To get all the possible moves available to a particular player.
     *
     * @param color The stone color for which to find all available moves.
     * @return List containing all available moves.
     */

    private ArrayList<Move> getAllPossibleMoves(char color)
    {
        ArrayList<Move> movePaths = new ArrayList<>();
        ArrayList<Pair<Position, Position>> moves = new ArrayList<>();
        HashMap<Position, Position> parents = new HashMap<>();

        for (int row = 0; row < boardSize; row++)
        {
            for (int col = 0; col < boardSize; col++)
            {
                if(board.getStoneColorAtPosition(row, col) == color)
                {
                    Position position = new Position(row, col);

                    depthFirstSearch(position, moves, parents);
                    getPath(parents, moves, movePaths);

                    parents.clear();
                    moves.clear();
                }
            }
        }
        return movePaths;
    }

    /**
     * To perform a depth-first search for a given position (node).
     *
     * @param startingPosition The first position from which to look for moves from.
     * @param moves List of initial moves found.
     * @param parents Parents of the given move.
     */

    private void depthFirstSearch(Position startingPosition, ArrayList<Pair<Position, Position>> moves, HashMap<Position, Position> parents)
    {
        boolean[][] visitedSlots = new boolean[boardSize][boardSize];

        setSlotsAsNotVisited(visitedSlots);

        Stack<Position> dfsStack = new Stack<>();
        char color = board.getStoneColorAtPosition(startingPosition.getRowPosition(), startingPosition.getColPosition());

        char opposingColor;

        if(color == Board.WHITE_STONE)
        {
            opposingColor = Board.BLACK_STONE;
        }
        else
        {
            opposingColor = Board.WHITE_STONE;
        }

        dfsStack.push(startingPosition);
        Position previous = startingPosition;

        board.setBoardAtPosition(startingPosition.getRowPosition(), startingPosition.getColPosition(), Board.OPEN_SLOT);

        while (!dfsStack.empty())
        {
            Pair<Position, Position> next;
            Position current = dfsStack.pop();

            if (!visitedSlots[current.getRowPosition()][current.getColPosition()] && !current.equals(previous))
            {
                if (!current.equals(startingPosition))
                {
                    visitedSlots[current.getRowPosition()][current.getColPosition()] = true;
                }

                next = new Pair<>(startingPosition, current);

                moves.add(next);
            }

            Position northPosition = current.getNorthPosition(current, board, opposingColor);
            Position southPosition = current.getSouthPosition(current, board, opposingColor);
            Position westPosition = current.getWestPosition(current, board, opposingColor);
            Position eastPosition = current.getEastPosition(current, board, opposingColor);

            if (westPosition != null && !visitedSlots[westPosition.getRowPosition()][westPosition.getColPosition()])
            {
                if (!isChild(current, westPosition, parents))
                {
                    dfsStack.push(westPosition);
                    parents.put(westPosition, current);
                }
            }

            if (southPosition != null && !visitedSlots[southPosition.getRowPosition()][southPosition.getColPosition()])
            {
                if (!isChild(current, southPosition, parents))
                {
                    dfsStack.push(southPosition);
                    parents.put(southPosition, current);
                }
            }

            if (eastPosition != null && !visitedSlots[eastPosition.getRowPosition()][eastPosition.getColPosition()])
            {
                if (!isChild(current, eastPosition, parents))
                {
                    dfsStack.push(eastPosition);
                    parents.put(eastPosition, current);
                }
            }

            if (northPosition != null && !visitedSlots[northPosition.getRowPosition()][northPosition.getColPosition()])
            {
                if (!isChild(current, northPosition, parents))
                {
                    dfsStack.push(northPosition);
                    parents.put(northPosition, current);
                }
            }
            previous = current;
        }
        board.setBoardAtPosition(startingPosition.getRowPosition(), startingPosition.getColPosition(), color);
    }

    /**
     * To be used in conjunction with depth-first search to set all the board positions initially as not visited.
     *
     * @param visitedPositions Two-dimensional array of Boolean value values to set to false, represents the board.
     */

    private void setSlotsAsNotVisited(boolean[][] visitedPositions)
    {
        for (int r = 0; r < boardSize; r++)
        {
            for (int c = 0; c < boardSize; c++)
            {
                visitedPositions[r][c] = false;
            }
        }
    }

    /**
     * To determine if a given position is already a child of another move.
     *
     * @param current The current position.
     * @param positionToCheck The position to check.
     * @param parents Hashmap containing parents of moves made up of positions.
     * @return Boolean variable indicating if the given position is a child or not.
     */

    private boolean isChild(Position current, Position positionToCheck, HashMap<Position, Position> parents)
    {
        if (parents.get(current) != null)
        {
            if (parents.get(current).equals(positionToCheck))
            {
                return true;
            }
        }
        return false;
    }
}

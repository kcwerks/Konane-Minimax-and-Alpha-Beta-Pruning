//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project: Two Player Konane - Project 3                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/28/18                                           *
//     ************************************************************


package edu.ramapo.kcalabro.konane.model;

/**
 * Created by KyleCalabro on 2/7/18.
 */

import android.os.Environment;
import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Serializer {
    //------------------------Data Members------------------------

    private int boardSizeToRestore;

    // The pattern to be matched for the board.
    private String pattern = "([BWO])";

    // The regular expression to use in conjunction with the pattern.
    private Pattern regex = Pattern.compile(pattern);

    //------------------------Member Functions------------------------

    /**
     * The default constructor for the Serializer class.
     */

    public Serializer()
    {
        boardSizeToRestore = 0;
    }

    /**
     * To restore a game from a serialized file properly.
     *
     * @param game Game object representing the current game.
     * @param fileName The name of the serialized file to load from.
     * @return Boolean value indicating if the given file could be loaded.
     */

    public boolean restoreFile(Game game, String fileName, int boardSizeToRestore)
    {
        String nextPlayer = null;

        File sdcard = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File serializedFile = new File(sdcard, fileName);

        Boolean isEndOfBoard = false;

        try {
            String fileLine;
            int lineNumber = 0;

            BufferedReader bufferedReader = new BufferedReader(new FileReader(serializedFile));

            try {
                while ((fileLine = bufferedReader.readLine()) != null)
                {
                    if (!fileLine.equals(""))
                    {
                        lineNumber++;

                        // First line contains the Black player's score.
                        if (lineNumber == 1)
                        {
                            String blackPlayerScore = fileLine.substring(fileLine.indexOf(':') + 2);
                            int blackScore = Integer.parseInt(blackPlayerScore);

                            game.getBoard().setNumWhiteStonesCaptured(blackScore);
                            game.getPlayers()[0].setRoundScore(blackScore);
                        }

                        // Second line contains the White player's score.
                        else if (lineNumber == 2)
                        {
                            String whitePlayerScore = fileLine.substring(fileLine.indexOf(':') + 2);
                            int whiteScore = Integer.parseInt(whitePlayerScore);

                            game.getBoard().setNumBlackStonesCaptured(whiteScore);
                            game.getPlayers()[1].setRoundScore(whiteScore);
                        }

                        // Board data comes next.
                        else if (lineNumber >= 4 && lineNumber < 4 + boardSizeToRestore)
                        {
                            if(fileLine.charAt(0) == 'W' || fileLine.charAt(0) == 'B' || fileLine.charAt(0) == 'O')
                            {
                                restoreBoard(game.getBoard(), fileLine, lineNumber - 4);
                            }
                        }

                        // Next Player data comes next.
                        else if (4 + boardSizeToRestore == lineNumber)
                        {
                            nextPlayer = fileLine.substring(fileLine.indexOf(':') + 2);
                        }

                        else if(5 + boardSizeToRestore == lineNumber)
                        {
                            String humanStoneColor = fileLine.substring(fileLine.indexOf(':') + 2);

                            if(humanStoneColor.equals("Black"))
                            {
                                if(nextPlayer.equals("Black")) // human stone color black next player
                                {
                                    game.turnColor = 'B';
                                    game.setIsHumanUsingBlack(true);
                                    game.getPlayers()[0].setStoneColor(Game.BLACK_PLAYER);
                                    game.getPlayers()[1].setStoneColor(Game.WHITE_PLAYER);
                                    game.setCurrentPlayer(Game.HUMAN_PLAYER);
                                }
                                else // white next player computer
                                {
                                    game.turnColor = 'W';
                                    game.setIsHumanUsingBlack(false);
                                    game.getPlayers()[0].setStoneColor(Game.BLACK_PLAYER);
                                    game.getPlayers()[1].setStoneColor(Game.WHITE_PLAYER);
                                    game.setCurrentPlayer(Game.COMPUTER_PLAYER);
                                }
                            }
                            else // human stone color white
                            {
                                if(nextPlayer.equals("Black"))
                                {
                                    game.turnColor = 'B';
                                    game.setIsHumanUsingBlack(false);
                                    game.getPlayers()[0].setStoneColor(Game.WHITE_PLAYER);
                                    game.getPlayers()[1].setStoneColor(Game.BLACK_PLAYER);
                                    game.setCurrentPlayer(Game.COMPUTER_PLAYER);
                                }
                                else // white next player
                                {
                                    game.turnColor = 'W';
                                    game.setIsHumanUsingBlack(false);
                                    game.getPlayers()[0].setStoneColor(Game.WHITE_PLAYER);
                                    game.getPlayers()[1].setStoneColor(Game.BLACK_PLAYER);
                                    game.setCurrentPlayer(Game.HUMAN_PLAYER);
                                }
                            }
                        }
                    }
                }
                // Clean up the BufferedReader.
                bufferedReader.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                return false;
            }
        } catch (FileNotFoundException fileException) {
            fileException.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * To serialize a given game in the correct format.
     *
     * @param game Object of the game class representing the game to be saved.
     * @param fileName The name of the file to save the game to.
     * @throws IOException If file could not be found.
     */

    public void serializeFile(Game game, String fileName) throws IOException
    {
        String filepath = Environment.getExternalStorageDirectory().toString();

        File serializedFile = new File(filepath, fileName);

        // If the file exists delete it so we can write a new file.
        if(serializedFile.exists())
        {
            serializedFile.delete();
        }

        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(serializedFile));

            // Write the black stone player's score to the file.
            bufferedWriter.write("Black: ");
            bufferedWriter.write(game.getPlayers()[0].getRoundScore() + "\n");

            // Write the white stone player's score to the file.
            bufferedWriter.write("White: ");
            bufferedWriter.write(game.getPlayers()[1].getRoundScore() + "\n");

            // Write the Board data to the file.
            bufferedWriter.write("Board: \n");
            serializeBoard(game.getBoard(), bufferedWriter);

            // Write the next player data to the file.
            bufferedWriter.write("Next Player: ");

            if(game.turnColor == 'B')
            {
                bufferedWriter.write("Black");
            }
            else
            {
                bufferedWriter.write("White");
            }

            bufferedWriter.write("\n");

            // Write the stone color of the human player to the file.
            bufferedWriter.write("Human: ");

            if(game.getIsHumanUsingBlack())
            {
                bufferedWriter.write("Black");
            }
            else
            {
                bufferedWriter.write("White");
            }

            // Clean up the buffered writer.
            bufferedWriter.close();
        }
        catch (FileNotFoundException fileException)
        {
            fileException.printStackTrace();
        }
    }

    //------------------------Restoring Functions------------------------

    /**
     * To determine the size of the board from any given file.
     *
     * @param fileName The filename from which to read data from.
     * @return Boolean value indicating if the file could be opened.
     */

    public Boolean getBoardSize(String fileName)
    {
        File sdcard = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File serializedFile = new File(sdcard, fileName);

        Boolean isEndOfBoard = false;

        try {
            String fileLine;
            int lineNumber = 0;

            BufferedReader bufferedReader = new BufferedReader(new FileReader(serializedFile));

            try {
                while ((fileLine = bufferedReader.readLine()) != null)
                {
                    if (!fileLine.equals(""))
                    {
                        lineNumber++;

                        // Board data comes next.
                        if (lineNumber >= 4)
                        {
                            if(fileLine.charAt(0) == 'W' || fileLine.charAt(0) == 'B' || fileLine.charAt(0) == 'O')
                            {
                                boardSizeToRestore++;
                            }
                        }
                    }
                }
                // Clean up the BufferedReader.
                bufferedReader.close();
            }
            catch (IOException ioException)
            {
                ioException.printStackTrace();
                return false;
            }
        }
        catch (FileNotFoundException fileException)
        {
            fileException.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * To get the size of the board to be restored.
     *
     * @return Integer value indicating size of the board to be restored.
     */

    public int getBoardSizeToRestore()
    {
        return boardSizeToRestore;
    }

    /**
     * To restore the board properly.
     *
     * @param board Object of the board class to restore.
     * @param boardLine The string containing the pertinent data.
     * @param rowNum the row number for the current line of data.
     * @throws IOException
     */

    private void restoreBoard(Board board, String boardLine, int rowNum) throws IOException
    {
        Matcher matcher = regex.matcher(boardLine);

        int colNum = 0;

        // Traverse all the regular expression matches found in the String.
        while (matcher.find())
        {
            board.setBoardAtPosition(rowNum, colNum, matcher.group(1).charAt(0));
            colNum++;
        }
    }

    //------------------------Serializing Functions------------------------

    /**
     * To save the board to a file.
     *
     * @param board The Board object which to save to a file.
     * @param bufferedWriter The bufferedWriter which writes to the open serialization file.
     * @throws IOException
     */

    private void serializeBoard(Board board, BufferedWriter bufferedWriter) throws IOException {

        // Traverse the board and write it to the file.
        for(int row = 0; row < board.getBoardSize(); row++)
        {
            for(int col = 0; col < board.getBoardSize(); col++)
            {
                // Write the stone at the index to the file.
                bufferedWriter.write(board.getBoard()[row][col]);
                bufferedWriter.write(" ");
                System.out.print(board.getBoard()[row][col] + " ");
            }
            bufferedWriter.write("\n");
            System.out.println("");
        }
    }
}

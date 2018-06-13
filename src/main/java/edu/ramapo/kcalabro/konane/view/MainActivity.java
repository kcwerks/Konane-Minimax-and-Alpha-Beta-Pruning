//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project: Two Player Konane - Project 3                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/28/18                                           *
//     ************************************************************

package edu.ramapo.kcalabro.konane.view;

import android.os.Environment;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.Vector;
import java.io.File;

import edu.ramapo.kcalabro.konane.R;
import edu.ramapo.kcalabro.konane.model.Serializer;

public class MainActivity extends AppCompatActivity {

    //------------------------Data Members------------------------

    public final static String EXTRA_NEWGAME = "edu.ramapo.kcalabro.konane.newGame";

    private Spinner fileSpinner;

    private Spinner boardSizeSpinner;

    private String selectedFile;

    private Integer selectedBoardSize;

    private Button loadGameButton;

    private Button newGameButton;

    private Serializer serializer;

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //------------------------Member Functions------------------------

    /**
     * Button Handler for the New Game button in the activity_main layout.
     *
     * @param view The current view.
     */

    public void startNewGame(View view)
    {
        // Set the intent to the RoundActivity class.
        Intent intent = new Intent(this, GameActivity.class);

        // Set the newround flag to true.
        intent.putExtra(EXTRA_NEWGAME, true);

        // Send over the board size as well.
        intent.putExtra("selectedBoardSize", selectedBoardSize);

        // Start the activity.
        startActivity(intent);
    }

    /**
     * Button Handler for the Load Game button in the activity_main layout.
     *
     * @param view The current view.
     */

    public void loadGame(View view)
    {
        serializer.getBoardSize(selectedFile);
        selectedBoardSize = serializer.getBoardSizeToRestore();

        // Set the intent to the RoundActivity class.
        Intent intent = new Intent(this, GameActivity.class);

        // Set the newgame flag to false.
        intent.putExtra(EXTRA_NEWGAME, false);

        // Send over the board size as well.
        intent.putExtra("selectedBoardSize", selectedBoardSize);

        // Send over the filename as well.
        intent.putExtra("selectedFile", selectedFile);

        // Start the activity.
        startActivity(intent);
    }

    /**
     * To handle when the MainActivity object is created.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);

        serializer = new Serializer();

        loadGameButton = findViewById(R.id.loadGameButton);
        newGameButton = findViewById(R.id.newGameButton);

        fileSpinner = (Spinner) findViewById(R.id.fileSpinner);
        boardSizeSpinner = (Spinner) findViewById(R.id.boardSizeSpinner);

        ArrayAdapter<CharSequence> boardSizeAdapter = ArrayAdapter.createFromResource(this, R.array.boardSizeArray, R.layout.spinner_field);
        boardSizeSpinner.setAdapter(boardSizeAdapter);
        boardSizeSpinner.setBackgroundResource(R.drawable.buttonborder);

        boardSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position != 0)
                {
                    selectedBoardSize = Integer.parseInt(boardSizeSpinner.getItemAtPosition(position).toString());
                    newGameButton.setEnabled(true);
                }
                else
                {
                    newGameButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter filePickerAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_field, getAllTextFiles());
        //ArrayAdapter<CharSequence> filePickerAdapter = ArrayAdapter.createFromResource(this, R.array.filesArray, android.R.layout.simple_spinner_item);
        filePickerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fileSpinner.setAdapter(filePickerAdapter);
        fileSpinner.setBackgroundResource(R.drawable.buttonborder);

        fileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position != 0)
                {
                    selectedFile = fileSpinner.getItemAtPosition(position).toString();
                    loadGameButton.setEnabled(true);
                }
                else
                {
                    loadGameButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Permissions stuff.
     *
     * @param activity The current activity.
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * To retrieve all the available text files on the external storage of a device.
     *
     * @return Vector of strings containing the filenames of text files.
     */

    private Vector<String> getAllTextFiles()
    {
        Vector<String> textFiles = new Vector<>();
        textFiles.addElement(" Select File:");

        // Finding the sdcard path on the tablet.
        File sdcard = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File[] files = sdcard.listFiles();

        for(int i = 0; i < files.length; i++)
        {
            File file = files[i];

            //It's assumed that all file in the path are in supported type.
            String filePath = file.getPath();

            if(filePath.endsWith(".txt"))
            {
                textFiles.add(filePath.substring(filePath.indexOf('0') + 2));
            }
        }
        return textFiles;
    }
}

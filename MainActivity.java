package com.example.guessmaster;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.* ;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // private variables declaration
    private TextView entityName;
    private TextView ticketsum;
    private Button guessButton;
    private EditText userIn;
    private Button btnclearContent;
    private String user_input;
    private ImageView entityImage;
    String answer;

    private int numOfEntities;
    private Entity[] entities;
    private int[] tickets;
    private int numOfTickets;
    String entName; //Stores Entity Name
    int entityid = 0;
    int currentTicketWon = 0;

    // Declaring entities
    Politician trudeau = new Politician("Justin Trudeau", new Date("December", 25, 1971), "Male", "Liberal", 0.25);
    Singer dion = new Singer("Celine Dion", new Date("March", 30, 1961), "Female", "La voix du bon Dieu",
            new Date("November", 6, 1981), 0.5);
    Person myCreator = new Person("myCreator", new Date("September", 1, 2000), "Female", 1);
    Country usa = new Country("United States", new Date("July", 4, 1776), "Washington D.C.", 0.1);

    public MainActivity() {
        numOfEntities = 0;
        entities = new Entity[100];
    }

    public void addEntity(Entity entity) {
        entities[numOfEntities++] = entity.clone(); // insert clone of entity into the entities array
    }

    public void playGame(int entityId) {
        Entity entity = entities[entityId];
        playGame(entity);
    }

    public void playGame(Entity entity) {
        // Set the name of the entity to be guessed in the entityName TextView
        entityName.setText(entity.getName());

        // Get input from the EditText
        answer = userIn.getText().toString();
        answer = answer.replace("\n", "").replace("\r", "");
        Date date;
        try {
            date = new Date(answer); // Assuming Date is a class you have defined to parse dates

            // Check user date input against entity's birth date
            if (date.precedes(entity.getBorn())) {
                showAlertDialog("Incorrect", "Try a later date.");
            } else if (entity.getBorn().precedes(date)) {
                showAlertDialog("Incorrect", "Try an earlier date.");
            } else {
                numOfTickets= entity.getAwardedTicketNumber();
                currentTicketWon += numOfTickets;

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("You won")
                        .setMessage("BINGO! " + entity.closingMessage())
                        .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "You won " + currentTicketWon + " tickets!", Toast.LENGTH_SHORT).show();
                                continueGame();
                            }
                        })
                        .show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Ok", null)
                .show();
    }


    public void playGame() {
            int entityId = genRandomEntityId();
            changeEntity(entityId);
            welcomeToGame(entities[entityId]);
            playGame(entityId);
    }

    public int genRandomEntityId() {
        Random randomNumber = new Random();
        return randomNumber.nextInt(numOfEntities);
    }

    public void changeEntity(int randomIndex) {
        // Get the new entity
        Entity newEntity = entities[randomIndex];

        // Update the UI with the new entity's details
        entityName.setText(newEntity.getName());

        ImageSetter(newEntity);

    }

    private void welcomeToGame(Entity entity) {
        AlertDialog.Builder welcomeAlert = new AlertDialog.Builder(MainActivity.this);
        welcomeAlert.setTitle("GuessMaster Game");
        welcomeAlert.setMessage(entity.welcomeMessage());
        welcomeAlert.setCancelable(false); // No Cancel Button

        welcomeAlert.setNegativeButton("START GAME", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Game is Starting... Enjoy", Toast.LENGTH_SHORT).show();
            }
        });

        // Show Dialog
        AlertDialog dialog = welcomeAlert.create();
        dialog.show();
    }

    public void ImageSetter(Entity entity) {    //setting image based on random entity
        int imageResId;

        switch (entity.getName()) {
            case "United States":
                imageResId = R.drawable.usaflag;
                break;
            case "Justin Trudeau":
                imageResId = R.drawable.justint;
                break;
            case "Celine Dion":
                imageResId = R.drawable.celidion;
                break;
            default:
                imageResId = R.drawable.mycreator; // Fallback image
                break;
        }

        entityImage.setImageResource(imageResId);
    }

    public void continueGame() {
        entityid = genRandomEntityId();
        Entity entity = entities[entityid];
        entName = entity.getName();

        ImageSetter(entity);
        entityName.setText(entName); // Set the new entity's name in the TextView
        userIn.getText().clear(); // Clear the previous entry in the EditText

        // Update the ticketsum TextView
        ticketsum.setText("Total Tickets: " + currentTicketWon);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        guessButton = (Button) findViewById(R.id.btnGuess);
        btnclearContent = (Button) findViewById(R.id.btnClear);
        //EditText for user input
        userIn = (EditText) findViewById(R.id.guessinput);
        entityName = (TextView) findViewById(R.id.entityName);
        //TextView for total tickets
        ticketsum = (TextView) findViewById(R.id.ticket);
        entityImage = (ImageView) findViewById(R.id.entityImage);

        // Initialize entities and add them to the array
        addEntity(trudeau);
        addEntity(dion);
        addEntity(myCreator);
        addEntity(usa);
        numOfEntities = 4;

        playGame();

        btnclearContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int entityId = genRandomEntityId();
                changeEntity(entityId);
                playGame(entityId);
            }
        });

        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playGame(entities[entityid]);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
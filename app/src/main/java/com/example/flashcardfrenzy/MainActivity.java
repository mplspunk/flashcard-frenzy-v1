package com.example.flashcardfrenzy;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.content.Intent;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    int currentCardDisplayedIndex = 0;
    Flashcard cardToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        allFlashcards = flashcardDatabase.getAllCards();

        if (allFlashcards != null && allFlashcards.size() > 0) {
            ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(0).getQuestion());
            ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(0).getAnswer());
        }

        findViewById(R.id.flashcard_question).setBackgroundColor(getResources().getColor(R.color.questionBackground,null));

        findViewById(R.id.flashcard_answer).setBackgroundColor(getResources().getColor(R.color.answerBackground, null));

        findViewById(R.id.flashcard_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
                View answerSideView = findViewById(R.id.flashcard_answer);

                // get the center for the clipping circle
                int cx = answerSideView.getWidth() / 2;
                int cy = answerSideView.getHeight() / 2;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(answerSideView, cx, cy, 0f, finalRadius);

                // hide the question and show the answer to prepare for playing the animation!
                findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);

                anim.setDuration(300);
                anim.start();
            }
        });

        findViewById(R.id.flashcard_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
                findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
            }
        });


        findViewById(R.id.addBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });


        findViewById(R.id.nextBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.left_out);
                final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_in);

                // advance our pointer index so we can show the next card
                currentCardDisplayedIndex++;

                // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
                if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                    currentCardDisplayedIndex = 0;
                }

                // start next card animation
                if (findViewById(R.id.flashcard_question).getVisibility() == View.INVISIBLE) {
                    findViewById(R.id.flashcard_answer).startAnimation(leftOutAnim);
                } else {
                    findViewById(R.id.flashcard_question).startAnimation(leftOutAnim);
                }

                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // this method is called when the animation first starts
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // this method is called when the animation is finished playing
                        // set the question and answer TextViews with data from the database
                        ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                        ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());

                        if (findViewById(R.id.flashcard_question).getVisibility() == View.INVISIBLE) {
                            findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                            findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
                        }

                        findViewById(R.id.flashcard_question).startAnimation(rightInAnim);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }
                });

            }

        });

        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //(((TextView) findViewById(R.id.flashcard_question)).getText().toString())
                Intent editCard = new Intent(MainActivity.this, AddCardActivity.class);
                editCard.putExtra("editQuestion", ((TextView) findViewById(R.id.flashcard_question)).getText().toString());
                editCard.putExtra("editAnswer", ((TextView) findViewById(R.id.flashcard_answer)).getText().toString());
                MainActivity.this.startActivityForResult(editCard, 200);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String string1 = data.getExtras().getString("string1");
            String string2 = data.getExtras().getString("string2");
            TextView flashcard_question = findViewById(R.id.flashcard_question);
            TextView flashcard_answer = findViewById(R.id.flashcard_answer);
            flashcard_question.setText(string1);
            flashcard_answer.setText(string2);
            flashcardDatabase.insertCard(new Flashcard(string1, string2));
            allFlashcards = flashcardDatabase.getAllCards();

            // display a snackbar message when a card is created
            Snackbar.make(findViewById(R.id.flashcard_question),
                    "Card successfully created!",
                    Snackbar.LENGTH_SHORT)
                    .show();
            }

        if (requestCode == 200 && resultCode == RESULT_OK) {
            String string1 = data.getExtras().getString("string1");
            String string2 = data.getExtras().getString("string2");
            TextView flashcard_question = findViewById(R.id.flashcard_question);
            TextView flashcard_answer = findViewById(R.id.flashcard_answer);
            flashcard_question.setText(string1);
            flashcard_answer.setText(string2);
            Flashcard temp = allFlashcards.get(currentCardDisplayedIndex);
            temp.setQuestion(string1);
            temp.setAnswer(string2);
            flashcardDatabase.updateCard(temp);
            allFlashcards = flashcardDatabase.getAllCards();

            // display a snackbar message when a card is created
            Snackbar.make(findViewById(R.id.flashcard_question),
                    "Card successfully updated!",
                    Snackbar.LENGTH_SHORT)
                    .show();
        }



        }


}


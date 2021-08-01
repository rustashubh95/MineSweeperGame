package com.example.minesweepergame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*


// need to get values of best time, last game time from shared pref



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonStart.setOnClickListener {
            selectGameLevel()
        }



    }

    private fun selectGameLevel() {
      when(radioGroupDifficulty.checkedRadioButtonId){
          radioButtonEasy.id -> startGame(9,9,15)
          radioButtonMedium.id->startGame(15,15,40)
          radioButtonHard.id ->startGame(20,20,50)
      }
    }

    private fun startGame(boardwidth: Int, boardheight: Int, mines: Int) {
        //Start the GameActivity and pass it number of rows, columns and mines
        val intent = Intent(this, MineSweeperGameActivity:: class.java).apply {
            putExtra("BOARDWIDTH", boardwidth)
            putExtra("BOARDHEIGHT", boardheight)
            putExtra("MINES", mines)
        }
        startActivity(intent)
    }
}
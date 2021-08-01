package com.example.minesweepergame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_mine_sweeper_game.*
import java.util.*

class MineSweeperGameActivity : AppCompatActivity() {
    private val MINE: Int = -1
    private var row :Int=9
    private var col :Int=9
    private var mines:Int= 20
    private var flag :Boolean =false;
    private var flagCount : Int = 0
    private var firstMove: Boolean =true;
    private lateinit var boardGame: Array<Array<MineButton>>
    private val  moveposition = intArrayOf(0,-1,1)
    private val handler = Handler()
    private var seconds = 0
    private var timerRunning = false
    private  lateinit var runnable: Runnable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mine_sweeper_game)
        setupTimer()
        row = intent.getIntExtra("BOARDWIDTH",9)
        col = intent.getIntExtra("BOARDHEIGHT",9)
        mines = intent.getIntExtra("MINES",20)
        minesNumber.setText(mines.toString())
        flagCount = mines
        boardGame = Array(row) { Array(col) { MineButton() } }

        createGameBoard()

        restartButton.setOnClickListener{
            refreshBoard()
        }
        flagMineImage.setOnClickListener{
            if(flag){
              flagMineImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.flag))
            }else{
               flagMineImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.mine))
            }
            flag = !flag
        }
    }

    private fun setupTimer() {
        runnable = Runnable { updateSeconds() }
        handler.post(runnable)
    }

    private fun updateSeconds() {
        val hours: Int = seconds / 3600
        val minutes: Int = seconds % 3600 / 60
        val secs: Int = seconds % 60

        //convert the elapsed time to proper format ie H:MM:SS
        val time = String
            .format(
                Locale.getDefault(),
                "%d:%02d:%02d", hours,
                minutes, secs
            )

        timeVal.text = time
        if(timerRunning)
        seconds++

        handler.postDelayed(runnable,1000)

    }

    private fun refreshBoard() {
        for(i in 0 until row){
            for( j in 0 until col){
                boardGame[i][j].value = 0
                boardGame[i][j].isFlagged = false
                boardGame[i][j].isShown = false
                val button = getButton(i,j) as Button
                button.text = "";
                button.isEnabled = true
                button.background = ContextCompat.getDrawable(this,R.drawable.button)
            }
        }
        flagCount = mines
        minesNumber.text = flagCount.toString()
        flag = false
        firstMove = true
        seconds = 0
        timerRunning =false
        flagMineImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.flag))

    }

    private fun createGameBoard() {
       val boardparams = LinearLayout.LayoutParams(
           LinearLayout.LayoutParams.MATCH_PARENT,
           0
       )
      val buttonparams = LinearLayout.LayoutParams(
          0,
          150
      )
      for (i in 0 until row){

          val boardlayout = LinearLayout(this)
          boardlayout.orientation  = LinearLayout.HORIZONTAL
          boardparams.weight =1.0f
          boardlayout.layoutParams = boardparams

          for(j in 0 until col){
              val button  = Button(this)
              buttonparams.weight =1.0f
              button.layoutParams = buttonparams
              button.setBackgroundResource(R.drawable.button)
              button.setOnClickListener {
                 playMove(i,j)
              }
              boardlayout.addView(button)

          }
          board.addView(boardlayout)
      }
    }

    private fun playMove(x: Int, y: Int) {
        val button  = getButton(x,y) as Button
        if(firstMove){
            setupMines(x,y)
            showNumbers(x,y)
            timerRunning = true
            firstMove=  !firstMove
        }else{
            if(flag){
              if(boardGame[x][y].isFlagged){
                  boardGame[x][y].isFlagged = !boardGame[x][y].isFlagged
                  button.text = ""
                  button.setBackgroundResource(R.drawable.button)
                  flagCount++
                  minesNumber.text = flagCount.toString()
              }else{
                  if(flagCount>0){
                      boardGame[x][y].isFlagged = !boardGame[x][y].isFlagged
                      button.setBackgroundResource(R.drawable.flag_button)
                      button.background = ContextCompat.getDrawable(this, R.drawable.flag)
                      flagCount--
                      minesNumber.text = flagCount.toString()
                  }
              }
            }else{
                 if(boardGame[x][y].isFlagged || boardGame[x][y].isShown){
                     return;
                 }
                if(boardGame[x][y].value==MINE){
                    gameOver()
                }else{
                    revealNumbers(x,y)
                }
            }
        }
        if(isGameComplete()){
            disableAllButtons()
            Toast.makeText(this,"Game Won",Toast.LENGTH_LONG).show()
        }

    }

    private fun isGameComplete(): Boolean {

        var allMinesFound = true
        var allNumbersShown = true
        boardGame.forEach { row->
            row.forEach {
               if(it.value==MINE){
                   if(!it.isFlagged){
                       allMinesFound = false

                   }
               }
            }
        }

        boardGame.forEach { row->
            row.forEach {
                if(it.value!=MINE){
                    if(!it.isShown){
                        allNumbersShown  = false
                    }
                }
            }
        }
        return allMinesFound || allNumbersShown


    }

    private fun gameOver() {
        showAllMines()
        disableAllButtons()
        Toast.makeText(this, "Game Over. Try Again", Toast.LENGTH_LONG).show()
    }
    private fun disableAllButtons() {
        for (x in 0 until row) {
            for (y in 0 until col) {
                val button = getButton(x, y) as Button
                button.isEnabled = false
                button.setTextColor(ContextCompat.getColor(this, R.color.disabledButtonTextColor))
            }
        }
    }

    private fun showAllMines() {
        for(i in 0 until row){
            for( j in 0 until col){
                if(boardGame[i][j].value==MINE){
                    val button = getButton(i, j) as Button
                    button.setBackgroundResource(R.drawable.mine)
                }

            }
        }
    }

    private fun showNumbers(x: Int, y: Int) {

        for( i in moveposition){
            for( j in moveposition){
                if(!(i==0 && j==0) && (x+i) in 0 until row && (y+j) in 0 until col){
                   revealNumbers(x+i,y+j)
                }
            }
        }

    }

    private fun revealNumbers(rowVal: Int, colVal: Int) {

        if(!boardGame[rowVal][colVal].isShown && !boardGame[rowVal][colVal].isFlagged && boardGame[rowVal][colVal].value!=MINE){
            val button  = getButton(rowVal,colVal) as Button
            button.text  = boardGame[rowVal][colVal].value.toString()
            button.isEnabled  =false
            button.background  =(ContextCompat.getDrawable(this,R.drawable.disabled_button))
            boardGame[rowVal][colVal].isShown  =true
            button.setTextColor(ContextCompat.getColor(this, R.color.disabledButtonTextColor))

            if(boardGame[rowVal][colVal].value==0){
                showNumbers(rowVal,colVal)
            }

        }

    }

    private fun getButton(rowVal: Int, colVal: Int): Any {

        val board = board.getChildAt(rowVal) as LinearLayout;

        return board.getChildAt(colVal) as Button

    }

    private fun setupMines(rowVal: Int, colVal: Int) {

     var i:Int=0
     while(i<mines){
         var x = (0 until row).random()
         var y = (0 until col).random()

         if(x!=rowVal && y!=colVal && boardGame[x][y].value!=MINE){
             boardGame[x][y].value  =MINE
             updateNeighborsVal(x,y)
             i++;
         }
     }
    }

    private fun updateNeighborsVal(x: Int, y: Int) {
        for(i in moveposition){
            for(j in moveposition){
                if((x+i ) in 0 until row && (y+j) in 0 until col && boardGame[x+i][y+j].value!=MINE){
                    boardGame[x+i][y+j].value++;
                }
            }
        }
    }
}
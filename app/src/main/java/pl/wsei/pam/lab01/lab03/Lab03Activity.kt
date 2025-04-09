package pl.wsei.pam.lab01.lab03

import MemoryBoardView
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.gridlayout.widget.GridLayout
import pl.wsei.pam.lab01.R
import java.util.*
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView

    private lateinit var completionPlayer: MediaPlayer
    private lateinit var negativePlayer: MediaPlayer

    private var isSound: Boolean = true

    private var rows: Int = 3
    private var cols: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3)
        rows = size[0]
        cols = size[1]

        mBoard = findViewById(R.id.main)
        mBoard.columnCount = cols
        mBoard.rowCount = rows

        createMemoryBoard(savedInstanceState)
    }

    private fun createMemoryBoard(savedInstanceState: Bundle?) {

        mBoardModel = MemoryBoardView(mBoard, cols, rows, this)

        if (savedInstanceState != null) {
            val savedState = savedInstanceState.getIntArray("game_state")
            savedState?.let {
                mBoardModel.setState(it)

                if (mBoardModel.isGameFinished()) {
                    Toast.makeText(this, "Game finished!", Toast.LENGTH_SHORT).show()
                }
            }

            isSound = savedInstanceState.getBoolean("is_sound", true)
        }

        mBoardModel.setOnGameChangeListener { event ->
            runOnUiThread {
                when (event.state) {
                    GameStates.Matching, GameStates.Match -> {
                        event.tiles.forEach { it.revealed = true }
                    }
                    GameStates.NoMatch -> {
                        event.tiles.forEach { it.revealed = true }
                        Timer().schedule(2000) {
                            runOnUiThread {
                                event.tiles.forEach { it.revealed = false }
                            }
                        }
                    }
                    GameStates.Finished -> {
                        Toast.makeText(this, "Game finished!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val currentState = mBoardModel.getState()

        val stateBundle = Bundle()
        stateBundle.putIntArray("game_state", currentState)
        stateBundle.putBoolean("is_sound", isSound)

        mBoard.removeAllViews()
        mBoard.columnCount = cols
        mBoard.rowCount = rows

        createMemoryBoard(stateBundle)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.board_activity_menu, menu)
        val item = menu.findItem(R.id.board_activity_sound)
        item.setIcon(if (isSound) R.drawable.baseline_campaign_24 else R.drawable.baseline_cancel_24)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.board_activity_sound -> {
                isSound = !isSound
                item.setIcon(if (isSound) R.drawable.baseline_campaign_24 else R.drawable.baseline_cancel_24)
                Toast.makeText(this, if (isSound) "Sound On" else "Sound Off", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePlayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)
    }

    override fun onPause() {
        super.onPause()
        completionPlayer.release()
        negativePlayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("game_state", mBoardModel.getState())
        outState.putBoolean("is_sound", isSound)
    }

    fun playCompletionSound() {
        if (isSound) {
            completionPlayer.start()
        }
    }

    fun playNegativeSound() {
        if (isSound) {
            negativePlayer.start()
        }
    }
}
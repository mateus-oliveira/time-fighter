package com.raywenderlich.timefighter.timefighter

// Bibliotecas importadas
import android.content.IntentSender
import android.nfc.Tag
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // Declaração de variáveis para os objetos da tela
    internal lateinit var tapMeButton: Button
    internal lateinit var gameScoreTextView: TextView
    internal lateinit var timeLeftTextView: TextView

    // Variáveis e valores internos
    internal var score = 0
    internal var gameStarted = false
    internal lateinit var countDownTimer: CountDownTimer
    internal val initialCountDown: Long = 60000
    internal val countDownInterval: Long = 1000
    internal val TAG = MainActivity::class.java.simpleName
    internal var timeLeftOnTimer: Long = 60000

    companion object {
        private val SCORE_KEY = "SCORE_KEY"
        private val TIME_LEFT_KEY ="TIME_LEFT_KEY"
    }

    // Função principal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Mensagem de depuração On Create
        Log.d(TAG, "onCreate called. Pontos: $score & Tempo: $timeLeftOnTimer")

        // Atribuindo os objetos às suas respectivas variáveis
        tapMeButton = findViewById<Button>(R.id.tap_me_button)
        gameScoreTextView = findViewById<TextView>(R.id.game_score_text_view)
        timeLeftTextView = findViewById<TextView>(R.id.time_left_text_view)

        // Função para reiniciar ou restaurar o jogo anterior
        if (savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            if (timeLeftOnTimer < 60000){ restoreGame() }
            else{ resetGame() }
        }
        else {
            resetGame()
        }

        // Definindo a ação do evento "clicar no botão"
        tapMeButton.setOnClickListener { view ->
            incrementScore()
        }
    }

    // Restaurando o jogo
    private fun restoreGame(){
        gameScoreTextView.text = getString(R.string.your_score, score.toString())
        val restoredTime = timeLeftOnTimer/1000
        timeLeftTextView.text = getString(R.string.time_left, restoredTime.toString())

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                timeLeftOnTimer = millisUntilFinished
                var timeLeft = millisUntilFinished/1000
                timeLeftTextView.text = getString(R.string.time_left, timeLeft.toString())
            }

            override fun onFinish(){
                endGame()
            }
        }
        startGame()
    }

    // Salvando estado ao rotacionar a tela
    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()

        // Mensagem de depuração On Save Instance State
        Log.d(TAG,"onSaveInstanceState: Salvando Pontos: $score & Tempo: $timeLeftOnTimer")
    }

    // Não destruir estado atual
    override fun onDestroy() {
        super.onDestroy()

        // Mensagem de depuração On Destroy
        Log.d(TAG, "onDestroy called.")
    }

    // Função para recomeçar o jogo
    private fun resetGame(){
        score = 0
        gameScoreTextView.text = getString(R.string.your_score, score.toString())
        timeLeftOnTimer = 60000

        val initialTimeLeft = initialCountDown / 1000
        timeLeftTextView.text = getString(R.string.time_left, initialTimeLeft.toString())

        countDownTimer = object: CountDownTimer(initialCountDown, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.time_left, timeLeft.toString())
            }

            override fun onFinish(){
                endGame()
            }
        }
        gameStarted = false
    }

    // Iniciando a contagem do tempo
    private fun startGame(){
        countDownTimer.start()
        gameStarted = true
    }

    // Finalizando o jogo
    private fun endGame(){
        Toast.makeText(this, getString(R.string.game_over_message, score.toString()), Toast.LENGTH_SHORT).show()
        resetGame()
    }

    // Função interna para incrementação do score
    private fun incrementScore(){
        if (!gameStarted){ startGame() }

        score += 1
        val newScore = getString(R.string.your_score, score.toString())
        gameScoreTextView.text = newScore
    }
}

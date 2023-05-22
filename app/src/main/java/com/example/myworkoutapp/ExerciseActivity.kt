package com.example.myworkoutapp

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myworkoutapp.databinding.ActivityExerciseBinding
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {


    private var restTimer: CountDownTimer? = null
    private var restProgress =0


    // Adding a variables for the 30 seconds Exercise timer
    // START
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    // END
    private var exerciseTimerDuration:Long = 30
    // TODO(Step 6 - The Variable for the exercise list and current position of exercise here it is -1 as the list starting element is 0.)
    // START
    private var exerciseList: ArrayList<Exercise>? = null
    private var currentExercisePosition = -1

    private var tts:TextToSpeech?=null
    private var player:MediaPlayer?=null
    // END
    // create a binding variable
    private var binding:ActivityExerciseBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//inflate the layout
        binding = ActivityExerciseBinding.inflate(layoutInflater)
// pass in binding?.root in the content view
        setContentView(binding?.root)
// then set support action bar and get toolBarExcerciser using the binding
//variable
        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }
        // TODO(Step 7 - Initializing and Assigning a default exercise list to our list variable.)
        // START
        exerciseList = Constants.defaultExerciseList()
        // END
        tts= TextToSpeech(this,this)
        setupRestView()
    }


    //Setting up the Get Ready View with 10 seconds of timer
    //START
    /**
     * Function is used to set the timer for REST.
     */
    private fun setupRestView() {

        try{
            val soundURI= Uri.parse("android.resource://com.example.myworkoutapp/"+R.raw.sound)
            player=MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping=false
            player?.start()
        }catch(e:Exception){
            e.printStackTrace()
        }

        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.upcomingLabel?.visibility=View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility=View.VISIBLE
        binding?.tvUpcomingExerciseName?.text=exerciseList!![currentExercisePosition+1].getName()
        /**
         * Here firstly we will check if the timer is running the and it is not null then cancel the running timer and start the new one.
         * And set the progress to initial which is 0.
         */
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        // This function is used to set the progress details.
        setRestProgressBar()
    }
    // END

    // Setting up the 10 seconds timer for rest view and updating it continuously.
    //START
    /**
     * Function is used to set the progress of timer using the progress
     */
    private fun setRestProgressBar() {

        binding?.progressBar?.progress = restProgress // Sets the current progress to the specified value.

        /**
         * @param millisInFuture The number of millis in the future from the call
         *   to {#start()} until the countdown is done and {#onFinish()}
         *   is called.
         * @param countDownInterval The interval along the way to receive
         *   {#onTick(long)} callbacks.
         */
        // Here we have started a timer of 10 seconds so the 10000 is milliseconds is 10 seconds and the countdown interval is 1 second so it 1000.
        restTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++ // It is increased by 1
                binding?.progressBar?.progress = 10 - restProgress // Indicates progress bar progress
                binding?.tvTimer?.text =
                    (10 - restProgress).toString()  // Current progress is set to text view in terms of seconds.
            }

            override fun onFinish() {
                // TODO(Step 8 - Increasing the current position of the exercise after rest view.)
                // START
                currentExercisePosition++
                setupExerciseView()
            }
        }.start()
    }
    //END


    // Setting up the Exercise View with a 30 seconds timer
    // START
    /**
     * Function is used to set the progress of the timer using the progress for Exercise View.
     */
    private fun setupExerciseView() {

        // Here according to the view make it visible as this is Exercise View so exercise view is visible and rest view is not.
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.upcomingLabel?.visibility=View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility=View.INVISIBLE

        /**
         * Here firstly we will check if the timer is running and it is not null then cancel the running timer and start the new one.
         * And set the progress to the initial value which is 0.
         */
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())

        // TODO(Step 9 - Setting up the current exercise name and image to view to the UI element.)
        // START
        /**
         * Here current exercise name and image is set to exercise view.
         */
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        // END
        setExerciseProgressBar()

    }
    // END


    // After REST View Setting up the 30 seconds timer for the Exercise view and updating it continuously
    // START
    /**
     * Function is used to set the progress of the timer using the progress for Exercise View for 30 Seconds
     */
    private fun setExerciseProgressBar() {

        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = exerciseTimerDuration.toInt() - exerciseProgress
                binding?.tvTimerExercise?.text = (exerciseTimerDuration.toInt() - exerciseProgress).toString()
            }

            override fun onFinish() {
                // TODO(Step 10 - Updating the view after completing the 30 seconds exercise.)
                // START
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    setupRestView()
                } else {

                    Toast.makeText(
                        this@ExerciseActivity,
                        "Congratulations! You have completed the 7 minutes workout.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // END
            }
        }.start()

    }
    // END


    // Destroying the timer when closing the activity or app
    //START
    /**
     * Here in the Destroy function we will reset the rest timer if it is running.
     */
    public override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player!=null){
            player!!.stop()
        }
        binding = null
    }

    override fun onInit(status: Int) {
        if(status==TextToSpeech.SUCCESS){
            val result=tts?.setLanguage(Locale.US)

            if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","The Lanquage specified is not supported!")
            }else{
                Log.e("TTS","Initialization Failed!")
            }
        }
    }
    private fun speakOut(text:String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
}
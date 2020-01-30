package ga.harrysullivan.langsy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ga.harrysullivan.langsy.adapters.CorrectAnswerAdapter
import ga.harrysullivan.langsy.adapters.RevealPanelAdapter
import ga.harrysullivan.langsy.constants.SpacedRepetition
import ga.harrysullivan.langsy.controllers.Engine
import ga.harrysullivan.langsy.data.Trainer
import ga.harrysullivan.langsy.utils.InjectorUtils
import ga.harrysullivan.langsy.utils.observeOnce
import ga.harrysullivan.langsy.view_models.ContentViewModel
import ga.harrysullivan.langsy.view_models.CurrentCourseViewModel
import ga.harrysullivan.langsy.view_models.TrainerViewModel
import kotlinx.android.synthetic.main.activity_assessment.*
import net.gcardone.junidecode.Junidecode.unidecode


class AssessmentActivity : AppCompatActivity() {

    private lateinit var mContentViewModel: ContentViewModel
    private lateinit var mCorrectAnswerAdapter: CorrectAnswerAdapter
    private lateinit var mTrainerViewModel: TrainerViewModel
    private lateinit var mCurrentCourseViewModel: CurrentCourseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment)

        mContentViewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(ContentViewModel::class.java)

        val revealPanelAdapter = RevealPanelAdapter(this.layoutInflater, assessment_root)
        assessment_reveal.setOnClickListener {
            revealPanelAdapter.show()
        }

        var dirty = false

        mCorrectAnswerAdapter = CorrectAnswerAdapter(this.layoutInflater, assessment_root)
        assessment_next_button.setOnClickListener {
            mCorrectAnswerAdapter.show()
        }

//        val spontaneousRecallAdapter = SpontaneousRecallAdapter(this.layoutInflater, assessment_root)
//        Handler().postDelayed({
//            spontaneousRecallAdapter.show()
//        }, 3000)

        val trainerFactory = InjectorUtils.provideTrainerViewModelFactory()
        mTrainerViewModel = ViewModelProviders.of(this, trainerFactory)
            .get(TrainerViewModel::class.java)

        val currentCourseFactory = InjectorUtils.provideCurrentCourseViewModelFactory()
        mCurrentCourseViewModel = ViewModelProviders.of(this, currentCourseFactory)
            .get(CurrentCourseViewModel::class.java)

        mTrainerViewModel.getTrainer().observeOnce(this, Observer { trainer ->
            assessment_content.text = trainer.content
            assessment_stage.text =
                toPct(trainer.contentObj.stage.toDouble() / SpacedRepetition.THRESHOLD_OF_MASTERY.toDouble())
            revealPanelAdapter.setContent(trainer.translation, unidecode(trainer.translation))
            mCorrectAnswerAdapter.setContent(trainer.translation)

            assessment_next_button.setOnClickListener {
                val userInput = assessment_edit_text.text.toString().toLowerCase().trim()
                if (userInput == trainer.translation.toLowerCase()) {

                    mContentViewModel.setLastReviewed(
                        trainer.contentObj.uid,
                        System.currentTimeMillis() / 1000
                    )

                    if (dirty) {
                        mCorrectAnswerAdapter.show()
                        setCallbackWrong()
                    } else {
                        mContentViewModel.addToStage(trainer.contentObj.uid, 1)
                        setCallbackRight(trainer)
                        mCorrectAnswerAdapter.show()
                    }

                } else {
                    revealPanelAdapter.show()
                    dirty = true
                }
            }
        })
    }

    private fun setCallbackWrong() {
        mCorrectAnswerAdapter.setCallback(View.OnClickListener {
            val intent =
                Intent(this@AssessmentActivity, VisualLearningActivity::class.java)
            startActivity(intent)
        })
    }

    private fun setCallbackRight(trainer: Trainer) {
        mCorrectAnswerAdapter.setCallback(View.OnClickListener {
            mContentViewModel.fetchByLanguageAndStage(
                trainer.contentObj.language,
                SpacedRepetition.THRESHOLD_OF_PROBABALISTIC_MASTERY
            ).observeOnce(this, Observer { selectedContent ->
                val shouldGetNew = Engine.shouldDoNew(selectedContent)
                if (shouldGetNew) {
                    mCurrentCourseViewModel.getCurrentCourse()
                        .observeOnce(this, Observer { currentCourse ->
                            val (content, trainer) = Engine.newContent(
                                selectedContent,
                                this.application,
                                currentCourse.course
                            )
                            mContentViewModel.insert(content)
                            mTrainerViewModel.editTrainer(trainer)

                            val intent =
                                Intent(this@AssessmentActivity, VisualLearningActivity::class.java)
                            startActivity(intent)
                        })
                } else {
                    val trainer = Engine.practice(selectedContent, this.application)

                    mTrainerViewModel.editTrainer(trainer)
                    val intent =
                        Intent(this@AssessmentActivity, AssessmentActivity::class.java)
                    startActivity(intent)
                }

            })
        })
    }

    private fun toPct(n: Double): String {
        return "${(n * 100).toInt()}%"
    }
}

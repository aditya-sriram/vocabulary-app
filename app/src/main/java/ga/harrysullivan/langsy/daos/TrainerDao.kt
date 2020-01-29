package ga.harrysullivan.langsy.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ga.harrysullivan.langsy.data.Trainer
import ga.harrysullivan.langsy.models.Course

class TrainerDao {

    private val trainer = MutableLiveData<Trainer>()

    fun editTrainer(newTrainer: Trainer) {
        trainer.value = newTrainer
    }

    fun getTrainer() = trainer as LiveData<Trainer>
}
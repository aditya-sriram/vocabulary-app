package ga.harrysullivan.langsy.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import ga.harrysullivan.langsy.constants.SpacedRepetition
import ga.harrysullivan.langsy.models.Content

@Dao
public interface ContentDao {
    @Query("select * from Content")
    fun fetchAll(): LiveData<List<Content>>

    @Query("select * from Content where language = :langCode and stage < :stage")
    fun fetchByLanguageAndStage(langCode: String, stage: Int): LiveData<List<Content>>

    @Query("select * from Content where language = :langCode and stage <= '${SpacedRepetition.THRESHOLD_OF_PROBABILISTIC_MASTERY}' order by stage limit 1")
    fun fetchPractice(langCode: String): LiveData<Content>

    @Query("update Content set stage = stage + :amt where uid = :uid")
    suspend fun addToStage(uid: Int, amt: Int)

    @Query("update Content set lastReviewed = :time where uid = :uid")
    suspend fun setLastReviewed(uid: Int, time: Long)

    @Query("delete from Content where language = :language")
    suspend fun dropCourse(language: String)

    @Insert
    suspend fun insert(arg: Content)

    @Update
    suspend fun update(arg: Content)

    @Delete
    suspend fun delete(arg: Content)
}
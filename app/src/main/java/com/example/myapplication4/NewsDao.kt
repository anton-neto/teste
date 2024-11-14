import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NewsDao {

    @Insert
    suspend fun insertNews(news: News)

    @Update
    suspend fun updateNews(news: News)

    @Delete
    suspend fun deleteNews(news: News)

    @Query("SELECT * FROM news")
    suspend fun getAllNews(): List<News>

    @Query("SELECT * FROM news WHERE isInteresting = 1")
    suspend fun getInterestingNews(): List<News>
}

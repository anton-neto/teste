data class NYTimesResponse(
    val results: List<Article>
)

data class Article(
    val title: String,
    val abstract: String?,
    val url: String
)

package com.winnerx0.pika.messages;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class KeywordSearchRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Document> search(String query, int limit){
        String sql = """
                SELECT
                id,
                content,
                (
                    ts_rank_cd(
                            search_vector,
                            websearch_to_tsquery('simple', ?)
                        )
                    +
                    CASE WHEN lower(content) LIKE '%' || lower(?) || '%'
                        THEN 1.0
                        ELSE 0.0
                    END
                ) AS score
                FROM vector_store
                WHERE search_vector @@ websearch_to_tsquery('simple', ?)
                OR lower(content) LIKE '%' || lower(?) || '%'
                ORDER BY score DESC
                LIMIT ?
                """;

        return jdbcTemplate.query(sql,
                (rs, rowsNum) ->
                        Document.builder()
                                .id(rs.getString("id"))
                                .text(rs.getString("content"))
                                .metadata("retriever", "keyword")
                                .score(rs.getDouble("score"))
                                .build(),
                query,
                query,
                query,
                query,
                limit);
    }
}

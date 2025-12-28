package kg.dementia.billing.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReportRepository {

    private final JdbcTemplate jdbcTemplate;

    /*
     * Тут юзаю нативный sql через JdbcTemplate.
     * Для сложной аналитики с группировками оно удобнее и быстрее, чем мучиться с JPQL или Criteria API.
     */
    public List<Map<String, Object>> getTariffAnalytics() {
        String sql = """
                    SELECT
                        t.name AS tariff_name,
                        COUNT(s.id) AS total_subscribers,
                        SUM(CASE WHEN s.is_active = true THEN 1 ELSE 0 END) AS active_subscribers,
                        AVG(s.balance) AS average_balance,
                        SUM(s.balance) AS total_money_stored
                    FROM tariffs t
                    LEFT JOIN subscribers s ON t.id = s.tariff_id
                    GROUP BY t.id, t.name
                    ORDER BY total_subscribers DESC
                """;

        return jdbcTemplate.queryForList(sql);
    }
}

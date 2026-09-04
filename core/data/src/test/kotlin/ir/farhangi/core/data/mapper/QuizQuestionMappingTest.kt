package ir.farhangi.core.data.mapper

import ir.farhangi.core.network.model.QuizQuestionDto
import org.junit.Assert.assertEquals
import org.junit.Test

class QuizQuestionMappingTest {

    @Test
    fun toDomain_keepsCorrectIndex() {
        val dto = QuizQuestionDto(
            id = "q2",
            prompt = "پایتخت ایران کجاست؟",
            options = listOf("تبریز", "تهران", "شیراز", "اصفهان"),
            correctIndex = 1,
        )
        val domain = dto.toDomain()
        assertEquals("q2", domain.id)
        assertEquals(1, domain.correctIndex)
        assertEquals(4, domain.options.size)
    }
}

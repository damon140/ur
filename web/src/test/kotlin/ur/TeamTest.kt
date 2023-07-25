import com.damon140.ur.Team
import kotlin.test.Test
import kotlin.test.assertEquals

class TeamTest {

    @Test
    fun other() {
        assertEquals(Team.White, Team.Black.other())
        assertEquals(Team.Black, Team.White.other())
    }

}

import com.damon140.ur.Team
import kotlin.test.Test
import kotlin.test.assertEquals

class TeamTest {

    @Test
    fun other() {
        assertEquals(Team.white, Team.black.other())
        assertEquals(Team.black, Team.white.other())
    }

}

package ur

import com.damon140.ur.PlayArea
import com.damon140.ur.Square.*
import com.damon140.ur.Team.Black
import com.damon140.ur.Team.White
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayAreaTest {

    @Test
    fun moveTakes() {
        val p = PlayArea(White)
        assertEquals(p.moveTakes(White, White_run_on_4, Shared_1), false)

        p.move(Off_board_unstarted, Black_run_on_4, Black)
        p.move(Black_run_on_4, Shared_1, Black)
        p.move(Off_board_unstarted, White_run_on_4, White)
        assertEquals(p.moveTakes(White, White_run_on_4, Shared_1), true)
        assertEquals(p.moveTakes(Black, White_run_on_4, Shared_1), false)
    }

    @Test
    fun moveIsOnShareRace() {
        val p = PlayArea(White)

        assertEquals(p.moveIsOnShareRace(Black, Shared_1, Shared_2), false)

        p.move(Off_board_unstarted, Black_run_on_4, Black)
        assertEquals(p.moveIsOnShareRace(Black, Black_run_on_4, Shared_1), false)

        p.move(Black_run_on_4, Shared_1, Black)
        assertEquals(p.moveIsOnShareRace(Black, Shared_1, Shared_2), true)
    }

    @Test
    fun empty() {
        val p = PlayArea(White)
        assertEquals(p.empty(White_run_on_4), true)

        p.move(Off_board_unstarted, White_run_on_4, White)
        assertEquals(p.empty(White_run_on_4), false)
    }

    @Test
    fun teamHasCounterOn() {
        val p = PlayArea(White)
        assertEquals(p.teamHasCounterOn(White, White_run_on_4), false)

        p.move(Off_board_unstarted, White_run_on_4, White)
        assertEquals(p.teamHasCounterOn(White, White_run_on_4), true)
        assertEquals(p.teamHasCounterOn(Black, White_run_on_4), false)
    }

}

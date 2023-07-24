package ur

import com.damon140.ur.PlayArea
import com.damon140.ur.Square.*
import com.damon140.ur.Team.black
import com.damon140.ur.Team.white
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayAreaTest {

    @Test
    fun moveTakes() {
        val p = PlayArea(white)
        assertEquals(p.moveTakes(white, white_run_on_4, shared_1), false)

        p.move(off_board_unstarted, black_run_on_4, black)
        p.move(black_run_on_4, shared_1, black)
        p.move(off_board_unstarted, white_run_on_4, white)
        assertEquals(p.moveTakes(white, white_run_on_4, shared_1), true)
        assertEquals(p.moveTakes(black, white_run_on_4, shared_1), false)
    }

    @Test
    fun moveIsOnShareRace() {
        val p = PlayArea(white)

        assertEquals(p.moveIsOnShareRace(black, shared_1, shared_2), false)

        p.move(off_board_unstarted, black_run_on_4, black)
        assertEquals(p.moveIsOnShareRace(black, black_run_on_4, shared_1), false)

        p.move(black_run_on_4, shared_1, black)
        assertEquals(p.moveIsOnShareRace(black, shared_1, shared_2), true)
    }

    @Test
    fun empty() {
        val p = PlayArea(white)
        assertEquals(p.empty(white_run_on_4), true)

        p.move(off_board_unstarted, white_run_on_4, white)
        assertEquals(p.empty(white_run_on_4), false)
    }

    @Test
    fun teamHasCounterOn() {
        val p = PlayArea(white)
        assertEquals(p.teamHasCounterOn(white, white_run_on_4), false)

        p.move(off_board_unstarted, white_run_on_4, white)
        assertEquals(p.teamHasCounterOn(white, white_run_on_4), true)
        assertEquals(p.teamHasCounterOn(black, white_run_on_4), false)
    }

}

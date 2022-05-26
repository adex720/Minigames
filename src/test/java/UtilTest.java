import io.github.adex720.minigames.util.Util;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * @author adex720
 */
public class UtilTest {

    @Test
    void millisecondsUntilNextWeek() {
        long millisecondsInOneWeek = 1000 * 60 * 60 * 24 * 7;
        long millisecondsBefore0 = 1000 * 60 * 60 * 24 * 3; // milliseconds from the start of the week containing 1/1/1970 to 1/1/1970

        int[] times = {4, 6167258, 1000 * 60 * 60 * 24};

        for (int time : times) {
            int expected = (int) (millisecondsInOneWeek - time);
            int received = Util.getMillisecondsUntilUtcNewWeek(time + millisecondsBefore0);
            MatcherAssert.assertThat("Amount of milliseconds until next week is wrong! Expected: "
                            + Util.formatNumber(expected) + ", received: " + Util.formatNumber(received),
                    expected == received);
        }
    }

}

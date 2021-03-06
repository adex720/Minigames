import com.google.gson.JsonObject;
import io.github.adex720.minigames.gameplay.guild.Guild;
import io.github.adex720.minigames.gameplay.guild.boss.GuildBoss;
import io.github.adex720.minigames.gameplay.guild.boss.GuildBossReward;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * @author adex720
 */
class GuildTest {

    Guild exampleGuild = new Guild(bot, 22L, "User#1234", System.currentTimeMillis(), new ArrayList<>(), new ArrayList<>(), "name", 123456L, true, 27, 12,
            new GuildBoss("Boss 1", 0, 1234567, 20, new GuildBossReward(200)));

    GuildTest() {
        exampleGuild.addMember(17L, "User1#1111", System.currentTimeMillis());
        exampleGuild.addMember(18L, "User2#2222", System.currentTimeMillis());
    }

    @Test
    void saveAndLoad() {
        JsonObject asJson = exampleGuild.getAsJson();
        JsonObject loadedJson = Guild.fromJson(asJson, null).getAsJson();

        MatcherAssert.assertThat("Json is not the same after loading", asJson.equals(loadedJson));
    }

    @Test
    void sizeIsCorrect() {
        MatcherAssert.assertThat("Guild size is wrong", exampleGuild.size() == 3);
        MatcherAssert.assertThat("Guild size without owner is wrong", exampleGuild.sizeWithoutOwner() == 2);
    }

    @Test
    void isInGuild() {

        MatcherAssert.assertThat("Owner is not counted as guild member", exampleGuild.isInGuild(22L));
        MatcherAssert.assertThat("All members are not counted as guild members", exampleGuild.isInGuild(17L));
        MatcherAssert.assertThat("All members are not counted as guild members", exampleGuild.isInGuild(18L));

        MatcherAssert.assertThat("Members not on the guild are counted as guild members", !exampleGuild.isInGuild(16L));
    }


}
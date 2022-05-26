import com.google.gson.JsonObject;
import io.github.adex720.minigames.gameplay.guild.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * @author adex720
 */
class GuildTest {

    Guild exampleGuild = new Guild(22L, new long[]{17L, 18L}, "test", 123456, 27, 12);


    @Test
    void saveAndLoad() {
        JsonObject asJson = exampleGuild.getAsJson();
        JsonObject loadedJson = Guild.fromJson(asJson).getAsJson();

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

    @Test
    void infoMessage() {
        MessageEmbed response = exampleGuild.getInfoMessage();
    }


}
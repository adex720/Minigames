package io.github.adex720.minigames.gameplay.profile.quest;

import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;

/**
 * QuestType determines when the quest gets progressed.
 */
public abstract class QuestType {

    public final int id;
    public final String name;

    public final String textStart; // If text was 'Win 2 minigames'
    public final String textEnd; // textStart = "Win" and textEnd = "minigames"

    public final String hint;

    public final int[] goals; // List for amount of actions needed to complete this quest for each difficulty. 0-> easy, 1 -> medium, 2 -> hard


    protected QuestType(int id, String name, String textStart, String textEnd, String hint, int[] goals) {
        this.id = id;
        this.name = name;
        this.textStart = textStart;
        this.textEnd = textEnd;
        this.hint = hint;
        this.goals = goals;
    }

    /**
     * Quest gets progressed when a minigame is finished.
     *
     * @param goals     array of goals for difficulties as [easy, normal, hard].
     * @param textStart text displayed before progress.
     * @param textEnd   text displayed after progress.
     */
    public static QuestType playAny(int id, String name, String textStart, String textEnd, String hint, int[] goals) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int minigamePlayed(MinigameType<? extends Minigame> type, Profile profile) {
                return 1;
            }
        };
    }

    /**
     * Quest gets progressed when a minigame is finished.
     *
     * @param minigame  type of minigame required.
     * @param goals     array of goals for difficulties as [easy, normal, hard].
     * @param textStart text displayed before progress.
     * @param textEnd   text displayed after progress.
     */
    public static QuestType playMinigame(int id, String name, String textStart, String textEnd, String hint, int[] goals, String minigame) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int minigamePlayed(MinigameType<? extends Minigame> type, Profile profile) {
                return type.name.equals(minigame) ? 1 : 0;
            }
        };
    }

    /**
     * Quest gets progressed when a party minigame is finished.
     *
     * @param goals     array of goals for difficulties as [easy, normal, hard].
     * @param textStart text displayed before progress.
     * @param textEnd   text displayed after progress.
     */
    public static QuestType playPartyMinigame(int id, String name, String textStart, String textEnd, String hint, int[] goals) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int minigamePlayed(MinigameType<? extends Minigame> type, Profile profile) {
                return profile.isInParty() ? 1 : 0;
            }
        };
    }

    /**
     * Quest gets progressed when a minigame is won.
     *
     * @param goals     array of goals for difficulties as [easy, normal, hard].
     * @param textStart text displayed before progress.
     * @param textEnd   text displayed after progress.
     */
    public static QuestType winAny(int id, String name, String textStart, String textEnd, String hint, int[] goals) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int minigameWon(MinigameType<? extends Minigame> type, Profile profile) {
                return 1;
            }
        };
    }

    /**
     * Quest gets progressed when a minigame is won.
     *
     * @param minigame  type of minigame required.
     * @param goals     array of goals for difficulties as [easy, normal, hard].
     * @param textStart text displayed before progress.
     * @param textEnd   text displayed after progress.
     */
    public static QuestType winMinigame(int id, String name, String textStart, String textEnd, String hint, int[] goals, String minigame) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int minigameWon(MinigameType<? extends Minigame> type, Profile profile) {
                return type.name.equals(minigame) ? 1 : 0;
            }
        };
    }

    /**
     * Quest gets progressed when a party minigame is won.
     *
     * @param goals     array of goals for difficulties as [easy, normal, hard].
     * @param textStart text displayed before progress.
     * @param textEnd   text displayed after progress.
     */
    public static QuestType winPartyMinigame(int id, String name, String textStart, String textEnd, String hint, int[] goals) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int minigameWon(MinigameType<? extends Minigame> type, Profile profile) {
                return profile.isInParty() ? 1 : 0;
            }
        };
    }

    /**
     * Quest gets progressed when coins are earned.
     *
     * @param goals     array of goals for difficulties as [easy, normal, hard].
     * @param textStart text displayed before progress.
     * @param textEnd   text displayed after progress.
     */
    public static QuestType earnCoins(int id, String name, String textStart, String textEnd, String hint, int[] goals) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int coinsEarned(int amount, Profile profile) {
                return amount;
            }
        };
    }

    /**
     * Quest gets progressed when crate is opened.
     *
     * @param goals     array of goals for difficulties as [easy, normal, hard].
     * @param textStart text displayed before progress.
     * @param textEnd   text displayed after progress.
     */
    public static QuestType openCrates(int id, String name, String textStart, String textEnd, String hint, int[] goals) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int crateOpened(CrateType rarity, Profile profile) {
                return 1;
            }
        };
    }

    /**
     * Quest gets progressed crate is opened.
     *
     * @param crate     type of crate required.
     * @param goals     array of goals for difficulties as [easy, normal, hard].
     * @param textStart text displayed before progress.
     * @param textEnd   text displayed after progress.
     */
    public static QuestType openCrates(int id, String name, String textStart, String textEnd, String hint, int[] goals, String crate) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int crateOpened(CrateType rarity, Profile profile) {
                return rarity.name.equals(crate) ? 1 : 0;
            }
        };
    }

    /**
     * Quest gets progressed when booster is used.
     *
     * @param goals     array of goals for difficulties as [easy, normal, hard].
     * @param textStart text displayed before progress.
     * @param textEnd   text displayed after progress.
     */
    public static QuestType useBoosters(int id, String name, String textStart, String textEnd, String hint, int[] goals) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int boosterUsed(Profile profile) {
                return 1;
            }
        };
    }

    /**
     * Quest gets progressed when kit is claimed.
     *
     * @param goals     array of goals for difficulties as [easy, normal, hard].
     * @param textStart text displayed before progress.
     * @param textEnd   text displayed after progress.
     */
    public static QuestType claimKits(int id, String name, String textStart, String textEnd, String hint, int[] goals) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int kitClaimed(String kit, Profile profile) {
                return 1;
            }
        };
    }

    /**
     * Quest gets progressed when kit is claimed.
     *
     * @param requiredKit name of kit required.
     * @param goals       array of goals for difficulties as [easy, normal, hard].
     * @param textStart   text displayed before progress.
     * @param textEnd     text displayed after progress.
     */
    public static QuestType claimKits(int id, String name, String textStart, String textEnd, String hint, int[] goals, String requiredKit) {
        return new QuestType(id, name, textStart, textEnd, hint, goals) {
            @Override
            public int kitClaimed(String kit, Profile profile) {
                return kit.equals(requiredKit) ? 1 : 0;
            }
        };
    }

    public String getName() {
        return name;
    }

    public String getChallenge(int difficultyId) {
        return textStart + " " + goals[difficultyId] + " " + textEnd;
    }

    public int minigamePlayed(MinigameType<? extends Minigame> type, Profile profile) {
        return 0;
    }

    public int minigameWon(MinigameType<? extends Minigame> type, Profile profile) {
        return 0;
    }

    public int coinsEarned(int amount, Profile profile) {
        return 0;
    }

    public int crateOpened(CrateType rarity, Profile profile) {
        return 0;
    }

    public int boosterUsed(Profile profile) {
        return 0;
    }

    public int kitClaimed(String kit, Profile profile) {
        return 0;
    }
}

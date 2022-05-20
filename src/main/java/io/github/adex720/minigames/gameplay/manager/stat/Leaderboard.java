package io.github.adex720.minigames.gameplay.manager.stat;

import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.util.Pair;
import io.github.adex720.minigames.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * A List containing a {@link Pair} containing value of a stat and the owner {@link Profile}
 * When the stat of the player gets changed {@link Leaderboard#update(Profile)} should be called.
 *
 * @author adex720
 */
public class Leaderboard extends AbstractSequentialList<Pair<Integer, Profile>> implements List<Pair<Integer, Profile>> {

    public final int statId;

    int size = 0;

    private Node first;
    private Node last;

    /**
     * Creates a new leaderboard with no entries.
     *
     * @param statId if of the stat to sort by
     */
    public Leaderboard(int statId) {
        this.statId = statId;
    }

    /**
     * Creates a new leaderboard with given profiles. The
     *
     * @param profiles Profiles to include on leaderboard
     * @param statId   if of the stat to sort by
     */
    public Leaderboard(Set<Profile> profiles, int statId) {
        this(statId);

        addAll(calculateValues(profiles, statId));
    }

    /**
     * Converts the profiles to {@link Collection} of {@link Pair} with the first entry being score on stat
     * and second being the {@link Profile}.
     * The entries are not sorted.
     *
     * @param profiles Profiles to convert
     * @param statId   id of the stat to sort by
     */
    public static Collection<Pair<Integer, Profile>> calculateValues(Set<Profile> profiles, int statId) {
        Collection<Pair<Integer, Profile>> values = new HashSet<>();

        for (Profile profile : profiles) {
            int value = profile.getStatValue(statId);
            values.add(new Pair<>(value, profile));
        }

        return values;
    }

    /**
     * Adds the profile on the leaderboard on the correct index.
     */
    public void add(Profile profile) {
        int value = profile.getStatValue(statId);
        final Pair<Integer, Profile> pair = new Pair<>(value, profile);

        if (value > 0) add(pair);
        else addLast(pair);
    }


    /**
     * Adds the profile on the leaderboard on the correct index.
     */
    public boolean add(Pair<Integer, Profile> pair) {
        if (pair.first > 0) add(firstIndexWithSmallerValue(pair.first), pair);
        else addLast(pair);

        return true;
    }


    /**
     * Adds the profile on the leaderboard on the given index.
     */
    public void add(int index, Pair<Integer, Profile> element) {
        if (index == size) linkLast(element);
        else linkBefore(element, getNode(index));
    }


    /**
     * Adds the profile on the leaderboard as last.
     */
    public void addLast(Pair<Integer, Profile> e) {
        linkLast(e);
    }


    /**
     * Adds the profiles on the leaderboard on the correct indexes.
     */
    public boolean addAll(Collection<? extends Pair<Integer, Profile>> collection) {
        for (Pair<Integer, Profile> pair : collection) {
            add(pair);
        }
        return true;
    }

    /**
     * Returns the index of the {@link Profile} on the leaderboard.
     */
    public int indexOf(Profile profile) {
        if (profile == null) return -1;
        ListIterator<Pair<Integer, Profile>> iterator = listIterator(size());

        while (iterator.hasNext()) {
            Pair<Integer, Profile> value = iterator.next();

            if (value.second == profile) return iterator.previousIndex(); // TODO: test if works
        }

        return -1;
    }

    /**
     * Returns the index of the user on the leaderboard.
     *
     * @param userId id of the user
     */
    public int indexOf(long userId) {
        ListIterator<Pair<Integer, Profile>> iterator = listIterator(size());

        while (iterator.hasNext()) {
            Pair<Integer, Profile> value = iterator.next();

            if (value.second.getId() == userId) return iterator.previousIndex(); // TODO: test if works
        }

        return -1;
    }

    /**
     * Returns the index of the first entry on the leader where the score is correct.
     *
     * @param statValue score
     */
    public int firstIndexOf(int statValue) {
        ListIterator<Pair<Integer, Profile>> iterator = listIterator();

        while (iterator.hasNext()) {
            Pair<Integer, Profile> value = iterator.next();

            if (value.first == statValue) return iterator.previousIndex();
        }

        return -1;
    }

    /**
     * Returns the index of the last entry on the leader where the score is correct.
     *
     * @param statValue score
     */
    public int lastIndexOf(int statValue) {
        if (statValue == 0) return size - 1;

        ListIterator<Pair<Integer, Profile>> iterator = listIterator(size());

        while (iterator.hasPrevious()) {
            Pair<Integer, Profile> value = iterator.previous();

            if (value.first == statValue) return iterator.nextIndex();
        }

        return -1;
    }

    /**
     * Returns the index of the first entry on the leader where the score is less than the given.
     *
     * @param value score
     */
    public int firstIndexWithSmallerValue(int value) {
        if (value == 0) return size;

        ListIterator<Pair<Integer, Profile>> iterator = listIterator();

        while (iterator.hasNext()) {
            Pair<Integer, Profile> pair = iterator.next();

            if (pair.first < value) return iterator.previousIndex();
        }

        return size;
    }

    /**
     * Removes the profile from the leaderboard.
     */
    public void remove(Profile profile) {
        remove(profile.getId());
    }

    /**
     * Removes the profile of the user from the leaderboard.
     *
     * @param userId id of the user.
     */
    public void remove(long userId) {
        unlink(getNode(userId));
    }

    /**
     * Removes the entry from the index from the leaderboard.
     */
    public Pair<Integer, Profile> remove(int index) {
        return unlink(getNode(index));
    }

    /**
     * Calculates and moves the entry of the {@link Profile} to its correct position.
     */
    public void update(Profile profile) {
        int stat = profile.getStatValue(statId);

        Node node = getNode(profile.getId());

        while (node.previous != null && node.previous.item.first < stat) {
            moveForward(node);
        }

        while (node.next != null && node.next.item.first > stat) {
            moveBackWards(node);
        }
    }

    /**
     * Moves tre {@link Leaderboard.Node} one place forward on ranks.
     */
    public void moveForward(Node forwardGoing) {
        Node backGoing = forwardGoing.next;

        if (backGoing == null) return; // The movable entry is the last node.

        Node previousNode = forwardGoing.previous;
        Node nextNode = backGoing.next;

        previousNode.next = backGoing;
        nextNode.previous = forwardGoing;

        forwardGoing.previous = backGoing;
        forwardGoing.next = nextNode;

        backGoing.previous = previousNode;
        backGoing.next = forwardGoing;
    }

    /**
     * Moves tre {@link Leaderboard.Node} one place backward on ranks.
     */
    public void moveBackWards(Node node) {
        if (node.previous == null) return; // The movable node is the first node.

        moveForward(node.previous);
    }

    /**
     * Returns the rank of the profile.
     * If multiple entries with the same score exist, the best rank is returned.
     */
    public int getRank(@Nonnull Profile profile) {
        return firstIndexOf(profile.getStatValue(statId)) + 1;
    }

    /**
     * Returns the score of the player on the index.
     */
    public int getScore(int index) {
        return getScore(getNode(index));
    }

    /**
     * Returns the score on the node.
     */
    public int getScore(@Nonnull Node node) {
        return node.item.first;
    }

    /**
     * Returns mention of the player on the index.
     */
    public String getMention(int index) {
        return getMention(getNode(index));
    }

    /**
     * Returns mention of the player on the node.
     */
    public String getMention(@Nonnull Node node) {
        return "<@" + node.item.second.getId() + ">";
    }

    /**
     * Returns {@link Profile} of the player on the index.
     */
    public Profile getProfile(int index) {
        return getProfile(getNode(index));
    }

    /**
     * Returns {@link Profile} on the node.
     */
    public Profile getProfile(@Nonnull Node node) {
        return node.item.second;
    }

    /**
     * Returns a String containing the rank, mention and score on the given index.
     */
    public String toEntry(int index) {
        return toEntry(getNode(index), index + 1);
    }

    /**
     * Returns a String containing the rank, mention and score.
     */
    public String toEntry(@Nonnull Node node, int rank) {
        return rank + ". " + getMention(node) + ": " + Util.formatNumber(getScore(node));
    }

    /**
     * Creates given amount of entries starting from given index.
     * The entries contain rank, mention of the user and the score.
     * Each entry is on its own line, and they are sorted starting from the best rank.
     *
     * @param index      index of the first entry
     * @param entryCount amount of entries
     * @param statName   name of the stat.
     */
    public String toEntry(int index, int entryCount, String statName) {
        Node node = getNode(index);
        StringBuilder entry = new StringBuilder();

        while (entryCount > 0) {
            index++;
            entry.append(toEntry(node, index))
                    .append(' ')
                    .append(statName);

            node = node.next;

            if (entryCount > 1) {
                entry.append('\n');
                entryCount--;
            } else break;
        }

        return entry.toString();
    }

    /**
     * Puts the node to the beginning of the entries.
     */
    private void linkFirst(@Nonnull Pair<Integer, Profile> node) {
        final Node f = first;
        final Node newNode = new Node(null, node, f);

        first = newNode;
        if (f == null) last = newNode;
        else f.previous = newNode;

        size++;
        modCount++;
    }

    /**
     * Puts the node to the end of the entries.
     */
    private void linkLast(@Nonnull Pair<Integer, Profile> node) {
        final Node l = last;
        final Node newNode = new Node(l, node, null);

        last = newNode;
        if (l == null) first = newNode;
        else l.next = newNode;

        size++;
        modCount++;
    }


    /**
     * Puts the entry before the given node.
     *
     * @param anchor Node to link the entry before
     */
    private void linkBefore(@Nonnull Pair<Integer, Profile> entry, @Nullable Node anchor) {
        if (anchor == null) { // Adding to first index
            linkFirst(entry);
            return;
        }

        final Node previous = anchor.previous;
        final Node newNode = new Node(previous, entry, anchor);
        anchor.previous = newNode;

        if (previous == null) first = newNode;
        else previous.next = newNode;

        size++;
        modCount++;
    }

    /**
     * Returns the {@link Leaderboard.Node} of the given profile.
     * If no entry exist the last Node is returned.
     */
    public Node getNode(final Profile profile) {
        Node node = first;
        while (node != null) {
            if (Objects.equals(node.item.second.getId(), profile.getId())) return node;
            node = node.next;
        }

        return last;
    }

    /**
     * Returns the {@link Leaderboard.Node} of the given user.
     * If no entry exist the last Node is returned.
     *
     * @param userId id of the user
     */
    public Node getNode(final long userId) {
        Node node = first;
        while (node != null) {
            if (node.item.second.getId() == userId) return node;
            node = node.next;
        }

        return last;
    }


    /**
     * Returns the {@link Leaderboard.Node} in the given index.
     */
    public Node getNode(final int index) {
        Node node;
        if (index < (size >> 1)) {
            node = first;
            for (int i = 0; i < index; i++)
                node = node.next;
        } else {
            node = last;
            for (int i = size - 1; i > index; i--)
                node = node.previous;
        }
        return node;
    }

    /**
     * Removes the node from the leaderboard.
     */
    private Pair<Integer, Profile> unlink(Node node) {
        final Pair<Integer, Profile> element = node.item;
        final Node next = node.next;
        final Node prev = node.previous;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.previous = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.previous = prev;
            node.next = null;
        }

        node.item = null;
        size--;
        modCount++;
        return element;
    }

    public boolean contains(Profile profile) {
        return indexOf(profile) > 0;
    }

    public boolean contains(long userId) {
        return indexOf(userId) > 0;
    }

    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size > 0;
    }

    /**
     * Returns a new Iterator for the Leaderboard.
     */
    public ListIterator<Pair<Integer, Profile>> listIterator(int index) {
        return new LeaderboardListIterator(index);
    }

    private class LeaderboardListIterator implements ListIterator<Pair<Integer, Profile>> {
        private Node lastReturned;
        private Node next;
        private int nextIndex;
        private int expectedModCount = modCount;

        LeaderboardListIterator(int index) {
            next = (index == size) ? null : getNode(index);
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public Pair<Integer, Profile> next() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public Pair<Integer, Profile> previous() {
            checkForComodification();
            if (!hasPrevious()) throw new NoSuchElementException();

            lastReturned = next = (next == null) ? last : next.previous;
            nextIndex--;
            return lastReturned.item;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();

            Node lastNext = lastReturned.next;
            unlink(lastReturned);

            if (next == lastReturned) next = lastNext;
            else nextIndex--;

            lastReturned = null;
            expectedModCount++;
        }

        public void set(Pair<Integer, Profile> e) {
            if (lastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.item = e;
        }

        public void add(Pair<Integer, Profile> e) {
            checkForComodification();
            lastReturned = null;

            if (next == null) linkLast(e);
            else linkBefore(e, next);

            nextIndex++;
            expectedModCount++;
        }

        public void forEachRemaining(Consumer<? super Pair<Integer, Profile>> action) {
            Objects.requireNonNull(action);
            while (modCount == expectedModCount && nextIndex < size) {
                action.accept(next.item);
                lastReturned = next;
                next = next.next;
                nextIndex++;
            }

            checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount) throw new ConcurrentModificationException();
        }
    }

    public static class Node {
        Pair<Integer, Profile> item;
        Node next;
        Node previous;

        Node(Node previous, Pair<Integer, Profile> element, Node next) {
            this.item = element;
            this.next = next;
            this.previous = previous;
        }
    }

}
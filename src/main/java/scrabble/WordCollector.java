package scrabble;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class WordCollector implements Collector<String, WordCollector, WordCollector> {

    private final Map<Integer,List<String>> map = Map.of(
            1, List.of("A", "I"),
            2, new LinkedList<>(),
            3, new LinkedList<>(),
            4, new LinkedList<>(),
            5, new LinkedList<>(),
            6, new LinkedList<>(),
            7, new LinkedList<>(),
            8, new LinkedList<>(),
            9, new LinkedList<>()
    );

    public void add(String word) {
        this.map.get(word.length()).add(word);
    }

    public void addAll(Collection<String> words) {
        words.forEach(this::add);
    }

    public List<String> getWordsOfLength(int length) {
        return this.map.get(length);
    }

    public void clearWordsOfLength(int length) {
        this.map.get(length).clear();
    }

    public Collection<String> getWords() {
        return this.map.values().stream().flatMap(List::stream).toList();
    }

    public WordCollector merge(WordCollector other) {
        this.addAll(other.getWords());
        return this;
    }

    @Override
    public Supplier<WordCollector> supplier() {
        return WordCollector::new;
    }

    @Override
    public BiConsumer<WordCollector,String> accumulator() {
        return WordCollector::add;
    }

    @Override
    public BinaryOperator<WordCollector> combiner() {
        return WordCollector::merge;
    }

    @Override
    public Function<WordCollector, WordCollector> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED);
    }
}

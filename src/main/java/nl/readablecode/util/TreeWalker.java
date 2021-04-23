package nl.readablecode.util;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Optional.ofNullable;

/**
 * Models a generic 'tree walker' that can perform a depth-first or breadth-first 'walk' over a
 * some generic tree type.
 *
 * @param <T>   the generic type of the nodes of the tree
 */
@RequiredArgsConstructor
public class TreeWalker<T> {
    /** A function that, given some node, returns the children of that node, or an empty list. */
    private final Function<T, List<T>> traverser;

    /** A Predicate that will be called with each visited node, until it returns <code>true</code>. */
    private final Predicate<T> visitor;

    /**
     * Performs a depth-first 'walk' over the tree represented by the given root nodes.
     *
     * @param roots a Collection of starting nodes (roots) of the tree
     * @return      the first node that satisfies the {@link #visitor} predicate, or <code>null</code>
     *              if no node satisfies the predicate
     */
    public T depthFirst(Collection<T> roots) {
        return findFirst(roots, this::depthFirst).orElse(null);
    }

    private T depthFirst(T root) {
        return ofNullable(returnIfMatches(root)).orElseGet(() -> depthFirst(traverser.apply(root)));
    }

    /**
     * Performs a breadth-first 'walk' over the tree represented by the given root nodes.
     *
     * @param roots a Collection of starting nodes (roots) of the tree
     * @return      the first node that satisfies the {@link #visitor} predicate, or <code>null</code>
     *              if no node satisfies the predicate
     */
    public T breadthFirst(Collection<T> roots) {
        return findFirst(roots, this::returnIfMatches).orElseGet(() ->
                findFirst(roots, traverser.andThen(this::breadthFirst)).orElse(null));
    }

    private T returnIfMatches(T t) {
        return visitor.test(t) ? t : null;
    }

    @SuppressWarnings("java:S4276") // UnaryOperator<T> not compatible with "traverser.andThen(this::breadthFirst)"
    private Optional<T> findFirst(Collection<T> collection, Function<T,T> matcher) {
        return collection.stream().map(matcher).filter(Objects::nonNull).findFirst();
    }
}

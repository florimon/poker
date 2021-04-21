package nl.readablecode.util;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

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

    private UnaryOperator<T> operator() {
        return t -> visitor.test(t) ? t : null;
    }

    /**
     * Performs a depth-first 'walk' over the tree represented by the given root nodes.
     *
     * @param roots a Collection of starting nodes (roots) of the tree
     * @return      the first node that satisfies the {@link #visitor} predicate, or <code>null</code>
     *              if no node satisfies the predicate
     */
    public T depthFirst(Collection<T> roots) {
        for (T root : roots) {
            if (visitor.test(root)) {
                return root;
            } else {
                T t = depthFirst(traverser.apply(root));
                if (t != null) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Performs a breadth-first 'walk' over the tree represented by the given root nodes.
     *
     * @param roots a Collection of starting nodes (roots) of the tree
     * @return      the first node that satisfies the {@link #visitor} predicate, or <code>null</code>
     *              if no node satisfies the predicate
     */
    public T breadthFirst(Collection<T> roots) {
        for (T root : roots) {
            if (visitor.test(root)) {
                return root;
            }
        }
        for (T root : roots) {
            T t = breadthFirst(traverser.apply(root));
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    public T depthFirst2(Collection<T> roots) {
        return findFirst(roots, this::depthFirst2).orElse(null);
    }

    private T depthFirst2(T root) {
        return ofNullable(operator().apply(root)).orElseGet(() -> depthFirst2(traverser.apply(root)));
    }

    public T breadthFirst2(Collection<T> roots) {
        return findFirst(roots, operator()).orElseGet(() ->
                findFirst(roots, root -> breadthFirst2(traverser.apply(root))).orElse(null));
    }

    private Optional<T> findFirst(Collection<T> collection, UnaryOperator<T> operator) {
        return collection.stream().map(operator).filter(Objects::nonNull).findFirst();
    }
}
